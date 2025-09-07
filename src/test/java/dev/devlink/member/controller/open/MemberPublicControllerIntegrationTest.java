package dev.devlink.member.controller.open;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.devlink.member.exception.MemberError;
import dev.devlink.member.exception.MemberException;
import dev.devlink.member.service.MemberService;
import dev.devlink.member.service.dto.request.SignInRequest;
import dev.devlink.member.service.dto.request.SignUpRequest;
import dev.devlink.member.service.dto.response.JwtTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberPublicController 통합 테스트")
class MemberPublicControllerIntegrationTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberPublicController memberPublicController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(memberPublicController).build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("테스트유저", "test@example.com", "테스트닉네임", "password123");
        doNothing().when(memberService).signUp(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(post("/api/public/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(memberService).signUp(any(SignUpRequest.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void signin_Success() throws Exception {
        // given
        SignInRequest request = new SignInRequest("test@example.com", "password123");
        JwtTokenResponse tokenResponse = new JwtTokenResponse("access-token", "refresh-token");
        given(memberService.signin(any(SignInRequest.class))).willReturn(tokenResponse);

        // when & then
        mockMvc.perform(post("/api/public/members/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));

        verify(memberService).signin(any(SignInRequest.class));
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패")
    void signup_EmailDuplicated_ReturnsBadRequest() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("테스트유저", "duplicate@example.com", "테스트닉네임", "password123");
        doThrow(new MemberException(MemberError.EMAIL_DUPLICATED))
                .when(memberService).signUp(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(post("/api/public/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(memberService).signUp(any(SignUpRequest.class));
    }

    @Test
    @DisplayName("비밀번호 불일치로 로그인 실패")
    void signin_PasswordNotMatched_ReturnsBadRequest() throws Exception {
        // given
        SignInRequest request = new SignInRequest("test@example.com", "wrongpassword");
        given(memberService.signin(any(SignInRequest.class)))
                .willThrow(new MemberException(MemberError.PASSWORD_NOT_MATCHED));

        // when & then
        mockMvc.perform(post("/api/public/members/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(memberService).signin(any(SignInRequest.class));
    }

    @Test
    @DisplayName("잘못된 회원가입 요청으로 검증 실패")
    void signup_InvalidRequest_ValidationFails() throws Exception {
        // given
        SignUpRequest invalidRequest = new SignUpRequest("", "", "", "");

        // when & then
        mockMvc.perform(post("/api/public/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(memberService);
    }
}
