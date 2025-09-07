package dev.devlink.common.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.lang.annotation.Annotation;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JpaAuditingConfiguration 테스트")
class JpaAuditingConfigurationTest {

    private final JpaAuditingConfiguration configuration = new JpaAuditingConfiguration();

    @Test
    @DisplayName("JPA Auditing이 활성화되어 있다")
    void jpaAuditingConfiguration_HasEnableJpaAuditingAnnotation() {
        // given
        Class<JpaAuditingConfiguration> configClass = JpaAuditingConfiguration.class;

        // when
        EnableJpaAuditing annotation = configClass.getAnnotation(EnableJpaAuditing.class);

        // then
        assertNotNull(annotation);
    }

    @Test
    @DisplayName("AuditorAware Bean을 생성한다")
    void auditorProvider_CreatesAuditorAware() {
        // when
        AuditorAware<String> auditorAware = configuration.auditorProvider();

        // then
        assertNotNull(auditorAware);
    }

    @Test
    @DisplayName("AuditorAware는 system을 반환한다")
    void auditorProvider_ReturnsSystem() {
        // given
        AuditorAware<String> auditorAware = configuration.auditorProvider();

        // when
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        // then
        assertAll(
                () -> assertTrue(currentAuditor.isPresent()),
                () -> assertEquals("system", currentAuditor.get())
        );
    }

    @Test
    @DisplayName("매번 동일한 auditor 값을 반환한다")
    void auditorProvider_ReturnsConsistentValue() {
        // given
        AuditorAware<String> auditorAware = configuration.auditorProvider();

        // when
        Optional<String> auditor1 = auditorAware.getCurrentAuditor();
        Optional<String> auditor2 = auditorAware.getCurrentAuditor();

        // then
        assertAll(
                () -> assertTrue(auditor1.isPresent()),
                () -> assertTrue(auditor2.isPresent()),
                () -> assertEquals(auditor1.get(), auditor2.get())
        );
    }
}
