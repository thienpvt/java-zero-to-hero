package phase05.support;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

/** Provided test fixture, not learner code. Ephemeral in-memory keys; no private-key export. */
public final class TestJwt {
    private static final Instant NOW = Instant.parse("2026-10-04T12:00:00Z");
    private static final RSAKey KEY = key("phase05-test");
    private static final RSAKey WRONG_KEY = key("phase05-wrong");
    private TestJwt() {}

    public static String signed(Map<String, Object> overrides) {
        return encode(KEY, "RS256", overrides);
    }

    public static String signedWithWrongKey(Map<String, Object> claims) {
        return encode(WRONG_KEY, "RS256", claims);
    }

    public static String signedWithAlgorithm(String algorithm, Map<String, Object> claims) {
        if ("HS256".equals(algorithm)) {
            // Confusion probe: even a MAC using public-key bytes must not become an RSA signature.
            try {
                var key = new OctetSequenceKey.Builder(KEY.toRSAPublicKey().getEncoded()).keyID("phase05-mac").build();
                var encoder = new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
                return encoder.encode(JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).keyId(key.getKeyID()).build(), claims(claims))).getTokenValue();
            } catch (com.nimbusds.jose.JOSEException failure) {
                throw new IllegalStateException("Cannot create test MAC key", failure);
            }
        }
        if (!List.of("RS256", "RS384", "RS512").contains(algorithm)) {
            throw new IllegalArgumentException("Unsupported test signing algorithm");
        }
        return encode(KEY, algorithm, claims);
    }

    public static String publicJwkSet() {
        return new JWKSet(KEY.toPublicJWK()).toString();
    }

    public static JwtDecoder decoder() {
        try {
            var decoder = NimbusJwtDecoder.withPublicKey(KEY.toRSAPublicKey())
                    .signatureAlgorithm(SignatureAlgorithm.RS256).build();
            var timestamp = new JwtTimestampValidator(Duration.ofSeconds(60));
            timestamp.setClock(Clock.fixed(NOW, ZoneOffset.UTC));
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(timestamp,
                    new JwtIssuerValidator("urn:phase05:test"), jwt ->
                    jwt.getAudience() != null && jwt.getAudience().contains("phase05-api") && jwt.getExpiresAt() != null
                            ? OAuth2TokenValidatorResult.success()
                            : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid token claims", null))));
            return decoder;
        } catch (com.nimbusds.jose.JOSEException failure) {
            throw new IllegalStateException("Cannot create test decoder", failure);
        }
    }

    private static String encode(RSAKey key, String algorithm, Map<String, Object> overrides) {
        var encoder = new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(SignatureAlgorithm.from(algorithm)).keyId(key.getKeyID()).build(),
                claims(overrides))).getTokenValue();
    }

    private static JwtClaimsSet claims(Map<String, Object> overrides) {
        return JwtClaimsSet.builder().issuer("urn:phase05:test").subject("alice")
                .audience(List.of("phase05-api")).issuedAt(NOW.minusSeconds(10))
                .notBefore(NOW.minusSeconds(10)).expiresAt(NOW.plusSeconds(300))
                .claim("scope", "products.read orders.read orders.write")
                .claims(claims -> claims.putAll(overrides)).build();
    }

    private static RSAKey key(String id) {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            var pair = generator.generateKeyPair();
            return new RSAKey.Builder((RSAPublicKey) pair.getPublic())
                    .privateKey((RSAPrivateKey) pair.getPrivate()).keyID(id).build();
        } catch (java.security.GeneralSecurityException failure) {
            throw new IllegalStateException("Cannot generate local test key", failure);
        }
    }
}
