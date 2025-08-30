package dev.devlink.follow.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class FollowErrorTest {

    @Test
    @DisplayName("ALREADY_FOLLOWING 에러 정보가 올바르다")
    void alreadyFollowing_IsCorrect() {
        // when & then
        assertThat(FollowError.ALREADY_FOLLOWING.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(FollowError.ALREADY_FOLLOWING.getCode()).isEqualTo("40010");
        assertThat(FollowError.ALREADY_FOLLOWING.getMessage()).isEqualTo("이미 팔로우한 사용자입니다.");
    }

    @Test
    @DisplayName("NOT_FOUND 에러 정보가 올바르다")
    void notFound_IsCorrect() {
        // when & then
        assertThat(FollowError.NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(FollowError.NOT_FOUND.getCode()).isEqualTo("40410");
        assertThat(FollowError.NOT_FOUND.getMessage()).isEqualTo("팔로우 관계가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("CANNOT_FOLLOW_SELF 에러 정보가 올바르다")
    void cannotFollowSelf_IsCorrect() {
        // when & then
        assertThat(FollowError.CANNOT_FOLLOW_SELF.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(FollowError.CANNOT_FOLLOW_SELF.getCode()).isEqualTo("40011");
        assertThat(FollowError.CANNOT_FOLLOW_SELF.getMessage()).isEqualTo("자기 자신은 팔로우할 수 없습니다.");
    }

    @Test
    @DisplayName("모든 에러가 정의되어 있다")
    void allErrors_AreDefined() {
        // when & then
        assertThat(FollowError.values()).hasSize(3);
        assertThat(FollowError.values()).contains(
                FollowError.ALREADY_FOLLOWING,
                FollowError.NOT_FOUND,
                FollowError.CANNOT_FOLLOW_SELF
        );
    }

    @Test
    @DisplayName("에러 코드가 모두 다르다")
    void errorCodes_AreUnique() {
        // when & then
        assertThat(FollowError.ALREADY_FOLLOWING.getCode()).isNotEqualTo(FollowError.NOT_FOUND.getCode());
        assertThat(FollowError.ALREADY_FOLLOWING.getCode()).isNotEqualTo(FollowError.CANNOT_FOLLOW_SELF.getCode());
        assertThat(FollowError.NOT_FOUND.getCode()).isNotEqualTo(FollowError.CANNOT_FOLLOW_SELF.getCode());
    }
}
