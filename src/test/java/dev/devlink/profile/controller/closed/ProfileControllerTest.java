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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                .setCustomArgumentResolvers(new AuthMemberIdArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getProfile_Success() throws Exception {
        // given
        Long memberId = 1L;
        Long targetId = 2L;
        ProfileResponse response = new ProfileResponse(
                targetId, "테스트닉네임", "2023-01-01", "안녕하세요",
                "/images/default.png", true, 10L, 5L
        );
        
        given(profileService.getProfile(memberId, targetId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/profile/{id}", targetId)
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.memberId").value(targetId))
                .andExpect(jsonPath("$.data.nickname").value("테스트닉네임"))
                .andExpect(jsonPath("$.data.bio").value("안녕하세요"))
                .andExpect(jsonPath("$.data.isFollowing").value(true))
                .andExpect(jsonPath("$.data.followersCount").value(10))
                .andExpect(jsonPath("$.data.followingsCount").value(5));
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void updateProfile_Success() throws Exception {
        // given
        Long memberId = 1L;
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        
        // when & then
        mockMvc.perform(put("/api/v1/profile")
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        then(profileService).should().updateBio(eq(memberId), any(ProfileUpdateRequest.class));
    }

    @Test
    @DisplayName("프로필 이미지 업로드 성공")
    void uploadImage_Success() throws Exception {
        // given
        Long memberId = 1L;
        String imageUrl = "https://example.com/image.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes()
        );
        
        given(profileService.updateImage(eq(memberId), any())).willReturn(imageUrl);

        // when & then
        mockMvc.perform(multipart("/api/v1/profile/image")
                        .file(file)
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.imageUrl").value(imageUrl));
    }
}
