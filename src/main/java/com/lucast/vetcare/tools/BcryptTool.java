package com.lucast.vetcare.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTool {
    public static void main(String[] args) {
        var encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("admin123"));
    }
}
