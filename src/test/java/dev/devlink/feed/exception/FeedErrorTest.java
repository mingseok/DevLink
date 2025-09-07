package dev.devlink.feed.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FeedError 테스트")
class FeedErrorTest {

    @Test
    @DisplayName("NOT_FOUND 에러 정보 확인")
    void notFoundError_HasCorrectInfo() {
        // when
        FeedError error = FeedError.NOT_FOUND;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, error.getHttpStatus()),
                () -> assertEquals("40010", error.getCode()),
                () -> assertEquals("피드를 찾을 수 없습니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("NO_PERMISSION 에러 정보 확인")
    void noPermissionError_HasCorrectInfo() {
        // when
        FeedError error = FeedError.NO_PERMISSION;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, error.getHttpStatus()),
                () -> assertEquals("40410", error.getCode()),
                () -> assertEquals("피드에 대한 권한이 없습니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("INVALID_CONTENT 에러 정보 확인")
    void invalidContentError_HasCorrectInfo() {
        // when
        FeedError error = FeedError.INVALID_CONTENT;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus()),
                () -> assertEquals("40011", error.getCode()),
                () -> assertEquals("피드 내용이 유효하지 않습니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("모든 FeedError enum 값 개수 확인")
    void feedErrorEnum_HasCorrectCount() {
        // when
        FeedError[] errors = FeedError.values();

        // then
        assertEquals(3, errors.length);
    }

    @Test
    @DisplayName("FeedError enum valueOf 테스트")
    void feedErrorValueOf_Success() {
        // when & then
        assertAll(
                () -> assertEquals(FeedError.NOT_FOUND, FeedError.valueOf("NOT_FOUND")),
                () -> assertEquals(FeedError.NO_PERMISSION, FeedError.valueOf("NO_PERMISSION")),
                () -> assertEquals(FeedError.INVALID_CONTENT, FeedError.valueOf("INVALID_CONTENT"))
        );
    }

    @Test
    @DisplayName("모든 에러 코드가 고유하다")
    void allErrorCodes_AreUnique() {
        // given
        FeedError[] errors = FeedError.values();

        // when & then
        for (int i = 0; i < errors.length; i++) {
            for (int j = i + 1; j < errors.length; j++) {
                assertNotEquals(errors[i].getCode(), errors[j].getCode(),
                        String.format("Error codes should be unique: %s and %s have same code",
                                errors[i], errors[j]));
            }
        }
    }

    @Test
    @DisplayName("모든 에러 메시지가 null이 아니고 비어있지 않다")
    void allErrorMessages_AreNotNullOrEmpty() {
        // given
        FeedError[] errors = FeedError.values();

        // when & then
        for (FeedError error : errors) {
            assertAll(
                    () -> assertNotNull(error.getMessage()),
                    () -> assertFalse(error.getMessage().isEmpty()),
                    () -> assertFalse(error.getMessage().isBlank())
            );
        }
    }

    @Test
    @DisplayName("모든 에러 코드가 null이 아니고 비어있지 않다")
    void allErrorCodes_AreNotNullOrEmpty() {
        // given
        FeedError[] errors = FeedError.values();

        // when & then
        for (FeedError error : errors) {
            assertAll(
                    () -> assertNotNull(error.getCode()),
                    () -> assertFalse(error.getCode().isEmpty()),
                    () -> assertFalse(error.getCode().isBlank())
            );
        }
    }

    @Test
    @DisplayName("모든 HTTP 상태가 null이 아니다")
    void allHttpStatuses_AreNotNull() {
        // given
        FeedError[] errors = FeedError.values();

        // when & then
        for (FeedError error : errors) {
            assertNotNull(error.getHttpStatus());
        }
    }
}
