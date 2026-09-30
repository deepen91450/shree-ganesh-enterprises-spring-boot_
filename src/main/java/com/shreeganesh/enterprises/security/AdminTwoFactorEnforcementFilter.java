package com.shreeganesh.enterprises.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AdminTwoFactorEnforcementFilter extends OncePerRequestFilter {

    private static final String VERIFIED_SESSION_ATTRIBUTE = "ADMIN_2FA_VERIFIED";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.startsWith("/admin/")
                || path.equals("/admin/login")
                || path.startsWith("/admin/2fa")
                || path.startsWith("/admin/css/")
                || path.startsWith("/admin/js/")
                || path.startsWith("/admin/images/")
                || path.equals("/admin/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {

            Boolean verified = (Boolean) request.getSession()
                    .getAttribute(VERIFIED_SESSION_ATTRIBUTE);

            if (!Boolean.TRUE.equals(verified)) {
                response.sendRedirect(request.getContextPath() + "/admin/2fa");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
