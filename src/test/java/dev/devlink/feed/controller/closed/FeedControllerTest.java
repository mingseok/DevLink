package dev.devlink.feed.controller.closed;

import dev.devlink.feed.service.FeedLikeService;
import dev.devlink.feed.service.FeedService;
import dev.devlink.feed.service.dto.response.FeedLikeResponse;
import dev.devlink.feed.service.dto.response.FeedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedControllerTest {

    @Mock
    private FeedService feedService;

    @Mock
    private FeedLikeService feedLikeService;

    @InjectMocks
    private FeedController feedController;

    @Test
    @DisplayName("텍스트만으로 피드를 생성할 수 있다")
    void createFeed_textOnly() throws Exception {
        // given
        String content = "새로운 피드 내용";
        Long memberId = 1L;

        doNothing().when(feedService).createFeed(eq(memberId), eq(content), isNull());

        // when
        ResponseEntity<?> response = feedController.createFeed(content, null, memberId);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(feedService).createFeed(eq(memberId), eq(content), isNull());
    }

    @Test
    @DisplayName("이미지와 함께 피드를 생성할 수 있다")
    void createFeed_withImage() throws Exception {
        // given
        String content = "이미지 포함 피드";
        Long memberId = 1L;
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test image data".getBytes());

        doNothing().when(feedService).createFeed(eq(memberId), eq(content), any());

        // when
        ResponseEntity<?> response = feedController.createFeed(content, imageFile, memberId);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(feedService).createFeed(eq(memberId), eq(content), any());
    }

    @Test
    @DisplayName("피드 목록을 조회할 수 있다")
    void getFeeds() throws Exception {
        // given
        Long memberId = 1L;
        List<FeedResponse> responses = Arrays.asList(
                createTestFeedResponse(1L, "첫 번째 피드"),
                createTestFeedResponse(2L, "두 번째 피드")
        );

        when(feedService.getFeeds(memberId)).thenReturn(responses);

        // when
        ResponseEntity<?> response = feedController.getFeeds(memberId);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(feedService).getFeeds(memberId);
    }

    @Test
    @DisplayName("피드를 삭제할 수 있다")
    void deleteFeed() throws Exception {
        // given
        Long memberId = 1L;
        Long feedId = 1L;

        doNothing().when(feedService).deleteFeed(memberId, feedId);

        // when
        ResponseEntity<?> response = feedController.deleteFeed(feedId, memberId);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(feedService).deleteFeed(memberId, feedId);
    }

    @Test
    @DisplayName("피드에 좋아요를 할 수 있다")
    void likeFeed() throws Exception {
        // given
        Long memberId = 1L;
        Long feedId = 1L;
        FeedLikeResponse likeResponse = FeedLikeResponse.from(true, 5L);

        when(feedLikeService.likeOrUnlike(memberId, feedId)).thenReturn(likeResponse);

        // when
        ResponseEntity<?> response = feedController.likeOrUnlike(feedId, memberId);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(feedLikeService).likeOrUnlike(memberId, feedId);
    }

    @Test
    @DisplayName("피드 좋아요를 취소할 수 있다")
    void unlikeFeed() throws Exception {
        // given
        Long memberId = 1L;
        Long feedId = 1L;
        FeedLikeResponse likeResponse = FeedLikeResponse.from(false, 3L);

        when(feedLikeService.likeOrUnlike(memberId, feedId)).thenReturn(likeResponse);

        // when
        ResponseEntity<?> response = feedController.likeOrUnlike(feedId, memberId);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(feedLikeService).likeOrUnlike(memberId, feedId);
    }

    private FeedResponse createTestFeedResponse(Long feedId, String content) {
        return new FeedResponse(
                feedId,
                1L,
                "testUser",
                "profile.jpg",
                content,
                null,
                "2025-01-01 12:00",
                false,
                false,
                0L,
                0L
        );
    }
}
