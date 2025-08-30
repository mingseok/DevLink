package dev.devlink.profile.service.dto.response;

import dev.devlink.common.file.FileConstants;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileResponseTest {

    @Test
    @DisplayName("회원 정보로 ProfileResponse를 생성할 수 있다")
    void from_Success() {
        // given
        Member member = Member.create("김민석", "test@example.com", "테스트닉네임", "password");
        String bio = "안녕하세요";
        String imageUrl = "https://example.com/image.jpg";
        boolean isFollowing = true;
        long followers = 10L;
        long followings = 5L;

        // when
        ProfileResponse response = ProfileResponse.from(
                member, bio, imageUrl, isFollowing, followers, followings
        );

        // then
        assertThat(response.getMemberId()).isEqualTo(member.getId());
        assertThat(response.getNickname()).isEqualTo(member.getNickname());
        assertThat(response.getBio()).isEqualTo(bio);
        assertThat(response.getImageUrl()).isEqualTo(imageUrl);
        assertThat(response.getIsFollowing()).isEqualTo(isFollowing);
        assertThat(response.getFollowersCount()).isEqualTo(followers);
        assertThat(response.getFollowingsCount()).isEqualTo(followings);
    }

    @Test
    @DisplayName("기본 값으로 ProfileResponse를 생성할 수 있다")
    void from_WithDefaults_Success() {
        // given
        Member member = Member.create("김선우", "test2@example.com", "테스트닉네임2", "password");
        String bio = "";
        String imageUrl = FileConstants.DEFAULT_IMAGE_URL;
        boolean isFollowing = false;
        long followers = 0L;
        long followings = 0L;

        // when
        ProfileResponse response = ProfileResponse.from(
                member, bio, imageUrl, isFollowing, followers, followings
        );

        // then
        assertThat(response.getMemberId()).isEqualTo(member.getId());
        assertThat(response.getNickname()).isEqualTo(member.getNickname());
        assertThat(response.getBio()).isEmpty();
        assertThat(response.getImageUrl()).isEqualTo(FileConstants.DEFAULT_IMAGE_URL);
        assertThat(response.getIsFollowing()).isFalse();
        assertThat(response.getFollowersCount()).isZero();
        assertThat(response.getFollowingsCount()).isZero();
    }

    @Test
    @DisplayName("팔로우 관계와 카운트가 올바르게 설정된다")
    void from_WithFollowData_Success() {
        // given
        Member member = Member.create("김현우", "test3@example.com", "테스트닉네임3", "password");
        String bio = "반갑습니다";
        String imageUrl = "https://example.com/profile.jpg";
        boolean isFollowing = true;
        long followers = 100L;
        long followings = 50L;

        // when
        ProfileResponse response = ProfileResponse.from(
                member, bio, imageUrl, isFollowing, followers, followings
        );

        // then
        assertThat(response.getIsFollowing()).isTrue();
        assertThat(response.getFollowersCount()).isEqualTo(100L);
        assertThat(response.getFollowingsCount()).isEqualTo(50L);
    }
}
