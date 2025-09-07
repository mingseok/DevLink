package dev.devlink.follow.service.dto.response;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FollowResponse 추가 테스트")
class FollowResponseAdvancedTest {

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.create("테스트유저", "test@example.com", "테스트닉네임", "password");
        ReflectionTestUtils.setField(testMember, "id", 1L);
        ReflectionTestUtils.setField(testMember, "createdAt", LocalDateTime.of(2024, 1, 1, 12, 0, 0));
    }

    @Test
    @DisplayName("from(Member) 정적 팩토리 메서드 - 기본 isFollowing false")
    void fromMember_DefaultFollowingFalse_Success() {
        // when
        FollowResponse response = FollowResponse.from(testMember);

        // then
        assertAll(
                () -> assertEquals(1L, response.getMemberId()),
                () -> assertEquals("테스트닉네임", response.getNickname()),
                () -> assertNotNull(response.getJoinedAt()),
                () -> assertFalse(response.getIsFollowing())
        );
    }

    @Test
    @DisplayName("from(Member, boolean) 정적 팩토리 메서드 - isFollowing true")
    void fromMemberWithFollowing_True_Success() {
        // when
        FollowResponse response = FollowResponse.from(testMember, true);

        // then
        assertAll(
                () -> assertEquals(1L, response.getMemberId()),
                () -> assertEquals("테스트닉네임", response.getNickname()),
                () -> assertNotNull(response.getJoinedAt()),
                () -> assertTrue(response.getIsFollowing())
        );
    }

    @Test
    @DisplayName("from(Member, boolean) 정적 팩토리 메서드 - isFollowing false")
    void fromMemberWithFollowing_False_Success() {
        // when
        FollowResponse response = FollowResponse.from(testMember, false);

        // then
        assertAll(
                () -> assertEquals(1L, response.getMemberId()),
                () -> assertEquals("테스트닉네임", response.getNickname()),
                () -> assertNotNull(response.getJoinedAt()),
                () -> assertFalse(response.getIsFollowing())
        );
    }

    @Test
    @DisplayName("생성자를 통한 직접 객체 생성")
    void constructorCreation_Success() {
        // given
        Long memberId = 999L;
        String nickname = "직접생성닉네임";
        String joinedAt = "2024-01-01";
        Boolean isFollowing = true;

        // when
        FollowResponse response = new FollowResponse(memberId, nickname, joinedAt, isFollowing);

        // then
        assertAll(
                () -> assertEquals(memberId, response.getMemberId()),
                () -> assertEquals(nickname, response.getNickname()),
                () -> assertEquals(joinedAt, response.getJoinedAt()),
                () -> assertEquals(isFollowing, response.getIsFollowing())
        );
    }

    @Test
    @DisplayName("null 값들로 생성된 FollowResponse")
    void nullValues_Success() {
        // when
        FollowResponse response = new FollowResponse(null, null, null, null);

        // then
        assertAll(
                () -> assertNull(response.getMemberId()),
                () -> assertNull(response.getNickname()),
                () -> assertNull(response.getJoinedAt()),
                () -> assertNull(response.getIsFollowing())
        );
    }

    @Test
    @DisplayName("두 from 메서드의 일관성 확인")
    void fromMethodsConsistency_Success() {
        // when
        FollowResponse response1 = FollowResponse.from(testMember);
        FollowResponse response2 = FollowResponse.from(testMember, false);

        // then
        assertAll(
                () -> assertEquals(response1.getMemberId(), response2.getMemberId()),
                () -> assertEquals(response1.getNickname(), response2.getNickname()),
                () -> assertEquals(response1.getJoinedAt(), response2.getJoinedAt()),
                () -> assertEquals(response1.getIsFollowing(), response2.getIsFollowing())
        );
    }

    @Test
    @DisplayName("다른 멤버로 생성된 FollowResponse들은 다른 값을 가진다")
    void differentMembers_ProduceDifferentResponses() {
        // given
        Member anotherMember = Member.create("다른유저", "another@example.com", "다른닉네임", "password");
        ReflectionTestUtils.setField(anotherMember, "id", 2L);
        ReflectionTestUtils.setField(anotherMember, "createdAt", LocalDateTime.of(2024, 2, 1, 12, 0, 0));

        // when
        FollowResponse response1 = FollowResponse.from(testMember);
        FollowResponse response2 = FollowResponse.from(anotherMember);

        // then
        assertAll(
                () -> assertNotEquals(response1.getMemberId(), response2.getMemberId()),
                () -> assertNotEquals(response1.getNickname(), response2.getNickname())
        );
    }

    @Test
    @DisplayName("긴 닉네임을 가진 멤버의 FollowResponse 생성")
    void longNickname_Success() {
        // given
        String longNickname = "매우긴닉네임".repeat(10); // 50글자
        Member memberWithLongNickname = Member.create("유저", "user@example.com", longNickname, "password");
        ReflectionTestUtils.setField(memberWithLongNickname, "id", 3L);
        ReflectionTestUtils.setField(memberWithLongNickname, "createdAt", LocalDateTime.now());

        // when
        FollowResponse response = FollowResponse.from(memberWithLongNickname);

        // then
        assertAll(
                () -> assertEquals(3L, response.getMemberId()),
                () -> assertEquals(longNickname, response.getNickname()),
                () -> assertFalse(response.getIsFollowing())
        );
    }

    @Test
    @DisplayName("특수문자가 포함된 닉네임의 FollowResponse 생성")
    void specialCharacterNickname_Success() {
        // given
        String specialNickname = "테스트@#$%^&*()_+닉네임";
        Member memberWithSpecialNickname = Member.create("유저", "user@example.com", specialNickname, "password");
        ReflectionTestUtils.setField(memberWithSpecialNickname, "id", 4L);
        ReflectionTestUtils.setField(memberWithSpecialNickname, "createdAt", LocalDateTime.now());

        // when
        FollowResponse response = FollowResponse.from(memberWithSpecialNickname);

        // then
        assertAll(
                () -> assertEquals(4L, response.getMemberId()),
                () -> assertEquals(specialNickname, response.getNickname()),
                () -> assertFalse(response.getIsFollowing())
        );
    }

    @Test
    @DisplayName("joinedAt 날짜 형식 확인")
    void joinedAtFormat_IsNotEmpty() {
        // when
        FollowResponse response = FollowResponse.from(testMember);

        // then
        assertAll(
                () -> assertNotNull(response.getJoinedAt()),
                () -> assertFalse(response.getJoinedAt().isEmpty()),
                () -> assertFalse(response.getJoinedAt().isBlank())
        );
    }
}
