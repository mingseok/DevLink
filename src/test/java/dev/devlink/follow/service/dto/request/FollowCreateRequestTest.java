package dev.devlink.follow.service.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FollowCreateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("유효한 팔로우 요청을 생성할 수 있다")
    void createRequest_WithValidFolloweeId_Success() {
        // given
        FollowCreateRequest request = new FollowCreateRequest(2L);

        // when
        Set<ConstraintViolation<FollowCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
        assertThat(request.getFolloweeId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("팔로우 대상 ID가 null이면 검증 실패")
    void createRequest_WithNullFolloweeId_ValidationFails() {
        // given
        FollowCreateRequest request = new FollowCreateRequest(null);

        // when
        Set<ConstraintViolation<FollowCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("팔로우할 대상 ID는 필수입니다.");
    }

    @Test
    @DisplayName("양수 ID로 팔로우 요청을 생성할 수 있다")
    void createRequest_WithPositiveId_Success() {
        // given
        FollowCreateRequest request = new FollowCreateRequest(100L);

        // when
        Set<ConstraintViolation<FollowCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
        assertThat(request.getFolloweeId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("getter가 올바르게 동작한다")
    void getters_WorkCorrectly() {
        // given
        Long followeeId = 123L;
        FollowCreateRequest request = new FollowCreateRequest(followeeId);

        // when & then
        assertThat(request.getFolloweeId()).isEqualTo(followeeId);
    }
}
