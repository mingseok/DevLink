package dev.devlink.comment.entity;

import dev.devlink.comment.exception.CommentError;
import dev.devlink.comment.exception.CommentException;
import dev.devlink.feed.entity.Feed;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeedCommentTest {

    private Member member;
    private Feed feed;

    @BeforeEach
    void setUp() {
        member = Member.create("testName", "test@example.com", "testNickname", "password123");
        ReflectionTestUtils.setField(member, "id", 1L);
        
        feed = Feed.create(member, "Test feed content", null);
        ReflectionTestUtils.setField(feed, "id", 1L);
    }

    @Test
    @DisplayName("피드 댓글을 정상적으로 생성한다")
    void createFeedComment_Success() {
        // given
        String content = "테스트 댓글입니다.";
        Long parentId = null;

        // when
        FeedComment comment = FeedComment.create(feed, member, parentId, content);

        // then
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getMember()).isEqualTo(member);
        assertThat(comment.getFeed()).isEqualTo(feed);
        assertThat(comment.getParentId()).isNull();
    }

    @Test
    @DisplayName("대댓글을 정상적으로 생성한다")
    void createReplyComment_Success() {
        // given
        String content = "대댓글입니다.";
        Long parentId = 1L;

        // when
        FeedComment comment = FeedComment.create(feed, member, parentId, content);

        // then
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getParentId()).isEqualTo(parentId);
    }

    @Test
    @DisplayName("피드 ID를 정상적으로 반환한다")
    void getFeedId_Success() {
        // given
        FeedComment comment = FeedComment.create(feed, member, null, "테스트 댓글");

        // when
        Long feedId = comment.getFeedId();

        // then
        assertThat(feedId).isEqualTo(feed.getId());
    }

    @Test
    @DisplayName("작성자 닉네임을 정상적으로 반환한다")
    void getWriterNickname_Success() {
        // given
        FeedComment comment = FeedComment.create(feed, member, null, "테스트 댓글");

        // when
        String nickname = comment.getWriterNickname();

        // then
        assertThat(nickname).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("작성자를 정상적으로 반환한다")
    void getWriter_Success() {
        // given
        FeedComment comment = FeedComment.create(feed, member, null, "테스트 댓글");

        // when
        Member writer = comment.getWriter();

        // then
        assertThat(writer).isEqualTo(member);
    }

    @Test
    @DisplayName("작성자가 일치하면 권한 검증을 통과한다")
    void checkAuthor_Success() {
        // given
        FeedComment comment = FeedComment.create(feed, member, null, "테스트 댓글");

        // when & then
        assertThatCode(() -> comment.checkAuthor(member.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("작성자가 일치하지 않으면 권한 없음 예외가 발생한다")
    void checkAuthor_NoPermission() {
        // given
        FeedComment comment = FeedComment.create(feed, member, null, "테스트 댓글");
        Long differentMemberId = 999L;

        // when & then
        assertThatThrownBy(() -> comment.checkAuthor(differentMemberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.NO_PERMISSION.getMessage());
    }
}
