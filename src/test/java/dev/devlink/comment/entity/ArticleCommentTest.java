package dev.devlink.comment.entity;

import dev.devlink.article.entity.Article;
import dev.devlink.comment.exception.CommentError;
import dev.devlink.comment.exception.CommentException;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArticleCommentTest {

    private Member member;
    private Article article;

    @BeforeEach
    void setUp() {
        member = Member.create("testName", "test@example.com", "testNickname", "password123");
        ReflectionTestUtils.setField(member, "id", 1L);
        
        article = Article.create(member, "Test Article", "Test content");
        ReflectionTestUtils.setField(article, "id", 1L);
    }

    @Test
    @DisplayName("아티클 댓글을 정상적으로 생성한다")
    void createArticleComment_Success() {
        // given
        String content = "테스트 댓글입니다.";
        Long parentId = null;

        // when
        ArticleComment comment = ArticleComment.create(article, member, parentId, content);

        // then
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getMember()).isEqualTo(member);
        assertThat(comment.getArticle()).isEqualTo(article);
        assertThat(comment.getParentId()).isNull();
    }

    @Test
    @DisplayName("대댓글을 정상적으로 생성한다")
    void createReplyComment_Success() {
        // given
        String content = "대댓글입니다.";
        Long parentId = 1L;

        // when
        ArticleComment comment = ArticleComment.create(article, member, parentId, content);

        // then
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getParentId()).isEqualTo(parentId);
    }

    @Test
    @DisplayName("작성자 닉네임을 정상적으로 반환한다")
    void getWriterNickname_Success() {
        // given
        ArticleComment comment = ArticleComment.create(article, member, null, "테스트 댓글");

        // when
        String nickname = comment.getWriterNickname();

        // then
        assertThat(nickname).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("작성자를 정상적으로 반환한다")
    void getWriter_Success() {
        // given
        ArticleComment comment = ArticleComment.create(article, member, null, "테스트 댓글");

        // when
        Member writer = comment.getWriter();

        // then
        assertThat(writer).isEqualTo(member);
    }

    @Test
    @DisplayName("작성자가 일치하면 권한 검증을 통과한다")
    void checkAuthor_Success() {
        // given
        ArticleComment comment = ArticleComment.create(article, member, null, "테스트 댓글");

        // when & then
        assertThatCode(() -> comment.checkAuthor(member.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("작성자가 일치하지 않으면 권한 없음 예외가 발생한다")
    void checkAuthor_NoPermission() {
        // given
        ArticleComment comment = ArticleComment.create(article, member, null, "테스트 댓글");
        Long differentMemberId = 999L;

        // when & then
        assertThatThrownBy(() -> comment.checkAuthor(differentMemberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.NO_PERMISSION.getMessage());
    }

    @Test
    @DisplayName("Builder 패턴으로 ArticleComment를 생성한다")
    void createByBuilderPattern_Success() {
        // given
        String content = "빌더로 생성한 댓글";
        Long parentId = 1L;

        // when
        ArticleComment comment = ArticleComment.builder()
                .article(article)
                .member(member)
                .content(content)
                .parentId(parentId)
                .build();

        // then
        assertThat(comment.getArticle()).isEqualTo(article);
        assertThat(comment.getMember()).isEqualTo(member);
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getParentId()).isEqualTo(parentId);
    }

    @Test
    @DisplayName("create 정적 메서드로 ArticleComment를 생성한다")
    void createByStaticMethod_Success() {
        // given
        String content = "정적 메서드로 생성한 댓글";
        Long parentId = 2L;

        // when
        ArticleComment comment = ArticleComment.create(article, member, parentId, content);

        // then
        assertThat(comment.getArticle()).isEqualTo(article);
        assertThat(comment.getMember()).isEqualTo(member);
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getParentId()).isEqualTo(parentId);
    }
}
