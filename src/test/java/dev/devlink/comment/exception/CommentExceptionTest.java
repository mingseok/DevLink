package dev.devlink.comment.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class CommentExceptionTest {

    @Test
    @DisplayName("CommentError를 사용하여 CommentException을 생성한다")
    void createCommentExceptionWithCommentError_Success() {
        // given
        CommentError error = CommentError.NOT_FOUND;

        // when
        CommentException exception = new CommentException(error);

        // then
        assertThat(exception.getCommonError()).isEqualTo(error);
        assertThat(exception.getMessage()).isEqualTo(error.getMessage());
    }

    @Test
    @DisplayName("각각의 CommentError에 대해 올바른 예외가 생성된다")
    void createExceptionForEachCommentError_Success() {
        // NOT_FOUND
        CommentException notFoundException = new CommentException(CommentError.NOT_FOUND);
        assertThat(notFoundException.getMessage()).isEqualTo("존재하지 않는 댓글입니다.");
        assertThat(notFoundException.getCommonError().getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(notFoundException.getCommonError().getCode()).isEqualTo("40420");

        // NO_PERMISSION
        CommentException noPermissionException = new CommentException(CommentError.NO_PERMISSION);
        assertThat(noPermissionException.getMessage()).isEqualTo("댓글을 삭제할 권한이 없습니다.");
        assertThat(noPermissionException.getCommonError().getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(noPermissionException.getCommonError().getCode()).isEqualTo("40321");

        // HAS_CHILD_COMMENTS
        CommentException hasChildException = new CommentException(CommentError.HAS_CHILD_COMMENTS);
        assertThat(hasChildException.getMessage()).isEqualTo("대댓글이 있어 삭제할 수 없습니다.");
        assertThat(hasChildException.getCommonError().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(hasChildException.getCommonError().getCode()).isEqualTo("40022");

        // PARENT_NOT_FOUND
        CommentException parentNotFoundException = new CommentException(CommentError.PARENT_NOT_FOUND);
        assertThat(parentNotFoundException.getMessage()).isEqualTo("부모 댓글이 존재하지 않습니다.");
        assertThat(parentNotFoundException.getCommonError().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(parentNotFoundException.getCommonError().getCode()).isEqualTo("40030");

        // REPLY_DEPTH_EXCEEDED
        CommentException depthExceededException = new CommentException(CommentError.REPLY_DEPTH_EXCEEDED);
        assertThat(depthExceededException.getMessage()).isEqualTo("답글에는 답글을 달 수 없습니다.");
        assertThat(depthExceededException.getCommonError().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(depthExceededException.getCommonError().getCode()).isEqualTo("40031");
    }

    @Test
    @DisplayName("CommentError enum의 모든 값이 올바르게 설정되어 있다")
    void commentErrorValues_AreCorrect() {
        // NOT_FOUND
        assertThat(CommentError.NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(CommentError.NOT_FOUND.getCode()).isEqualTo("40420");
        assertThat(CommentError.NOT_FOUND.getMessage()).isEqualTo("존재하지 않는 댓글입니다.");

        // NO_PERMISSION
        assertThat(CommentError.NO_PERMISSION.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(CommentError.NO_PERMISSION.getCode()).isEqualTo("40321");
        assertThat(CommentError.NO_PERMISSION.getMessage()).isEqualTo("댓글을 삭제할 권한이 없습니다.");

        // HAS_CHILD_COMMENTS
        assertThat(CommentError.HAS_CHILD_COMMENTS.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(CommentError.HAS_CHILD_COMMENTS.getCode()).isEqualTo("40022");
        assertThat(CommentError.HAS_CHILD_COMMENTS.getMessage()).isEqualTo("대댓글이 있어 삭제할 수 없습니다.");

        // PARENT_NOT_FOUND
        assertThat(CommentError.PARENT_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(CommentError.PARENT_NOT_FOUND.getCode()).isEqualTo("40030");
        assertThat(CommentError.PARENT_NOT_FOUND.getMessage()).isEqualTo("부모 댓글이 존재하지 않습니다.");

        // REPLY_DEPTH_EXCEEDED
        assertThat(CommentError.REPLY_DEPTH_EXCEEDED.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(CommentError.REPLY_DEPTH_EXCEEDED.getCode()).isEqualTo("40031");
        assertThat(CommentError.REPLY_DEPTH_EXCEEDED.getMessage()).isEqualTo("답글에는 답글을 달 수 없습니다.");
    }

    @Test
    @DisplayName("CommentException이 RuntimeException을 상속한다")
    void commentExceptionInheritance_ExtendsRuntimeException() {
        // given
        CommentException exception = new CommentException(CommentError.NOT_FOUND);

        // then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
