package dev.devlink.article.repository;

import dev.devlink.article.entity.Article;
import dev.devlink.article.entity.ArticleLike;
import dev.devlink.member.entity.Member;
import dev.devlink.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArticleLikeRepositoryTest {

    @Autowired
    private ArticleLikeRepository articleLikeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@test.com")
                .nickname("testUser")
                .password("password")
                .build();
        memberRepository.save(testMember);

        testArticle = Article.builder()
                .title("Test Article")
                .content("Test Content")
                .member(testMember)
                .build();
        articleRepository.save(testArticle);
    }

    @Test
    void countByArticle_좋아요개수조회() {
        // given
        ArticleLike like1 = ArticleLike.builder()
                .article(testArticle)
                .member(testMember)
                .build();
        articleLikeRepository.save(like1);

        // when
        long count = articleLikeRepository.countByArticle(testArticle);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void findByArticleAndMember_좋아요조회() {
        // given
        ArticleLike like = ArticleLike.builder()
                .article(testArticle)
                .member(testMember)
                .build();
        articleLikeRepository.save(like);

        // when
        Optional<ArticleLike> result = articleLikeRepository.findByArticleAndMember(testArticle, testMember);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getArticle()).isEqualTo(testArticle);
        assertThat(result.get().getMember()).isEqualTo(testMember);
    }

    @Test
    void findByArticleAndMember_좋아요없는경우() {
        // when
        Optional<ArticleLike> result = articleLikeRepository.findByArticleAndMember(testArticle, testMember);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void deleteByArticleAndMember_좋아요삭제() {
        // given
        ArticleLike like = ArticleLike.builder()
                .article(testArticle)
                .member(testMember)
                .build();
        articleLikeRepository.save(like);

        assertThat(articleLikeRepository.findByArticleAndMember(testArticle, testMember)).isPresent();

        // when
        articleLikeRepository.deleteByArticleAndMember(testArticle, testMember);
        articleLikeRepository.flush();

        // then
        Optional<ArticleLike> result = articleLikeRepository.findByArticleAndMember(testArticle, testMember);
        assertThat(result).isEmpty();
    }
}
