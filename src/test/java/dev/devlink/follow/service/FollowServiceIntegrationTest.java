package dev.devlink.follow.service;

import dev.devlink.follow.entity.Follow;
import dev.devlink.follow.exception.FollowError;
import dev.devlink.follow.exception.FollowException;
import dev.devlink.follow.repository.FollowRepository;
import dev.devlink.follow.service.dto.request.FollowCreateRequest;
import dev.devlink.follow.service.dto.response.FollowResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowService 통합 테스트")
class FollowServiceIntegrationTest {

    @Mock
    private MemberService memberService;

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private FollowService followService;

    private Member follower;
    private Member followee;
    private Follow follow;

    @BeforeEach
    void setUp() {
        follower = Member.create("팔로워", "follower@test.com", "팔로워닉", "password");
        followee = Member.create("팔로위", "followee@test.com", "팔로위닉", "password");
        ReflectionTestUtils.setField(follower, "id", 1L);
        ReflectionTestUtils.setField(followee, "id", 2L);
        
        follow = Follow.create(follower, followee);
        ReflectionTestUtils.setField(follow, "id", 1L);
    }

    @Test
    @DisplayName("팔로우 성공")
    void follow_Success() {
        // given
        Long followerId = 1L;
        FollowCreateRequest request = new FollowCreateRequest(2L);
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(request.getFolloweeId())).willReturn(followee);
        given(followRepository.findByFollowerAndFollowee(follower, followee)).willReturn(Optional.empty());
        given(followRepository.save(any(Follow.class))).willReturn(follow);

        // when
        assertDoesNotThrow(() -> followService.follow(followerId, request));

        // then
        verify(memberService).findMemberById(followerId);
        verify(memberService).findMemberById(request.getFolloweeId());
        verify(followRepository).findByFollowerAndFollowee(follower, followee);
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    @DisplayName("자기 자신을 팔로우할 수 없다")
    void follow_CannotFollowSelf_ThrowsException() {
        // given
        Long followerId = 1L;
        FollowCreateRequest request = new FollowCreateRequest(1L); // 자기 자신

        // when & then
        FollowException exception = assertThrows(FollowException.class, () ->
                followService.follow(followerId, request));
        
        assertEquals(FollowError.CANNOT_FOLLOW_SELF, exception.getCommonError());
        verifyNoInteractions(memberService);
        verifyNoInteractions(followRepository);
    }

    @Test
    @DisplayName("이미 팔로우한 사용자를 다시 팔로우할 수 없다")
    void follow_AlreadyFollowing_ThrowsException() {
        // given
        Long followerId = 1L;
        FollowCreateRequest request = new FollowCreateRequest(2L);
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(request.getFolloweeId())).willReturn(followee);
        given(followRepository.findByFollowerAndFollowee(follower, followee)).willReturn(Optional.of(follow));

        // when & then
        FollowException exception = assertThrows(FollowException.class, () ->
                followService.follow(followerId, request));
        
        assertEquals(FollowError.ALREADY_FOLLOWING, exception.getCommonError());
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    @DisplayName("언팔로우 성공")
    void unfollow_Success() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.findByFollowerAndFollowee(follower, followee)).willReturn(Optional.of(follow));

        // when
        assertDoesNotThrow(() -> followService.unfollow(followerId, followeeId));

        // then
        verify(memberService).findMemberById(followerId);
        verify(memberService).findMemberById(followeeId);
        verify(followRepository).findByFollowerAndFollowee(follower, followee);
        verify(followRepository).delete(follow);
    }

    @Test
    @DisplayName("팔로우하지 않은 사용자를 언팔로우할 수 없다")
    void unfollow_NotFollowing_ThrowsException() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.findByFollowerAndFollowee(follower, followee)).willReturn(Optional.empty());

        // when & then
        FollowException exception = assertThrows(FollowException.class, () ->
                followService.unfollow(followerId, followeeId));
        
        assertEquals(FollowError.NOT_FOUND, exception.getCommonError());
        verify(followRepository, never()).delete(any(Follow.class));
    }

    @Test
    @DisplayName("팔로워 목록 조회 성공")
    void getFollowers_Success() {
        // given
        Long memberId = 2L;
        Member anotherFollower = Member.create("다른팔로워", "another@test.com", "다른닉", "password");
        ReflectionTestUtils.setField(anotherFollower, "id", 3L);
        
        Follow anotherFollow = Follow.create(anotherFollower, followee);
        List<Follow> followers = Arrays.asList(follow, anotherFollow);
        
        given(memberService.findMemberById(memberId)).willReturn(followee);
        given(followRepository.findAllByFollowee(followee)).willReturn(followers);
        given(followRepository.existsByFollowerAndFollowee(followee, follower)).willReturn(false);
        given(followRepository.existsByFollowerAndFollowee(followee, anotherFollower)).willReturn(true);

        // when
        List<FollowResponse> result = followService.getFollowers(memberId);

        // then
        assertEquals(2, result.size());
        verify(memberService).findMemberById(memberId);
        verify(followRepository).findAllByFollowee(followee);
    }

    @Test
    @DisplayName("팔로잉 목록 조회 성공")
    void getFollowings_Success() {
        // given
        Long memberId = 1L;
        Member anotherFollowee = Member.create("다른팔로위", "another@test.com", "다른닉", "password");
        ReflectionTestUtils.setField(anotherFollowee, "id", 3L);
        
        Follow anotherFollow = Follow.create(follower, anotherFollowee);
        List<Follow> followings = Arrays.asList(follow, anotherFollow);
        
        given(memberService.findMemberById(memberId)).willReturn(follower);
        given(followRepository.findAllByFollower(follower)).willReturn(followings);

        // when
        List<FollowResponse> result = followService.getFollowings(memberId);

        // then
        assertEquals(2, result.size());
        verify(memberService).findMemberById(memberId);
        verify(followRepository).findAllByFollower(follower);
    }

    @Test
    @DisplayName("팔로우 여부 확인 - 팔로우 중")
    void isFollowing_WhenFollowing_ReturnsTrue() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.existsByFollowerAndFollowee(follower, followee)).willReturn(true);

        // when
        boolean result = followService.isFollowing(followerId, followeeId);

        // then
        assertTrue(result);
        verify(followRepository).existsByFollowerAndFollowee(follower, followee);
    }

    @Test
    @DisplayName("팔로우 여부 확인 - 팔로우하지 않음")
    void isFollowing_WhenNotFollowing_ReturnsFalse() {
        // given
        Long followerId = 1L;
        Long followeeId = 2L;
        
        given(memberService.findMemberById(followerId)).willReturn(follower);
        given(memberService.findMemberById(followeeId)).willReturn(followee);
        given(followRepository.existsByFollowerAndFollowee(follower, followee)).willReturn(false);

        // when
        boolean result = followService.isFollowing(followerId, followeeId);

        // then
        assertFalse(result);
        verify(followRepository).existsByFollowerAndFollowee(follower, followee);
    }

    @Test
    @DisplayName("팔로워 수 조회 성공")
    void getFollowerCount_Success() {
        // given
        Long memberId = 2L;
        long expectedCount = 10L;
        
        given(memberService.findMemberById(memberId)).willReturn(followee);
        given(followRepository.countByFollowee(followee)).willReturn(expectedCount);

        // when
        long result = followService.getFollowerCount(memberId);

        // then
        assertEquals(expectedCount, result);
        verify(memberService).findMemberById(memberId);
        verify(followRepository).countByFollowee(followee);
    }

    @Test
    @DisplayName("팔로잉 수 조회 성공")
    void getFollowingCount_Success() {
        // given
        Long memberId = 1L;
        long expectedCount = 5L;
        
        given(memberService.findMemberById(memberId)).willReturn(follower);
        given(followRepository.countByFollower(follower)).willReturn(expectedCount);

        // when
        long result = followService.getFollowingCount(memberId);

        // then
        assertEquals(expectedCount, result);
        verify(memberService).findMemberById(memberId);
        verify(followRepository).countByFollower(follower);
    }

    @Test
    @DisplayName("빈 팔로워 목록 조회")
    void getFollowers_EmptyList_Success() {
        // given
        Long memberId = 2L;
        
        given(memberService.findMemberById(memberId)).willReturn(followee);
        given(followRepository.findAllByFollowee(followee)).willReturn(Arrays.asList());

        // when
        List<FollowResponse> result = followService.getFollowers(memberId);

        // then
        assertTrue(result.isEmpty());
        verify(memberService).findMemberById(memberId);
        verify(followRepository).findAllByFollowee(followee);
    }

    @Test
    @DisplayName("빈 팔로잉 목록 조회")
    void getFollowings_EmptyList_Success() {
        // given
        Long memberId = 1L;
        
        given(memberService.findMemberById(memberId)).willReturn(follower);
        given(followRepository.findAllByFollower(follower)).willReturn(Arrays.asList());

        // when
        List<FollowResponse> result = followService.getFollowings(memberId);

        // then
        assertTrue(result.isEmpty());
        verify(memberService).findMemberById(memberId);
        verify(followRepository).findAllByFollower(follower);
    }
}
