package com.shreeganesh.enterprises.security;

import com.shreeganesh.enterprises.entity.User;
import com.shreeganesh.enterprises.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public OAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        // ✅ Save or update user
        userService.createOrUpdateOauthUser(
                token.getAuthorizedClientRegistrationId(),
                providerId,
                name,
                email
        );

        // ✅ FETCH USER
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isPresent()) {

            User user = userOpt.get();

            // 🔒 BLOCKED USER CHECK
            if (!user.isEnabled()) {

                // logout session
                request.getSession().invalidate();

                response.sendRedirect("/login?blocked=true");

                return;
            }

            // 📱 CHECK PHONE
            if (user.getPhone() == null || user.getPhone().isBlank()) {

                response.sendRedirect("/enter-phone");

                return;
            }
        }

// ✅ NORMAL LOGIN
        response.sendRedirect("/");
    }
}