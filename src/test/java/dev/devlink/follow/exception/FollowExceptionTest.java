package dev.devlink.follow.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FollowExceptionTest {

    @Test
    @DisplayName("ALREADY_FOLLOWING 에러로 예외를 생성할 수 있다")
    void createException_WithAlreadyFollowing_Success() {
        // when
        FollowException exception = new FollowException(FollowError.ALREADY_FOLLOWING);

        // then
        assertThat(exception.getCommonError()).isEqualTo(FollowError.ALREADY_FOLLOWING);
        assertThat(exception.getMessage()).isEqualTo("이미 팔로우한 사용자입니다.");
    }

    @Test
    @DisplayName("NOT_FOUND 에러로 예외를 생성할 수 있다")
    void createException_WithNotFound_Success() {
        // when
        FollowException exception = new FollowException(FollowError.NOT_FOUND);

        // then
        assertThat(exception.getCommonError()).isEqualTo(FollowError.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("팔로우 관계가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("CANNOT_FOLLOW_SELF 에러로 예외를 생성할 수 있다")
    void createException_WithCannotFollowSelf_Success() {
        // when
        FollowException exception = new FollowException(FollowError.CANNOT_FOLLOW_SELF);

        // then
        assertThat(exception.getCommonError()).isEqualTo(FollowError.CANNOT_FOLLOW_SELF);
        assertThat(exception.getMessage()).isEqualTo("자기 자신은 팔로우할 수 없습니다.");
    }
}
