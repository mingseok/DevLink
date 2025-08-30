package dev.devlink.profile.service.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileUpdateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("유효한 소개글로 요청을 생성할 수 있다")
    void createRequest_WithValidBio_Success() throws Exception {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        setBio(request, "안녕하세요. 반갑습니다.");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
        assertThat(request.getBio()).isEqualTo("안녕하세요. 반갑습니다.");
    }

    @Test
    @DisplayName("소개글이 255자를 초과하면 검증 실패")
    void createRequest_WithBioTooLong_ValidationFails() throws Exception {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        String longBio = "a".repeat(256);
        setBio(request, longBio);

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("소개글은 255자 이하로 입력해주세요.");
    }

    @Test
    @DisplayName("소개글이 null이어도 검증 통과")
    void createRequest_WithNullBio_Success() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest();

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
        assertThat(request.getBio()).isNull();
    }

    @Test
    @DisplayName("소개글이 빈 문자열이어도 검증 통과")
    void createRequest_WithEmptyBio_Success() throws Exception {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        setBio(request, "");

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
        assertThat(request.getBio()).isEmpty();
    }

    @Test
    @DisplayName("소개글이 255자 정확히면 검증 통과")
    void createRequest_WithBioExactly255Chars_Success() throws Exception {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        String bio = "a".repeat(255);
        setBio(request, bio);

        // when
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
        assertThat(request.getBio()).hasSize(255);
    }

    private void setBio(ProfileUpdateRequest request, String bio) throws Exception {
        Field bioField = ProfileUpdateRequest.class.getDeclaredField("bio");
        bioField.setAccessible(true);
        bioField.set(request, bio);
    }
}
