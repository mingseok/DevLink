package dev.devlink.follow.entity;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FollowTest {

    @Test
    @DisplayName("팔로우 관계를 생성할 수 있다")
    void create_Success() {
        // given
        Member follower = Member.create("김민석", "follower@example.com", "팔로워닉네임", "password");
        Member followee = Member.create("김선우", "followee@example.com", "팔로위닉네임", "password");

        // when
        Follow follow = Follow.create(follower, followee);

        // then
        assertThat(follow.getFollower()).isEqualTo(follower);
        assertThat(follow.getFollowee()).isEqualTo(followee);
    }

    @Test
    @DisplayName("빌더 패턴으로 팔로우 관계를 생성할 수 있다")
    void builder_Success() {
        // given
        Member follower = Member.create("김현우", "follower2@example.com", "팔로워닉네임2", "password");
        Member followee = Member.create("이영희", "followee2@example.com", "팔로위닉네임2", "password");

        // when
        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();

        // then
        assertThat(follow.getFollower()).isEqualTo(follower);
        assertThat(follow.getFollowee()).isEqualTo(followee);
    }

    @Test
    @DisplayName("팔로우 관계가 올바르게 설정된다")
    void followRelationship_SetCorrectly() {
        // given
        Member follower = Member.create("박철수", "test1@example.com", "철수닉네임", "password");
        Member followee = Member.create("박영희", "test2@example.com", "영희닉네임", "password");

        // when
        Follow follow = Follow.create(follower, followee);

        // then
        assertThat(follow.getFollower().getNickname()).isEqualTo("철수닉네임");
        assertThat(follow.getFollowee().getNickname()).isEqualTo("영희닉네임");
        assertThat(follow.getFollower().getEmail()).isEqualTo("test1@example.com");
        assertThat(follow.getFollowee().getEmail()).isEqualTo("test2@example.com");
    }
}
