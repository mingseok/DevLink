package dev.devlink.member.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberError 테스트")
class MemberErrorTest {

    @Test
    @DisplayName("MEMBER_NOT_FOUND 에러 정보 확인")
    void memberNotFoundError_HasCorrectInfo() {
        // when
        MemberError error = MemberError.MEMBER_NOT_FOUND;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, error.getHttpStatus()),
                () -> assertEquals("40401", error.getCode()),
                () -> assertEquals("존재하지 않는 회원입니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("EMAIL_NOT_FOUND 에러 정보 확인")
    void emailNotFoundError_HasCorrectInfo() {
        // when
        MemberError error = MemberError.EMAIL_NOT_FOUND;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, error.getHttpStatus()),
                () -> assertEquals("40402", error.getCode()),
                () -> assertEquals("존재하지 않는 회원 이메일입니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("EMAIL_DUPLICATED 에러 정보 확인")
    void emailDuplicatedError_HasCorrectInfo() {
        // when
        MemberError error = MemberError.EMAIL_DUPLICATED;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus()),
                () -> assertEquals("40003", error.getCode()),
                () -> assertEquals("이미 등록된 이메일입니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("NICKNAME_DUPLICATED 에러 정보 확인")
    void nicknameDuplicatedError_HasCorrectInfo() {
        // when
        MemberError error = MemberError.NICKNAME_DUPLICATED;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus()),
                () -> assertEquals("40004", error.getCode()),
                () -> assertEquals("이미 사용 중인 닉네임입니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("PASSWORD_NOT_MATCHED 에러 정보 확인")
    void passwordNotMatchedError_HasCorrectInfo() {
        // when
        MemberError error = MemberError.PASSWORD_NOT_MATCHED;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus()),
                () -> assertEquals("40005", error.getCode()),
                () -> assertEquals("비밀번호가 일치하지 않습니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("UNAUTHORIZED_ACCESS 에러 정보 확인")
    void unauthorizedAccessError_HasCorrectInfo() {
        // when
        MemberError error = MemberError.UNAUTHORIZED_ACCESS;

        // then
        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, error.getHttpStatus()),
                () -> assertEquals("40301", error.getCode()),
                () -> assertEquals("접근 권한이 없습니다.", error.getMessage())
        );
    }

    @Test
    @DisplayName("모든 MemberError enum 값 개수 확인")
    void memberErrorEnum_HasCorrectCount() {
        // when
        MemberError[] errors = MemberError.values();

        // then
        assertEquals(6, errors.length);
    }

    @Test
    @DisplayName("MemberError enum valueOf 테스트")
    void memberErrorValueOf_Success() {
        // when & then
        assertAll(
                () -> assertEquals(MemberError.MEMBER_NOT_FOUND, MemberError.valueOf("MEMBER_NOT_FOUND")),
                () -> assertEquals(MemberError.EMAIL_NOT_FOUND, MemberError.valueOf("EMAIL_NOT_FOUND")),
                () -> assertEquals(MemberError.EMAIL_DUPLICATED, MemberError.valueOf("EMAIL_DUPLICATED")),
                () -> assertEquals(MemberError.NICKNAME_DUPLICATED, MemberError.valueOf("NICKNAME_DUPLICATED")),
                () -> assertEquals(MemberError.PASSWORD_NOT_MATCHED, MemberError.valueOf("PASSWORD_NOT_MATCHED")),
                () -> assertEquals(MemberError.UNAUTHORIZED_ACCESS, MemberError.valueOf("UNAUTHORIZED_ACCESS"))
        );
    }

    @Test
    @DisplayName("모든 에러 코드가 고유하다")
    void allErrorCodes_AreUnique() {
        // given
        MemberError[] errors = MemberError.values();

        // when & then
        for (int i = 0; i < errors.length; i++) {
            for (int j = i + 1; j < errors.length; j++) {
                assertNotEquals(errors[i].getCode(), errors[j].getCode(),
                        String.format("Error codes should be unique: %s and %s have same code",
                                errors[i], errors[j]));
            }
        }
    }

    @Test
    @DisplayName("모든 에러 메시지가 null이 아니고 비어있지 않다")
    void allErrorMessages_AreNotNullOrEmpty() {
        // given
        MemberError[] errors = MemberError.values();

        // when & then
        for (MemberError error : errors) {
            assertAll(
                    () -> assertNotNull(error.getMessage()),
                    () -> assertFalse(error.getMessage().isEmpty()),
                    () -> assertFalse(error.getMessage().isBlank())
            );
        }
    }

    @Test
    @DisplayName("모든 에러 코드가 null이 아니고 비어있지 않다")
    void allErrorCodes_AreNotNullOrEmpty() {
        // given
        MemberError[] errors = MemberError.values();

        // when & then
        for (MemberError error : errors) {
            assertAll(
                    () -> assertNotNull(error.getCode()),
                    () -> assertFalse(error.getCode().isEmpty()),
                    () -> assertFalse(error.getCode().isBlank())
            );
        }
    }

    @Test
    @DisplayName("모든 HTTP 상태가 null이 아니다")
    void allHttpStatuses_AreNotNull() {
        // given
        MemberError[] errors = MemberError.values();

        // when & then
        for (MemberError error : errors) {
            assertNotNull(error.getHttpStatus());
        }
    }

    @Test
    @DisplayName("BAD_REQUEST 상태를 가진 에러들 확인")
    void badRequestErrors_AreIdentified() {
        // when
        MemberError[] badRequestErrors = {
                MemberError.EMAIL_DUPLICATED,
                MemberError.NICKNAME_DUPLICATED,
                MemberError.PASSWORD_NOT_MATCHED
        };

        // then
        for (MemberError error : badRequestErrors) {
            assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus());
        }
    }

    @Test
    @DisplayName("NOT_FOUND 상태를 가진 에러들 확인")
    void notFoundErrors_AreIdentified() {
        // when
        MemberError[] notFoundErrors = {
                MemberError.MEMBER_NOT_FOUND,
                MemberError.EMAIL_NOT_FOUND
        };

        // then
        for (MemberError error : notFoundErrors) {
            assertEquals(HttpStatus.NOT_FOUND, error.getHttpStatus());
        }
    }

    @Test
    @DisplayName("FORBIDDEN 상태를 가진 에러들 확인")
    void forbiddenErrors_AreIdentified() {
        // when
        MemberError error = MemberError.UNAUTHORIZED_ACCESS;

        // then
        assertEquals(HttpStatus.FORBIDDEN, error.getHttpStatus());
    }
}
