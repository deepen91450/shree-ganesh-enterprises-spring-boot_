package com.shreeganesh.enterprises.config;

import com.shreeganesh.enterprises.security.CustomOAuth2UserService;
import com.shreeganesh.enterprises.security.OAuth2LoginSuccessHandler;
import com.shreeganesh.enterprises.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                .securityMatcher("/**")   // applies only to user section
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/products/**", "/about", "/contact",
                                "/css/**", "/js/**", "/images/**",
                                "/uploads/**", "/cart/count", "/search/**", "/banners"
                        ).permitAll()



                        .requestMatchers("/login", "/signup", "/forgot-password", "/reset-password",
                                "/reset-password/**",  "/oauth2/**", "/login/oauth2/**").permitAll()

                        .requestMatchers( "/user/**", "/cart/**", "/my-orders/**", "/my-profile/**")
                        .hasRole("USER")

                        .anyRequest().permitAll()  // <-- FIX: So no redirect loop
                )

                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                // ⭐ GOOGLE LOGIN (THIS WAS MISSING / BLOCKED)
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/", true)
                )



                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                )
                // ⭐ COMBINED EXCEPTION HANDLING (Correct way)
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied") // if user is logged in but forbidden

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
                .csrf(csrf -> csrf.disable());

        http.authenticationProvider(userProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider userProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
