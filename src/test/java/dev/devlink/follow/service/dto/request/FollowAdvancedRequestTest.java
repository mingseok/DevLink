package dev.devlink.follow.service.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FollowCreateRequest 추가 테스트")
class FollowAdvancedRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 팔로우 요청 검증 성공")
    void validFollowCreateRequest_Success() {
        // given
        FollowCreateRequest request = new FollowCreateRequest(2L);

        // when
        Set<ConstraintViolation<FollowCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("null followeeId로 요청 시 검증 실패")
    void nullFolloweeId_ValidationFails() {
        // given
        FollowCreateRequest request = new FollowCreateRequest(null);

        // when
        Set<ConstraintViolation<FollowCreateRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains("팔로우할 대상 ID는 필수입니다")));
    }

    @Test
    @DisplayName("기본 생성자로 생성된 FollowCreateRequest")
    void defaultConstructor_Success() {
        // when
        FollowCreateRequest request = new FollowCreateRequest();

        // then
        assertNotNull(request);
        assertNull(request.getFolloweeId());
    }

    @Test
    @DisplayName("음수 followeeId도 유효성 검증 통과")
    void negativeFolloweeId_ValidationPass() {
        // given
        FollowCreateRequest request = new FollowCreateRequest(-1L);

        // when
        Set<ConstraintViolation<FollowCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("0인 followeeId도 유효성 검증 통과")
    void zeroFolloweeId_ValidationPass() {
        // given
        FollowCreateRequest request = new FollowCreateRequest(0L);

        // when
        Set<ConstraintViolation<FollowCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("매우 큰 followeeId도 유효성 검증 통과")
    void largeFolloweeId_ValidationPass() {
        // given
        FollowCreateRequest request = new FollowCreateRequest(Long.MAX_VALUE);

        // when
        Set<ConstraintViolation<FollowCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("검증 에러 메시지 정확성 확인")
    void validationErrorMessage_IsCorrect() {
        // given
        FollowCreateRequest request = new FollowCreateRequest(null);

        // when
        Set<ConstraintViolation<FollowCreateRequest>> violations = validator.validate(request);

        // then
        assertEquals(1, violations.size());
        ConstraintViolation<FollowCreateRequest> violation = violations.iterator().next();
        assertEquals("팔로우할 대상 ID는 필수입니다.", violation.getMessage());
        assertEquals("followeeId", violation.getPropertyPath().toString());
    }
}
