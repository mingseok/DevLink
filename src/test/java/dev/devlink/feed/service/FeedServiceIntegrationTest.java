package dev.devlink.feed.service;

import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.exception.FeedError;
import dev.devlink.feed.exception.FeedException;
import dev.devlink.feed.repository.FeedRepository;
import dev.devlink.feed.service.dto.request.FeedCreateRequest;
import dev.devlink.feed.service.dto.response.FeedResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeedService 통합 테스트")
class FeedServiceIntegrationTest {

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private FeedService feedService;

    private Member member;
    private Feed feed;

    @BeforeEach
    void setUp() {
        member = Member.create("testUser", "test@example.com", "testNick", "password");
        ReflectionTestUtils.setField(member, "id", 1L);
        
        feed = Feed.create(member, "테스트 피드 내용", null);
        ReflectionTestUtils.setField(feed, "id", 1L);
        ReflectionTestUtils.setField(feed, "createdAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("피드 생성 성공")
    void createFeed_Success() {
        // given
        FeedCreateRequest request = new FeedCreateRequest("새로운 피드 내용");
        Long memberId = 1L;
        
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(feedRepository.save(any(Feed.class))).willReturn(feed);

        // when
        Long result = feedService.createFeed(request, memberId);

        // then
        assertEquals(1L, result);
        verify(memberRepository).findById(memberId);
        verify(feedRepository).save(any(Feed.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 피드 생성 시 예외 발생")
    void createFeed_MemberNotFound_ThrowsException() {
        // given
        FeedCreateRequest request = new FeedCreateRequest("새로운 피드 내용");
        Long nonExistentMemberId = 999L;
        
        given(memberRepository.findById(nonExistentMemberId)).willReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> 
                feedService.createFeed(request, nonExistentMemberId));
        
        verify(memberRepository).findById(nonExistentMemberId);
        verifyNoInteractions(feedRepository);
    }

    @Test
    @DisplayName("피드 목록 조회 성공")
    void getFeedList_Success() {
        // given
        List<Feed> feeds = Arrays.asList(feed);
        given(feedRepository.findAllByOrderByCreatedAtDesc()).willReturn(feeds);

        // when
        List<FeedResponse> result = feedService.getFeedList();

        // then
        assertEquals(1, result.size());
        assertEquals("테스트 피드 내용", result.get(0).getContent());
        assertEquals("testNick", result.get(0).getAuthorNickname());
        verify(feedRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("빈 피드 목록 조회")
    void getFeedList_EmptyList_Success() {
        // given
        given(feedRepository.findAllByOrderByCreatedAtDesc()).willReturn(Arrays.asList());

        // when
        List<FeedResponse> result = feedService.getFeedList();

        // then
        assertTrue(result.isEmpty());
        verify(feedRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("피드 상세 조회 성공")
    void getFeedDetail_Success() {
        // given
        Long feedId = 1L;
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));

        // when
        FeedResponse result = feedService.getFeedDetail(feedId);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("테스트 피드 내용", result.getContent()),
                () -> assertEquals("testNick", result.getAuthorNickname()),
                () -> assertEquals(1L, result.getAuthorId())
        );
        verify(feedRepository).findById(feedId);
    }

    @Test
    @DisplayName("존재하지 않는 피드 상세 조회 시 예외 발생")
    void getFeedDetail_NotFound_ThrowsException() {
        // given
        Long nonExistentFeedId = 999L;
        given(feedRepository.findById(nonExistentFeedId)).willReturn(Optional.empty());

        // when & then
        FeedException exception = assertThrows(FeedException.class, () -> 
                feedService.getFeedDetail(nonExistentFeedId));
        
        assertEquals(FeedError.NOT_FOUND, exception.getCommonError());
        verify(feedRepository).findById(nonExistentFeedId);
    }

    @Test
    @DisplayName("피드 수정 성공")
    void updateFeed_Success() {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        FeedCreateRequest request = new FeedCreateRequest("수정된 피드 내용");
        
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));
        given(feedRepository.save(any(Feed.class))).willReturn(feed);

        // when
        feedService.updateFeed(feedId, request, memberId);

        // then
        verify(feedRepository).findById(feedId);
        verify(feedRepository).save(any(Feed.class));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 피드 수정 시 예외 발생")
    void updateFeed_NoPermission_ThrowsException() {
        // given
        Long feedId = 1L;
        Long unauthorizedMemberId = 2L;
        FeedCreateRequest request = new FeedCreateRequest("수정된 피드 내용");
        
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));

        // when & then
        FeedException exception = assertThrows(FeedException.class, () -> 
                feedService.updateFeed(feedId, request, unauthorizedMemberId));
        
        assertEquals(FeedError.NO_PERMISSION, exception.getCommonError());
        verify(feedRepository).findById(feedId);
        verify(feedRepository, never()).save(any(Feed.class));
    }

    @Test
    @DisplayName("피드 삭제 성공")
    void deleteFeed_Success() {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));

        // when
        feedService.deleteFeed(feedId, memberId);

        // then
        verify(feedRepository).findById(feedId);
        verify(feedRepository).delete(feed);
    }

    @Test
    @DisplayName("권한이 없는 사용자가 피드 삭제 시 예외 발생")
    void deleteFeed_NoPermission_ThrowsException() {
        // given
        Long feedId = 1L;
        Long unauthorizedMemberId = 2L;
        
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));

        // when & then
        FeedException exception = assertThrows(FeedException.class, () -> 
                feedService.deleteFeed(feedId, unauthorizedMemberId));
        
        assertEquals(FeedError.NO_PERMISSION, exception.getCommonError());
        verify(feedRepository).findById(feedId);
        verify(feedRepository, never()).delete(any(Feed.class));
    }

    @Test
    @DisplayName("특정 회원의 피드 목록 조회 성공")
    void getMemberFeeds_Success() {
        // given
        Long memberId = 1L;
        List<Feed> memberFeeds = Arrays.asList(feed);
        
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(feedRepository.findByMemberOrderByCreatedAtDesc(member)).willReturn(memberFeeds);

        // when
        List<FeedResponse> result = feedService.getMemberFeeds(memberId);

        // then
        assertEquals(1, result.size());
        assertEquals("테스트 피드 내용", result.get(0).getContent());
        verify(memberRepository).findById(memberId);
        verify(feedRepository).findByMemberOrderByCreatedAtDesc(member);
    }

    @Test
    @DisplayName("피드 개수 조회 성공")
    void getFeedCount_Success() {
        // given
        given(feedRepository.count()).willReturn(10L);

        // when
        long result = feedService.getFeedCount();

        // then
        assertEquals(10L, result);
        verify(feedRepository).count();
    }

    @Test
    @DisplayName("특정 회원의 피드 개수 조회 성공")
    void getMemberFeedCount_Success() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(feedRepository.countByMember(member)).willReturn(5L);

        // when
        long result = feedService.getMemberFeedCount(memberId);

        // then
        assertEquals(5L, result);
        verify(memberRepository).findById(memberId);
        verify(feedRepository).countByMember(member);
    }
}
