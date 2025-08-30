package dev.devlink.feed.service.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedResponseTest {

    @Test
    @DisplayName("생성자로 FeedResponse를 생성할 수 있다")
    void constructor() {
        // when
        FeedResponse response = new FeedResponse(
                1L,
                1L,
                "testUser",
                "profile.jpg",
                "생성자 테스트",
                "constructor-image.jpg",
                "2025-01-01 12:00",
                true,
                true,
                10L,
                5L
        );

        // then
        assertThat(response.getFeedId()).isEqualTo(1L);
        assertThat(response.getContent()).isEqualTo("생성자 테스트");
        assertThat(response.getImageUrl()).isEqualTo("constructor-image.jpg");
        assertThat(response.getWriterId()).isEqualTo(1L);
        assertThat(response.getWriterNickname()).isEqualTo("testUser");
        assertThat(response.getWriterProfileImageUrl()).isEqualTo("profile.jpg");
        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(10L);
        assertThat(response.getCommentCount()).isEqualTo(5L);
        assertThat(response.getCreatedAt()).isEqualTo("2025-01-01 12:00");
        assertThat(response.isMyFeed()).isTrue();
    }

    @Test
    @DisplayName("이미지가 없는 FeedResponse를 생성할 수 있다")
    void constructor_withoutImage() {
        // when
        FeedResponse response = new FeedResponse(
                2L,
                2L,
                "anotherUser",
                "another-profile.jpg",
                "이미지 없는 피드",
                null,
                "2025-01-02 15:30",
                false,
                false,
                3L,
                2L
        );

        // then
        assertThat(response.getFeedId()).isEqualTo(2L);
        assertThat(response.getContent()).isEqualTo("이미지 없는 피드");
        assertThat(response.getImageUrl()).isNull();
        assertThat(response.getWriterId()).isEqualTo(2L);
        assertThat(response.isMyFeed()).isFalse();
        assertThat(response.isLiked()).isFalse();
    }

    @Test
    @DisplayName("모든 필드가 올바르게 설정된다")
    void allFieldsSet() {
        // when
        FeedResponse response = new FeedResponse(
                999L,
                888L,
                "userName",
                "userProfile.png",
                "테스트 내용",
                "test.jpg",
                "2025-12-31 23:59",
                true,
                false,
                100L,
                50L
        );

        // then
        assertThat(response.getFeedId()).isEqualTo(999L);
        assertThat(response.getWriterId()).isEqualTo(888L);
        assertThat(response.getWriterNickname()).isEqualTo("userName");
        assertThat(response.getWriterProfileImageUrl()).isEqualTo("userProfile.png");
        assertThat(response.getContent()).isEqualTo("테스트 내용");
        assertThat(response.getImageUrl()).isEqualTo("test.jpg");
        assertThat(response.getCreatedAt()).isEqualTo("2025-12-31 23:59");
        assertThat(response.isMyFeed()).isTrue();
        assertThat(response.isLiked()).isFalse();
        assertThat(response.getLikeCount()).isEqualTo(100L);
        assertThat(response.getCommentCount()).isEqualTo(50L);
    }
}
