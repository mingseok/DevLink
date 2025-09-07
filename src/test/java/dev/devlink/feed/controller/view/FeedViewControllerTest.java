package dev.devlink.feed.controller.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeedViewController 테스트")
class FeedViewControllerTest {

    @InjectMocks
    private FeedViewController feedViewController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(feedViewController).build();
    }

    @Test
    @DisplayName("피드 목록 페이지 요청 성공")
    void feedList_ReturnsCorrectView() throws Exception {
        // when & then
        mockMvc.perform(get("/view/feeds"))
                .andExpect(status().isOk())
                .andExpect(view().name("feed/list"));
    }

    @Test
    @DisplayName("피드 생성 페이지 요청 성공")
    void feedCreate_ReturnsCorrectView() throws Exception {
        // when & then
        mockMvc.perform(get("/view/feeds/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("feed/create"));
    }

    @Test
    @DisplayName("피드 목록 페이지는 모델 데이터 없이 반환된다")
    void feedList_ReturnsViewWithoutModel() throws Exception {
        // when & then
        mockMvc.perform(get("/view/feeds"))
                .andExpect(status().isOk())
                .andExpect(model().size(0))
                .andExpect(view().name("feed/list"));
    }

    @Test
    @DisplayName("피드 생성 페이지는 모델 데이터 없이 반환된다")
    void feedCreate_ReturnsViewWithoutModel() throws Exception {
        // when & then
        mockMvc.perform(get("/view/feeds/create"))
                .andExpect(status().isOk())
                .andExpect(model().size(0))
                .andExpect(view().name("feed/create"));
    }

    @Test
    @DisplayName("존재하지 않는 경로 요청 시 404 에러")
    void nonExistentPath_Returns404() throws Exception {
        // when & then
        mockMvc.perform(get("/view/feeds/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST 요청은 지원하지 않음")
    void postRequest_NotSupported() throws Exception {
        // when & then
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/view/feeds"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Controller 어노테이션이 정상적으로 설정되어 있다")
    void feedViewController_HasControllerAnnotation() {
        // given
        Class<FeedViewController> controllerClass = FeedViewController.class;

        // then
        assertTrue(controllerClass.isAnnotationPresent(org.springframework.stereotype.Controller.class));
    }

    @Test
    @DisplayName("RequestMapping 어노테이션이 정상적으로 설정되어 있다")
    void feedViewController_HasRequestMappingAnnotation() {
        // given
        Class<FeedViewController> controllerClass = FeedViewController.class;
        org.springframework.web.bind.annotation.RequestMapping requestMapping = 
                controllerClass.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);

        // then
        assertAll(
                () -> assertNotNull(requestMapping),
                () -> assertEquals("/view/feeds", requestMapping.value()[0])
        );
    }

    @Test
    @DisplayName("feedList 메서드가 올바른 GetMapping을 가진다")
    void feedListMethod_HasCorrectGetMapping() throws NoSuchMethodException {
        // given
        var method = FeedViewController.class.getMethod("feedList");
        var getMapping = method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);

        // then
        assertAll(
                () -> assertNotNull(getMapping),
                () -> assertEquals(0, getMapping.value().length) // 기본 경로
        );
    }

    @Test
    @DisplayName("feedCreate 메서드가 올바른 GetMapping을 가진다")
    void feedCreateMethod_HasCorrectGetMapping() throws NoSuchMethodException {
        // given
        var method = FeedViewController.class.getMethod("feedCreate");
        var getMapping = method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);

        // then
        assertAll(
                () -> assertNotNull(getMapping),
                () -> assertEquals("/create", getMapping.value()[0])
        );
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

    private static void assertAll(org.junit.jupiter.api.function.Executable... executables) {
        org.junit.jupiter.api.Assertions.assertAll(executables);
    }
}
