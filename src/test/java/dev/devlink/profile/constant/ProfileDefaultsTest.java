package dev.devlink.profile.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileDefaultsTest {

    @Test
    @DisplayName("기본 소개글 상수가 올바르게 정의되어 있다")
    void defaultBio_IsCorrect() {
        // when & then
        assertThat(ProfileDefaults.DEFAULT_BIO).isEqualTo("간단한 자기소개를 남겨보세요.");
    }

    @Test
    @DisplayName("기본 소개글 상수가 null이 아니다")
    void defaultBio_IsNotNull() {
        // when & then
        assertThat(ProfileDefaults.DEFAULT_BIO).isNotNull();
    }

    @Test
    @DisplayName("기본 소개글 상수가 빈 문자열이 아니다")
    void defaultBio_IsNotEmpty() {
        // when & then
        assertThat(ProfileDefaults.DEFAULT_BIO).isNotEmpty();
    }
}
