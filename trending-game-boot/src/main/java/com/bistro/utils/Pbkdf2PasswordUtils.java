package com.bistro.utils;

import com.bistro.common.utils.spring.SpringUtils;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class Pbkdf2PasswordUtils {

    //线上开启salt 与测试环境区分开
    private static final String SALT = "prod".equals(SpringUtils.getActiveProfile()) ? "_a*&%prod)Z6Wo05i$Ua12" : "";


    public static String encode(String raw) {
        Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();
        return pbkdf2PasswordEncoder.encode(raw);
    }

    public static String encodeWithSalt(String raw) {
        return encode(raw + SALT);
    }

    public static boolean matches(String raw, String encode) {
        Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();
        return pbkdf2PasswordEncoder.matches(raw, encode);
    }


    public static boolean matchesWithSalt(String raw, String encode) {
        return matches(raw + SALT, encode);
    }

}
