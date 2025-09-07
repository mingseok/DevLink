package dev.devlink.article.repository;

import dev.devlink.article.entity.Article;
import dev.devlink.member.entity.Member;
import dev.devlink.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@test.com")
                .nickname("testUser")
                .password("password")
                .build();
        memberRepository.save(testMember);
    }

    @Test
    void findAllWithMember_조회성공() {
        // given
        Article article = Article.builder()
                .title("Test Article")
                .content("Test Content")
                .member(testMember)
                .build();
        articleRepository.save(article);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Article> result = articleRepository.findAllWithMember(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMember()).isNotNull();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Article");
    }

    @Test
    void findDetailById_조회성공() {
        // given
        Article article = Article.builder()
                .title("Test Article")
                .content("Test Content")
                .member(testMember)
                .build();
        Article savedArticle = articleRepository.save(article);

        // when
        Optional<Article> result = articleRepository.findDetailById(savedArticle.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMember()).isNotNull();
        assertThat(result.get().getTitle()).isEqualTo("Test Article");
    }

    @Test
    void findDetailById_존재하지않는경우() {
        // when
        Optional<Article> result = articleRepository.findDetailById(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void findTopByViews_조회수순정렬() {
        // given
        Article article1 = Article.builder()
                .title("Article 1")
                .content("Content 1")
                .member(testMember)
                .viewCount(100L)
                .build();

        Article article2 = Article.builder()
                .title("Article 2")
                .content("Content 2")
                .member(testMember)
                .viewCount(200L)
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Article> result = articleRepository.findTopByViews(pageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getViewCount()).isEqualTo(200L);
        assertThat(result.get(1).getViewCount()).isEqualTo(100L);
    }
}
