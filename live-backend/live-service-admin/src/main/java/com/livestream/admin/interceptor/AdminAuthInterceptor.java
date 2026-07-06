package com.livestream.admin.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理后台鉴权拦截器（C-08安全修复）
 * 校验请求头中的X-User-Role是否为ADMIN，拒绝非管理员访问管理后台接口
 */
@Slf4j
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String role = request.getHeader("X-User-Role");
        
        if (role == null || !"ADMIN".equals(role)) {
            log.warn("非管理员用户尝试访问管理后台接口: path={}, role={}", request.getRequestURI(), role);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("无权限访问管理后台");
            return false;
        }
        
        return true;
    }
}
