package dev.devlink.article.constant;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArticleConstantsTest {

    @Test
    void 상수값확인() {
        // then
        assertThat(ArticleConstants.SORT_BY_ID).isEqualTo("id");
    }

    @Test
    void 생성자접근불가확인() throws NoSuchMethodException {
        // given
        Constructor<ArticleConstants> constructor = ArticleConstants.class.getDeclaredConstructor();

        // when & then
        assertThrows(IllegalAccessException.class, constructor::newInstance);
    }

    @Test
    void 리플렉션으로생성자접근시도() throws Exception {
        // given
        Constructor<ArticleConstants> constructor = ArticleConstants.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // when & then
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }
}
