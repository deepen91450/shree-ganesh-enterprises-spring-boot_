package com.shreeganesh.enterprises.config;

import com.shreeganesh.enterprises.security.CustomOAuth2UserService;
import com.shreeganesh.enterprises.security.OAuth2LoginSuccessHandler;
import com.shreeganesh.enterprises.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
@Order(2)
public class UserSecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain userFilter(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/**")

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/products/**", "/product/**", "/categories/**",
                                "/about", "/contact", "/submit-enquiry",
                                "/css/**", "/js/**", "/images/**", "/Images/**",
                                "/uploads/**", "/cart/count", "/search/**", "/banners",
                                "/favicon.ico", "/error"
                        ).permitAll()

                        .requestMatchers(
                                "/login", "/signup", "/forgot-password", "/reset-password",
                                "/reset-password/**", "/oauth2/**", "/login/oauth2/**"
                        ).permitAll()

                        .requestMatchers("/user/**", "/cart/**", "/my-orders/**", "/my-profile/**")
                        .hasRole("USER")

                        .anyRequest().authenticated()
                )

                // ✅ NORMAL LOGIN — uses custom success handler for redirect-back
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")

                        // ✅ SUCCESS
                        .successHandler(loginSuccessHandler())

                        // ❌ FAILED LOGIN
                        .failureHandler((request, response, exception) -> {

                            if (exception instanceof org.springframework.security.authentication.DisabledException) {

                                response.sendRedirect("/login?blocked=true");

                            } else {

                                response.sendRedirect("/login?error=true");
                            }
                        })

                        .permitAll()
                )

                // 🔥 GOOGLE LOGIN
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                // ✅ LOGOUT
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                )

                // ✅ EXCEPTION HANDLING
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                        .authenticationEntryPoint((req, res, authException) -> {
                            if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
                                res.setStatus(401);
                                res.setContentType("application/json");
                                res.getWriter().write(
                                        "{\"error\":\"not_logged_in\", \"redirect\":\"/login\"}"
                                );
                            } else {
                                res.sendRedirect("/login");
                            }
                        })
                )

                // 🔒 SECURITY HEADERS
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "img-src 'self' data: https:; " +
                                                "script-src 'self' 'unsafe-inline' https://challenges.cloudflare.com; " +
                                                "style-src 'self' 'unsafe-inline';" +
                                                "frame-src https://challenges.cloudflare.com;"
                                )
                        )
                        .frameOptions(frame -> frame.sameOrigin())
                );

        http.authenticationProvider(userProvider());

        return http.build();
    }

    // ✅ CUSTOM SUCCESS HANDLER — redirects back to original page after login
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication)
                    throws IOException {

                // Read ?redirect= param set by script.js
                String redirectParam = request.getParameter("redirect");

                if (redirectParam != null && !redirectParam.isBlank()) {
                    String decoded = URLDecoder.decode(redirectParam, StandardCharsets.UTF_8);

                    // ✅ Safety check — only allow redirects to our own pages
                    if (decoded.startsWith("/") || decoded.contains("localhost") || decoded.contains(request.getServerName())) {
                        response.sendRedirect(decoded);
                        return;
                    }
                }

                // Default fallback if no redirect param
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider userProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}