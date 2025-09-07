package dev.devlink.profile.service.dto.response;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProfileResponse 고급 테스트")
class ProfileResponseAdvancedTest {

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.create("테스트유저", "test@example.com", "테스트닉네임", "password");
        ReflectionTestUtils.setField(testMember, "id", 1L);
        ReflectionTestUtils.setField(testMember, "createdAt", LocalDateTime.of(2024, 1, 1, 12, 0, 0));
    }

    @Test
    @DisplayName("정적 팩토리 메서드로 ProfileResponse 생성 성공")
    void fromMember_Success() {
        // given
        String bio = "안녕하세요. 개발자입니다.";
        String imageUrl = "https://example.com/profile.jpg";
        boolean isFollowing = true;
        long followers = 100L;
        long followings = 50L;

        // when
        ProfileResponse response = ProfileResponse.from(testMember, bio, imageUrl, isFollowing, followers, followings);

        // then
        assertAll(
                () -> assertEquals(1L, response.getMemberId()),
                () -> assertEquals("테스트닉네임", response.getNickname()),
                () -> assertEquals(bio, response.getBio()),
                () -> assertEquals(imageUrl, response.getImageUrl()),
                () -> assertTrue(response.getIsFollowing()),
                () -> assertEquals(100L, response.getFollowers()),
                () -> assertEquals(50L, response.getFollowings()),
                () -> assertNotNull(response.getCreatedAt())
        );
    }

    @Test
    @DisplayName("빌더로 ProfileResponse 생성 성공")
    void builderPattern_Success() {
        // given & when
        ProfileResponse response = ProfileResponse.builder()
                .memberId(2L)
                .nickname("빌더닉네임")
                .bio("빌더로 생성된 소개글")
                .imageUrl("https://example.com/builder-profile.jpg")
                .isFollowing(false)
                .followers(200L)
                .followings(100L)
                .createdAt(LocalDateTime.now())
                .build();

        // then
        assertAll(
                () -> assertEquals(2L, response.getMemberId()),
                () -> assertEquals("빌더닉네임", response.getNickname()),
                () -> assertEquals("빌더로 생성된 소개글", response.getBio()),
                () -> assertEquals("https://example.com/builder-profile.jpg", response.getImageUrl()),
                () -> assertFalse(response.getIsFollowing()),
                () -> assertEquals(200L, response.getFollowers()),
                () -> assertEquals(100L, response.getFollowings()),
                () -> assertNotNull(response.getCreatedAt())
        );
    }

    @Test
    @DisplayName("null 값들로 ProfileResponse 생성")
    void nullValues_Success() {
        // when
        ProfileResponse response = ProfileResponse.from(testMember, null, null, false, 0L, 0L);

        // then
        assertAll(
                () -> assertEquals(1L, response.getMemberId()),
                () -> assertEquals("테스트닉네임", response.getNickname()),
                () -> assertNull(response.getBio()),
                () -> assertNull(response.getImageUrl()),
                () -> assertFalse(response.getIsFollowing()),
                () -> assertEquals(0L, response.getFollowers()),
                () -> assertEquals(0L, response.getFollowings())
        );
    }

    @Test
    @DisplayName("매우 긴 소개글로 ProfileResponse 생성")
    void longBio_Success() {
        // given
        String longBio = "매우 긴 소개글입니다. ".repeat(50);
        
        // when
        ProfileResponse response = ProfileResponse.from(testMember, longBio, "https://example.com/profile.jpg", false, 1000L, 500L);

        // then
        assertAll(
                () -> assertEquals(longBio, response.getBio()),
                () -> assertTrue(response.getBio().length() > 100),
                () -> assertEquals(1000L, response.getFollowers()),
                () -> assertEquals(500L, response.getFollowings())
        );
    }

    @Test
    @DisplayName("특수문자가 포함된 데이터로 ProfileResponse 생성")
    void specialCharacters_Success() {
        // given
        String bioWithSpecialChars = "안녕하세요! 😊 개발자입니다. @#$%^&*()";
        String imageUrlWithQuery = "https://example.com/profile.jpg?size=200&quality=high";
        
        // when
        ProfileResponse response = ProfileResponse.from(testMember, bioWithSpecialChars, imageUrlWithQuery, true, 150L, 75L);

        // then
        assertAll(
                () -> assertEquals(bioWithSpecialChars, response.getBio()),
                () -> assertEquals(imageUrlWithQuery, response.getImageUrl()),
                () -> assertTrue(response.getIsFollowing())
        );
    }

    @Test
    @DisplayName("팔로우 상태가 true인 경우")
    void followingTrue_Success() {
        // when
        ProfileResponse response = ProfileResponse.from(testMember, "팔로우 중", "https://example.com/following.jpg", true, 50L, 30L);

        // then
        assertTrue(response.getIsFollowing());
    }

    @Test
    @DisplayName("팔로우 상태가 false인 경우")
    void followingFalse_Success() {
        // when
        ProfileResponse response = ProfileResponse.from(testMember, "팔로우 안함", "https://example.com/not-following.jpg", false, 50L, 30L);

        // then
        assertFalse(response.getIsFollowing());
    }
}
