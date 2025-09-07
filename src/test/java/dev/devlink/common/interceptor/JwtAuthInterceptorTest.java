package dev.devlink.common.interceptor;

import dev.devlink.common.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthInterceptor 테스트")
class JwtAuthInterceptorTest {

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private JwtAuthInterceptor jwtAuthInterceptor;

    @BeforeEach
    void setUp() {
        jwtAuthInterceptor = new JwtAuthInterceptor(tokenProvider);
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증 성공")
    void preHandle_ValidToken_Success() throws Exception {
        // given
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        Long memberId = 1L;
        
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(tokenProvider.validateToken(token)).willReturn(true);
        given(tokenProvider.extractMemberId(token)).willReturn(memberId);

        // when
        boolean result = jwtAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result);
        verify(request).setAttribute("memberId", memberId);
        verifyNoInteractions(response);
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 인증 실패")
    void preHandle_NoAuthHeader_Fails() throws Exception {
        // given
        given(request.getHeader("Authorization")).willReturn(null);

        // when
        boolean result = jwtAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
        verifyNoInteractions(tokenProvider);
    }

    @Test
    @DisplayName("Bearer로 시작하지 않는 Authorization 헤더면 인증 실패")
    void preHandle_InvalidAuthHeaderFormat_Fails() throws Exception {
        // given
        given(request.getHeader("Authorization")).willReturn("Basic sometoken");

        // when
        boolean result = jwtAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
        verifyNoInteractions(tokenProvider);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰으로 인증 실패")
    void preHandle_InvalidToken_Fails() throws Exception {
        // given
        String token = "invalid.jwt.token";
        String authHeader = "Bearer " + token;
        
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(tokenProvider.validateToken(token)).willReturn(false);

        // when
        boolean result = jwtAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
        verify(tokenProvider).validateToken(token);
        verify(tokenProvider, never()).extractMemberId(anyString());
    }

    @Test
    @DisplayName("빈 Bearer 토큰으로 인증 실패")
    void preHandle_EmptyBearerToken_Fails() throws Exception {
        // given
        given(request.getHeader("Authorization")).willReturn("Bearer ");

        // when
        boolean result = jwtAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
        verifyNoInteractions(tokenProvider);
    }

    @Test
    @DisplayName("Bearer만 있고 토큰이 없으면 인증 실패")
    void preHandle_BearerOnly_Fails() throws Exception {
        // given
        given(request.getHeader("Authorization")).willReturn("Bearer");

        // when
        boolean result = jwtAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
        verifyNoInteractions(tokenProvider);
    }

    @Test
    @DisplayName("토큰 검증은 성공하지만 memberId 추출 실패")
    void preHandle_ValidTokenButExtractionFails() throws Exception {
        // given
        String token = "valid.but.problematic.token";
        String authHeader = "Bearer " + token;
        
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(tokenProvider.validateToken(token)).willReturn(true);
        given(tokenProvider.extractMemberId(token)).willReturn(null);

        // when
        boolean result = jwtAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result); // 검증은 통과하지만 memberId가 null로 설정됨
        verify(request).setAttribute("memberId", null);
        verifyNoInteractions(response);
    }

    @Test
    @DisplayName("대소문자 구분하여 Bearer 처리")
    void preHandle_CaseSensitiveBearer_Fails() throws Exception {
        // given
        String token = "valid.jwt.token";
        String authHeader = "bearer " + token; // 소문자
        
        given(request.getHeader("Authorization")).willReturn(authHeader);

        // when
        boolean result = jwtAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result); // 소문자 bearer는 인식하지 않음
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
    }

    @Test
    @DisplayName("토큰 검증 중 예외 발생 시 인증 실패")
    void preHandle_TokenValidationThrowsException_Fails() throws Exception {
        // given
        String token = "problematic.jwt.token";
        String authHeader = "Bearer " + token;
        
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(tokenProvider.validateToken(token)).willThrow(new RuntimeException("Token validation error"));

        // when
        assertThrows(RuntimeException.class, () -> 
                jwtAuthInterceptor.preHandle(request, response, new Object()));

        // then
        verify(tokenProvider).validateToken(token);
        verify(tokenProvider, never()).extractMemberId(anyString());
    }

    @Test
    @DisplayName("memberId 추출 중 예외 발생 시 예외 전파")
    void preHandle_MemberIdExtractionThrowsException_PropagatesException() throws Exception {
        // given
        String token = "valid.but.extraction.fails.token";
        String authHeader = "Bearer " + token;
        
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(tokenProvider.validateToken(token)).willReturn(true);
        given(tokenProvider.extractMemberId(token)).willThrow(new RuntimeException("Member ID extraction error"));

        // when
        assertThrows(RuntimeException.class, () -> 
                jwtAuthInterceptor.preHandle(request, response, new Object()));

        // then
        verify(tokenProvider).validateToken(token);
        verify(tokenProvider).extractMemberId(token);
    }

    @Test
    @DisplayName("정상적인 토큰으로 여러 번 호출해도 올바르게 동작")
    void preHandle_MultipleCallsWithValidToken_Success() throws Exception {
        // given
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        Long memberId = 1L;
        
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(tokenProvider.validateToken(token)).willReturn(true);
        given(tokenProvider.extractMemberId(token)).willReturn(memberId);

        // when
        boolean result1 = jwtAuthInterceptor.preHandle(request, response, new Object());
        boolean result2 = jwtAuthInterceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result1);
        assertTrue(result2);
        verify(request, times(2)).setAttribute("memberId", memberId);
        verify(tokenProvider, times(2)).validateToken(token);
        verify(tokenProvider, times(2)).extractMemberId(token);
    }
}
