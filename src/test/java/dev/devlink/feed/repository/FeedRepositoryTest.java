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
    void delete() {
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
