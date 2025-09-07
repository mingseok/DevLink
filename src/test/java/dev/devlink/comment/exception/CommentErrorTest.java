package dev.devlink.comment.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommentError 테스트")
class CommentErrorTest {

    @Test
    @DisplayName("NOT_FOUND 에러 정보 확인")
    void notFoundError() {
        // when
        CommentError error = CommentError.NOT_FOUND;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, error.getHttpStatus()),
                () -> assertEquals("40420", error.getCode()),
                () -> assertEquals("존재하지 않는 댓글입니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("NO_PERMISSION 에러 정보 확인")
    void noPermissionError() {
        // when
        CommentError error = CommentError.NO_PERMISSION;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, error.getHttpStatus()),
                () -> assertEquals("40321", error.getCode()),
                () -> assertEquals("댓글을 삭제할 권한이 없습니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("HAS_CHILD_COMMENTS 에러 정보 확인")
    void hasChildCommentsError() {
        // when
        CommentError error = CommentError.HAS_CHILD_COMMENTS;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus()),
                () -> assertEquals("40022", error.getCode()),
                () -> assertEquals("대댓글이 있어 삭제할 수 없습니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("PARENT_NOT_FOUND 에러 정보 확인")
    void parentNotFoundError() {
        // when
        CommentError error = CommentError.PARENT_NOT_FOUND;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus()),
                () -> assertEquals("40030", error.getCode()),
                () -> assertEquals("부모 댓글이 존재하지 않습니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("REPLY_DEPTH_EXCEEDED 에러 정보 확인")
    void replyDepthExceededError() {
        // when
        CommentError error = CommentError.REPLY_DEPTH_EXCEEDED;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus()),
                () -> assertEquals("40031", error.getCode()),
                () -> assertEquals("답글에는 답글을 달 수 없습니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("모든 CommentError enum 값 개수 확인")
    void commentErrorEnumCount() {
        // when
        CommentError[] errors = CommentError.values();

        // then
        assertEquals(5, errors.length);
    }

    @Test
    @DisplayName("CommentError enum valueOf 테스트")
    void commentErrorValueOf() {
        // when & then
        assertAll(
                () -> assertEquals(CommentError.NOT_FOUND, CommentError.valueOf("NOT_FOUND")),
                () -> assertEquals(CommentError.NO_PERMISSION, CommentError.valueOf("NO_PERMISSION")),
                () -> assertEquals(CommentError.HAS_CHILD_COMMENTS, CommentError.valueOf("HAS_CHILD_COMMENTS")),
                () -> assertEquals(CommentError.PARENT_NOT_FOUND, CommentError.valueOf("PARENT_NOT_FOUND")),
                () -> assertEquals(CommentError.REPLY_DEPTH_EXCEEDED, CommentError.valueOf("REPLY_DEPTH_EXCEEDED"))
        );
    }
}
