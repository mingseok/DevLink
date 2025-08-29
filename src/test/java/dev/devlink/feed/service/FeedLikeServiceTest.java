package dev.devlink.feed.service;

import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.entity.FeedLike;
import dev.devlink.feed.exception.FeedException;
import dev.devlink.feed.repository.FeedLikeRepository;
import dev.devlink.feed.repository.FeedRepository;
import dev.devlink.feed.service.dto.response.FeedLikeResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedLikeServiceTest {

    @Mock
    private FeedLikeRepository feedLikeRepository;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private FeedLikeService feedLikeService;

    private Member testMember;
    private Feed testFeed;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .name("testUser")
                .email("test@test.com")
                .nickname("testNick")
                .password("password")
                .build();

        testFeed = Feed.create(testMember, "테스트 피드", null);
    }

    @Test
    @DisplayName("좋아요하지 않은 피드에 좋아요를 추가할 수 있다")
    void likeOrUnlike_addLike() {
        // given
        Long memberId = 1L;
        Long feedId = 1L;
        long expectedLikeCount = 1L;

        when(memberService.findMemberById(memberId)).thenReturn(testMember);
        when(feedRepository.findById(feedId)).thenReturn(Optional.of(testFeed));
        when(feedLikeRepository.existsByFeedAndMember(testFeed, testMember)).thenReturn(false);
        when(feedLikeRepository.countByFeed(testFeed)).thenReturn(expectedLikeCount);

        // when
        FeedLikeResponse response = feedLikeService.likeOrUnlike(memberId, feedId);

        // then
        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(expectedLikeCount);
        
        verify(memberService).findMemberById(memberId);
        verify(feedRepository).findById(feedId);
        verify(feedLikeRepository).existsByFeedAndMember(testFeed, testMember);
        verify(feedLikeRepository).save(any(FeedLike.class));
        verify(feedLikeRepository).countByFeed(testFeed);
    }

    @Test
    @DisplayName("이미 좋아요한 피드의 좋아요를 취소할 수 있다")
    void likeOrUnlike_removeLike() {
        // given
        Long memberId = 1L;
        Long feedId = 1L;
        long expectedLikeCount = 0L;

        when(memberService.findMemberById(memberId)).thenReturn(testMember);
        when(feedRepository.findById(feedId)).thenReturn(Optional.of(testFeed));
        when(feedLikeRepository.existsByFeedAndMember(testFeed, testMember)).thenReturn(true);
        when(feedLikeRepository.countByFeed(testFeed)).thenReturn(expectedLikeCount);

        // when
        FeedLikeResponse response = feedLikeService.likeOrUnlike(memberId, feedId);

        // then
        assertThat(response.isLiked()).isFalse();
        assertThat(response.getLikeCount()).isEqualTo(expectedLikeCount);
        
        verify(memberService).findMemberById(memberId);
        verify(feedRepository).findById(feedId);
        verify(feedLikeRepository).existsByFeedAndMember(testFeed, testMember);
        verify(feedLikeRepository).deleteByFeedAndMember(testFeed, testMember);
        verify(feedLikeRepository).countByFeed(testFeed);
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 좋아요 요청시 예외가 발생한다")
    void likeOrUnlike_memberNotFound() {
        // given
        Long memberId = 999L;
        Long feedId = 1L;

        when(memberService.findMemberById(memberId)).thenThrow(new RuntimeException("Member not found"));

        // when & then
        assertThatThrownBy(() -> feedLikeService.likeOrUnlike(memberId, feedId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Member not found");

        verify(memberService).findMemberById(memberId);
        verify(feedRepository, never()).findById(any());
        verify(feedLikeRepository, never()).existsByFeedAndMember(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 피드에 좋아요 요청시 예외가 발생한다")
    void likeOrUnlike_feedNotFound() {
        // given
        Long memberId = 1L;
        Long feedId = 999L;

        when(memberService.findMemberById(memberId)).thenReturn(testMember);
        when(feedRepository.findById(feedId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedLikeService.likeOrUnlike(memberId, feedId))
                .isInstanceOf(FeedException.class);

        verify(memberService).findMemberById(memberId);
        verify(feedRepository).findById(feedId);
        verify(feedLikeRepository, never()).existsByFeedAndMember(any(), any());
    }
}
