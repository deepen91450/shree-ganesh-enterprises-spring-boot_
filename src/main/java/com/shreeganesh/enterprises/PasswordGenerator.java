package com.shreeganesh.enterprises;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String bcrypt = encoder.encode("Deepen@9145");
        System.out.println("Your Bcrypt Password:");
        System.out.println(bcrypt);
    }
}

