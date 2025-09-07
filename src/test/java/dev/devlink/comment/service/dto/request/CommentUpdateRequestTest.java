package dev.devlink.comment.service.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommentUpdateRequest DTO 테스트")
class CommentUpdateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("CommentCreateRequest 객체 생성 및 getter 테스트")
    void createCommentCreateRequest_Success() {
        // given
        String content = "테스트 댓글 내용";
        Long parentId = 1L;

        // when
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", content);
        ReflectionTestUtils.setField(request, "parentId", parentId);

        // then
        assertAll(
                () -> assertEquals(content, request.getContent()),
                () -> assertEquals(parentId, request.getParentId())
        );
    }

    @Test
    @DisplayName("유효한 CommentCreateRequest 검증 성공")
    void validCommentCreateRequest_Success() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "유효한 댓글 내용");
        ReflectionTestUtils.setField(request, "parentId", null);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("빈 내용으로 CommentCreateRequest 검증 실패")
    void invalidCommentCreateRequest_EmptyContent_ValidationFails() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "");

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains("댓글 내용은 비어 있을 수 없습니다")));
    }

    @Test
    @DisplayName("null 내용으로 CommentCreateRequest 검증 실패")
    void invalidCommentCreateRequest_NullContent_ValidationFails() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", null);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains("댓글 내용은 비어 있을 수 없습니다")));
    }

    @Test
    @DisplayName("너무 긴 내용으로 CommentCreateRequest 검증 실패")
    void invalidCommentCreateRequest_TooLongContent_ValidationFails() {
        // given
        String longContent = "a".repeat(10001); // 10000자 초과
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", longContent);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getMessage().contains("1자 이상 10000자 이하이어야 합니다")));
    }

    @Test
    @DisplayName("최대 길이 내용으로 CommentCreateRequest 검증 성공")
    void validCommentCreateRequest_MaxLength_Success() {
        // given
        String maxContent = "a".repeat(10000); // 10000자 정확히
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", maxContent);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }
}
