package phase05.d03_configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_AutoConfigurationAndSecretsTest {
    @Test
    @DisplayName("B03: chọn database local/test theo profile; thiếu signing key phải fail-fast")
    void requiredSecretFailsFast() {
        assertEquals("test-only-key", Ex01_AutoConfigurationAndSecrets.productionSigningKey(
                Map.of("app.signing-key", "test-only-key")));

        Properties settings = new Properties();
        settings.setProperty("db.local.url", "jdbc:postgresql://localhost/phase05_local_lab");
        settings.setProperty("db.test.url", "jdbc:postgresql://localhost/phase05_test_lab");
        assertEquals("jdbc:postgresql://localhost/phase05_local_lab",
                Ex01_AutoConfigurationAndSecrets.databaseUrl("local", settings));
        assertEquals("jdbc:postgresql://localhost/phase05_test_lab",
                Ex01_AutoConfigurationAndSecrets.databaseUrl("test", settings));
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.databaseUrl(null, settings));
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.databaseUrl("production", settings));
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.databaseUrl("test", new Properties()));
        settings.setProperty("db.test.url", " ");
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.databaseUrl("test", settings));

        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.productionSigningKey(Map.of()));
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.productionSigningKey(Map.of("app.signing-key", " ")));
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.productionSigningKey(
                        Map.of("spring.profiles.active", "production")));
    }
}
