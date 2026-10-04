package phase05.d17_capstone;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import phase05.support.TestJwt;

/** Provided test-only Boot SPI. Never packaged in main; public key only, signer remains TestJwt. */
public final class CapstoneTestRuntime implements EnvironmentPostProcessor {
    static final ThreadLocal<Map<String, Object>> SETTINGS = new ThreadLocal<>();

    static String publicKey() {
        try {
            var key = (RSAKey) JWKSet.parse(TestJwt.publicJwkSet()).getKeys().getFirst();
            return Base64.getEncoder().encodeToString(key.toRSAPublicKey().getEncoded());
        } catch (Exception failure) {
            throw new IllegalStateException("Cannot prepare public test key");
        }
    }

    @Override public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!environment.getProperty("capstone.test.jwt-defaults", Boolean.class, true)) return;
        var defaults = new HashMap<String, Object>();
        defaults.put("capstone.jwt.issuer", "urn:phase05:test");
        defaults.put("capstone.jwt.audience", "phase05-api");
        defaults.put("capstone.jwt.public-key", publicKey());
        environment.getPropertySources().addLast(new MapPropertySource("test-only-public-jwt-defaults", defaults));
        var settings = SETTINGS.get();
        if (settings != null) environment.getPropertySources().addFirst(new MapPropertySource("test-only-runtime", settings));
    }
}
