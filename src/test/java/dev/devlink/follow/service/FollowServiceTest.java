package dev.devlink.follow.service;

import dev.devlink.follow.entity.Follow;
import dev.devlink.follow.exception.FollowException;
import dev.devlink.follow.repository.FollowRepository;
import dev.devlink.follow.service.dto.request.FollowCreateRequest;
import dev.devlink.follow.service.dto.response.FollowResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @InjectMocks
    private FollowService followService;

    @Mock
    private MemberService memberService;

    @Mock
    private FollowRepository followRepository;

    @Test
    @DisplayName("팔로우 성공")
    void follow_Success() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        FollowCreateRequest request = new FollowCreateRequest(followeeId);
        
        Member follower = Member.create("김민석", "follower@example.com", "팔로워", "password");
        Member followee = Member.create("김선우", "followee@example.com", "팔로위", "password");
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.findByFollowerAndFollowee(follower, followee)).willReturn(Optional.empty());

        // when
        followService.follow(followerId, request);

        // then
        then(followRepository).should().save(any(Follow.class));
    }

    @Test
    @DisplayName("자기 자신을 팔로우하면 예외 발생")
    void follow_WhenSelf_ThrowsException() {
        // given
        Long memberId = 1L;
        FollowCreateRequest request = new FollowCreateRequest(memberId);

        // when & then
        assertThatThrownBy(() -> followService.follow(memberId, request))
                .isInstanceOf(FollowException.class)
                .hasMessageContaining("자기 자신은 팔로우할 수 없습니다");
    }

    @Test
    @DisplayName("이미 팔로우한 사용자를 팔로우하면 예외 발생")
    void follow_WhenAlreadyFollowing_ThrowsException() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        FollowCreateRequest request = new FollowCreateRequest(followeeId);
        
        Member follower = Member.create("김민석", "follower@example.com", "팔로워", "password");
        Member followee = Member.create("김선우", "followee@example.com", "팔로위", "password");
        Follow existingFollow = Follow.create(follower, followee);
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.findByFollowerAndFollowee(follower, followee))
                .willReturn(Optional.of(existingFollow));

        // when & then
        assertThatThrownBy(() -> followService.follow(followerId, request))
                .isInstanceOf(FollowException.class)
                .hasMessageContaining("이미 팔로우한 사용자입니다");
    }

    @Test
    @DisplayName("언팔로우 성공")
    void unfollow_Success() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        
        Member follower = Member.create("김민석", "follower@example.com", "팔로워", "password");
        Member followee = Member.create("김선우", "followee@example.com", "팔로위", "password");
        Follow follow = Follow.create(follower, followee);
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.findByFollowerAndFollowee(follower, followee))
                .willReturn(Optional.of(follow));

        // when
        followService.unfollow(followerId, followeeId);

        // then
        then(followRepository).should().delete(follow);
    }

    @Test
    @DisplayName("팔로우하지 않은 사용자를 언팔로우하면 예외 발생")
    void unfollow_WhenNotFollowing_ThrowsException() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        
        Member follower = Member.create("김민석", "follower@example.com", "팔로워", "password");
        Member followee = Member.create("김선우", "followee@example.com", "팔로위", "password");
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.findByFollowerAndFollowee(follower, followee))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> followService.unfollow(followerId, followeeId))
                .isInstanceOf(FollowException.class)
                .hasMessageContaining("팔로우 관계가 존재하지 않습니다");
    }

    @Test
    @DisplayName("팔로워 목록 조회 성공")
    void getFollowers_Success() {
        // given
        Long memberId = 1L;
        Member member = Member.create("김민석", "member@example.com", "멤버", "password");
        Member follower1 = Member.create("김선우", "follower1@example.com", "팔로워1", "password");
        Member follower2 = Member.create("김현우", "follower2@example.com", "팔로워2", "password");
        
        Follow follow1 = Follow.create(follower1, member);
        Follow follow2 = Follow.create(follower2, member);
        
        given(memberService.findMemberById(memberId)).willReturn(member);
        given(followRepository.findAllByFollowee(member)).willReturn(List.of(follow1, follow2));
        given(followRepository.existsByFollowerAndFollowee(member, follower1)).willReturn(true);
        given(followRepository.existsByFollowerAndFollowee(member, follower2)).willReturn(false);

        // when
        List<FollowResponse> result = followService.getFollowers(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNickname()).isEqualTo("팔로워1");
        assertThat(result.get(0).getIsFollowing()).isTrue();
        assertThat(result.get(1).getNickname()).isEqualTo("팔로워2");
        assertThat(result.get(1).getIsFollowing()).isFalse();
    }

    @Test
    @DisplayName("팔로잉 목록 조회 성공")
    void getFollowings_Success() {
        // given
        Long memberId = 1L;
        Member member = Member.create("김민석", "member@example.com", "멤버", "password");
        Member followee1 = Member.create("김선우", "followee1@example.com", "팔로위1", "password");
        Member followee2 = Member.create("김현우", "followee2@example.com", "팔로위2", "password");
        
        Follow follow1 = Follow.create(member, followee1);
        Follow follow2 = Follow.create(member, followee2);
        
        given(memberService.findMemberById(memberId)).willReturn(member);
        given(followRepository.findAllByFollower(member)).willReturn(List.of(follow1, follow2));

        // when
        List<FollowResponse> result = followService.getFollowings(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNickname()).isEqualTo("팔로위1");
        assertThat(result.get(1).getNickname()).isEqualTo("팔로위2");
    }

    @Test
    @DisplayName("팔로우 여부 확인 - 팔로우 중인 경우")
    void isFollowing_WhenFollowing_ReturnTrue() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        
        Member follower = Member.create("김민석", "follower@example.com", "팔로워", "password");
        Member followee = Member.create("김선우", "followee@example.com", "팔로위", "password");
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.existsByFollowerAndFollowee(follower, followee)).willReturn(true);

        // when
        boolean result = followService.isFollowing(followerId, followeeId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("팔로우 여부 확인 - 팔로우하지 않는 경우")
    void isFollowing_WhenNotFollowing_ReturnFalse() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        
        Member follower = Member.create("김민석", "follower@example.com", "팔로워", "password");
        Member followee = Member.create("김선우", "followee@example.com", "팔로위", "password");
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.existsByFollowerAndFollowee(follower, followee)).willReturn(false);

        // when
        boolean result = followService.isFollowing(followerId, followeeId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("팔로워 수 조회")
    void getFollowerCount_Success() {
        // given
        Long memberId = 1L;
        Member member = Member.create("김민석", "member@example.com", "멤버", "password");
        
        given(memberService.findMemberById(memberId)).willReturn(member);
        given(followRepository.countByFollowee(member)).willReturn(10L);

        // when
        long result = followService.getFollowerCount(memberId);

        // then
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("팔로잉 수 조회")
    void getFollowingCount_Success() {
        // given
        Long memberId = 1L;
        Member member = Member.create("김민석", "member@example.com", "멤버", "password");
        
        given(memberService.findMemberById(memberId)).willReturn(member);
        given(followRepository.countByFollower(member)).willReturn(5L);

        // when
        long result = followService.getFollowingCount(memberId);

        // then
        assertThat(result).isEqualTo(5L);
    }
}
