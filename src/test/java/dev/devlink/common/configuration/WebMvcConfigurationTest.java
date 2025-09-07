package dev.devlink.common.configuration;

import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.common.interceptor.JwtAuthInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebMvcConfiguration 테스트")
class WebMvcConfigurationTest {

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @Mock
    private JwtAuthInterceptor jwtAuthInterceptor;

    private WebMvcConfiguration webMvcConfiguration;

    @BeforeEach
    void setUp() {
        webMvcConfiguration = new WebMvcConfiguration(authMemberIdArgumentResolver, jwtAuthInterceptor);
    }

    @Test
    @DisplayName("AuthMemberIdArgumentResolver를 ArgumentResolver에 추가한다")
    void addArgumentResolvers_AddsAuthMemberIdArgumentResolver() {
        // given
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

        // when
        webMvcConfiguration.addArgumentResolvers(resolvers);

        // then
        assertAll(
                () -> assertEquals(1, resolvers.size()),
                () -> assertEquals(authMemberIdArgumentResolver, resolvers.get(0))
        );
    }

    @Test
    @DisplayName("JwtAuthInterceptor를 인터셉터에 추가한다")
    void addInterceptors_AddsJwtAuthInterceptor() {
        // when
        webMvcConfiguration.addInterceptors(interceptorRegistry);

        // then
        verify(interceptorRegistry).addInterceptor(jwtAuthInterceptor);
    }

    @Test
    @DisplayName("빈 ArgumentResolver 리스트에도 정상적으로 추가된다")
    void addArgumentResolvers_WorksWithEmptyList() {
        // given
        List<HandlerMethodArgumentResolver> emptyResolvers = new ArrayList<>();

        // when
        webMvcConfiguration.addArgumentResolvers(emptyResolvers);

        // then
        assertAll(
                () -> assertEquals(1, emptyResolvers.size()),
                () -> assertInstanceOf(AuthMemberIdArgumentResolver.class, emptyResolvers.get(0))
        );
    }

    @Test
    @DisplayName("기존 ArgumentResolver가 있는 리스트에 추가된다")
    void addArgumentResolvers_AddsToExistingList() {
        // given
        List<HandlerMethodArgumentResolver> existingResolvers = new ArrayList<>();
        HandlerMethodArgumentResolver mockResolver = new MockArgumentResolver();
        existingResolvers.add(mockResolver);

        // when
        webMvcConfiguration.addArgumentResolvers(existingResolvers);

        // then
        assertAll(
                () -> assertEquals(2, existingResolvers.size()),
                () -> assertEquals(mockResolver, existingResolvers.get(0)),
                () -> assertEquals(authMemberIdArgumentResolver, existingResolvers.get(1))
        );
    }

    // 테스트용 Mock ArgumentResolver
    private static class MockArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
            return false;
        }

        @Override
        public Object resolveArgument(org.springframework.core.MethodParameter parameter,
                                    org.springframework.web.method.support.ModelAndViewContainer mavContainer,
                                    org.springframework.web.context.request.NativeWebRequest webRequest,
                                    org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
            return null;
        }
    }
}
