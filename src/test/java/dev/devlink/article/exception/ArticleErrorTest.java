package dev.devlink.article.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleErrorTest {

    @Test
    void ARTICLE_NOT_FOUND_에러정보확인() {
        // given
        ArticleError error = ArticleError.ARTICLE_NOT_FOUND;

        // then
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(error.getCode()).isEqualTo("40410");
        assertThat(error.getMessage()).isEqualTo("존재하지 않는 게시글입니다.");
    }

    @Test
    void NO_PERMISSION_에러정보확인() {
        // given
        ArticleError error = ArticleError.NO_PERMISSION;

        // then
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(error.getCode()).isEqualTo("40311");
        assertThat(error.getMessage()).isEqualTo("수정 권한이 없습니다.");
    }

    @Test
    void CONCURRENT_LIKE_REQUEST_에러정보확인() {
        // given
        ArticleError error = ArticleError.CONCURRENT_LIKE_REQUEST;

        // then
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(error.getCode()).isEqualTo("40911");
        assertThat(error.getMessage()).isEqualTo("동시 요청으로 인해 좋아요 처리가 실패했습니다.");
    }

    @Test
    void LOCK_INTERRUPTED_에러정보확인() {
        // given
        ArticleError error = ArticleError.LOCK_INTERRUPTED;

        // then
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(error.getCode()).isEqualTo("50020");
        assertThat(error.getMessage()).isEqualTo("좋아요 처리 중 인터럽트가 발생했습니다.");
    }
}
