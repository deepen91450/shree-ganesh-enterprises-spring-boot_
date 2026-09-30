package com.shreeganesh.enterprises.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final List<Rule> rules = List.of(
            new Rule("POST", "/login", "user-login", 5, Duration.ofMinutes(1), "/login?rateLimited=true"),
            new Rule("POST", "/admin/login", "admin-login", 5, Duration.ofMinutes(1), "/admin/login?rateLimited=true"),
            new Rule("POST", "/forgot-password", "forgot-password", 3, Duration.ofMinutes(15), "/forgot-password?rateLimited=true"),
            new Rule("POST", "/reset-password", "reset-password", 5, Duration.ofMinutes(15), "/forgot-password?rateLimited=true"),
            new Rule("POST", "/admin/2fa/verify", "admin-otp", 5, Duration.ofMinutes(5), "/admin/2fa?rateLimited=true"),
            new Rule("POST", "/submit-enquiry", "contact-enquiry", 3, Duration.ofMinutes(1), "/contact?rateLimited=true"),
            new Rule("POST", "/cart/enquiry", "cart-enquiry", 3, Duration.ofMinutes(1), "/cart/view?rateLimited=true")
    );

    public RateLimitingFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Rule rule = matchingRule(request);
        if (rule == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = rule.keyPrefix + ":" + clientIp(request);
        if (rateLimiterService.tryAcquire(key, rule.maxRequests, rule.windowDuration)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", String.valueOf(rule.windowDuration.toSeconds()));

        if (isAjaxOrApi(request)) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"too_many_requests\"}");
            return;
        }

        response.sendRedirect(request.getContextPath() + rule.redirectPath);
    }

    private Rule matchingRule(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        for (Rule rule : rules) {
            if (rule.method.equalsIgnoreCase(request.getMethod()) && rule.path.equals(path)) {
                return rule;
            }
        }

        return null;
    }

    /**
     * Trusted proxy addresses — only these are allowed to set X-Forwarded-For / X-Real-IP.
     * Add your nginx/load-balancer IP(s) here if running behind a reverse proxy.
     * Loopback addresses (127.x, ::1) are trusted by default (nginx on the same machine).
     */
    private static final Set<String> TRUSTED_PROXIES = Set.of(
            "127.0.0.1",
            "::1"
            // Add your nginx/proxy server IP here, e.g. "10.0.0.1"
    );

    private String clientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();

        // Only honour X-Forwarded-For / X-Real-IP when the direct connection
        // comes from a trusted proxy. If the request is from an untrusted source,
        // trusting these headers would allow IP spoofing to bypass rate limits.
        if (TRUSTED_PROXIES.contains(remoteAddr)) {
            String forwardedFor = request.getHeader("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                // The leftmost IP is the original client; the rest are proxy hops.
                return forwardedFor.split(",")[0].trim();
            }

            String realIp = request.getHeader("X-Real-IP");
            if (realIp != null && !realIp.isBlank()) {
                return realIp.trim();
            }
        }

        // Fall back to the TCP-level remote address (cannot be spoofed).
        return remoteAddr;
    }

    private boolean isAjaxOrApi(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        return "XMLHttpRequest".equals(requestedWith)
                || (accept != null && accept.contains("application/json"));
    }

    private record Rule(String method,
                        String path,
                        String keyPrefix,
                        int maxRequests,
                        Duration windowDuration,
                        String redirectPath) {
    }
}
