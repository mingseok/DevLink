package dev.devlink.profile.controller.view;

import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.common.jwt.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileViewController.class)
class ProfileViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @Test
    @DisplayName("프로필 페이지 조회 성공")
    void showProfilePage_Success() throws Exception {
        // given
        Long profileId = 1L;

        // when & then
        mockMvc.perform(get("/view/profile/{id}", profileId))
                .andExpect(status().isOk())
                .andExpect(view().name("/profile/detail"));
    }
}
