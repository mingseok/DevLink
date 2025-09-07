package dev.devlink.article.constant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LikeStatusTest {

    @Test
    void enum값확인() {
        // then
        assertThat(LikeStatus.values()).hasSize(2);
        assertThat(LikeStatus.values()).containsExactly(
                LikeStatus.LIKE_ADDED,
                LikeStatus.LIKE_REMOVED
        );
    }

    @Test
    void LIKE_ADDED_값확인() {
        // given
        LikeStatus status = LikeStatus.LIKE_ADDED;

        // then
        assertThat(status.name()).isEqualTo("LIKE_ADDED");
        assertThat(status.toString()).isEqualTo("LIKE_ADDED");
    }

    @Test
    void LIKE_REMOVED_값확인() {
        // given
        LikeStatus status = LikeStatus.LIKE_REMOVED;

        // then
        assertThat(status.name()).isEqualTo("LIKE_REMOVED");
        assertThat(status.toString()).isEqualTo("LIKE_REMOVED");
    }

    @Test
    void valueOf_성공() {
        // when & then
        assertThat(LikeStatus.valueOf("LIKE_ADDED")).isEqualTo(LikeStatus.LIKE_ADDED);
        assertThat(LikeStatus.valueOf("LIKE_REMOVED")).isEqualTo(LikeStatus.LIKE_REMOVED);
    }
}
