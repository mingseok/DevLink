package dev.devlink.common.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenProvider 인터페이스 테스트")
class TokenProviderTest {

    @Test
    @DisplayName("TokenProvider 인터페이스 메서드 정의 확인")
    void tokenProviderInterface_HasCorrectMethods() throws NoSuchMethodException {
        // given
        Class<TokenProvider> providerClass = TokenProvider.class;

        // when & then
        assertAll(
                () -> assertNotNull(providerClass.getDeclaredMethod("generateToken", Long.class)),
                () -> assertNotNull(providerClass.getDeclaredMethod("validateToken", String.class)),
                () -> assertNotNull(providerClass.getDeclaredMethod("extractMemberId", String.class))
        );
    }

    @Test
    @DisplayName("TokenProvider 메서드 반환 타입 확인")
    void tokenProviderInterface_HasCorrectReturnTypes() throws NoSuchMethodException {
        // given
        Class<TokenProvider> providerClass = TokenProvider.class;

        // when & then
        assertAll(
                () -> assertEquals(JwtToken.class, 
                        providerClass.getDeclaredMethod("generateToken", Long.class).getReturnType()),
                () -> assertEquals(boolean.class, 
                        providerClass.getDeclaredMethod("validateToken", String.class).getReturnType()),
                () -> assertEquals(Long.class, 
                        providerClass.getDeclaredMethod("extractMemberId", String.class).getReturnType())
        );
    }

    @Test
    @DisplayName("TokenProvider는 인터페이스이다")
    void tokenProvider_IsInterface() {
        // then
        assertTrue(TokenProvider.class.isInterface());
    }

    @Test
    @DisplayName("TokenProvider 메서드 파라미터 타입 확인")
    void tokenProviderInterface_HasCorrectParameterTypes() throws NoSuchMethodException {
        // given
        Class<TokenProvider> providerClass = TokenProvider.class;
        var generateTokenMethod = providerClass.getDeclaredMethod("generateToken", Long.class);
        var validateTokenMethod = providerClass.getDeclaredMethod("validateToken", String.class);
        var extractMemberIdMethod = providerClass.getDeclaredMethod("extractMemberId", String.class);

        // when & then
        assertAll(
                () -> assertEquals(1, generateTokenMethod.getParameterCount()),
                () -> assertEquals(Long.class, generateTokenMethod.getParameterTypes()[0]),
                
                () -> assertEquals(1, validateTokenMethod.getParameterCount()),
                () -> assertEquals(String.class, validateTokenMethod.getParameterTypes()[0]),
                
                () -> assertEquals(1, extractMemberIdMethod.getParameterCount()),
                () -> assertEquals(String.class, extractMemberIdMethod.getParameterTypes()[0])
        );
    }

    @Test
    @DisplayName("TokenProvider 메서드들이 public abstract이다")
    void tokenProviderMethods_ArePublicAbstract() throws NoSuchMethodException {
        // given
        Class<TokenProvider> providerClass = TokenProvider.class;
        var generateTokenMethod = providerClass.getDeclaredMethod("generateToken", Long.class);
        var validateTokenMethod = providerClass.getDeclaredMethod("validateToken", String.class);
        var extractMemberIdMethod = providerClass.getDeclaredMethod("extractMemberId", String.class);

        // when & then
        assertAll(
                () -> assertTrue(java.lang.reflect.Modifier.isPublic(generateTokenMethod.getModifiers())),
                () -> assertTrue(java.lang.reflect.Modifier.isAbstract(generateTokenMethod.getModifiers())),
                
                () -> assertTrue(java.lang.reflect.Modifier.isPublic(validateTokenMethod.getModifiers())),
                () -> assertTrue(java.lang.reflect.Modifier.isAbstract(validateTokenMethod.getModifiers())),
                
                () -> assertTrue(java.lang.reflect.Modifier.isPublic(extractMemberIdMethod.getModifiers())),
                () -> assertTrue(java.lang.reflect.Modifier.isAbstract(extractMemberIdMethod.getModifiers()))
        );
    }

    @Test
    @DisplayName("TokenProvider 인터페이스는 정확히 3개의 메서드를 가진다")
    void tokenProviderInterface_HasExactlyThreeMethods() {
        // given
        Class<TokenProvider> providerClass = TokenProvider.class;

        // when
        int methodCount = providerClass.getDeclaredMethods().length;

        // then
        assertEquals(3, methodCount);
    }
}
