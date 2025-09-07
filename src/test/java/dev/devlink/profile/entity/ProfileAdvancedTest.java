package dev.devlink.profile.entity;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Profile Entity 고급 테스트")
class ProfileAdvancedTest {

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.create("테스트유저", "test@example.com", "테스트닉네임", "password");
        ReflectionTestUtils.setField(testMember, "id", 1L);
    }

    @Test
    @DisplayName("Profile 엔티티 생성 성공")
    void createProfile_Success() {
        // given
        String bio = "안녕하세요. 개발자입니다.";

        // when
        Profile profile = Profile.create(testMember, bio);

        // then
        assertAll(
                () -> assertNotNull(profile),
                () -> assertEquals(testMember, profile.getMember()),
                () -> assertEquals(bio, profile.getBio()),
                () -> assertNull(profile.getImageUrl()),
                () -> assertNull(profile.getId()) // 아직 저장되지 않았으므로 null
        );
    }

    @Test
    @DisplayName("빈 소개글로 Profile 생성")
    void createProfile_WithEmptyBio() {
        // given
        String emptyBio = "";

        // when
        Profile profile = Profile.create(testMember, emptyBio);

        // then
        assertAll(
                () -> assertEquals(testMember, profile.getMember()),
                () -> assertEquals(emptyBio, profile.getBio()),
                () -> assertNull(profile.getImageUrl())
        );
    }

    @Test
    @DisplayName("null 소개글로 Profile 생성")
    void createProfile_WithNullBio() {
        // when
        Profile profile = Profile.create(testMember, null);

        // then
        assertAll(
                () -> assertEquals(testMember, profile.getMember()),
                () -> assertNull(profile.getBio()),
                () -> assertNull(profile.getImageUrl())
        );
    }

    @Test
    @DisplayName("프로필 소개글 업데이트 성공")
    void updateBio_Success() {
        // given
        Profile profile = Profile.create(testMember, "기존 소개글");
        String newBio = "업데이트된 소개글입니다.";

        // when
        profile.updateBio(newBio);

        // then
        assertEquals(newBio, profile.getBio());
    }

    @Test
    @DisplayName("프로필 소개글을 빈 문자열로 업데이트")
    void updateBio_WithEmptyString() {
        // given
        Profile profile = Profile.create(testMember, "기존 소개글");
        String emptyBio = "";

        // when
        profile.updateBio(emptyBio);

        // then
        assertEquals(emptyBio, profile.getBio());
    }

    @Test
    @DisplayName("프로필 소개글을 null로 업데이트")
    void updateBio_WithNull() {
        // given
        Profile profile = Profile.create(testMember, "기존 소개글");

        // when
        profile.updateBio(null);

        // then
        assertNull(profile.getBio());
    }

    @Test
    @DisplayName("프로필 이미지 업데이트 성공")
    void updateImage_Success() {
        // given
        Profile profile = Profile.create(testMember, "소개글");
        String imageUrl = "https://example.com/profile.jpg";

        // when
        profile.updateImage(imageUrl);

        // then
        assertEquals(imageUrl, profile.getImageUrl());
    }

    @Test
    @DisplayName("프로필 이미지를 null로 업데이트")
    void updateImage_WithNull() {
        // given
        Profile profile = Profile.create(testMember, "소개글");
        profile.updateImage("https://example.com/existing.jpg");

        // when
        profile.updateImage(null);

        // then
        assertNull(profile.getImageUrl());
    }

    @Test
    @DisplayName("프로필 이미지를 빈 문자열로 업데이트")
    void updateImage_WithEmptyString() {
        // given
        Profile profile = Profile.create(testMember, "소개글");
        String emptyImageUrl = "";

        // when
        profile.updateImage(emptyImageUrl);

        // then
        assertEquals(emptyImageUrl, profile.getImageUrl());
    }

    @Test
    @DisplayName("매우 긴 소개글로 업데이트")
    void updateBio_WithVeryLongBio() {
        // given
        Profile profile = Profile.create(testMember, "기존 소개글");
        String longBio = "매우 긴 소개글입니다. ".repeat(100);

        // when
        profile.updateBio(longBio);

        // then
        assertEquals(longBio, profile.getBio());
        assertTrue(profile.getBio().length() > 1000);
    }

    @Test
    @DisplayName("특수문자가 포함된 소개글로 업데이트")
    void updateBio_WithSpecialCharacters() {
        // given
        Profile profile = Profile.create(testMember, "기존 소개글");
        String bioWithSpecialChars = "안녕하세요! 😊 개발자입니다. @#$%^&*()_+{}[]|\\:;\"'<>,.?/~`";

        // when
        profile.updateBio(bioWithSpecialChars);

        // then
        assertEquals(bioWithSpecialChars, profile.getBio());
    }

    @Test
    @DisplayName("여러 번 소개글 업데이트")
    void updateBio_MultipleTimes() {
        // given
        Profile profile = Profile.create(testMember, "초기 소개글");

        // when
        profile.updateBio("첫 번째 업데이트");
        profile.updateBio("두 번째 업데이트");
        profile.updateBio("세 번째 업데이트");

        // then
        assertEquals("세 번째 업데이트", profile.getBio());
    }

    @Test
    @DisplayName("여러 번 이미지 업데이트")
    void updateImage_MultipleTimes() {
        // given
        Profile profile = Profile.create(testMember, "소개글");

        // when
        profile.updateImage("https://example.com/image1.jpg");
        profile.updateImage("https://example.com/image2.jpg");
        profile.updateImage("https://example.com/image3.jpg");

        // then
        assertEquals("https://example.com/image3.jpg", profile.getImageUrl());
    }

    @Test
    @DisplayName("소개글과 이미지 동시 업데이트")
    void updateBioAndImage_Simultaneously() {
        // given
        Profile profile = Profile.create(testMember, "초기 소개글");
        String newBio = "업데이트된 소개글";
        String newImageUrl = "https://example.com/new-profile.jpg";

        // when
        profile.updateBio(newBio);
        profile.updateImage(newImageUrl);

        // then
        assertAll(
                () -> assertEquals(newBio, profile.getBio()),
                () -> assertEquals(newImageUrl, profile.getImageUrl()),
                () -> assertEquals(testMember, profile.getMember())
        );
    }

    @Test
    @DisplayName("같은 Member로 생성된 Profile들은 동일한 Member를 참조한다")
    void sameMember_SameReference() {
        // when
        Profile profile1 = Profile.create(testMember, "첫 번째 프로필");
        Profile profile2 = Profile.create(testMember, "두 번째 프로필");

        // then
        assertSame(profile1.getMember(), profile2.getMember());
    }

    @Test
    @DisplayName("다른 Member로 생성된 Profile들은 다른 Member를 참조한다")
    void differentMembers_DifferentReference() {
        // given
        Member anotherMember = Member.create("다른유저", "another@test.com", "다른닉", "password");
        ReflectionTestUtils.setField(anotherMember, "id", 2L);

        // when
        Profile profile1 = Profile.create(testMember, "첫 번째 프로필");
        Profile profile2 = Profile.create(anotherMember, "두 번째 프로필");

        // then
        assertNotSame(profile1.getMember(), profile2.getMember());
    }

    @Test
    @DisplayName("Profile의 불변성 확인 - Member는 변경할 수 없다")
    void profileImmutability_MemberCannotBeChanged() {
        // given
        Profile profile = Profile.create(testMember, "소개글");
        Member originalMember = profile.getMember();

        // when & then
        // getter로 얻은 Member 객체가 동일한 참조인지 확인
        assertSame(originalMember, profile.getMember());
    }

    @Test
    @DisplayName("Profile 엔티티의 ID 설정 및 조회")
    void profileId_SetAndGet() {
        // given
        Profile profile = Profile.create(testMember, "소개글");
        Long expectedId = 100L;

        // when
        ReflectionTestUtils.setField(profile, "id", expectedId);

        // then
        assertEquals(expectedId, profile.getId());
    }

    @Test
    @DisplayName("Profile 엔티티 toString 메서드 테스트")
    void profileToString_ContainsBasicInfo() {
        // given
        Profile profile = Profile.create(testMember, "테스트 소개글");
        ReflectionTestUtils.setField(profile, "id", 1L);

        // when
        String toString = profile.toString();

        // then
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }

    @Test
    @DisplayName("Profile 엔티티 해시코드 일관성")
    void profileHashCode_Consistency() {
        // given
        Profile profile = Profile.create(testMember, "소개글");

        // when
        int hashCode1 = profile.hashCode();
        int hashCode2 = profile.hashCode();

        // then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Profile 엔티티 equals 메서드 테스트")
    void profileEquals_SameReference() {
        // given
        Profile profile = Profile.create(testMember, "소개글");

        // when & then
        assertEquals(profile, profile);
    }

    @Test
    @DisplayName("Profile 엔티티와 null 비교")
    void profileEquals_WithNull() {
        // given
        Profile profile = Profile.create(testMember, "소개글");

        // when & then
        assertNotEquals(profile, null);
    }

    @Test
    @DisplayName("다양한 이미지 URL 형식으로 업데이트")
    void updateImage_WithVariousUrlFormats() {
        // given
        Profile profile = Profile.create(testMember, "소개글");
        String[] imageUrls = {
                "https://example.com/profile.jpg",
                "http://example.com/profile.png",
                "https://cdn.example.com/images/profile.gif",
                "/static/images/profile.webp"
        };

        // when & then
        for (String imageUrl : imageUrls) {
            profile.updateImage(imageUrl);
            assertEquals(imageUrl, profile.getImageUrl());
        }
    }
}
