package dev.devlink.follow.service.dto.response;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FollowResponseTest {

    @Test
    @DisplayName("회원 정보로 FollowResponse를 생성할 수 있다")
    void from_WithMember_Success() {
        // given
        Member member = Member.create("김민석", "test@example.com", "테스트닉네임", "password");

        // when
        FollowResponse response = FollowResponse.from(member);

        // then
        assertThat(response.getMemberId()).isEqualTo(member.getId());
        assertThat(response.getNickname()).isEqualTo(member.getNickname());
        assertThat(response.getIsFollowing()).isFalse();
    }

    @Test
    @DisplayName("회원 정보와 팔로우 여부로 FollowResponse를 생성할 수 있다")
    void from_WithMemberAndFollowStatus_Success() {
        // given
        Member member = Member.create("김선우", "test2@example.com", "테스트닉네임2", "password");
        boolean isFollowing = true;

        // when
        FollowResponse response = FollowResponse.from(member, isFollowing);

        // then
        assertThat(response.getMemberId()).isEqualTo(member.getId());
        assertThat(response.getNickname()).isEqualTo(member.getNickname());
        assertThat(response.getIsFollowing()).isTrue();
    }

    @Test
    @DisplayName("팔로우하지 않는 상태로 FollowResponse를 생성할 수 있다")
    void from_WithNotFollowing_Success() {
        // given
        Member member = Member.create("김현우", "test3@example.com", "테스트닉네임3", "password");
        boolean isFollowing = false;

        // when
        FollowResponse response = FollowResponse.from(member, isFollowing);

        // then
        assertThat(response.getMemberId()).isEqualTo(member.getId());
        assertThat(response.getNickname()).isEqualTo(member.getNickname());
        assertThat(response.getIsFollowing()).isFalse();
    }

    @Test
    @DisplayName("생성자로 직접 FollowResponse를 생성할 수 있다")
    void constructor_Success() {
        // given
        Long memberId = 1L;
        String nickname = "테스트닉네임";
        String joinedAt = "2023-01-01";
        Boolean isFollowing = true;

        // when
        FollowResponse response = new FollowResponse(memberId, nickname, joinedAt, isFollowing);

        // then
        assertThat(response.getMemberId()).isEqualTo(memberId);
        assertThat(response.getNickname()).isEqualTo(nickname);
        assertThat(response.getJoinedAt()).isEqualTo(joinedAt);
        assertThat(response.getIsFollowing()).isEqualTo(isFollowing);
    }
}
