package dev.devlink.member.exception;

import dev.devlink.common.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberException 테스트")
class MemberExceptionTest {

    @Test
    @DisplayName("MemberError를 사용하여 MemberException을 생성한다")
    void createMemberException_WithMemberError_Success() {
        // given
        MemberError error = MemberError.MEMBER_NOT_FOUND;

        // when
        MemberException exception = new MemberException(error);

        // then
        assertAll(
                () -> assertEquals(error, exception.getCommonError()),
                () -> assertEquals(error.getMessage(), exception.getMessage())
        );
    }

    @Test
    @DisplayName("각각의 MemberError에 대해 올바른 예외가 생성된다")
    void createExceptionForEachMemberError_Success() {
        // MEMBER_NOT_FOUND
        MemberException memberNotFoundException = new MemberException(MemberError.MEMBER_NOT_FOUND);
        assertAll(
                () -> assertEquals("존재하지 않는 회원입니다.", memberNotFoundException.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, memberNotFoundException.getCommonError().getHttpStatus()),
                () -> assertEquals("40401", memberNotFoundException.getCommonError().getCode())
        );

        // EMAIL_NOT_FOUND
        MemberException emailNotFoundException = new MemberException(MemberError.EMAIL_NOT_FOUND);
        assertAll(
                () -> assertEquals("존재하지 않는 회원 이메일입니다.", emailNotFoundException.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, emailNotFoundException.getCommonError().getHttpStatus()),
                () -> assertEquals("40402", emailNotFoundException.getCommonError().getCode())
        );

        // EMAIL_DUPLICATED
        MemberException emailDuplicatedException = new MemberException(MemberError.EMAIL_DUPLICATED);
        assertAll(
                () -> assertEquals("이미 등록된 이메일입니다.", emailDuplicatedException.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, emailDuplicatedException.getCommonError().getHttpStatus()),
                () -> assertEquals("40003", emailDuplicatedException.getCommonError().getCode())
        );

        // NICKNAME_DUPLICATED
        MemberException nicknameDuplicatedException = new MemberException(MemberError.NICKNAME_DUPLICATED);
        assertAll(
                () -> assertEquals("이미 사용 중인 닉네임입니다.", nicknameDuplicatedException.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, nicknameDuplicatedException.getCommonError().getHttpStatus()),
                () -> assertEquals("40004", nicknameDuplicatedException.getCommonError().getCode())
        );

        // PASSWORD_NOT_MATCHED
        MemberException passwordNotMatchedException = new MemberException(MemberError.PASSWORD_NOT_MATCHED);
        assertAll(
                () -> assertEquals("비밀번호가 일치하지 않습니다.", passwordNotMatchedException.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, passwordNotMatchedException.getCommonError().getHttpStatus()),
                () -> assertEquals("40005", passwordNotMatchedException.getCommonError().getCode())
        );

        // UNAUTHORIZED_ACCESS
        MemberException unauthorizedException = new MemberException(MemberError.UNAUTHORIZED_ACCESS);
        assertAll(
                () -> assertEquals("접근 권한이 없습니다.", unauthorizedException.getMessage()),
                () -> assertEquals(HttpStatus.FORBIDDEN, unauthorizedException.getCommonError().getHttpStatus()),
                () -> assertEquals("40301", unauthorizedException.getCommonError().getCode())
        );
    }

    @Test
    @DisplayName("MemberException이 ServiceException을 상속한다")
    void memberException_ExtendsServiceException() {
        // given
        MemberException exception = new MemberException(MemberError.MEMBER_NOT_FOUND);

        // then
        assertInstanceOf(ServiceException.class, exception);
    }

    @Test
    @DisplayName("MemberException이 RuntimeException을 상속한다")
    void memberException_ExtendsRuntimeException() {
        // given
        MemberException exception = new MemberException(MemberError.MEMBER_NOT_FOUND);

        // then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("예외 스택 트레이스가 올바르게 설정된다")
    void memberException_HasCorrectStackTrace() {
        // given
        MemberError error = MemberError.EMAIL_DUPLICATED;

        // when
        MemberException exception = new MemberException(error);

        // then
        assertAll(
                () -> assertNotNull(exception.getStackTrace()),
                () -> assertTrue(exception.getStackTrace().length > 0)
        );
    }

    @Test
    @DisplayName("다른 MemberError로 생성된 예외들은 서로 다른 메시지를 가진다")
    void differentMemberErrors_ProduceDifferentExceptions() {
        // given
        MemberException exception1 = new MemberException(MemberError.MEMBER_NOT_FOUND);
        MemberException exception2 = new MemberException(MemberError.EMAIL_DUPLICATED);
        MemberException exception3 = new MemberException(MemberError.NICKNAME_DUPLICATED);

        // when & then
        assertAll(
                () -> assertNotEquals(exception1.getMessage(), exception2.getMessage()),
                () -> assertNotEquals(exception2.getMessage(), exception3.getMessage()),
                () -> assertNotEquals(exception1.getMessage(), exception3.getMessage())
        );
    }

    @Test
    @DisplayName("예외 생성 시 원인이 설정되지 않는다")
    void memberException_HasNoCause() {
        // given
        MemberException exception = new MemberException(MemberError.MEMBER_NOT_FOUND);

        // then
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("동일한 MemberError로 생성된 예외들은 동일한 메시지를 가진다")
    void sameMemberError_ProducesSameMessage() {
        // given
        MemberError error = MemberError.PASSWORD_NOT_MATCHED;
        MemberException exception1 = new MemberException(error);
        MemberException exception2 = new MemberException(error);

        // then
        assertEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    @DisplayName("모든 MemberError로 예외 생성 가능")
    void allMemberErrors_CanCreateExceptions() {
        // given
        MemberError[] allErrors = MemberError.values();

        // when & then
        for (MemberError error : allErrors) {
            assertDoesNotThrow(() -> {
                MemberException exception = new MemberException(error);
                assertNotNull(exception);
                assertNotNull(exception.getMessage());
                assertEquals(error, exception.getCommonError());
            });
        }
    }
}
