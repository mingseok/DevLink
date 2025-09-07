package dev.devlink.common.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordEncoderConfiguration 테스트")
class PasswordEncoderConfigurationTest {

    private final PasswordEncoderConfiguration configuration = new PasswordEncoderConfiguration();

    @Test
    @DisplayName("BCryptPasswordEncoder Bean을 생성한다")
    void passwordEncoder_CreatesBCryptPasswordEncoder() {
        // when
        PasswordEncoder passwordEncoder = configuration.passwordEncoder();

        // then
        assertAll(
                () -> assertNotNull(passwordEncoder),
                () -> assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder)
        );
    }

    @Test
    @DisplayName("생성된 PasswordEncoder로 비밀번호를 암호화할 수 있다")
    void passwordEncoder_CanEncodePassword() {
        // given
        PasswordEncoder passwordEncoder = configuration.passwordEncoder();
        String rawPassword = "testPassword123";

        // when
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // then
        assertAll(
                () -> assertNotNull(encodedPassword),
                () -> assertNotEquals(rawPassword, encodedPassword),
                () -> assertTrue(encodedPassword.startsWith("$2a$")), // BCrypt 해시 형식
                () -> assertTrue(passwordEncoder.matches(rawPassword, encodedPassword))
        );
    }

    @Test
    @DisplayName("동일한 비밀번호라도 매번 다른 해시값을 생성한다")
    void passwordEncoder_GeneratesDifferentHashForSamePassword() {
        // given
        PasswordEncoder passwordEncoder = configuration.passwordEncoder();
        String rawPassword = "samePassword";

        // when
        String hash1 = passwordEncoder.encode(rawPassword);
        String hash2 = passwordEncoder.encode(rawPassword);

        // then
        assertAll(
                () -> assertNotEquals(hash1, hash2),
                () -> assertTrue(passwordEncoder.matches(rawPassword, hash1)),
                () -> assertTrue(passwordEncoder.matches(rawPassword, hash2))
        );
    }

    @Test
    @DisplayName("잘못된 비밀번호는 매칭되지 않는다")
    void passwordEncoder_DoesNotMatchWrongPassword() {
        // given
        PasswordEncoder passwordEncoder = configuration.passwordEncoder();
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordEncoder.encode(correctPassword);

        // when & then
        assertFalse(passwordEncoder.matches(wrongPassword, encodedPassword));
    }
}
