package dev.devlink.feed.service;

import dev.devlink.comment.repository.FeedCommentRepository;
import dev.devlink.common.file.FileUploadService;
import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.exception.FeedException;
import dev.devlink.feed.repository.FeedLikeRepository;
import dev.devlink.feed.repository.FeedRepository;
import dev.devlink.feed.service.dto.response.FeedResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import dev.devlink.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private FeedLikeRepository feedLikeRepository;

    @Mock
    private FeedCommentRepository feedCommentRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private FeedService feedService;

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
    @DisplayName("이미지 없이 피드를 생성할 수 있다")
    void createFeed_withoutImage() {
        // given
        Long memberId = 1L;
        String content = "새로운 피드";
        MultipartFile file = null;

        when(memberService.findMemberById(memberId)).thenReturn(testMember);

        // when
        feedService.createFeed(memberId, content, file);

        // then
        verify(memberService).findMemberById(memberId);
        verify(feedRepository).save(any(Feed.class));
        verify(fileUploadService, never()).uploadFile(any(), any());
    }

    @Test
    @DisplayName("이미지와 함께 피드를 생성할 수 있다")
    void createFeed_withImage() {
        // given
        Long memberId = 1L;
        String content = "새로운 피드";
        MultipartFile file = mock(MultipartFile.class);
        String imageUrl = "uploaded-image.jpg";

        when(memberService.findMemberById(memberId)).thenReturn(testMember);
        when(file.isEmpty()).thenReturn(false);
        when(fileUploadService.uploadFile(eq(file), any())).thenReturn(imageUrl);

        // when
        feedService.createFeed(memberId, content, file);

        // then
        verify(memberService).findMemberById(memberId);
        verify(fileUploadService).uploadFile(eq(file), any());
        verify(feedRepository).save(any(Feed.class));
    }

    @Test
    @DisplayName("팔로우한 사용자들의 피드 목록을 조회할 수 있다")
    void getFeeds() {
        // given
        Long memberId = 1L;
        
        // Mock으로 완전히 제어된 Feed 생성
        Feed mockFeed = mock(Feed.class);
        when(mockFeed.getId()).thenReturn(1L);
        when(mockFeed.getWriterId()).thenReturn(1L);
        when(mockFeed.getContent()).thenReturn("테스트 피드");
        when(mockFeed.getImageUrl()).thenReturn(null);
        when(mockFeed.getWriterNickname()).thenReturn("testNick");
        when(mockFeed.getCreatedAt()).thenReturn(java.time.LocalDateTime.now());
        when(mockFeed.isAuthor(memberId)).thenReturn(true);
        
        List<Feed> feeds = Arrays.asList(mockFeed);
        String profileImageUrl = "profile.jpg";

        when(memberService.findMemberById(memberId)).thenReturn(testMember);
        when(feedRepository.findFeedsByFollowing(testMember)).thenReturn(feeds);
        when(feedLikeRepository.existsByFeedAndMember(mockFeed, testMember)).thenReturn(true);
        when(feedLikeRepository.countByFeed(mockFeed)).thenReturn(5L);
        when(feedCommentRepository.countByFeedId(1L)).thenReturn(3L);
        when(profileService.getProfileImageUrl(1L)).thenReturn(profileImageUrl);

        // when
        List<FeedResponse> responses = feedService.getFeeds(memberId);

        // then
        assertThat(responses).hasSize(1);
        verify(memberService).findMemberById(memberId);
        verify(feedRepository).findFeedsByFollowing(testMember);
        verify(feedLikeRepository).existsByFeedAndMember(mockFeed, testMember);
        verify(feedLikeRepository).countByFeed(mockFeed);
        verify(feedCommentRepository).countByFeedId(1L);
        verify(profileService).getProfileImageUrl(1L);
    }

    @Test
    @DisplayName("본인이 작성한 피드를 삭제할 수 있다")
    void deleteFeed_byAuthor() {
        // given
        Long memberId = 1L;
        Long feedId = 1L;
        
        // Mock으로 완전히 제어된 Feed 생성
        Feed mockFeed = mock(Feed.class);
        when(mockFeed.isAuthor(memberId)).thenReturn(true);
        
        when(feedRepository.findById(feedId)).thenReturn(Optional.of(mockFeed));

        // when
        feedService.deleteFeed(memberId, feedId);

        // then
        verify(feedRepository).findById(feedId);
        verify(feedRepository).delete(mockFeed);
        verify(mockFeed).isAuthor(memberId);
    }

    @Test
    @DisplayName("다른 사용자의 피드는 삭제할 수 없다")
    void deleteFeed_notAuthor() {
        // given
        Long memberId = 2L; // 다른 사용자
        Long feedId = 1L;
        
        // Mock으로 완전히 제어된 Feed 생성
        Feed mockFeed = mock(Feed.class);
        when(mockFeed.isAuthor(memberId)).thenReturn(false);
        
        when(feedRepository.findById(feedId)).thenReturn(Optional.of(mockFeed));

        // when & then
        assertThatThrownBy(() -> feedService.deleteFeed(memberId, feedId))
                .isInstanceOf(FeedException.class);

        verify(feedRepository).findById(feedId);
        verify(feedRepository, never()).delete(any());
        verify(mockFeed).isAuthor(memberId);
    }

    @Test
    @DisplayName("존재하지 않는 피드는 삭제할 수 없다")
    void deleteFeed_notFound() {
        // given
        Long memberId = 1L;
        Long feedId = 999L;
        
        when(feedRepository.findById(feedId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedService.deleteFeed(memberId, feedId))
                .isInstanceOf(FeedException.class);

        verify(feedRepository).findById(feedId);
        verify(feedRepository, never()).delete(any());
    }
}
