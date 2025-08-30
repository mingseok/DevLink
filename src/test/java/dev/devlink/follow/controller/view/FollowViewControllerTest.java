package dev.devlink.follow.controller.view;

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

@WebMvcTest(FollowViewController.class)
class FollowViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @Test
    @DisplayName("팔로워 페이지를 조회할 수 있다")
    void followers_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/view/follow/followers"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/followers"));
    }

    @Test
    @DisplayName("팔로잉 페이지를 조회할 수 있다")
    void following_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/view/follow/following"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/following"));
    }
}
