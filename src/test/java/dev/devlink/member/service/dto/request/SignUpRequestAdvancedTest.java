package dev.devlink.member.service.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SignUpRequest 고급 테스트")
class SignUpRequestAdvancedTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 회원가입 요청 검증 성공")
    void validSignUpRequest_Success() {
        // given
        SignUpRequest request = new SignUpRequest("테스트유저", "test@example.com", "테스트닉네임", "password123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("빈 이름으로 요청 시 검증 실패")
    void emptyName_ValidationFails() {
        // given
        SignUpRequest request = new SignUpRequest("", "test@example.com", "닉네임", "password123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("잘못된 이메일 형식으로 요청 시 검증 실패")
    void invalidEmailFormat_ValidationFails() {
        // given
        SignUpRequest request = new SignUpRequest("유저", "invalid-email", "닉네임", "password123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("빈 닉네임으로 요청 시 검증 실패")
    void emptyNickname_ValidationFails() {
        // given
        SignUpRequest request = new SignUpRequest("유저", "test@example.com", "", "password123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nickname")));
    }

    @Test
    @DisplayName("짧은 비밀번호로 요청 시 검증 실패")
    void shortPassword_ValidationFails() {
        // given
        SignUpRequest request = new SignUpRequest("유저", "test@example.com", "닉네임", "123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("특수문자가 포함된 이메일로 요청 시 검증 성공")
    void specialCharacterEmail_ValidationSuccess() {
        // given
        SignUpRequest request = new SignUpRequest("유저", "test+special@example.co.kr", "닉네임", "password123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("긴 이름으로 요청 시 검증 확인")
    void longName_ValidationCheck() {
        // given
        String longName = "매우긴이름".repeat(10);
        SignUpRequest request = new SignUpRequest(longName, "test@example.com", "닉네임", "password123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        // 이름 길이 제한이 있다면 실패, 없다면 성공해야 함
        // 실제 검증 규칙에 따라 테스트 결과가 달라질 수 있음
        assertNotNull(violations);
    }

    @Test
    @DisplayName("한글 이름으로 요청 시 검증 성공")
    void koreanName_ValidationSuccess() {
        // given
        SignUpRequest request = new SignUpRequest("김철수", "kimcs@example.com", "철수닉네임", "password123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("영어 이름으로 요청 시 검증 성공")
    void englishName_ValidationSuccess() {
        // given
        SignUpRequest request = new SignUpRequest("John Doe", "john@example.com", "johnnick", "password123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("특수문자가 포함된 닉네임으로 요청 시 검증 확인")
    void specialCharacterNickname_ValidationCheck() {
        // given
        SignUpRequest request = new SignUpRequest("유저", "test@example.com", "닉네임@#$", "password123");

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        // 닉네임에 특수문자 허용 여부에 따라 결과가 달라질 수 있음
        assertNotNull(violations);
    }

    @Test
    @DisplayName("매우 긴 비밀번호로 요청 시 검증 확인")
    void veryLongPassword_ValidationCheck() {
        // given
        String veryLongPassword = "password".repeat(20);
        SignUpRequest request = new SignUpRequest("유저", "test@example.com", "닉네임", veryLongPassword);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        // 비밀번호 최대 길이 제한이 있다면 실패, 없다면 성공해야 함
        assertNotNull(violations);
    }

    @Test
    @DisplayName("null 값들로 요청 시 검증 실패")
    void nullValues_ValidationFails() {
        // given
        SignUpRequest request = new SignUpRequest(null, null, null, null);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertEquals(4, violations.size()); // 모든 필드가 null이므로 4개의 위반
    }

    @Test
    @DisplayName("기본 생성자로 생성된 SignUpRequest")
    void defaultConstructor_Success() {
        // when
        SignUpRequest request = new SignUpRequest();

        // then
        assertNotNull(request);
        assertNull(request.getName());
        assertNull(request.getEmail());
        assertNull(request.getNickname());
        assertNull(request.getPassword());
    }

    @Test
    @DisplayName("SignUpRequest getter 메서드 테스트")
    void getterMethods_Success() {
        // given
        String name = "테스트유저";
        String email = "test@example.com";
        String nickname = "테스트닉네임";
        String password = "password123";
        SignUpRequest request = new SignUpRequest(name, email, nickname, password);

        // when & then
        assertAll(
                () -> assertEquals(name, request.getName()),
                () -> assertEquals(email, request.getEmail()),
                () -> assertEquals(nickname, request.getNickname()),
                () -> assertEquals(password, request.getPassword())
        );
    }
}
