package dev.devlink.member.service.dto.response;

import dev.devlink.common.jwt.JwtToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenResponse 고급 테스트")
class JwtTokenResponseAdvancedTest {

    @Test
    @DisplayName("JwtToken으로부터 JwtTokenResponse 생성 성공")
    void fromJwtToken_Success() {
        // given
        String accessToken = "access-token-123";
        String refreshToken = "refresh-token-456";
        JwtToken jwtToken = new JwtToken(accessToken, refreshToken);

        // when
        JwtTokenResponse response = JwtTokenResponse.from(jwtToken);

        // then
        assertAll(
                () -> assertEquals(accessToken, response.getAccessToken()),
                () -> assertEquals(refreshToken, response.getRefreshToken())
        );
    }

    @Test
    @DisplayName("직접 생성자로 JwtTokenResponse 생성 성공")
    void directConstructor_Success() {
        // given
        String accessToken = "direct-access-token";
        String refreshToken = "direct-refresh-token";

        // when
        JwtTokenResponse response = new JwtTokenResponse(accessToken, refreshToken);

        // then
        assertAll(
                () -> assertEquals(accessToken, response.getAccessToken()),
                () -> assertEquals(refreshToken, response.getRefreshToken())
        );
    }

    @Test
    @DisplayName("null 토큰으로 JwtTokenResponse 생성")
    void nullTokens_Success() {
        // when
        JwtTokenResponse response = new JwtTokenResponse(null, null);

        // then
        assertAll(
                () -> assertNull(response.getAccessToken()),
                () -> assertNull(response.getRefreshToken())
        );
    }

    @Test
    @DisplayName("빈 문자열 토큰으로 JwtTokenResponse 생성")
    void emptyTokens_Success() {
        // when
        JwtTokenResponse response = new JwtTokenResponse("", "");

        // then
        assertAll(
                () -> assertEquals("", response.getAccessToken()),
                () -> assertEquals("", response.getRefreshToken())
        );
    }

    @Test
    @DisplayName("매우 긴 토큰으로 JwtTokenResponse 생성")
    void veryLongTokens_Success() {
        // given
        String longAccessToken = "very-long-access-token".repeat(10);
        String longRefreshToken = "very-long-refresh-token".repeat(10);
        JwtToken jwtToken = new JwtToken(longAccessToken, longRefreshToken);

        // when
        JwtTokenResponse response = JwtTokenResponse.from(jwtToken);

        // then
        assertAll(
                () -> assertEquals(longAccessToken, response.getAccessToken()),
                () -> assertEquals(longRefreshToken, response.getRefreshToken()),
                () -> assertTrue(response.getAccessToken().length() > 100),
                () -> assertTrue(response.getRefreshToken().length() > 100)
        );
    }

    @Test
    @DisplayName("특수문자가 포함된 토큰으로 JwtTokenResponse 생성")
    void specialCharacterTokens_Success() {
        // given
        String accessTokenWithSpecial = "access.token-with_special@chars!";
        String refreshTokenWithSpecial = "refresh.token-with_special@chars!";
        JwtToken jwtToken = new JwtToken(accessTokenWithSpecial, refreshTokenWithSpecial);

        // when
        JwtTokenResponse response = JwtTokenResponse.from(jwtToken);

        // then
        assertAll(
                () -> assertEquals(accessTokenWithSpecial, response.getAccessToken()),
                () -> assertEquals(refreshTokenWithSpecial, response.getRefreshToken())
        );
    }

    @Test
    @DisplayName("같은 토큰 값으로 생성된 JwtTokenResponse 비교")
    void sameTokenValues_Equal() {
        // given
        String accessToken = "same-access-token";
        String refreshToken = "same-refresh-token";
        JwtTokenResponse response1 = new JwtTokenResponse(accessToken, refreshToken);
        JwtTokenResponse response2 = new JwtTokenResponse(accessToken, refreshToken);

        // then
        assertAll(
                () -> assertEquals(response1.getAccessToken(), response2.getAccessToken()),
                () -> assertEquals(response1.getRefreshToken(), response2.getRefreshToken())
        );
    }

    @Test
    @DisplayName("다른 토큰 값으로 생성된 JwtTokenResponse 비교")
    void differentTokenValues_NotEqual() {
        // given
        JwtTokenResponse response1 = new JwtTokenResponse("access1", "refresh1");
        JwtTokenResponse response2 = new JwtTokenResponse("access2", "refresh2");

        // then
        assertAll(
                () -> assertNotEquals(response1.getAccessToken(), response2.getAccessToken()),
                () -> assertNotEquals(response1.getRefreshToken(), response2.getRefreshToken())
        );
    }

    @Test
    @DisplayName("정적 팩토리 메서드와 생성자의 일관성")
    void factoryMethodAndConstructor_Consistency() {
        // given
        String accessToken = "consistent-access";
        String refreshToken = "consistent-refresh";
        JwtToken jwtToken = new JwtToken(accessToken, refreshToken);

        // when
        JwtTokenResponse fromFactory = JwtTokenResponse.from(jwtToken);
        JwtTokenResponse fromConstructor = new JwtTokenResponse(accessToken, refreshToken);

        // then
        assertAll(
                () -> assertEquals(fromFactory.getAccessToken(), fromConstructor.getAccessToken()),
                () -> assertEquals(fromFactory.getRefreshToken(), fromConstructor.getRefreshToken())
        );
    }

    @Test
    @DisplayName("숫자로만 이루어진 토큰으로 JwtTokenResponse 생성")
    void numericTokens_Success() {
        // given
        String numericAccessToken = "123456789";
        String numericRefreshToken = "987654321";
        JwtToken jwtToken = new JwtToken(numericAccessToken, numericRefreshToken);

        // when
        JwtTokenResponse response = JwtTokenResponse.from(jwtToken);

        // then
        assertAll(
                () -> assertEquals(numericAccessToken, response.getAccessToken()),
                () -> assertEquals(numericRefreshToken, response.getRefreshToken())
        );
    }

    @Test
    @DisplayName("하나만 null인 토큰으로 JwtTokenResponse 생성")
    void oneNullToken_Success() {
        // given
        JwtToken jwtTokenWithNullAccess = new JwtToken(null, "refresh-token");
        JwtToken jwtTokenWithNullRefresh = new JwtToken("access-token", null);

        // when
        JwtTokenResponse responseWithNullAccess = JwtTokenResponse.from(jwtTokenWithNullAccess);
        JwtTokenResponse responseWithNullRefresh = JwtTokenResponse.from(jwtTokenWithNullRefresh);

        // then
        assertAll(
                () -> assertNull(responseWithNullAccess.getAccessToken()),
                () -> assertEquals("refresh-token", responseWithNullAccess.getRefreshToken()),
                () -> assertEquals("access-token", responseWithNullRefresh.getAccessToken()),
                () -> assertNull(responseWithNullRefresh.getRefreshToken())
        );
    }

    @Test
    @DisplayName("JwtTokenResponse toString 메서드 테스트")
    void jwtTokenResponseToString_ContainsTokenInfo() {
        // given
        JwtTokenResponse response = new JwtTokenResponse("access", "refresh");

        // when
        String toString = response.toString();

        // then
        assertAll(
                () -> assertNotNull(toString),
                () -> assertFalse(toString.isEmpty())
        );
    }
}
