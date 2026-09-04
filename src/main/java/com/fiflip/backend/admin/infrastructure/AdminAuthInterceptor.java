package com.fiflip.backend.admin.infrastructure;

import com.fiflip.backend.admin.application.AdminAuthUseCases;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final AdminAuthUseCases adminAuthUseCases;

    public AdminAuthInterceptor(AdminAuthUseCases adminAuthUseCases) {
        this.adminAuthUseCases = adminAuthUseCases;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        String token = (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;
        if (token == null || !adminAuthUseCases.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"No autorizado\"}");
            return false;
        }
        return true;
    }
}
