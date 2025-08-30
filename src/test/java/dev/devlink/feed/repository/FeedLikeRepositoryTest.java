package dev.devlink.feed.repository;

import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.entity.FeedLike;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedLikeRepositoryTest {

    @Mock
    private FeedLikeRepository feedLikeRepository;
    
    private Member author;
    private Member liker;
    private Feed feed;
    private FeedLike feedLike;

    @BeforeEach
    void setUp() {
        author = createMember("author", "author@test.com", "authorNick");
        liker = createMember("liker", "liker@test.com", "likerNick");
        feed = createFeed(author, "테스트 피드");
        feedLike = createFeedLike(feed, liker);
    }

    @Test
    @DisplayName("피드의 좋아요 개수를 조회할 수 있다")
    void countByFeed() {
        // given
        when(feedLikeRepository.countByFeed(feed)).thenReturn(2L);

        // when
        long count = feedLikeRepository.countByFeed(feed);

        // then
        assertThat(count).isEqualTo(2L);
        verify(feedLikeRepository).countByFeed(feed);
    }

    @Test
    @DisplayName("피드와 사용자로 좋아요를 삭제할 수 있다")
    void deleteByFeedAndMember() {
        // given
        doNothing().when(feedLikeRepository).deleteByFeedAndMember(feed, liker);
        when(feedLikeRepository.countByFeed(feed)).thenReturn(0L);

        // when
        feedLikeRepository.deleteByFeedAndMember(feed, liker);

        // then
        long count = feedLikeRepository.countByFeed(feed);
        assertThat(count).isEqualTo(0L);
        verify(feedLikeRepository).deleteByFeedAndMember(feed, liker);
    }

    @Test
    @DisplayName("피드와 사용자의 좋아요 존재 여부를 확인할 수 있다")
    void existsByFeedAndMember() {
        // given
        when(feedLikeRepository.existsByFeedAndMember(feed, liker))
                .thenReturn(false)
                .thenReturn(true);

        // when & then - 좋아요하지 않은 상태
        boolean beforeLike = feedLikeRepository.existsByFeedAndMember(feed, liker);
        assertThat(beforeLike).isFalse();

        // when & then - 좋아요한 상태
        boolean afterLike = feedLikeRepository.existsByFeedAndMember(feed, liker);
        assertThat(afterLike).isTrue();

        verify(feedLikeRepository, times(2)).existsByFeedAndMember(feed, liker);
    }

    @Test
    @DisplayName("좋아요를 저장할 수 있다")
    void save() {
        // given
        when(feedLikeRepository.save(any(FeedLike.class))).thenReturn(feedLike);

        // when
        FeedLike savedLike = feedLikeRepository.save(feedLike);

        // then
        assertThat(savedLike).isNotNull();
        assertThat(savedLike.getFeed()).isEqualTo(feed);
        assertThat(savedLike.getMember()).isEqualTo(liker);
        verify(feedLikeRepository).save(any(FeedLike.class));
    }

    private Member createMember(String name, String email, String nickname) {
        return Member.builder()
                .name(name)
                .email(email)
                .nickname(nickname)
                .password("password")
                .build();
    }

    private Feed createFeed(Member author, String content) {
        return Feed.create(author, content, null);
    }

    private FeedLike createFeedLike(Feed feed, Member member) {
        return FeedLike.create(feed, member);
    }
}
