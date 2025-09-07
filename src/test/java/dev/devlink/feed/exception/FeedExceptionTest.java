package dev.devlink.feed.exception;

import dev.devlink.common.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FeedException 테스트")
class FeedExceptionTest {

    @Test
    @DisplayName("FeedError를 사용하여 FeedException을 생성한다")
    void createFeedException_WithFeedError_Success() {
        // given
        FeedError error = FeedError.NOT_FOUND;

        // when
        FeedException exception = new FeedException(error);

        // then
        assertAll(
                () -> assertEquals(error, exception.getCommonError()),
                () -> assertEquals(error.getMessage(), exception.getMessage())
        );
    }

    @Test
    @DisplayName("각각의 FeedError에 대해 올바른 예외가 생성된다")
    void createExceptionForEachFeedError_Success() {
        // NOT_FOUND
        FeedException notFoundException = new FeedException(FeedError.NOT_FOUND);
        assertAll(
                () -> assertEquals("피드를 찾을 수 없습니다.", notFoundException.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, notFoundException.getCommonError().getHttpStatus()),
                () -> assertEquals("40010", notFoundException.getCommonError().getCode())
        );

        // NO_PERMISSION
        FeedException noPermissionException = new FeedException(FeedError.NO_PERMISSION);
        assertAll(
                () -> assertEquals("피드에 대한 권한이 없습니다.", noPermissionException.getMessage()),
                () -> assertEquals(HttpStatus.FORBIDDEN, noPermissionException.getCommonError().getHttpStatus()),
                () -> assertEquals("40410", noPermissionException.getCommonError().getCode())
        );

        // INVALID_CONTENT
        FeedException invalidContentException = new FeedException(FeedError.INVALID_CONTENT);
        assertAll(
                () -> assertEquals("피드 내용이 유효하지 않습니다.", invalidContentException.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, invalidContentException.getCommonError().getHttpStatus()),
                () -> assertEquals("40011", invalidContentException.getCommonError().getCode())
        );
    }

    @Test
    @DisplayName("FeedException이 ServiceException을 상속한다")
    void feedException_ExtendsServiceException() {
        // given
        FeedException exception = new FeedException(FeedError.NOT_FOUND);

        // then
        assertInstanceOf(ServiceException.class, exception);
    }

    @Test
    @DisplayName("FeedException이 RuntimeException을 상속한다")
    void feedException_ExtendsRuntimeException() {
        // given
        FeedException exception = new FeedException(FeedError.NOT_FOUND);

        // then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("예외 스택 트레이스가 올바르게 설정된다")
    void feedException_HasCorrectStackTrace() {
        // given
        FeedError error = FeedError.INVALID_CONTENT;

        // when
        FeedException exception = new FeedException(error);

        // then
        assertAll(
                () -> assertNotNull(exception.getStackTrace()),
                () -> assertTrue(exception.getStackTrace().length > 0)
        );
    }

    @Test
    @DisplayName("다른 FeedError로 생성된 예외들은 서로 다른 메시지를 가진다")
    void differentFeedErrors_ProduceDifferentExceptions() {
        // given
        FeedException exception1 = new FeedException(FeedError.NOT_FOUND);
        FeedException exception2 = new FeedException(FeedError.NO_PERMISSION);
        FeedException exception3 = new FeedException(FeedError.INVALID_CONTENT);

        // when & then
        assertAll(
                () -> assertNotEquals(exception1.getMessage(), exception2.getMessage()),
                () -> assertNotEquals(exception2.getMessage(), exception3.getMessage()),
                () -> assertNotEquals(exception1.getMessage(), exception3.getMessage())
        );
    }

    @Test
    @DisplayName("예외 생성 시 원인이 설정되지 않는다")
    void feedException_HasNoCause() {
        // given
        FeedException exception = new FeedException(FeedError.NOT_FOUND);

        // then
        assertNull(exception.getCause());
    }
}
