package com.crm.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * XSS过滤器
 * 防止跨站脚本攻击
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 包装请求，对参数进行XSS过滤
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(xssRequest, response);
    }

    /**
     * XSS请求包装器
     * 对请求参数进行XSS过滤
     */
    public static class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

        // XSS攻击模式
        private static final Pattern[] XSS_PATTERNS = {
                // Script标签
                Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
                Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
                Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // src属性
                Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'",
                        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
                        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // eval表达式
                Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // expression表达式
                Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // javascript:
                Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
                // vbscript:
                Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
                // onload事件
                Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // onerror事件
                Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // onclick事件
                Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // onmouseover事件
                Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
        };

        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String[] getParameterValues(String parameter) {
            String[] values = super.getParameterValues(parameter);
            if (values == null) {
                return null;
            }

            int count = values.length;
            String[] encodedValues = new String[count];
            for (int i = 0; i < count; i++) {
                encodedValues[i] = cleanXss(values[i]);
            }
            return encodedValues;
        }

        @Override
        public String getParameter(String parameter) {
            String value = super.getParameter(parameter);
            return cleanXss(value);
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return cleanXss(value);
        }

        /**
         * 清理XSS攻击代码
         */
        private String cleanXss(String value) {
            if (value == null || value.isEmpty()) {
                return value;
            }

            String cleanValue = value;
            for (Pattern pattern : XSS_PATTERNS) {
                cleanValue = pattern.matcher(cleanValue).replaceAll("");
            }

            // HTML实体编码
            cleanValue = cleanValue
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");

            return cleanValue;
        }
    }
}
