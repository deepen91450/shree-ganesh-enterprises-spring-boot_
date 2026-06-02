package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.User;
import com.shreeganesh.enterprises.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(input)
                .orElseGet(() -> userRepository.findByPhone(input)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));

        // Prevent admin login to user login page
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new UsernameNotFoundException("Admins must login from /admin/login");
        }


        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())

                // 🔒 BLOCK USER LOGIN
                .disabled(!user.isEnabled())

                .build();
    }
}
