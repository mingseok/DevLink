package dev.devlink.common.identity.annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthMemberId 어노테이션 테스트")
class AuthMemberIdTest {

    @Test
    @DisplayName("AuthMemberId는 어노테이션이다")
    void authMemberId_IsAnnotation() {
        // then
        assertTrue(AuthMemberId.class.isAnnotation());
    }

    @Test
    @DisplayName("AuthMemberId는 PARAMETER에만 적용 가능하다")
    void authMemberId_TargetsParameter() {
        // given
        Target target = AuthMemberId.class.getAnnotation(Target.class);

        // then
        assertAll(
                () -> assertNotNull(target),
                () -> assertEquals(1, target.value().length),
                () -> assertEquals(ElementType.PARAMETER, target.value()[0])
        );
    }

    @Test
    @DisplayName("AuthMemberId는 RUNTIME까지 유지된다")
    void authMemberId_RetainedAtRuntime() {
        // given
        Retention retention = AuthMemberId.class.getAnnotation(Retention.class);

        // then
        assertAll(
                () -> assertNotNull(retention),
                () -> assertEquals(RetentionPolicy.RUNTIME, retention.value())
        );
    }

    @Test
    @DisplayName("AuthMemberId 어노테이션에는 추가 속성이 없다")
    void authMemberId_HasNoAdditionalAttributes() {
        // given
        var methods = AuthMemberId.class.getDeclaredMethods();

        // then
        assertEquals(0, methods.length);
    }

    @Test
    @DisplayName("AuthMemberId 어노테이션은 올바른 메타 어노테이션을 가진다")
    void authMemberId_HasCorrectMetaAnnotations() {
        // given
        var annotations = AuthMemberId.class.getDeclaredAnnotations();

        // then
        assertEquals(2, annotations.length); // @Target, @Retention
    }

    @Test
    @DisplayName("AuthMemberId를 파라미터에 적용할 수 있다")
    void authMemberId_CanBeAppliedToParameter() throws NoSuchMethodException {
        // given
        var method = TestClass.class.getDeclaredMethod("testMethod", Long.class);
        var parameter = method.getParameters()[0];

        // when
        AuthMemberId annotation = parameter.getAnnotation(AuthMemberId.class);

        // then
        assertNotNull(annotation);
    }

    @Test
    @DisplayName("AuthMemberId 어노테이션의 기본값들이 올바르다")
    void authMemberId_HasCorrectDefaults() {
        // given
        AuthMemberId annotation = TestClass.class
                .getDeclaredMethods()[0]
                .getParameters()[0]
                .getAnnotation(AuthMemberId.class);

        // then
        assertNotNull(annotation);
        // 기본값이 없으므로 단순히 존재 확인
    }

    // 테스트용 클래스
    private static class TestClass {
        public void testMethod(@AuthMemberId Long memberId) {
            // 테스트용 메서드
        }
    }
}
