package dev.devlink.feed.service.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedLikeResponseTest {

    @Test
    @DisplayName("좋아요 상태와 개수로 FeedLikeResponse를 생성할 수 있다")
    void from_liked() {
        // given
        boolean isLiked = true;
        long likeCount = 5L;

        // when
        FeedLikeResponse response = FeedLikeResponse.from(isLiked, likeCount);

        // then
        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("좋아요 취소 상태와 개수로 FeedLikeResponse를 생성할 수 있다")
    void from_unliked() {
        // given
        boolean isLiked = false;
        long likeCount = 0L;

        // when
        FeedLikeResponse response = FeedLikeResponse.from(isLiked, likeCount);

        // then
        assertThat(response.isLiked()).isFalse();
        assertThat(response.getLikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("좋아요 개수가 0일 때도 올바르게 생성된다")
    void from_zeroLikeCount() {
        // given
        boolean isLiked = false;
        long likeCount = 0L;

        // when
        FeedLikeResponse response = FeedLikeResponse.from(isLiked, likeCount);

        // then
        assertThat(response.isLiked()).isFalse();
        assertThat(response.getLikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("높은 좋아요 개수도 올바르게 처리된다")
    void from_highLikeCount() {
        // given
        boolean isLiked = true;
        long likeCount = 999999L;

        // when
        FeedLikeResponse response = FeedLikeResponse.from(isLiked, likeCount);

        // then
        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(999999L);
    }

    @Test
    @DisplayName("모든 인수 생성자로 객체를 생성할 수 있다")
    void allArgsConstructor() {
        // when
        FeedLikeResponse response = new FeedLikeResponse(true, 7L);

        // then
        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(7L);
    }
}
