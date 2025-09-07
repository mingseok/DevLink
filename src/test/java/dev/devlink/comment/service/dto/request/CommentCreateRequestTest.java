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

import static org.assertj.core.api.Assertions.assertThat;

class CommentCreateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 댓글 생성 요청이 검증을 통과한다")
    void validRequest_Success() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "유효한 댓글 내용입니다.");
        ReflectionTestUtils.setField(request, "parentId", null);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("유효한 대댓글 생성 요청이 검증을 통과한다")
    void validReplyRequest_Success() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "유효한 대댓글 내용입니다.");
        ReflectionTestUtils.setField(request, "parentId", 1L);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("빈 댓글 내용은 검증에 실패한다")
    void blankContent_ValidationFails() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "");

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(2); // @NotBlank와 @Size 위반
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsAnyOf(
                        "댓글 내용은 비어 있을 수 없습니다.",
                        "댓글 내용은 1자 이상 10000자 이하이어야 합니다."
                );
    }

    @Test
    @DisplayName("null 댓글 내용은 검증에 실패한다")
    void nullContent_ValidationFails() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", null);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("댓글 내용은 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("공백만으로 이루어진 댓글 내용은 검증에 실패한다")
    void whitespaceOnlyContent_ValidationFails() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "   ");

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("댓글 내용은 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("10000자를 초과하는 댓글 내용은 검증에 실패한다")
    void contentTooLong_ValidationFails() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        String longContent = "a".repeat(10001);
        ReflectionTestUtils.setField(request, "content", longContent);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("댓글 내용은 1자 이상 10000자 이하이어야 합니다.");
    }

    @Test
    @DisplayName("정확히 10000자인 댓글 내용은 검증을 통과한다")
    void contentExactly10000Characters_Success() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        String maxContent = "a".repeat(10000);
        ReflectionTestUtils.setField(request, "content", maxContent);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("1자인 댓글 내용은 검증을 통과한다")
    void contentExactlyOneCharacter_Success() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "a");

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("parentId가 null인 경우도 유효하다")
    void nullParentId_IsValid() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "댓글 내용");
        ReflectionTestUtils.setField(request, "parentId", null);

        // when
        Set<ConstraintViolation<CommentCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }
}
