package dev.devlink.follow.controller.closed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.follow.exception.FollowError;
import dev.devlink.follow.exception.FollowException;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowController 통합 테스트")
class FollowControllerIntegrationTest {

    @Mock
    private FollowService followService;

    @Mock
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @InjectMocks
    private FollowController followController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(followController)
                .setCustomArgumentResolvers(authMemberIdArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("팔로우 성공")
    void follow_Success() throws Exception {
        // given
        Long memberId = 1L;
        FollowCreateRequest request = new FollowCreateRequest(2L);
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        doNothing().when(followService).follow(eq(memberId), any(FollowCreateRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(followService).follow(eq(memberId), any(FollowCreateRequest.class));
    }

    @Test
    @DisplayName("자기 자신 팔로우 시 예외 발생")
    void follow_SelfFollow_ThrowsException() throws Exception {
        // given
        Long memberId = 1L;
        FollowCreateRequest request = new FollowCreateRequest(1L);
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        doThrow(new FollowException(FollowError.CANNOT_FOLLOW_SELF))
                .when(followService).follow(eq(memberId), any(FollowCreateRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(followService).follow(eq(memberId), any(FollowCreateRequest.class));
    }

    @Test
    @DisplayName("언팔로우 성공")
    void unfollow_Success() throws Exception {
        // given
        Long memberId = 1L;
        Long followeeId = 2L;
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        doNothing().when(followService).unfollow(memberId, followeeId);

        // when & then
        mockMvc.perform(delete("/api/v1/follows/{followeeId}", followeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(followService).unfollow(memberId, followeeId);
    }

    @Test
    @DisplayName("팔로워 목록 조회 성공")
    void getFollowers_Success() throws Exception {
        // given
        Long memberId = 1L;
        List<FollowResponse> followers = Arrays.asList(
                FollowResponse.builder()
                        .id(2L)
                        .nickname("팔로워1")
                        .following(false)
                        .createdAt(LocalDateTime.now())
                        .build(),
                FollowResponse.builder()
                        .id(3L)
                        .nickname("팔로워2")
                        .following(true)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        given(followService.getFollowers(memberId)).willReturn(followers);

        // when & then
        mockMvc.perform(get("/api/v1/follows/followers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").hasSize(2));

        verify(followService).getFollowers(memberId);
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 팔로우 요청 시 401 에러")
    void follow_Unauthorized_ReturnsUnauthorized() throws Exception {
        // given
        FollowCreateRequest request = new FollowCreateRequest(2L);

        // when & then
        mockMvc.perform(post("/api/v1/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(followService);
    }
}
