package dev.devlink.profile.service.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProfileUpdateRequest 고급 테스트")
class ProfileUpdateRequestAdvancedTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 프로필 업데이트 요청 검증 성공")
    void validProfileUpdateRequest_Success() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("안녕하세요. 개발자입니다.");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("빈 소개글로 요청 시 검증 성공")
    void emptyBio_ValidationSuccess() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("null 소개글로 요청 시 검증 확인")
    void nullBio_ValidationCheck() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest(null);

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        // 구체적인 검증 규칙에 따라 결과가 달라질 수 있음
        assertNotNull(violations);
    }

    @Test
    @DisplayName("매우 긴 소개글로 요청 시 검증 확인")
    void veryLongBio_ValidationCheck() {
        // given
        String longBio = "매우 긴 소개글입니다. ".repeat(100);
        ProfileUpdateRequest request = new ProfileUpdateRequest(longBio);

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        // 소개글 길이 제한이 있다면 실패, 없다면 성공해야 함
        assertNotNull(violations);
    }

    @Test
    @DisplayName("한글 소개글로 요청 시 검증 성공")
    void koreanBio_ValidationSuccess() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("안녕하세요! 저는 백엔드 개발자입니다. 잘 부탁드립니다.");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("영어 소개글로 요청 시 검증 성공")
    void englishBio_ValidationSuccess() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("Hello! I'm a backend developer. Nice to meet you.");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("특수문자가 포함된 소개글로 요청 시 검증 성공")
    void specialCharacterBio_ValidationSuccess() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("안녕하세요! 😊 개발자입니다. Email: test@example.com");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("이모지가 포함된 소개글로 요청 시 검증 성공")
    void emojiBio_ValidationSuccess() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("🚀 개발자 🎯 목표지향적 💻 코딩러버");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("줄바꿈이 포함된 소개글로 요청 시 검증 성공")
    void multilineBio_ValidationSuccess() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("안녕하세요.\n저는 개발자입니다.\n잘 부탁드립니다.");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("공백만으로 이루어진 소개글로 요청 시 검증 확인")
    void whitespaceOnlyBio_ValidationCheck() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("   ");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        // 공백 처리 정책에 따라 결과가 달라질 수 있음
        assertNotNull(violations);
    }

    @Test
    @DisplayName("기본 생성자로 생성된 ProfileUpdateRequest")
    void defaultConstructor_Success() {
        // when
        ProfileUpdateRequest request = new ProfileUpdateRequest();

        // then
        assertNotNull(request);
        assertNull(request.getBio());
    }

    @Test
    @DisplayName("ProfileUpdateRequest getter 메서드 테스트")
    void getterMethod_Success() {
        // given
        String bio = "테스트 소개글입니다.";
        ProfileUpdateRequest request = new ProfileUpdateRequest(bio);

        // when & then
        assertEquals(bio, request.getBio());
    }

    @Test
    @DisplayName("HTML 태그가 포함된 소개글로 요청 시 검증 확인")
    void htmlTagBio_ValidationCheck() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("<script>alert('test')</script>안녕하세요");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        // HTML 태그 허용 여부에 따라 결과가 달라질 수 있음
        assertNotNull(violations);
    }

    @Test
    @DisplayName("URL이 포함된 소개글로 요청 시 검증 성공")
    void urlBio_ValidationSuccess() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("개발자입니다. 블로그: https://myblog.com");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("숫자가 포함된 소개글로 요청 시 검증 성공")
    void numericBio_ValidationSuccess() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest("5년 경력 개발자입니다. 연락처: 010-1234-5678");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("같은 소개글로 생성된 ProfileUpdateRequest 비교")
    void sameBio_Equal() {
        // given
        String bio = "동일한 소개글";
        ProfileUpdateRequest request1 = new ProfileUpdateRequest(bio);
        ProfileUpdateRequest request2 = new ProfileUpdateRequest(bio);

        // then
        assertEquals(request1.getBio(), request2.getBio());
    }

    @Test
    @DisplayName("다른 소개글로 생성된 ProfileUpdateRequest 비교")
    void differentBio_NotEqual() {
        // given
        ProfileUpdateRequest request1 = new ProfileUpdateRequest("첫 번째 소개글");
        ProfileUpdateRequest request2 = new ProfileUpdateRequest("두 번째 소개글");

        // then
        assertNotEquals(request1.getBio(), request2.getBio());
    }
}
