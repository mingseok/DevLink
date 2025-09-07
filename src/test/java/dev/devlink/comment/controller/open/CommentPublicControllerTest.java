package dev.devlink.comment.controller.open;

import dev.devlink.comment.service.ArticleCommentService;
import dev.devlink.comment.service.dto.response.CommentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentPublicController 테스트")
class CommentPublicControllerTest {

    @Mock
    private ArticleCommentService articleCommentService;

    @InjectMocks
    private CommentPublicController commentPublicController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentPublicController).build();
    }

    @Test
    @DisplayName("공개 댓글 조회 성공")
    void getComments_Success() throws Exception {
        // given
        Long articleId = 1L;
        List<CommentResponse> responses = Arrays.asList(
                CommentResponse.builder()
                        .id(1L)
                        .content("공개 댓글 1")
                        .writer("사용자1")
                        .writerId(1L)
                        .createdAt(LocalDateTime.now())
                        .build(),
                CommentResponse.builder()
                        .id(2L)
                        .content("공개 댓글 2")
                        .writer("사용자2")
                        .writerId(2L)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        given(articleCommentService.getComments(articleId)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/public/articles/{articleId}/comments", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").hasSize(2))
                .andExpect(jsonPath("$.data[0].content").value("공개 댓글 1"))
                .andExpect(jsonPath("$.data[1].content").value("공개 댓글 2"));
    }

    @Test
    @DisplayName("빈 댓글 목록 조회")
    void getComments_EmptyList_Success() throws Exception {
        // given
        Long articleId = 1L;
        given(articleCommentService.getComments(articleId)).willReturn(Arrays.asList());

        // when & then
        mockMvc.perform(get("/api/public/articles/{articleId}/comments", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
