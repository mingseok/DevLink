package dev.devlink.comment.controller.closed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.devlink.comment.service.ArticleCommentService;
import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleCommentController 테스트")
class ArticleCommentControllerTest {

    @Mock
    private ArticleCommentService articleCommentService;

    @Mock
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @InjectMocks
    private ArticleCommentController articleCommentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(articleCommentController)
                .setCustomArgumentResolvers(authMemberIdArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_Success() throws Exception {
        // given
        Long articleId = 1L;
        Long memberId = 1L;
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글 내용")
                .parentId(null)
                .build();

        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        willDoNothing().given(articleCommentService).save(any(CommentCreateRequest.class), eq(articleId), eq(memberId));

        // when & then
        mockMvc.perform(post("/api/v1/articles/{articleId}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("댓글 조회 성공")
    void getComments_Success() throws Exception {
        // given
        Long articleId = 1L;
        List<CommentResponse> responses = Arrays.asList(
                CommentResponse.builder()
                        .id(1L)
                        .content("첫 번째 댓글")
                        .memberNickname("사용자1")
                        .createdAt(LocalDateTime.now())
                        .build(),
                CommentResponse.builder()
                        .id(2L)
                        .content("두 번째 댓글")
                        .memberNickname("사용자2")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        given(articleCommentService.getComments(articleId)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/articles/{articleId}/comments", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").hasSize(2))
                .andExpect(jsonPath("$.data[0].content").value("첫 번째 댓글"))
                .andExpect(jsonPath("$.data[1].content").value("두 번째 댓글"));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() throws Exception {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(authMemberIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authMemberIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(memberId);
        willDoNothing().given(articleCommentService).delete(commentId, memberId);

        // when & then
        mockMvc.perform(delete("/api/v1/articles/{articleId}/comments/{commentId}", 1L, commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("잘못된 요청으로 댓글 생성 실패")
    void createComment_InvalidRequest() throws Exception {
        // given
        Long articleId = 1L;
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("") // 빈 내용
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/articles/{articleId}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
