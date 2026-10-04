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
        throw new UnsupportedOperationException("TODO B5");
    }

    @Bean
    public JwtDecoder jwtDecoder(Environment environment) {
        throw new UnsupportedOperationException("TODO B5");
    }

    private static String required(Environment environment, String name) {
        throw new UnsupportedOperationException("TODO B5");
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, JwtDecoder decoder, Environment environment) throws Exception {
        throw new UnsupportedOperationException("TODO B5");
    }

    static void unauthorized(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
            org.springframework.security.core.AuthenticationException failure) throws java.io.IOException {
        throw new UnsupportedOperationException("TODO B5");
    }

    private static void problem(jakarta.servlet.http.HttpServletResponse response, int status, String title, String detail)
            throws java.io.IOException {
        throw new UnsupportedOperationException("TODO B5");
    }
}
