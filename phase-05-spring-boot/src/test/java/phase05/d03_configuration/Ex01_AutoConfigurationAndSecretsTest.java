package phase05.d03_configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Ex01_AutoConfigurationAndSecretsTest {
    @Test
    @DisplayName("Q01: auto-configuration fournit un bean seulement si aucun bean applicatif n'existe")
    void autoConfigurationProvidesConditionalDefault() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ex01_AutoConfigurationAndSecrets.GreetingAutoConfiguration.class))
                .run(context -> {
                    assertEquals("auto-configured", context.getBean(Ex01_AutoConfigurationAndSecrets.Greeting.class).message());
                    var report = ConditionEvaluationReport.get(context.getBeanFactory());
                    assertFalse(report.getConditionAndOutcomesBySource().isEmpty());
                });
    }

    @Test
    @DisplayName("Q01: bean applicatif làm auto-configuration back off")
    void backsOffWhenApplicationProvidesBean() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ex01_AutoConfigurationAndSecrets.GreetingAutoConfiguration.class))
                .withUserConfiguration(Ex01_AutoConfigurationAndSecrets.UserGreetingConfiguration.class)
                .run(context -> assertEquals("local", context.getBean(Ex01_AutoConfigurationAndSecrets.Greeting.class).message()));
    }

    @Test
    @DisplayName("Q02: secret manquant provoque un échec explicite sans lire la machine")
    void requiredSecretFailsFast() {
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.signingKey(Map.of()));
        assertEquals("test-only-key", Ex01_AutoConfigurationAndSecrets.signingKey(Map.of("app.signing-key", "test-only-key")));
    }


    @Test
    @DisplayName("Q02: propriété locale de base de données est explicite et ne contient aucun secret")
    void localDatabaseUrlIsNonSecretConfiguration() {
        assertEquals("jdbc:postgresql://localhost/lab", Ex01_AutoConfigurationAndSecrets.localDatabaseUrl());
    }

    @Test
    @DisplayName("Q03: profile et secret appartiennent à des propriétés distinctes")
    void profileIsNotSigningKeySource() {
        assertEquals("production", Ex01_AutoConfigurationAndSecrets.selectedProfile(
                Map.of("spring.profiles.active", "production")));
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.productionSigningKey(Map.of("spring.profiles.active", "production")));
    }

}
