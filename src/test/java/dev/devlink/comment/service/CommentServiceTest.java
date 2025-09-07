package dev.devlink.comment.service;

import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommentService 인터페이스 테스트")
class CommentServiceTest {

    @Test
    @DisplayName("CommentService 인터페이스 메서드 정의 확인")
    void commentServiceInterfaceTest() {
        // given
        Class<CommentService> serviceClass = CommentService.class;

        // when & then
        assertAll(
                () -> assertNotNull(serviceClass.getDeclaredMethod("save", CommentCreateRequest.class, Long.class, Long.class)),
                () -> assertNotNull(serviceClass.getDeclaredMethod("getComments", Long.class)),
                () -> assertNotNull(serviceClass.getDeclaredMethod("delete", Long.class, Long.class))
        );
    }

    @Test
    @DisplayName("CommentService 반환 타입 확인")
    void commentServiceReturnTypeTest() throws NoSuchMethodException {
        // given
        Class<CommentService> serviceClass = CommentService.class;

        // when & then
        assertAll(
                () -> assertEquals(void.class, serviceClass.getDeclaredMethod("save", CommentCreateRequest.class, Long.class, Long.class).getReturnType()),
                () -> assertEquals(List.class, serviceClass.getDeclaredMethod("getComments", Long.class).getReturnType()),
                () -> assertEquals(void.class, serviceClass.getDeclaredMethod("delete", Long.class, Long.class).getReturnType())
        );
    }
}
