package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.User;
import com.shreeganesh.enterprises.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    // ✅ Only USER accounts
    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(u -> "USER".equals(u.getRole()))
                .toList();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // 🔒 Block user
    public void blockUser(Long id) {

        User user = getUser(id);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setEnabled(false);

        userRepository.saveAndFlush(user);
    }

    // 🔓 Unblock user
    public void unblockUser(Long id) {

        User user = getUser(id);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setEnabled(true);

        userRepository.saveAndFlush(user);
    }
}
