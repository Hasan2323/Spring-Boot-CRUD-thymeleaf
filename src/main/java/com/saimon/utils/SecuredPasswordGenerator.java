package com.saimon.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author Muhammad Saimon
 * @since Dec 27/12/2020 23:55
 */

public class SecuredPasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPass = "4321";
        String encodedPass = encoder.encode(rawPass);

        System.out.println(encodedPass);
    }
}
