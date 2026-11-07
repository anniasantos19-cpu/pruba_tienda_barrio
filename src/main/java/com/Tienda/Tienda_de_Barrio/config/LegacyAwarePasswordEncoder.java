package com.Tienda.Tienda_de_Barrio.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LegacyAwarePasswordEncoder implements PasswordEncoder {
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        if (esBcrypt(encodedPassword)) {
            return bcrypt.matches(rawPassword, encodedPassword);
        }
        return MessageDigest.isEqual(rawPassword.toString().getBytes(StandardCharsets.UTF_8),
                encodedPassword.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return !esBcrypt(encodedPassword) || bcrypt.upgradeEncoding(encodedPassword);
    }

    private boolean esBcrypt(String contrasena) {
        return contrasena != null && (contrasena.startsWith("$2a$") || contrasena.startsWith("$2b$")
                || contrasena.startsWith("$2y$"));
    }
}
