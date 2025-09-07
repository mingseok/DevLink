package dev.devlink.comment.service.dto.response;

import dev.devlink.comment.entity.ArticleComment;
import dev.devlink.comment.entity.FeedComment;
import dev.devlink.article.entity.Article;
import dev.devlink.feed.entity.Feed;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentResponseTest {

    private Member member;
    private Feed feed;
    private Article article;
    private FeedComment feedComment;
    private ArticleComment articleComment;

    @BeforeEach
    void setUp() {
        member = Member.create("testName", "test@example.com", "testNickname", "password123");
        ReflectionTestUtils.setField(member, "id", 1L);

        feed = Feed.create(member, "Test feed content", null);
        ReflectionTestUtils.setField(feed, "id", 1L);

        article = Article.create(member, "Test Article", "Test article content");
        ReflectionTestUtils.setField(article, "id", 1L);

        feedComment = FeedComment.create(feed, member, null, "테스트 피드 댓글입니다.");
        ReflectionTestUtils.setField(feedComment, "id", 1L);
        
        articleComment = ArticleComment.create(article, member, null, "테스트 아티클 댓글입니다.");
        ReflectionTestUtils.setField(articleComment, "id", 2L);
        
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(feedComment, "createdAt", now);
        ReflectionTestUtils.setField(articleComment, "createdAt", now);
    }

    @Test
    @DisplayName("FeedComment로부터 CommentResponse를 정상적으로 생성한다")
    void fromFeedComment() {
        // when
        CommentResponse response = CommentResponse.from(feedComment);

        // then
        assertThat(response.getId()).isEqualTo(feedComment.getId());
        assertThat(response.getContent()).isEqualTo(feedComment.getContent());
        assertThat(response.getWriter()).isEqualTo(feedComment.getWriterNickname());
        assertThat(response.getWriterId()).isEqualTo(feedComment.getWriter().getId());
        assertThat(response.getParentId()).isEqualTo(feedComment.getParentId());
        assertThat(response.getCreatedAt()).isEqualTo(feedComment.getCreatedAt());
        assertThat(response.getChildren()).isEmpty();
    }

    @Test
    @DisplayName("대댓글을 포함한 FeedComment로부터 CommentResponse를 정상적으로 생성한다")
    void fromFeedCommentWithParent_Success() {
        // given
        FeedComment replyComment = FeedComment.create(feed, member, 1L, "대댓글입니다.");
        ReflectionTestUtils.setField(replyComment, "id", 2L);
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(replyComment, "createdAt", now);

        // when
        CommentResponse response = CommentResponse.from(replyComment);

        // then
        assertThat(response.getId()).isEqualTo(replyComment.getId());
        assertThat(response.getContent()).isEqualTo(replyComment.getContent());
        assertThat(response.getParentId()).isEqualTo(1L);
        assertThat(response.getChildren()).isEmpty();
    }

    @Test
    @DisplayName("자식 댓글을 정상적으로 추가한다")
    void addChild_Success() {
        // given
        CommentResponse parentResponse = CommentResponse.from(feedComment);
        
        FeedComment childComment = FeedComment.create(feed, member, 1L, "자식 댓글입니다.");
        ReflectionTestUtils.setField(childComment, "id", 2L);
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(childComment, "createdAt", now);
        
        CommentResponse childResponse = CommentResponse.from(childComment);

        // when
        parentResponse.addChild(childResponse);

        // then
        assertThat(parentResponse.getChildren()).hasSize(1);
        assertThat(parentResponse.getChildren().get(0)).isEqualTo(childResponse);
        assertThat(parentResponse.getChildren().get(0).getContent()).isEqualTo("자식 댓글입니다.");
    }

    @Test
    @DisplayName("여러 자식 댓글을 정상적으로 추가한다")
    void addMultipleChildren_Success() {
        // given
        CommentResponse parentResponse = CommentResponse.from(feedComment);
        
        // 첫 번째 자식 댓글
        FeedComment childComment1 = FeedComment.create(feed, member, 1L, "첫 번째 자식 댓글");
        ReflectionTestUtils.setField(childComment1, "id", 2L);
        ReflectionTestUtils.setField(childComment1, "createdAt", LocalDateTime.now());
        CommentResponse childResponse1 = CommentResponse.from(childComment1);

        // 두 번째 자식 댓글
        FeedComment childComment2 = FeedComment.create(feed, member, 1L, "두 번째 자식 댓글");
        ReflectionTestUtils.setField(childComment2, "id", 3L);
        ReflectionTestUtils.setField(childComment2, "createdAt", LocalDateTime.now());
        CommentResponse childResponse2 = CommentResponse.from(childComment2);

        // when
        parentResponse.addChild(childResponse1);
        parentResponse.addChild(childResponse2);

        // then
        assertThat(parentResponse.getChildren()).hasSize(2);
        assertThat(parentResponse.getChildren()).containsExactly(childResponse1, childResponse2);
    }

    @Test
    @DisplayName("기본 생성자로 생성된 CommentResponse는 빈 children 리스트를 가진다")
    void createByDefaultConstructor_HasEmptyChildren() {
        // when
        CommentResponse response = new CommentResponse();

        // then
        assertThat(response.getChildren()).isEmpty();
        assertThat(response.getChildren()).isNotNull();
    }

    @Test
    @DisplayName("CommentResponse의 모든 필드가 정확히 매핑된다")
    void allFieldsMapping_Success() {
        // given
        LocalDateTime fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        ReflectionTestUtils.setField(feedComment, "createdAt", fixedTime);

        // when
        CommentResponse response = CommentResponse.from(feedComment);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getContent()).isEqualTo("테스트 피드 댓글입니다.");
        assertThat(response.getWriter()).isEqualTo("testNickname");
        assertThat(response.getWriterId()).isEqualTo(1L);
        assertThat(response.getParentId()).isNull();
        assertThat(response.getCreatedAt()).isEqualTo(fixedTime);
        assertThat(response.getChildren()).isEmpty();
    }
}
