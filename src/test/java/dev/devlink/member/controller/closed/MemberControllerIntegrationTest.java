package dev.devlink.member.controller.closed;

import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.member.exception.MemberError;
import dev.devlink.member.exception.MemberException;
import dev.devlink.member.service.MemberService;
import dev.devlink.member.service.dto.response.NicknameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberController 통합 테스트")
class MemberControllerIntegrationTest {

    @Mock
    private MemberService memberService;

    @Mock
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setCustomArgumentResolvers(authMemberIdArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("현재 회원 닉네임 조회 성공")
    void findCurrentNickname_Success() throws Exception {
        // given
        Long memberId = 1L;
        NicknameResponse nicknameResponse = NicknameResponse.from("테스트닉네임");
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        given(memberService.findNicknameById(memberId)).willReturn(nicknameResponse);

        // when & then
        mockMvc.perform(get("/api/v1/members/self"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.nickname").value("테스트닉네임"));

        verify(memberService).findNicknameById(memberId);
    }

    @Test
    @DisplayName("존재하지 않는 회원 닉네임 조회 시 404 에러")
    void findCurrentNickname_MemberNotFound_ReturnsNotFound() throws Exception {
        // given
        Long memberId = 999L;
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        given(memberService.findNicknameById(memberId))
                .willThrow(new MemberException(MemberError.MEMBER_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/members/self"))
                .andExpect(status().isNotFound());

        verify(memberService).findNicknameById(memberId);
    }

    @Test
    @DisplayName("인증되지 않은 사용자 접근 시 401 에러")
    void findCurrentNickname_Unauthorized_ReturnsUnauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/members/self"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(memberService);
    }

    @Test
    @DisplayName("잘못된 HTTP 메서드 사용 시 405 에러")
    void wrongHttpMethod_ReturnsMethodNotAllowed() throws Exception {
        // when & then
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/v1/members/self"))
                .andExpect(status().isMethodNotAllowed());

        verifyNoInteractions(memberService);
    }

    @Test
    @DisplayName("존재하지 않는 엔드포인트 접근 시 404 에러")
    void nonExistentEndpoint_ReturnsNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/members/nonexistent"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(memberService);
    }

    @Test
    @DisplayName("여러 번의 요청에 대해 일관된 응답 반환")
    void multipleRequests_ConsistentResponse() throws Exception {
        // given
        Long memberId = 1L;
        NicknameResponse nicknameResponse = NicknameResponse.from("일관된닉네임");
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        given(memberService.findNicknameById(memberId)).willReturn(nicknameResponse);

        // when & then
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/v1/members/self"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.nickname").value("일관된닉네임"));
        }

        verify(memberService, times(3)).findNicknameById(memberId);
    }

    @Test
    @DisplayName("특수문자가 포함된 닉네임 조회 성공")
    void findCurrentNickname_WithSpecialCharacters_Success() throws Exception {
        // given
        Long memberId = 1L;
        String specialNickname = "테스트@#$%닉네임!";
        NicknameResponse nicknameResponse = NicknameResponse.from(specialNickname);
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        given(memberService.findNicknameById(memberId)).willReturn(nicknameResponse);

        // when & then
        mockMvc.perform(get("/api/v1/members/self"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.nickname").value(specialNickname));

        verify(memberService).findNicknameById(memberId);
    }

    @Test
    @DisplayName("긴 닉네임 조회 성공")
    void findCurrentNickname_WithLongNickname_Success() throws Exception {
        // given
        Long memberId = 1L;
        String longNickname = "매우긴닉네임".repeat(5);
        NicknameResponse nicknameResponse = NicknameResponse.from(longNickname);
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        given(memberService.findNicknameById(memberId)).willReturn(nicknameResponse);

        // when & then
        mockMvc.perform(get("/api/v1/members/self"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.nickname").value(longNickname));

        verify(memberService).findNicknameById(memberId);
    }

    @Test
    @DisplayName("컨트롤러 어노테이션 검증")
    void controllerAnnotations_AreCorrect() {
        // given
        Class<MemberController> controllerClass = MemberController.class;

        // then
        assertTrue(controllerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class));
        
        org.springframework.web.bind.annotation.RequestMapping requestMapping = 
                controllerClass.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
        assertNotNull(requestMapping);
        assertEquals("/api/v1/members", requestMapping.value()[0]);
    }

    @Test
    @DisplayName("findCurrentNickname 메서드 어노테이션 검증")
    void findCurrentNicknameMethod_HasCorrectAnnotations() throws NoSuchMethodException {
        // given
        var method = MemberController.class.getMethod("findCurrentNickname", Long.class);
        var getMapping = method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);

        // then
        assertNotNull(getMapping);
        assertEquals("/self", getMapping.value()[0]);
    }

    // 추가 import를 위한 static import
    private static void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }

    private static void assertEquals(Object expected, Object actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    private static void assertNotNull(Object actual) {
        org.junit.jupiter.api.Assertions.assertNotNull(actual);
    }
}
