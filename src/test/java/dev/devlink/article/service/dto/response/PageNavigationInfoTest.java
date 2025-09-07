package dev.devlink.article.service.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageNavigationInfoTest {

    @Test
    void 생성자_성공() {
        // given
        int startPage = 1;
        int endPage = 10;

        // when
        PageNavigationInfo info = new PageNavigationInfo(startPage, endPage);

        // then
        assertThat(info.getStartPage()).isEqualTo(startPage);
        assertThat(info.getEndPage()).isEqualTo(endPage);
    }

    @Test
    void 페이지범위_정상() {
        // given
        PageNavigationInfo info = new PageNavigationInfo(5, 15);

        // then
        assertThat(info.getStartPage()).isEqualTo(5);
        assertThat(info.getEndPage()).isEqualTo(15);
    }

    @Test
    void 동일페이지_처리() {
        // given
        PageNavigationInfo info = new PageNavigationInfo(1, 1);

        // then
        assertThat(info.getStartPage()).isEqualTo(1);
        assertThat(info.getEndPage()).isEqualTo(1);
    }
}
