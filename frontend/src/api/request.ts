/**
 * Axios 请求封装
 * 提供统一的 HTTP 请求接口，包含拦截器、错误处理、Token 管理等功能
 */

import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import type { Result } from '@/types/common'

/**
 * 请求配置接口
 * 扩展 Axios 配置，添加自定义选项
 */
interface RequestConfig extends AxiosRequestConfig {
  /** 是否显示错误提示，默认 true */
  showError?: boolean
  /** 是否显示加载提示，默认 false */
  showLoading?: boolean
  /** 是否需要 Token，默认 true */
  needToken?: boolean
}

/**
 * Token 存储键名
 */
const TOKEN_KEY = 'access_token'
const REFRESH_TOKEN_KEY = 'refresh_token'

/**
 * 创建 Axios 实例
 */
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // API 基础路径
  timeout: 30000, // 请求超时时间 30 秒
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

/**
 * 是否正在刷新 Token
 */
let isRefreshing = false

/**
 * 待重试的请求队列
 */
let requestQueue: Array<(token: string) => void> = []

/**
 * 获取 Token
 */
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置 Token
 */
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除 Token
 */
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}

/**
 * 获取刷新 Token
 */
export function getRefreshToken(): string | null {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

/**
 * 设置刷新 Token
 */
export function setRefreshToken(token: string): void {
  localStorage.setItem(REFRESH_TOKEN_KEY, token)
}

/**
 * 刷新 Token
 * 使用 refresh_token 获取新的 access_token
 */
async function refreshToken(): Promise<string> {
  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    throw new Error('No refresh token')
  }

  try {
    const response = await axios.post<Result<{ accessToken: string; refreshToken: string }>>(
      `${import.meta.env.VITE_API_BASE_URL || '/api'}/auth/refresh`,
      { refreshToken }
    )

    if (response.data.code === 200) {
      const { accessToken, refreshToken: newRefreshToken } = response.data.data
      setToken(accessToken)
      setRefreshToken(newRefreshToken)
      return accessToken
    } else {
      throw new Error('Refresh token failed')
    }
  } catch (error) {
    // 刷新失败，清除 Token 并跳转登录页
    removeToken()
    window.location.href = '/login'
    throw error
  }
}

/**
 * 请求拦截器
 * 在请求发送前进行处理
 */
service.interceptors.request.use(
  (config: any) => {
    const customConfig = config as RequestConfig

    // 添加 Token 到请求头
    if (customConfig.needToken !== false) {
      const token = getToken()
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
    }

    // 显示加载提示
    if (customConfig.showLoading) {
      // TODO: 显示全局 Loading
      // window.$message?.loading('加载中...', { duration: 0 })
    }

    return config
  },
  (error: AxiosError) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 在响应返回后进行处理
 */
service.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const customConfig = response.config as RequestConfig

    // 关闭加载提示
    if (customConfig.showLoading) {
      // TODO: 关闭全局 Loading
      // window.$message?.destroyAll()
    }

    const { code, msg, data } = response.data

    // 请求成功
    if (code === 200) {
      return response
    }

    // 业务错误
    if (customConfig.showError !== false) {
      // TODO: 显示错误提示
      console.error('业务错误:', msg)
      // window.$message?.error(msg || '请求失败')
    }

    return Promise.reject(new Error(msg || '请求失败'))
  },
  async (error: AxiosError<Result>) => {
    const customConfig = error.config as RequestConfig

    // 关闭加载提示
    if (customConfig?.showLoading) {
      // TODO: 关闭全局 Loading
      // window.$message?.destroyAll()
    }

    // 请求被取消
    if (axios.isCancel(error)) {
      console.log('请求被取消:', error.message)
      return Promise.reject(error)
    }

    // 网络错误
    if (!error.response) {
      console.error('网络错误:', error.message)
      // TODO: 显示错误提示
      // window.$message?.error('网络连接失败，请检查网络设置')
      return Promise.reject(error)
    }

    const { status, data } = error.response

    // 401 未授权 - Token 过期或无效
    if (status === 401) {
      // 如果正在刷新 Token，将请求加入队列
      if (isRefreshing) {
        return new Promise((resolve) => {
          requestQueue.push((token: string) => {
            if (error.config) {
              error.config.headers.Authorization = `Bearer ${token}`
              resolve(service(error.config))
            }
          })
        })
      }

      // 开始刷新 Token
      isRefreshing = true

      try {
        const newToken = await refreshToken()

        // 重试队列中的请求
        requestQueue.forEach((callback) => callback(newToken))
        requestQueue = []

        // 重试当前请求
        if (error.config) {
          error.config.headers.Authorization = `Bearer ${newToken}`
          return service(error.config)
        }
      } catch (refreshError) {
        // 刷新失败，清空队列
        requestQueue = []
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    // 403 无权限
    if (status === 403) {
      console.error('无权限访问')
      // TODO: 显示错误提示
      // window.$message?.error('无权限访问该资源')
    }

    // 404 资源不存在
    if (status === 404) {
      console.error('资源不存在')
      // TODO: 显示错误提示
      // window.$message?.error('请求的资源不存在')
    }

    // 500 服务器错误
    if (status === 500) {
      console.error('服务器错误:', data?.msg)
      // TODO: 显示错误提示
      // window.$message?.error(data?.msg || '服务器内部错误')
    }

    // 其他错误
    if (customConfig?.showError !== false) {
      const errorMsg = data?.msg || error.message || '请求失败'
      console.error('请求错误:', errorMsg)
      // TODO: 显示错误提示
      // window.$message?.error(errorMsg)
    }

    return Promise.reject(error)
  }
)

/**
 * 通用请求方法
 */
class Request {
  /**
   * GET 请求
   */
  get<T = any>(url: string, config?: RequestConfig): Promise<Result<T>> {
    return service.get(url, config).then((res) => res.data)
  }

  /**
   * POST 请求
   */
  post<T = any>(url: string, data?: any, config?: RequestConfig): Promise<Result<T>> {
    return service.post(url, data, config).then((res) => res.data)
  }

  /**
   * PUT 请求
   */
  put<T = any>(url: string, data?: any, config?: RequestConfig): Promise<Result<T>> {
    return service.put(url, data, config).then((res) => res.data)
  }

  /**
   * DELETE 请求
   */
  delete<T = any>(url: string, config?: RequestConfig): Promise<Result<T>> {
    return service.delete(url, config).then((res) => res.data)
  }

  /**
   * PATCH 请求
   */
  patch<T = any>(url: string, data?: any, config?: RequestConfig): Promise<Result<T>> {
    return service.patch(url, data, config).then((res) => res.data)
  }

  /**
   * 上传文件
   */
  upload<T = any>(url: string, file: File, config?: RequestConfig): Promise<Result<T>> {
    const formData = new FormData()
    formData.append('file', file)

    return service.post(url, formData, {
      ...config,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }).then((res) => res.data)
  }

  /**
   * 下载文件
   */
  download(url: string, filename: string, config?: RequestConfig): Promise<void> {
    return service.get(url, {
      ...config,
      responseType: 'blob'
    }).then((res) => {
      const blob = new Blob([res.data])
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = filename
      link.click()
      URL.revokeObjectURL(link.href)
    })
  }
}

// 导出请求实例
export const request = new Request()

// 导出 Axios 实例（用于特殊场景）
export default service
