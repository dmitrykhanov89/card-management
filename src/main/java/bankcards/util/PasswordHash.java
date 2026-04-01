package bankcards.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("admin123")); // пароль для админа
        System.out.println(encoder.encode("user123"));  // пароль для обычного юзера
    }
}
