package dev.devlink.feed.repository;

import dev.devlink.feed.entity.Feed;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedRepositoryTest {

    @Mock
    private FeedRepository feedRepository;

    private Member currentMember;
    private Member followee;
    private Feed ownFeed;
    private Feed followeeFeed;

    @BeforeEach
    void setUp() {
        currentMember = createMember("currentUser", "current@test.com", "currentUser");
        followee = createMember("followee", "followee@test.com", "followeeNick");
        ownFeed = createFeed(1L, currentMember, "내 피드", null);
        followeeFeed = createFeed(2L, followee, "팔로우한 사람 피드", null);
    }

    @Test
    @DisplayName("팔로우한 사용자들의 피드를 시간 역순으로 조회할 수 있다")
    void findFeedsByFollowing() {
        // given
        List<Feed> expectedFeeds = Arrays.asList(ownFeed);
        when(feedRepository.findFeedsByFollowing(currentMember)).thenReturn(expectedFeeds);

        // when
        List<Feed> result = feedRepository.findFeedsByFollowing(currentMember);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("내 피드");
        verify(feedRepository).findFeedsByFollowing(currentMember);
    }

    @Test
    @DisplayName("피드 ID로 피드를 조회할 수 있다")
    void findById() {
        // given
        Feed testFeed = createFeed(1L, currentMember, "테스트 피드", "image.jpg");
        when(feedRepository.findById(1L)).thenReturn(Optional.of(testFeed));

        // when
        Feed result = feedRepository.findById(1L).orElse(null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("테스트 피드");
        assertThat(result.getImageUrl()).isEqualTo("image.jpg");
        verify(feedRepository).findById(1L);
    }

    @Test
    @DisplayName("피드를 저장할 수 있다")
    void save() {
        // given
        Feed newFeed = createFeed(null, currentMember, "새 피드", null);
        Feed savedFeed = createFeed(1L, currentMember, "새 피드", null);
        when(feedRepository.save(any(Feed.class))).thenReturn(savedFeed);

        // when
        Feed result = feedRepository.save(newFeed);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("새 피드");
        verify(feedRepository).save(any(Feed.class));
    }

    @Test
    @DisplayName("피드를 삭제할 수 있다")
    void delete_Success() {
        // given
        Feed feedToDelete = createFeed(1L, currentMember, "삭제할 피드", null);
        doNothing().when(feedRepository).delete(feedToDelete);
        when(feedRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        feedRepository.delete(feedToDelete);

        // then
        Feed result = feedRepository.findById(1L).orElse(null);
        assertThat(result).isNull();
        verify(feedRepository).delete(feedToDelete);
    }

    @Test
    @DisplayName("존재하지 않는 피드 ID로 조회 시 빈 Optional 반환")
    void findById_NotFound_ReturnsEmpty() {
        // given
        when(feedRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Feed> result = feedRepository.findById(999L);

        // then
        assertThat(result).isEmpty();
        verify(feedRepository).findById(999L);
    }

    @Test
    @DisplayName("특정 회원의 모든 피드를 조회할 수 있다")
    void findByMember_Success() {
        // given
        List<Feed> memberFeeds = Arrays.asList(
                createFeed(1L, currentMember, "첫 번째 피드", null),
                createFeed(2L, currentMember, "두 번째 피드", "image.jpg")
        );
        when(feedRepository.findByMember(currentMember)).thenReturn(memberFeeds);

        // when
        List<Feed> result = feedRepository.findByMember(currentMember);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContent()).isEqualTo("첫 번째 피드");
        assertThat(result.get(1).getContent()).isEqualTo("두 번째 피드");
        verify(feedRepository).findByMember(currentMember);
    }

    @Test
    @DisplayName("피드가 없는 회원 조회 시 빈 리스트 반환")
    void findByMember_NoFeeds_ReturnsEmptyList() {
        // given
        when(feedRepository.findByMember(followee)).thenReturn(Arrays.asList());

        // when
        List<Feed> result = feedRepository.findByMember(followee);

        // then
        assertThat(result).isEmpty();
        verify(feedRepository).findByMember(followee);
    }

    @Test
    @DisplayName("모든 피드를 시간 역순으로 조회할 수 있다")
    void findAllOrderByCreatedAtDesc_Success() {
        // given
        List<Feed> allFeeds = Arrays.asList(followeeFeed, ownFeed); // 시간 역순
        when(feedRepository.findAllByOrderByCreatedAtDesc()).thenReturn(allFeeds);

        // when
        List<Feed> result = feedRepository.findAllByOrderByCreatedAtDesc();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContent()).isEqualTo("팔로우한 사람 피드");
        assertThat(result.get(1).getContent()).isEqualTo("내 피드");
        verify(feedRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("피드 개수를 카운트할 수 있다")
    void count_Success() {
        // given
        when(feedRepository.count()).thenReturn(2L);

        // when
        long result = feedRepository.count();

        // then
        assertThat(result).isEqualTo(2L);
        verify(feedRepository).count();
    }

    @Test
    @DisplayName("특정 회원의 피드 개수를 카운트할 수 있다")
    void countByMember_Success() {
        // given
        when(feedRepository.countByMember(currentMember)).thenReturn(5L);

        // when
        long result = feedRepository.countByMember(currentMember);

        // then
        assertThat(result).isEqualTo(5L);
        verify(feedRepository).countByMember(currentMember);
    }

    @Test
    @DisplayName("피드 존재 여부를 확인할 수 있다")
    void existsById_Success() {
        // given
        when(feedRepository.existsById(1L)).thenReturn(true);
        when(feedRepository.existsById(999L)).thenReturn(false);

        // when
        boolean exists = feedRepository.existsById(1L);
        boolean notExists = feedRepository.existsById(999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        verify(feedRepository).existsById(1L);
        verify(feedRepository).existsById(999L);
    }

    private Member createMember(String name, String email, String nickname) {
        return Member.builder()
                .name(name)
                .email(email)
                .nickname(nickname)
                .password("password")
                .build();
    }

    private Feed createFeed(Long id, Member author, String content, String imageUrl) {
        return Feed.create(author, content, imageUrl);
    }
}
