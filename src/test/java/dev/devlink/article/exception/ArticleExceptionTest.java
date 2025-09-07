package dev.devlink.article.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleExceptionTest {

    @Test
    void 예외생성_성공() {
        // given
        ArticleError error = ArticleError.ARTICLE_NOT_FOUND;

        // when
        ArticleException exception = new ArticleException(error);

        // then
        assertThat(exception.getCommonError()).isEqualTo(error);
        assertThat(exception.getMessage()).isEqualTo(error.getMessage());
    }

    @Test
    void 예외_메시지확인() {
        // given
        ArticleError error = ArticleError.NO_PERMISSION;

        // when
        ArticleException exception = new ArticleException(error);

        // then
        assertThat(exception.getMessage()).isEqualTo("수정 권한이 없습니다.");
    }
}
