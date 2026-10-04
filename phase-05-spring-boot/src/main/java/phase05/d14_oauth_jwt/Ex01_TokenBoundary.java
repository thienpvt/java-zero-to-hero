package phase05.d14_oauth_jwt;

import java.security.interfaces.RSAPublicKey;
import java.time.Clock;
import java.time.Duration;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

/**
 * <h2>Chủ đề 14 — Ranh giới token</h2>
 * <p>Nguồn: 05-spring-boot.md §14, Q1–Q5, B14.
 * Tiên quyết: Java 21, HTTP, Spring Security/JUnit, PostgreSQL thật.
 * IntelliJ: mở Ex01_TokenBoundaryTest, chạy test rồi đặt breakpoint trong body B14.
 * Chỉ cấu hình explicit của topic; không scan phase05 hoặc dùng identity provider mạng.</p>
 * Q1 [DỰ ĐOÁN] Access token và ID token có cùng mục đích không?
 * <p>Nguồn §14 Q1; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở decoder, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TokenBoundaryTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q1; không suy luận từ mock decoder.</p>
 * Q2 [CODE] Vì sao xác minh issuer/signature nhưng bỏ audience vẫn có thể nhận token không dành cho API?
 * <p>Nguồn §14 Q2; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở decoder audience, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TokenBoundaryTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q2; không suy luận từ mock decoder.</p>
 * Q3 [TỰ TRẢ LỜI] `ROLE_` và `SCOPE_` khác semantics thế nào?
 * <p>Nguồn §14 Q3; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở authority mapping, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TokenBoundaryTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q3; không suy luận từ mock decoder.</p>
 * Q4 [THÍ NGHIỆM] Stateless API có luôn an toàn khi tắt CSRF không?
 * <p>Nguồn §14 Q4; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở bearer-only CSRF, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TokenBoundaryTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q4; không suy luận từ mock decoder.</p>
 * Q5 [CODE] CORS có thể thay thế kiểm tra authorization không?
 * <p>Nguồn §14 Q5; tiên quyết: HTTP/principal/DB boundary.
 * Cách làm: mở decoder/filter CORS, đọc Javadoc Spring Security/Boot theo dependency đã cài;
 * debugger ở boundary và chạy Ex01_TokenBoundaryTest. Hoàn thành: quan sát status/state
 * hoặc kết quả DB/metric thật, ghi ANSWER Q5; không suy luận từ mock decoder.</p>
 * ANSWER Q1:
 *
 * ANSWER Q2:
 *
 * ANSWER Q3:
 *
 * ANSWER Q4:
 *
 * ANSWER Q5:
 *
 */
public final class Ex01_TokenBoundary {
    private Ex01_TokenBoundary() {}

    /** Trusted local public key only; issuer lookup/network discovery intentionally absent. */
    public static JwtDecoder decoder(RSAPublicKey key, Clock clock, String issuer, String audience) {
        throw new UnsupportedOperationException("TODO B14");
    }
}
