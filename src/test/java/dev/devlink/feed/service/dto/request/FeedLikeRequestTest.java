package dev.devlink.feed.service.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedLikeRequestTest {

    @Test
    @DisplayName("올바른 피드 ID로 FeedLikeRequest를 생성할 수 있다")
    void createRequest_validFeedId() {
        // given
        Long feedId = 1L;

        // when
        FeedLikeRequest request = new FeedLikeRequest(feedId);

        // then
        assertThat(request.getFeedId()).isEqualTo(feedId);
    }

    @Test
    @DisplayName("피드 ID가 null인 FeedLikeRequest를 생성할 수 있다")
    void createRequest_nullFeedId() {
        // given
        Long feedId = null;

        // when
        FeedLikeRequest request = new FeedLikeRequest(feedId);

        // then
        assertThat(request.getFeedId()).isNull();
    }

    @Test
    @DisplayName("피드 ID가 음수인 FeedLikeRequest를 생성할 수 있다")
    void createRequest_negativeFeedId() {
        // given
        Long feedId = -1L;

        // when
        FeedLikeRequest request = new FeedLikeRequest(feedId);

        // then
        assertThat(request.getFeedId()).isEqualTo(feedId);
    }

    @Test
    @DisplayName("피드 ID가 0인 FeedLikeRequest를 생성할 수 있다")
    void createRequest_zeroFeedId() {
        // given
        Long feedId = 0L;

        // when
        FeedLikeRequest request = new FeedLikeRequest(feedId);

        // then
        assertThat(request.getFeedId()).isEqualTo(feedId);
    }

    @Test
    @DisplayName("양의 큰 피드 ID로 FeedLikeRequest를 생성할 수 있다")
    void createRequest_largeFeedId() {
        // given
        Long feedId = Long.MAX_VALUE;

        // when
        FeedLikeRequest request = new FeedLikeRequest(feedId);

        // then
        assertThat(request.getFeedId()).isEqualTo(feedId);
    }
}
