package dev.devlink.profile.entity;

import dev.devlink.common.file.FileConstants;
import dev.devlink.member.entity.Member;
import dev.devlink.profile.constant.ProfileDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileTest {

    @Test
    @DisplayName("프로필을 생성할 수 있다")
    void create_Success() {
        // given
        Member member = Member.create("김민석", "test@example.com", "테스트닉네임", "password");
        String bio = "안녕하세요";

        // when
        Profile profile = Profile.create(member, bio);

        // then
        assertThat(profile.getMember()).isEqualTo(member);
        assertThat(profile.getBio()).isEqualTo(bio);
        assertThat(profile.getImageUrl()).isEqualTo(FileConstants.DEFAULT_IMAGE_URL);
    }

    @Test
    @DisplayName("빌더 패턴으로 프로필을 생성할 수 있다")
    void builder_Success() {
        // given
        Member member = Member.create("김선우", "test2@example.com", "테스트닉네임2", "password");
        String bio = "반갑습니다";
        String imageUrl = "https://example.com/image.jpg";

        // when
        Profile profile = Profile.builder()
                .member(member)
                .bio(bio)
                .imageUrl(imageUrl)
                .build();

        // then
        assertThat(profile.getMember()).isEqualTo(member);
        assertThat(profile.getBio()).isEqualTo(bio);
        assertThat(profile.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("프로필 소개글을 수정할 수 있다")
    void updateBio_Success() {
        // given
        Member member = Member.create("김현우", "test3@example.com", "테스트닉네임3", "password");
        Profile profile = Profile.create(member, "기존 소개글");
        String newBio = "새로운 소개글";

        // when
        profile.updateBio(newBio);

        // then
        assertThat(profile.getBio()).isEqualTo(newBio);
    }

    @Test
    @DisplayName("프로필 이미지를 수정할 수 있다")
    void updateImage_Success() {
        // given
        Member member = Member.create("이영희", "test4@example.com", "테스트닉네임4", "password");
        Profile profile = Profile.create(member, "소개글");
        String newImageUrl = "https://example.com/new-image.jpg";

        // when
        profile.updateImage(newImageUrl);

        // then
        assertThat(profile.getImageUrl()).isEqualTo(newImageUrl);
    }
}
