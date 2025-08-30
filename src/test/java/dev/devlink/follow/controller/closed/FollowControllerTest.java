package dev.devlink.follow.controller.closed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.follow.service.FollowService;
import dev.devlink.follow.service.dto.request.FollowCreateRequest;
import dev.devlink.follow.service.dto.response.FollowResponse;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {

    @Mock
    private FollowService followService;

    @InjectMocks
    private FollowController followController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(followController)
                .setCustomArgumentResolvers(new AuthMemberIdArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("팔로우 성공")
    void follow_Success() throws Exception {
        // given
        Long memberId = 1L;
        Long followeeId = 2L;
        FollowCreateRequest request = new FollowCreateRequest(followeeId);

        // when & then
        mockMvc.perform(post("/api/v1/follows")
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        then(followService).should().follow(eq(memberId), any(FollowCreateRequest.class));
    }

    @Test
    @DisplayName("언팔로우 성공")
    void unfollow_Success() throws Exception {
        // given
        Long memberId = 1L;
        Long followeeId = 2L;

        // when & then
        mockMvc.perform(delete("/api/v1/follows/{followeeId}", followeeId)
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        then(followService).should().unfollow(memberId, followeeId);
    }

    @Test
    @DisplayName("팔로워 목록 조회 성공")
    void getFollowers_Success() throws Exception {
        // given
        Long memberId = 1L;
        List<FollowResponse> responses = List.of(
                new FollowResponse(2L, "팔로워1", "2023-01-01", true),
                new FollowResponse(3L, "팔로워2", "2023-01-02", false)
        );
        
        given(followService.getFollowers(memberId)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/follows/followers")
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].memberId").value(2L))
                .andExpect(jsonPath("$.data[0].nickname").value("팔로워1"))
                .andExpect(jsonPath("$.data[0].isFollowing").value(true))
                .andExpect(jsonPath("$.data[1].memberId").value(3L))
                .andExpect(jsonPath("$.data[1].nickname").value("팔로워2"))
                .andExpect(jsonPath("$.data[1].isFollowing").value(false));
    }

    @Test
    @DisplayName("팔로잉 목록 조회 성공")
    void getFollowings_Success() throws Exception {
        // given
        Long memberId = 1L;
        List<FollowResponse> responses = List.of(
                new FollowResponse(2L, "팔로잉1", "2023-01-01", false),
                new FollowResponse(3L, "팔로잉2", "2023-01-02", false)
        );
        
        given(followService.getFollowings(memberId)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/follows/following")
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].memberId").value(2L))
                .andExpect(jsonPath("$.data[0].nickname").value("팔로잉1"))
                .andExpect(jsonPath("$.data[1].memberId").value(3L))
                .andExpect(jsonPath("$.data[1].nickname").value("팔로잉2"));
    }

    @Test
    @DisplayName("유효하지 않은 요청으로 팔로우시 검증 실패")
    void follow_WithInvalidRequest_ValidationFails() throws Exception {
        // given
        Long memberId = 1L;
        FollowCreateRequest request = new FollowCreateRequest(null);

        // when & then
        mockMvc.perform(post("/api/v1/follows")
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
