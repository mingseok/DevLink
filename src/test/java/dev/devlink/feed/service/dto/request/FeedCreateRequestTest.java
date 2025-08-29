package dev.devlink.feed.service.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedCreateRequestTest {

    @Test
    @DisplayName("올바른 내용으로 FeedCreateRequest를 생성할 수 있다")
    void createRequest_validContent() {
        // given
        String content = "테스트 피드 내용";

        // when
        FeedCreateRequest request = new FeedCreateRequest(content);

        // then
        assertThat(request.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("null 내용으로 FeedCreateRequest를 생성할 수 있다")
    void createRequest_nullContent() {
        // given
        String content = null;

        // when
        FeedCreateRequest request = new FeedCreateRequest(content);

        // then
        assertThat(request.getContent()).isNull();
    }

    @Test
    @DisplayName("빈 문자열 내용으로 FeedCreateRequest를 생성할 수 있다")
    void createRequest_emptyContent() {
        // given
        String content = "";

        // when
        FeedCreateRequest request = new FeedCreateRequest(content);

        // then
        assertThat(request.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("공백 내용으로 FeedCreateRequest를 생성할 수 있다")
    void createRequest_blankContent() {
        // given
        String content = "   ";

        // when
        FeedCreateRequest request = new FeedCreateRequest(content);

        // then
        assertThat(request.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("긴 내용으로 FeedCreateRequest를 생성할 수 있다")
    void createRequest_longContent() {
        // given
        String content = "a".repeat(1000);

        // when
        FeedCreateRequest request = new FeedCreateRequest(content);

        // then
        assertThat(request.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("최대 길이를 초과하는 내용으로 FeedCreateRequest를 생성할 수 있다")
    void createRequest_tooLongContent() {
        // given
        String content = "a".repeat(1001);

        // when
        FeedCreateRequest request = new FeedCreateRequest(content);

        // then
        assertThat(request.getContent()).isEqualTo(content);
    }
}
