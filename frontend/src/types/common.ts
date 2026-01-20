/**
 * 通用类型定义
 * 定义系统中常用的 TypeScript 类型
 */

/**
 * 统一响应结果封装
 * 对应后端 Result<T> 类
 */
export interface Result<T = any> {
  /** 响应码：200 成功，其他为失败 */
  code: number
  /** 响应消息 */
  msg: string
  /** 响应数据 */
  data: T
}

/**
 * 分页响应结果
 * 对应后端分页查询返回的数据结构
 */
export interface PageResult<T = any> {
  /** 数据列表 */
  records: T[]
  /** 总记录数 */
  total: number
  /** 当前页码 */
  current: number
  /** 每页大小 */
  size: number
  /** 总页数 */
  pages: number
}

/**
 * 分页查询参数
 * 用于列表查询时的分页参数
 */
export interface PageParams {
  /** 当前页码，从 1 开始 */
  current: number
  /** 每页大小 */
  size: number
  /** 排序字段 */
  orderBy?: string
  /** 排序方式：asc 升序，desc 降序 */
  order?: 'asc' | 'desc'
}

/**
 * 下拉选项
 * 用于下拉框、单选框等组件的选项数据
 */
export interface Option {
  /** 选项值 */
  value: string | number
  /** 选项标签 */
  label: string
  /** 是否禁用 */
  disabled?: boolean
  /** 子选项（用于级联选择） */
  children?: Option[]
}

/**
 * 树形节点
 * 用于树形组件的节点数据
 */
export interface TreeNode {
  /** 节点 ID */
  id: string | number
  /** 节点标签 */
  label: string
  /** 父节点 ID */
  parentId?: string | number
  /** 子节点 */
  children?: TreeNode[]
  /** 是否禁用 */
  disabled?: boolean
  /** 是否为叶子节点 */
  isLeaf?: boolean
}

/**
 * 表格列配置
 * 用于动态表格的列定义
 */
export interface TableColumn {
  /** 列标题 */
  title: string
  /** 列字段名 */
  key: string
  /** 列宽度 */
  width?: number
  /** 是否固定列 */
  fixed?: 'left' | 'right'
  /** 是否可排序 */
  sortable?: boolean
  /** 是否可筛选 */
  filterable?: boolean
  /** 对齐方式 */
  align?: 'left' | 'center' | 'right'
}

/**
 * 用户信息
 * 登录用户的基本信息
 */
export interface UserInfo {
  /** 用户 ID */
  id: number
  /** 用户名 */
  username: string
  /** 真实姓名 */
  realName: string
  /** 头像 URL */
  avatar?: string
  /** 邮箱 */
  email?: string
  /** 手机号 */
  phone?: string
  /** 部门 ID */
  deptId?: number
  /** 部门名称 */
  deptName?: string
  /** 角色列表 */
  roles: string[]
  /** 权限列表 */
  permissions: string[]
}

/**
 * 登录响应数据
 */
export interface LoginResult {
  /** 访问令牌 */
  accessToken: string
  /** 刷新令牌 */
  refreshToken: string
  /** 令牌过期时间（秒） */
  expiresIn: number
  /** 用户信息 */
  userInfo: UserInfo
}

/**
 * 菜单项
 * 用于侧边栏菜单和路由配置
 */
export interface MenuItem {
  /** 菜单 ID */
  id: number
  /** 菜单名称 */
  name: string
  /** 菜单标题 */
  title: string
  /** 菜单图标 */
  icon?: string
  /** 路由路径 */
  path: string
  /** 组件路径 */
  component?: string
  /** 父菜单 ID */
  parentId?: number
  /** 排序号 */
  sort: number
  /** 是否隐藏 */
  hidden?: boolean
  /** 是否缓存 */
  keepAlive?: boolean
  /** 子菜单 */
  children?: MenuItem[]
}

/**
 * 表单规则
 * 用于表单验证
 */
export interface FormRule {
  /** 是否必填 */
  required?: boolean
  /** 错误提示信息 */
  message?: string
  /** 触发方式 */
  trigger?: 'blur' | 'change' | 'input'
  /** 最小长度 */
  min?: number
  /** 最大长度 */
  max?: number
  /** 正则表达式 */
  pattern?: RegExp
  /** 自定义验证函数 */
  validator?: (rule: any, value: any) => boolean | Promise<boolean>
}
