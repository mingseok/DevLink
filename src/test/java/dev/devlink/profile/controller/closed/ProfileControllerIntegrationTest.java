package dev.devlink.profile.controller.closed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.profile.service.ProfileService;
import dev.devlink.profile.service.dto.request.ProfileUpdateRequest;
import dev.devlink.profile.service.dto.response.ProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileController 통합 테스트")
class ProfileControllerIntegrationTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @InjectMocks
    private ProfileController profileController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                .setCustomArgumentResolvers(authMemberIdArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getProfile_Success() throws Exception {
        // given
        Long memberId = 1L;
        Long targetId = 2L;
        ProfileResponse profileResponse = ProfileResponse.builder()
                .memberId(targetId)
                .nickname("타겟닉네임")
                .bio("안녕하세요. 개발자입니다.")
                .imageUrl("https://example.com/profile.jpg")
                .isFollowing(true)
                .followers(100L)
                .followings(50L)
                .createdAt(LocalDateTime.now())
                .build();
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        given(profileService.getProfile(memberId, targetId)).willReturn(profileResponse);

        // when & then
        mockMvc.perform(get("/api/v1/profile/{id}", targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.memberId").value(targetId))
                .andExpect(jsonPath("$.data.nickname").value("타겟닉네임"));

        verify(profileService).getProfile(memberId, targetId);
    }

    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile_Success() throws Exception {
        // given
        Long memberId = 1L;
        ProfileUpdateRequest request = new ProfileUpdateRequest("업데이트된 소개글입니다.");
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        doNothing().when(profileService).updateBio(eq(memberId), any(ProfileUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(profileService).updateBio(eq(memberId), any(ProfileUpdateRequest.class));
    }

    @Test
    @DisplayName("프로필 이미지 업로드 성공")
    void uploadImage_Success() throws Exception {
        // given
        Long memberId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes());
        String imageUrl = "https://example.com/uploaded-image.jpg";
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        given(profileService.updateImage(eq(memberId), any())).willReturn(imageUrl);

        // when & then
        mockMvc.perform(multipart("/api/v1/profile/image")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(profileService).updateImage(eq(memberId), any());
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 프로필 조회 시 401 에러")
    void getProfile_Unauthorized_ReturnsUnauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/profile/{id}", 1L))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(profileService);
    }

    @Test
    @DisplayName("잘못된 프로필 업데이트 요청으로 검증 실패")
    void updateProfile_InvalidRequest_ValidationFails() throws Exception {
        // given
        ProfileUpdateRequest invalidRequest = new ProfileUpdateRequest("");

        // when & then
        mockMvc.perform(put("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(profileService);
    }
}
