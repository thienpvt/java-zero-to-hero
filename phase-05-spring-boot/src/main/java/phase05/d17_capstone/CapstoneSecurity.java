package phase05.d17_capstone;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

/** B5: header-only access tokens. No live issuer, signing key, cookie or request-owner resolver. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CapstoneSecurity {
    private static final String LOOPBACK = "127.0.0.1";
    public static void runtimeSettings(SpringApplication app) {
        // SOLUTION-BEGIN throw B5
        app.setDefaultProperties(Map.of(
                "spring.jpa.open-in-view", false, "spring.jpa.hibernate.ddl-auto", "validate",
                "management.endpoints.web.exposure.include", "health",
                "management.endpoint.health.show-details", "never",
                "management.endpoint.health.show-components", "never",
                "management.endpoint.health.probes.enabled", true,
                "spring.datasource.hikari.pool-name", "capstone"));
        app.addInitializers(context -> {
            var environment = context.getEnvironment();
            if (environment.acceptsProfiles(Profiles.of("local-lab"))) {
                // Defaults only: native CLI/property/environment precedence remains intact.
                var defaults = new java.util.HashMap<String, Object>();
                defaults.put("management.endpoints.web.exposure.include", "health,metrics");
                defaults.put("server.address", "127.0.0.1");
                String managementPort = environment.getProperty("management.server.port");
                String mainPort = environment.getProperty("server.port");
                // Native Boot treats port 0 as a separate management listener even when both are 0.
                boolean separatePort = managementPort != null
                        && (managementPort.equals("0") || !managementPort.equals(mainPort));
                // Only a truly separate management listener gets a management address: same-port
                // contexts keep native Boot's "no management-specific address" contract.
                if (separatePort) defaults.put("management.server.address", LOOPBACK);
                environment.getPropertySources().addBefore("defaultProperties",
                        new MapPropertySource("capstone-local-lab-defaults", defaults));
                String managementAddress = environment.getProperty("management.server.address");
                if (separatePort && !LOOPBACK.equals(managementAddress))
                    throw new IllegalStateException("local-lab requires loopback management.server.address");
            }
        });
        // SOLUTION-END
    }

    @Bean
    public JwtDecoder jwtDecoder(Environment environment) {
        // SOLUTION-BEGIN throw B5
        String issuer = required(environment, "capstone.jwt.issuer");
        String audience = required(environment, "capstone.jwt.audience");
        String configured = required(environment, "capstone.jwt.public-key");
        RSAPublicKey key;
        try {
            String base64 = configured.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
            key = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64)));
            if (key.getModulus().bitLength() < 2048) throw new IllegalArgumentException();
        } catch (Exception failure) {
            throw new IllegalStateException("Invalid capstone JWT RSA public key configuration");
        }
        var decoder = NimbusJwtDecoder.withPublicKey(key).signatureAlgorithm(SignatureAlgorithm.RS256).build();
        var timestamp = new JwtTimestampValidator(Duration.ofSeconds(60)); // System clock; never fixture clock.
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(timestamp, new JwtIssuerValidator(issuer), jwt ->
                jwt.getExpiresAt() != null && jwt.getNotBefore() != null
                        && jwt.getSubject() != null && !jwt.getSubject().isBlank()
                        && jwt.getAudience() != null && jwt.getAudience().contains(audience)
                        && (jwt.getClaim("token_use") == null || "access".equals(jwt.getClaim("token_use")))
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", null, null))));
        return decoder;
        // SOLUTION-END
    }

    private static String required(Environment environment, String name) {
        // SOLUTION-BEGIN throw B5
        String value = environment.getProperty(name);
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing required setting: " + name);
        return value;
        // SOLUTION-END
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, JwtDecoder decoder, Environment environment) throws Exception {
        // SOLUTION-BEGIN throw B5
        boolean localLab = environment.acceptsProfiles(Profiles.of("local-lab"));
        if (localLab) {
            if (!LOOPBACK.equals(environment.getProperty("server.address")))
                throw new IllegalStateException("local-lab requires server.address=127.0.0.1");
            String managementPort = environment.getProperty("management.server.port");
            String mainPort = environment.getProperty("server.port");
            if (managementPort != null && (managementPort.equals("0") || !managementPort.equals(mainPort))
                    && !LOOPBACK.equals(environment.getProperty("management.server.address")))
                throw new IllegalStateException("local-lab requires loopback management.server.address");
        }
        var bearer403 = new BearerTokenAccessDeniedHandler();
        org.springframework.security.web.AuthenticationEntryPoint unauthorized = CapstoneSecurity::unauthorized;
        org.springframework.security.web.access.AccessDeniedHandler forbidden = (request, response, failure) -> {
            bearer403.handle(request, response, failure);
            problem(response, response.getStatus(), "Forbidden", "Permission is required.");
        };
        return http.csrf(csrf -> csrf.disable()) // Only Authorization-header bearer API; no cookie/basic login.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(cache -> cache.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/liveness", "/actuator/health/readiness").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/orders", "/api/orders/{id}/cancel").hasAuthority("SCOPE_orders.write")
                            .requestMatchers(HttpMethod.GET, "/api/orders", "/api/orders/{id}").hasAuthority("SCOPE_orders.read")
                            .requestMatchers(HttpMethod.GET, "/api/products").hasAuthority("SCOPE_products.read");
                    if (localLab) auth.requestMatchers(HttpMethod.GET, "/actuator/metrics", "/actuator/metrics/{name}").hasAuthority("SCOPE_metrics.read");
                    auth.dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll().anyRequest().denyAll();
                })
                .exceptionHandling(errors -> errors.authenticationEntryPoint(unauthorized).accessDeniedHandler(forbidden))
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.decoder(decoder))
                        .authenticationEntryPoint(unauthorized).accessDeniedHandler(forbidden)).build();
        // SOLUTION-END
    }

    static void unauthorized(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
            org.springframework.security.core.AuthenticationException failure) throws java.io.IOException {
        // SOLUTION-BEGIN throw B5
        var safeFailure = failure;
        if (failure instanceof OAuth2AuthenticationException oauth) {
            var error = oauth.getError();
            OAuth2Error safeError = error instanceof BearerTokenError bearer
                    ? new BearerTokenError(error.getErrorCode(), bearer.getHttpStatus(), null, error.getUri(), bearer.getScope())
                    : new OAuth2Error(error.getErrorCode(), null, error.getUri());
            safeFailure = new OAuth2AuthenticationException(safeError);
        }
        new BearerTokenAuthenticationEntryPoint().commence(request, response, safeFailure);
        if (!(failure instanceof OAuth2AuthenticationException)) response.setHeader("WWW-Authenticate", "Bearer");
        problem(response, response.getStatus(), "Unauthorized", "Authentication is required.");
        // SOLUTION-END
    }

    private static void problem(jakarta.servlet.http.HttpServletResponse response, int status, String title, String detail)
            throws java.io.IOException {
        // SOLUTION-BEGIN throw B5
        response.setContentType("application/problem+json");
        response.getWriter().write("{\"type\":\"urn:phase05:security\",\"title\":\"" + title
                + "\",\"status\":" + status + ",\"detail\":\"" + detail + "\"}");
        // SOLUTION-END
    }
}
