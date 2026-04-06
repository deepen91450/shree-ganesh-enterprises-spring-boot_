package com.shreeganesh.enterprises.config;

import com.shreeganesh.enterprises.service.AdminUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)
public class AdminSecurityConfig {

    @Autowired
    private AdminUserDetailsService adminUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {

        http
                // Applies ONLY to admin URLs
                .securityMatcher("/admin/**")

                .authorizeHttpRequests(auth -> auth

                        // Public admin pages
                        .requestMatchers(
                                "/admin/login",
                                "/admin/2fa/**",          // ⭐ allow OTP pages
                                "/admin/css/**",
                                "/admin/js/**",
                                "/admin/images/**"
                        ).permitAll()

                        .requestMatchers("/admin/about/**").hasRole("ADMIN")


                        // Everything else requires ADMIN
                        .anyRequest().hasRole("ADMIN")
                )


                .formLogin(login -> login
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/2fa", true)

                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                )
                .exceptionHandling(e -> e.accessDeniedPage("/access-denied"))


                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)               // single admin session
                        .maxSessionsPreventsLogin(false) // ✅ IMPORTANT
                )


                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/admin/login",
                                "/admin/logout"
                        ));


        http.authenticationProvider(adminProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider adminProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
