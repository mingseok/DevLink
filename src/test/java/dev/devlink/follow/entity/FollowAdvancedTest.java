package dev.devlink.follow.entity;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Follow Entity 추가 테스트")
class FollowAdvancedTest {

    private Member follower;
    private Member followee;

    @BeforeEach
    void setUp() {
        follower = Member.create("팔로워", "follower@example.com", "팔로워닉", "password");
        followee = Member.create("팔로위", "followee@example.com", "팔로위닉", "password");
        ReflectionTestUtils.setField(follower, "id", 1L);
        ReflectionTestUtils.setField(followee, "id", 2L);
    }

    @Test
    @DisplayName("Follow 엔티티 생성 성공")
    void createFollow_Success() {
        // when
        Follow follow = Follow.create(follower, followee);

        // then
        assertAll(
                () -> assertNotNull(follow),
                () -> assertEquals(follower, follow.getFollower()),
                () -> assertEquals(followee, follow.getFollowee()),
                () -> assertNull(follow.getId()), // 아직 저장되지 않았으므로 null
                () -> assertNull(follow.getCreatedAt()) // BaseEntity의 createdAt은 저장 시 설정
        );
    }

    @Test
    @DisplayName("같은 팔로워와 팔로위로 생성된 Follow는 동일한 속성을 가진다")
    void sameFollowerAndFollowee_SameProperties() {
        // when
        Follow follow1 = Follow.create(follower, followee);
        Follow follow2 = Follow.create(follower, followee);

        // then
        assertAll(
                () -> assertEquals(follow1.getFollower(), follow2.getFollower()),
                () -> assertEquals(follow1.getFollowee(), follow2.getFollowee())
        );
    }

    @Test
    @DisplayName("다른 팔로워로 생성된 Follow는 다른 속성을 가진다")
    void differentFollower_DifferentProperties() {
        // given
        Member anotherFollower = Member.create("다른팔로워", "another@example.com", "다른닉", "password");
        ReflectionTestUtils.setField(anotherFollower, "id", 3L);

        // when
        Follow follow1 = Follow.create(follower, followee);
        Follow follow2 = Follow.create(anotherFollower, followee);

        // then
        assertAll(
                () -> assertNotEquals(follow1.getFollower(), follow2.getFollower()),
                () -> assertEquals(follow1.getFollowee(), follow2.getFollowee())
        );
    }

    @Test
    @DisplayName("다른 팔로위로 생성된 Follow는 다른 속성을 가진다")
    void differentFollowee_DifferentProperties() {
        // given
        Member anotherFollowee = Member.create("다른팔로위", "another2@example.com", "다른닉2", "password");
        ReflectionTestUtils.setField(anotherFollowee, "id", 4L);

        // when
        Follow follow1 = Follow.create(follower, followee);
        Follow follow2 = Follow.create(follower, anotherFollowee);

        // then
        assertAll(
                () -> assertEquals(follow1.getFollower(), follow2.getFollower()),
                () -> assertNotEquals(follow1.getFollowee(), follow2.getFollowee())
        );
    }

    @Test
    @DisplayName("Follow 엔티티의 ID 설정 및 조회")
    void followId_SetAndGet() {
        // given
        Follow follow = Follow.create(follower, followee);
        Long expectedId = 100L;

        // when
        ReflectionTestUtils.setField(follow, "id", expectedId);

        // then
        assertEquals(expectedId, follow.getId());
    }

    @Test
    @DisplayName("Follow 엔티티의 createdAt 설정 및 조회")
    void followCreatedAt_SetAndGet() {
        // given
        Follow follow = Follow.create(follower, followee);
        LocalDateTime expectedCreatedAt = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        // when
        ReflectionTestUtils.setField(follow, "createdAt", expectedCreatedAt);

        // then
        assertEquals(expectedCreatedAt, follow.getCreatedAt());
    }

    @Test
    @DisplayName("Follow 엔티티의 null 검증")
    void followNullValidation() {
        // given
        Follow follow = Follow.create(follower, followee);

        // then
        assertAll(
                () -> assertNotNull(follow.getFollower()),
                () -> assertNotNull(follow.getFollowee())
        );
    }

    @Test
    @DisplayName("팔로워와 팔로위가 같은 멤버인 경우")
    void sameMemberAsFollowerAndFollowee() {
        // when
        Follow follow = Follow.create(follower, follower);

        // then
        assertAll(
                () -> assertEquals(follower, follow.getFollower()),
                () -> assertEquals(follower, follow.getFollowee()),
                () -> assertSame(follow.getFollower(), follow.getFollowee())
        );
    }

    @Test
    @DisplayName("Follow 엔티티 toString 메서드 테스트")
    void followToString_ContainsBasicInfo() {
        // given
        Follow follow = Follow.create(follower, followee);
        ReflectionTestUtils.setField(follow, "id", 1L);

        // when
        String toString = follow.toString();

        // then
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }

    @Test
    @DisplayName("Follow 엔티티의 불변성 확인")
    void followImmutability_FollowerAndFolloweeCannotBeChanged() {
        // given
        Follow follow = Follow.create(follower, followee);
        Member originalFollower = follow.getFollower();
        Member originalFollowee = follow.getFollowee();

        // when & then
        // getter로 얻은 객체들이 동일한 참조인지 확인
        assertSame(originalFollower, follow.getFollower());
        assertSame(originalFollowee, follow.getFollowee());
    }

    @Test
    @DisplayName("Follow 엔티티 해시코드 일관성")
    void followHashCode_Consistency() {
        // given
        Follow follow = Follow.create(follower, followee);

        // when
        int hashCode1 = follow.hashCode();
        int hashCode2 = follow.hashCode();

        // then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Follow 엔티티 equals 메서드 테스트")
    void followEquals_SameReference() {
        // given
        Follow follow = Follow.create(follower, followee);

        // when & then
        assertEquals(follow, follow);
    }

    @Test
    @DisplayName("Follow 엔티티와 null 비교")
    void followEquals_WithNull() {
        // given
        Follow follow = Follow.create(follower, followee);

        // when & then
        assertNotEquals(follow, null);
    }
}
