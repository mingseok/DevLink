package dev.devlink.follow.repository;

import dev.devlink.follow.entity.Follow;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowRepositoryTest {

    @Mock
    private FollowRepository followRepository;

    private Member follower;
    private Member followee;
    private Follow follow;

    @BeforeEach
    void setUp() {
        follower = Member.create("김민석", "follower@example.com", "팔로워", "password");
        followee = Member.create("김선우", "followee@example.com", "팔로위", "password");
        ReflectionTestUtils.setField(follower, "id", 1L);
        ReflectionTestUtils.setField(followee, "id", 2L);
        
        follow = Follow.create(follower, followee);
        ReflectionTestUtils.setField(follow, "id", 1L);
    }

    @Test
    @DisplayName("팔로워와 팔로위로 팔로우 관계를 조회할 수 있다")
    void findByFollowerAndFollowee_Success() {
        // given
        given(followRepository.findByFollowerAndFollowee(follower, followee))
                .willReturn(Optional.of(follow));

        // when
        Optional<Follow> result = followRepository.findByFollowerAndFollowee(follower, followee);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getFollower().getId()).isEqualTo(follower.getId());
        assertThat(result.get().getFollowee().getId()).isEqualTo(followee.getId());
        verify(followRepository).findByFollowerAndFollowee(follower, followee);
    }

    @Test
    @DisplayName("팔로워로 팔로잉 목록을 조회할 수 있다")
    void findAllByFollower_Success() {
        // given
        Member anotherFollowee = Member.create("김현우", "followee2@example.com", "팔로위2", "password");
        Follow anotherFollow = Follow.create(follower, anotherFollowee);
        
        given(followRepository.findAllByFollower(follower))
                .willReturn(List.of(follow, anotherFollow));

        // when
        List<Follow> result = followRepository.findAllByFollower(follower);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFollower()).isEqualTo(follower);
        assertThat(result.get(1).getFollower()).isEqualTo(follower);
        verify(followRepository).findAllByFollower(follower);
    }

    @Test
    @DisplayName("팔로위로 팔로워 목록을 조회할 수 있다")
    void findAllByFollowee_Success() {
        // given
        Member anotherFollower = Member.create("김현우", "follower2@example.com", "팔로워2", "password");
        Follow anotherFollow = Follow.create(anotherFollower, followee);
        
        given(followRepository.findAllByFollowee(followee))
                .willReturn(List.of(follow, anotherFollow));

        // when
        List<Follow> result = followRepository.findAllByFollowee(followee);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFollowee()).isEqualTo(followee);
        assertThat(result.get(1).getFollowee()).isEqualTo(followee);
        verify(followRepository).findAllByFollowee(followee);
    }

    @Test
    @DisplayName("팔로우 관계 존재 여부를 확인할 수 있다")
    void existsByFollowerAndFollowee_Success() {
        // given
        given(followRepository.existsByFollowerAndFollowee(follower, followee)).willReturn(true);

        // when
        boolean result = followRepository.existsByFollowerAndFollowee(follower, followee);

        // then
        assertThat(result).isTrue();
        verify(followRepository).existsByFollowerAndFollowee(follower, followee);
    }

    @Test
    @DisplayName("팔로워 수를 조회할 수 있다")
    void countByFollowee_Success() {
        // given
        given(followRepository.countByFollowee(followee)).willReturn(10L);

        // when
        long result = followRepository.countByFollowee(followee);

        // then
        assertThat(result).isEqualTo(10L);
        verify(followRepository).countByFollowee(followee);
    }

    @Test
    @DisplayName("팔로잉 수를 조회할 수 있다")
    void countByFollower_Success() {
        // given
        given(followRepository.countByFollower(follower)).willReturn(5L);

        // when
        long result = followRepository.countByFollower(follower);

        // then
        assertThat(result).isEqualTo(5L);
        verify(followRepository).countByFollower(follower);
    }

    @Test
    @DisplayName("존재하지 않는 팔로우 관계 조회시 빈 결과를 반환한다")
    void findByFollowerAndFollowee_WhenNotExists_ReturnEmpty() {
        // given
        given(followRepository.findByFollowerAndFollowee(follower, followee))
                .willReturn(Optional.empty());

        // when
        Optional<Follow> result = followRepository.findByFollowerAndFollowee(follower, followee);

        // then
        assertThat(result).isEmpty();
        verify(followRepository).findByFollowerAndFollowee(follower, followee);
    }

    @Test
    @DisplayName("팔로우 관계가 존재하지 않을 때 false를 반환한다")
    void existsByFollowerAndFollowee_WhenNotExists_ReturnFalse() {
        // given
        given(followRepository.existsByFollowerAndFollowee(follower, followee)).willReturn(false);

        // when
        boolean result = followRepository.existsByFollowerAndFollowee(follower, followee);

        // then
        assertThat(result).isFalse();
        verify(followRepository).existsByFollowerAndFollowee(follower, followee);
    }

    @Test
    @DisplayName("팔로워가 없는 회원의 팔로잉 목록은 빈 리스트이다")
    void findAllByFollower_WhenNoFollowing_ReturnEmptyList() {
        // given
        given(followRepository.findAllByFollower(follower)).willReturn(List.of());

        // when
        List<Follow> result = followRepository.findAllByFollower(follower);

        // then
        assertThat(result).isEmpty();
        verify(followRepository).findAllByFollower(follower);
    }

    @Test
    @DisplayName("팔로위가 없는 회원의 팔로워 목록은 빈 리스트이다")
    void findAllByFollowee_WhenNoFollowers_ReturnEmptyList() {
        // given
        given(followRepository.findAllByFollowee(followee)).willReturn(List.of());

        // when
        List<Follow> result = followRepository.findAllByFollowee(followee);

        // then
        assertThat(result).isEmpty();
        verify(followRepository).findAllByFollowee(followee);
    }

    @Test
    @DisplayName("팔로워가 없는 회원의 팔로워 수는 0이다")
    void countByFollowee_WhenNoFollowers_ReturnZero() {
        // given
        given(followRepository.countByFollowee(followee)).willReturn(0L);

        // when
        long result = followRepository.countByFollowee(followee);

        // then
        assertThat(result).isEqualTo(0L);
        verify(followRepository).countByFollowee(followee);
    }

    @Test
    @DisplayName("팔로잉이 없는 회원의 팔로잉 수는 0이다")
    void countByFollower_WhenNoFollowing_ReturnZero() {
        // given
        given(followRepository.countByFollower(follower)).willReturn(0L);

        // when
        long result = followRepository.countByFollower(follower);

        // then
        assertThat(result).isEqualTo(0L);
        verify(followRepository).countByFollower(follower);
    }

    @Test
    @DisplayName("팔로우 관계를 삭제할 수 있다")
    void deleteByFollowerAndFollowee_Success() {
        // given
        given(followRepository.deleteByFollowerAndFollowee(follower, followee)).willReturn(1L);

        // when
        long result = followRepository.deleteByFollowerAndFollowee(follower, followee);

        // then
        assertThat(result).isEqualTo(1L);
        verify(followRepository).deleteByFollowerAndFollowee(follower, followee);
    }

    @Test
    @DisplayName("존재하지 않는 팔로우 관계 삭제시 0을 반환한다")
    void deleteByFollowerAndFollowee_WhenNotExists_ReturnZero() {
        // given
        given(followRepository.deleteByFollowerAndFollowee(follower, followee)).willReturn(0L);

        // when
        long result = followRepository.deleteByFollowerAndFollowee(follower, followee);

        // then
        assertThat(result).isEqualTo(0L);
        verify(followRepository).deleteByFollowerAndFollowee(follower, followee);
    }
}
