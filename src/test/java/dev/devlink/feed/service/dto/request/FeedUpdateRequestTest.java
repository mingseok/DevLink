package dev.devlink.feed.service.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FeedUpdateRequest DTO 테스트")
class FeedUpdateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 피드 수정 요청 검증 성공")
    void validFeedUpdateRequest_Success() {
        // given
        FeedCreateRequest request = new FeedCreateRequest("수정된 피드 내용입니다.");

        // when
        Set<ConstraintViolation<FeedCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("빈 내용으로 피드 수정 시 검증 실패")
    void emptyContent_ValidationFails() {
        // given
        FeedCreateRequest request = new FeedCreateRequest("");

        // when
        Set<ConstraintViolation<FeedCreateRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains("피드 내용은 필수입니다")));
    }

    @Test
    @DisplayName("null 내용으로 피드 수정 시 검증 실패")
    void nullContent_ValidationFails() {
        // given
        FeedCreateRequest request = new FeedCreateRequest(null);

        // when
        Set<ConstraintViolation<FeedCreateRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains("피드 내용은 필수입니다")));
    }

    @Test
    @DisplayName("1000자 초과 내용으로 피드 수정 시 검증 실패")
    void contentTooLong_ValidationFails() {
        // given
        String longContent = "a".repeat(1001);
        FeedCreateRequest request = new FeedCreateRequest(longContent);

        // when
        Set<ConstraintViolation<FeedCreateRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains("1000자를 초과할 수 없습니다")));
    }

    @Test
    @DisplayName("정확히 1000자 내용으로 피드 수정 시 검증 성공")
    void contentExactly1000Characters_Success() {
        // given
        String maxContent = "a".repeat(1000);
        FeedCreateRequest request = new FeedCreateRequest(maxContent);

        // when
        Set<ConstraintViolation<FeedCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("공백만으로 이루어진 내용으로 피드 수정 시 검증 실패")
    void whitespaceOnlyContent_ValidationFails() {
        // given
        FeedCreateRequest request = new FeedCreateRequest("   ");

        // when
        Set<ConstraintViolation<FeedCreateRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains("피드 내용은 필수입니다")));
    }

    @Test
    @DisplayName("FeedCreateRequest 객체 생성 및 getter 테스트")
    void createFeedUpdateRequest_Success() {
        // given
        String content = "수정할 피드 내용";

        // when
        FeedCreateRequest request = new FeedCreateRequest(content);

        // then
        assertEquals(content, request.getContent());
    }

    @Test
    @DisplayName("기본 생성자로 생성된 FeedCreateRequest")
    void defaultConstructor_Success() {
        // when
        FeedCreateRequest request = new FeedCreateRequest();

        // then
        assertNotNull(request);
        assertNull(request.getContent());
    }

    @Test
    @DisplayName("한글 내용으로 피드 수정 시 검증 성공")
    void koreanContent_Success() {
        // given
        String koreanContent = "한글로 작성된 피드 내용입니다. 이모지도 포함될 수 있어요! 😊";
        FeedCreateRequest request = new FeedCreateRequest(koreanContent);

        // when
        Set<ConstraintViolation<FeedCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("특수문자 내용으로 피드 수정 시 검증 성공")
    void specialCharacterContent_Success() {
        // given
        String specialContent = "특수문자 테스트! @#$%^&*()_+{}[]|\\:;\"'<>,.?/~`";
        FeedCreateRequest request = new FeedCreateRequest(specialContent);

        // when
        Set<ConstraintViolation<FeedCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }
}
