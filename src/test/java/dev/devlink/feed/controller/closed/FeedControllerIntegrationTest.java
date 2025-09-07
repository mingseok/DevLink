package dev.devlink.feed.controller.closed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.devlink.feed.service.FeedService;
import dev.devlink.feed.service.dto.request.FeedCreateRequest;
import dev.devlink.feed.service.dto.response.FeedResponse;
import dev.devlink.feed.exception.FeedError;
import dev.devlink.feed.exception.FeedException;
import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
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
@DisplayName("FeedController 통합 테스트")
class FeedControllerIntegrationTest {

    @Mock
    private FeedService feedService;

    @Mock
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @InjectMocks
    private FeedController feedController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(feedController)
                .setCustomArgumentResolvers(authMemberIdArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("피드 생성 성공")
    void createFeed_Success() throws Exception {
        // given
        Long memberId = 1L;
        FeedCreateRequest request = new FeedCreateRequest("새로운 피드 내용");
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        given(feedService.createFeed(any(FeedCreateRequest.class), eq(memberId))).willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/v1/feeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(1));

        verify(feedService).createFeed(any(FeedCreateRequest.class), eq(memberId));
    }

    @Test
    @DisplayName("잘못된 요청으로 피드 생성 실패")
    void createFeed_InvalidRequest_BadRequest() throws Exception {
        // given
        FeedCreateRequest invalidRequest = new FeedCreateRequest(""); // 빈 내용

        // when & then
        mockMvc.perform(post("/api/v1/feeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(feedService);
    }

    @Test
    @DisplayName("피드 목록 조회 성공")
    void getFeedList_Success() throws Exception {
        // given
        List<FeedResponse> feeds = Arrays.asList(
                FeedResponse.builder()
                        .id(1L)
                        .content("첫 번째 피드")
                        .authorNickname("user1")
                        .authorId(1L)
                        .createdAt(LocalDateTime.now())
                        .build(),
                FeedResponse.builder()
                        .id(2L)
                        .content("두 번째 피드")
                        .authorNickname("user2")
                        .authorId(2L)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        
        given(feedService.getFeedList()).willReturn(feeds);

        // when & then
        mockMvc.perform(get("/api/v1/feeds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").hasSize(2))
                .andExpect(jsonPath("$.data[0].content").value("첫 번째 피드"))
                .andExpect(jsonPath("$.data[1].content").value("두 번째 피드"));

        verify(feedService).getFeedList();
    }

    @Test
    @DisplayName("피드 상세 조회 성공")
    void getFeedDetail_Success() throws Exception {
        // given
        Long feedId = 1L;
        FeedResponse feedResponse = FeedResponse.builder()
                .id(feedId)
                .content("피드 상세 내용")
                .authorNickname("author")
                .authorId(1L)
                .createdAt(LocalDateTime.now())
                .build();
        
        given(feedService.getFeedDetail(feedId)).willReturn(feedResponse);

        // when & then
        mockMvc.perform(get("/api/v1/feeds/{feedId}", feedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("피드 상세 내용"))
                .andExpect(jsonPath("$.data.authorNickname").value("author"));

        verify(feedService).getFeedDetail(feedId);
    }

    @Test
    @DisplayName("존재하지 않는 피드 조회 시 404 에러")
    void getFeedDetail_NotFound_ReturnsNotFound() throws Exception {
        // given
        Long nonExistentFeedId = 999L;
        given(feedService.getFeedDetail(nonExistentFeedId))
                .willThrow(new FeedException(FeedError.NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/feeds/{feedId}", nonExistentFeedId))
                .andExpect(status().isNotFound());

        verify(feedService).getFeedDetail(nonExistentFeedId);
    }

    @Test
    @DisplayName("피드 수정 성공")
    void updateFeed_Success() throws Exception {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        FeedCreateRequest request = new FeedCreateRequest("수정된 피드 내용");
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        doNothing().when(feedService).updateFeed(eq(feedId), any(FeedCreateRequest.class), eq(memberId));

        // when & then
        mockMvc.perform(put("/api/v1/feeds/{feedId}", feedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(feedService).updateFeed(eq(feedId), any(FeedCreateRequest.class), eq(memberId));
    }

    @Test
    @DisplayName("권한 없는 사용자의 피드 수정 시 403 에러")
    void updateFeed_NoPermission_ReturnsForbidden() throws Exception {
        // given
        Long feedId = 1L;
        Long memberId = 2L;
        FeedCreateRequest request = new FeedCreateRequest("수정된 피드 내용");
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        doThrow(new FeedException(FeedError.NO_PERMISSION))
                .when(feedService).updateFeed(eq(feedId), any(FeedCreateRequest.class), eq(memberId));

        // when & then
        mockMvc.perform(put("/api/v1/feeds/{feedId}", feedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(feedService).updateFeed(eq(feedId), any(FeedCreateRequest.class), eq(memberId));
    }

    @Test
    @DisplayName("피드 삭제 성공")
    void deleteFeed_Success() throws Exception {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        
        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        doNothing().when(feedService).deleteFeed(feedId, memberId);

        // when & then
        mockMvc.perform(delete("/api/v1/feeds/{feedId}", feedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(feedService).deleteFeed(feedId, memberId);
    }

    @Test
    @DisplayName("빈 피드 목록 조회")
    void getFeedList_EmptyList_Success() throws Exception {
        // given
        given(feedService.getFeedList()).willReturn(Arrays.asList());

        // when & then
        mockMvc.perform(get("/api/v1/feeds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(feedService).getFeedList();
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 피드 생성 시 401 에러")
    void createFeed_Unauthorized_ReturnsUnauthorized() throws Exception {
        // given
        FeedCreateRequest request = new FeedCreateRequest("새로운 피드 내용");

        // when & then
        mockMvc.perform(post("/api/v1/feeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(feedService);
    }
}
