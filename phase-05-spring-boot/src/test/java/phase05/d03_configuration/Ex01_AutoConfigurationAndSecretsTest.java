package phase05.d03_configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_AutoConfigurationAndSecretsTest {
    @Test
    @DisplayName("B03: thiếu signing key phải fail-fast; profile không thay secret")
    void requiredSecretFailsFast() {
        assertEquals("test-only-key", Ex01_AutoConfigurationAndSecrets.productionSigningKey(
                Map.of("app.signing-key", "test-only-key")));

        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.productionSigningKey(Map.of()));
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.productionSigningKey(Map.of("app.signing-key", " ")));
        assertThrows(IllegalStateException.class,
                () -> Ex01_AutoConfigurationAndSecrets.productionSigningKey(
                        Map.of("spring.profiles.active", "production")));
    }
}
