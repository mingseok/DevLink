package dev.devlink.member.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberRole 테스트")
class MemberRoleTest {

    @Test
    @DisplayName("MemberRole enum 값 개수 확인")
    void memberRoleEnum_HasCorrectCount() {
        // when
        MemberRole[] roles = MemberRole.values();

        // then
        assertEquals(2, roles.length);
    }

    @Test
    @DisplayName("USER 역할 확인")
    void userRole_IsCorrect() {
        // when
        MemberRole userRole = MemberRole.USER;

        // then
        assertAll(
                () -> assertNotNull(userRole),
                () -> assertEquals("USER", userRole.name()),
                () -> assertEquals(0, userRole.ordinal())
        );
    }

    @Test
    @DisplayName("ADMIN 역할 확인")
    void adminRole_IsCorrect() {
        // when
        MemberRole adminRole = MemberRole.ADMIN;

        // then
        assertAll(
                () -> assertNotNull(adminRole),
                () -> assertEquals("ADMIN", adminRole.name()),
                () -> assertEquals(1, adminRole.ordinal())
        );
    }

    @Test
    @DisplayName("MemberRole valueOf 테스트")
    void memberRoleValueOf_Success() {
        // when & then
        assertAll(
                () -> assertEquals(MemberRole.USER, MemberRole.valueOf("USER")),
                () -> assertEquals(MemberRole.ADMIN, MemberRole.valueOf("ADMIN"))
        );
    }

    @Test
    @DisplayName("존재하지 않는 역할로 valueOf 호출시 예외 발생")
    void memberRoleValueOf_WithInvalidRole_ThrowsException() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> 
                MemberRole.valueOf("INVALID_ROLE"));
    }

    @Test
    @DisplayName("MemberRole 비교 테스트")
    void memberRoleComparison_Success() {
        // given
        MemberRole user1 = MemberRole.USER;
        MemberRole user2 = MemberRole.USER;
        MemberRole admin = MemberRole.ADMIN;

        // when & then
        assertAll(
                () -> assertEquals(user1, user2),
                () -> assertNotEquals(user1, admin),
                () -> assertSame(user1, user2), // enum은 싱글톤
                () -> assertNotSame(user1, admin)
        );
    }

    @Test
    @DisplayName("MemberRole toString 테스트")
    void memberRoleToString_ReturnsName() {
        // when & then
        assertAll(
                () -> assertEquals("USER", MemberRole.USER.toString()),
                () -> assertEquals("ADMIN", MemberRole.ADMIN.toString())
        );
    }

    @Test
    @DisplayName("MemberRole ordinal 순서 확인")
    void memberRoleOrdinal_CorrectOrder() {
        // when & then
        assertAll(
                () -> assertTrue(MemberRole.USER.ordinal() < MemberRole.ADMIN.ordinal()),
                () -> assertEquals(0, MemberRole.USER.ordinal()),
                () -> assertEquals(1, MemberRole.ADMIN.ordinal())
        );
    }

    @Test
    @DisplayName("MemberRole switch 문 테스트")
    void memberRoleSwitch_WorksCorrectly() {
        // given & when & then
        for (MemberRole role : MemberRole.values()) {
            switch (role) {
                case USER:
                    assertEquals(MemberRole.USER, role);
                    break;
                case ADMIN:
                    assertEquals(MemberRole.ADMIN, role);
                    break;
                default:
                    fail("Unknown role: " + role);
            }
        }
    }

    @Test
    @DisplayName("MemberRole compareTo 테스트")
    void memberRoleCompareTo_Success() {
        // given
        MemberRole user = MemberRole.USER;
        MemberRole admin = MemberRole.ADMIN;

        // when & then
        assertAll(
                () -> assertTrue(user.compareTo(admin) < 0), // USER가 ADMIN보다 앞서 정의됨
                () -> assertTrue(admin.compareTo(user) > 0),
                () -> assertEquals(0, user.compareTo(MemberRole.USER))
        );
    }

    @Test
    @DisplayName("모든 MemberRole 값 순회 테스트")
    void allMemberRoleValues_CanBeIterated() {
        // given
        MemberRole[] allRoles = MemberRole.values();
        boolean foundUser = false;
        boolean foundAdmin = false;

        // when
        for (MemberRole role : allRoles) {
            if (role == MemberRole.USER) foundUser = true;
            if (role == MemberRole.ADMIN) foundAdmin = true;
        }

        // then
        assertAll(
                () -> assertTrue(foundUser),
                () -> assertTrue(foundAdmin),
                () -> assertEquals(2, allRoles.length)
        );
    }

    @Test
    @DisplayName("MemberRole과 null 비교")
    void memberRoleWithNull_NotEqual() {
        // given
        MemberRole user = MemberRole.USER;

        // then
        assertNotEquals(user, null);
    }
}
