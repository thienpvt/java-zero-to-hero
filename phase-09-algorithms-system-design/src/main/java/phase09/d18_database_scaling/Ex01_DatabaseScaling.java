package phase09.d18_database_scaling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Database scaling và lựa chọn consistency.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 18, câu 1–5 và Bài thực hành.
 * Cần làm trước: query plan/index, transaction, replication lag; phase04/05 và đơn vị timer.
 * Cách làm: mở file trong IDE, trả lời Q1–Q5 rồi chạy Ex01_DatabaseScalingTest cho B1.
 * <p>
 * Q1 [TỰ TRẢ LỜI] Index giúp query nào và gây cost gì cho write/storage?
 *   Bắt đầu: chọn filter/sort của query và xem execution plan.
 *   Tra cứu: selectivity, composite index và write amplification.
 *   Hoàn thành khi: ANSWER Q1 nối access pattern với chi phí cập nhật và dung lượng.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Read replica có thể trả dữ liệu cũ trong tình huống nào?
 *   Bắt đầu: vẽ thời điểm commit primary và apply replica.
 *   Tra cứu: asynchronous replication, lag và read-after-write.
 *   Hoàn thành khi: ANSWER Q2 nêu cửa sổ stale và invariant cần đọc primary.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Shard key kém có thể tạo hot partition ra sao?
 *   Bắt đầu: phân bố request và dữ liệu theo key.
 *   Tra cứu: skew, hot tenant và range partition.
 *   Hoàn thành khi: ANSWER Q3 giải thích một shard bão hòa dù shard khác còn rảnh.
 * <p>
 * Q4 [TỰ TRẢ LỜI] CAP áp dụng trade-off trong điều kiện nào?
 *   Bắt đầu: tách mạng giữa hai replica rồi xét một read/write.
 *   Tra cứu: partition, linearizability và availability theo CAP.
 *   Hoàn thành khi: ANSWER Q4 tránh khẩu hiệu chọn hai trong ba mọi lúc.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Availability và durability khác nhau thế nào?
 *   Bắt đầu: phân biệt phục vụ request với giữ write đã xác nhận.
 *   Tra cứu: failover, replication và backup/restore.
 *   Hoàn thành khi: ANSWER Q5 có tình huống mất availability nhưng vẫn giữ dữ liệu.
 * <p>
 * B1 [CODE/THÍ NGHIỆM]: cùng dataset disposable, concurrency, request cap và duration;
 * đổi đúng một setting có lý do, không để harness đổi cấu hình app.
 * Contract: run baseline/changed; root URI chỉ HTTP localhost/127.0.0.1/[::1], không
 * user-info/query/fragment; redirects NEVER, không proxy. GET /api/products?page=0&amp;size=20.
 * Token products.read không lưu/log; CLI lấy PHASE09_BEARER_TOKEN, thiếu token là lỗi.
 * Concurrency 1..16 (mặc định 4), requests 1..1000, timeout dương tối đa 10s (mặc định 2s),
 * duration dương tối đa 30s (mặc định 5s). Warmup đúng hai request, không nằm trong mẫu đo.
 * Method không nhận null; malformed input lỗi IllegalArgumentException trước networking.
 * CSV UTF-8 không quote, header run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds;
 * đúng bốn row baseline/changed × start/end, cùng hikaricp.connections.acquire và pool.
 * UTC kết thúc Z; snapshot bracket request trong margin 5s; window tối đa 300s.
 * Count nguyên không âm; cumulative seconds hữu hạn không âm; tăng trong mỗi run,
 * deltaCount dương; reset giữa hai run hợp lệ. Mean acquisition = delta seconds / delta count:
 * pool wait + acquisition overhead, không phải lease/usage hoặc HTTP latency.
 * Properties có đúng 11 key theo plan; validate trước write, CREATE_NEW không ghi đè.
 * HTTP p50/p95 nearest-rank nanoseconds; throughput = successes / elapsed seconds;
 * 2xx thành công, mọi lỗi khác ghi errors; CLI lỗi trả UNAVAILABLE và exit 1.
 * <p>
 * CLI: run baseline http://127.0.0.1:8080 4 1000 5 pool=4 --output baseline.properties
 * (dạng ngắn: run baseline http://127.0.0.1:8080 1000 pool=4 --output baseline.properties,
 * vẫn bắt buộc requests, mặc định concurrency=4/duration=5s); lặp changed sau đổi setting.
 * compare baseline.properties changed.properties telemetry.csv
 * Export actual protected Actuator metric bằng PowerShell, scope metrics.read; không poll/parse JSON.
 * Hoàn thành khi: tests hợp đồng xanh và báo cáo actual HTTP/pool độc lập kèm cấu hình,
 * môi trường, khoảng đo, độ lệch, error rate và giới hạn. Tests dùng HttpServer nhỏ chỉ kiểm tra
 * harness, không phải production measurement. Phase05 app chưa được xây: actual capstone
 * measurement PENDING/NOT RUN; không tạo zero telemetry hoặc coi tests là số đo app.
 */
public class Ex01_DatabaseScaling {
    public static void main(String[] args) {
        // SOLUTION-BEGIN throw B1
        System.exit(cli(args, System.getenv("PHASE09_BEARER_TOKEN"), System.out));
        // SOLUTION-END
    }

    static int cli(String[] args, String token, PrintStream out) {
        // SOLUTION-BEGIN throw B1
        try {
            if (args.length == 4 && args[0].equals("compare")) {
                var report = compare(readRun(Path.of(args[1])), readRun(Path.of(args[2])), Path.of(args[3]));
                printRun(report.baseline(), report.baselineAcquisition(), out);
                printRun(report.changed(), report.changedAcquisition(), out);
            } else if ((args.length == 9 || args.length == 7) && args[0].equals("run")) {
                boolean defaults = args.length == 7;
                int label = defaults ? 4 : 6;
                if (!args[label + 1].equals("--output")) throw unavailable();
                Path output = Path.of(args[label + 2]);
                if (Files.exists(output)) throw unavailable();
                HttpRun result = measureRun(args[1], URI.create(args[2]), token,
                        defaults ? 4 : Integer.parseInt(args[3]), Integer.parseInt(args[defaults ? 3 : 4]),
                        Duration.ofSeconds(2), Duration.ofSeconds(defaults ? 5 : Long.parseLong(args[5])), args[label]);
                if (result.errors() != 0) throw unavailable();
                writeRun(result, output);
                out.println("HTTP run=" + result.run() + " samples=" + result.samples()
                        + " successes=" + result.successes() + " errors=" + result.errors()
                        + " p50Nanos=" + result.p50Nanos() + " p95Nanos=" + result.p95Nanos()
                        + " throughputPerSecond=" + result.throughputPerSecond());
            } else throw unavailable();
            return 0;
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            out.println("UNAVAILABLE: interrupted measurement");
            return 1;
        } catch (IOException | RuntimeException failure) {
            // Do not print exception messages: supplied URI/setting/path could contain secrets.
            out.println("UNAVAILABLE: invalid input, failed HTTP run, or missing/invalid telemetry/artifact");
            return 1;
        }
        // SOLUTION-END
    }

    record MetricSnapshot(String run, String snapshot, Instant capturedAtUtc, String metricName,
                          String pool, long count, double totalTimeSeconds) {}
    record HttpRun(String run, String settingLabel, Instant start, Instant end, int samples,
                   long successes, long errors, long elapsedNanos, long p50Nanos, long p95Nanos,
                   double throughputPerSecond) {
        HttpRun {
            // SOLUTION-BEGIN throw B1
            if (!isRun(run) || !nonblank(settingLabel) || start == null || end == null
                    || !start.isBefore(end) || Duration.between(start, end).compareTo(Duration.ofSeconds(300)) > 0
                    || samples <= 0 || samples > 1000 || successes < 0 || errors < 0
                    || successes > samples || errors > samples || successes + errors != samples
                    || elapsedNanos <= 0 || p50Nanos < 0 || p95Nanos < p50Nanos || p95Nanos > elapsedNanos
                    || !Double.isFinite(throughputPerSecond) || throughputPerSecond < 0) throw unavailable();
            long wallNanos = Duration.between(start, end).toNanos();
            // Wall clock may drift slightly; a unit error or changed clock must not look like valid data.
            if (Math.abs(wallNanos - elapsedNanos) > 10_000_000L
                    || Math.abs(throughputPerSecond - successes * 1_000_000_000.0 / elapsedNanos)
                    > Math.max(1e-9, throughputPerSecond * 1e-9)) throw unavailable();
            // SOLUTION-END
        }
    }
    record AcquisitionResult(String run, long deltaCount, double meanAcquisitionSeconds) {}
    record ComparisonReport(HttpRun baseline, HttpRun changed, AcquisitionResult baselineAcquisition,
                            AcquisitionResult changedAcquisition) {}

    static void writeRun(HttpRun run, Path output) throws IOException {
        // SOLUTION-BEGIN throw B1
        if (run == null || output == null) throw unavailable();
        // Revalidate before touching disk, even if callers supply an existing record.
        new HttpRun(run.run(), run.settingLabel(), run.start(), run.end(), run.samples(), run.successes(),
                run.errors(), run.elapsedNanos(), run.p50Nanos(), run.p95Nanos(), run.throughputPerSecond());
        Properties fields = new Properties();
        fields.setProperty("run", run.run()); fields.setProperty("setting", run.settingLabel());
        fields.setProperty("start", run.start().toString()); fields.setProperty("end", run.end().toString());
        fields.setProperty("samples", Integer.toString(run.samples()));
        fields.setProperty("successes", Long.toString(run.successes())); fields.setProperty("errors", Long.toString(run.errors()));
        fields.setProperty("elapsedNanos", Long.toString(run.elapsedNanos()));
        fields.setProperty("p50Nanos", Long.toString(run.p50Nanos())); fields.setProperty("p95Nanos", Long.toString(run.p95Nanos()));
        fields.setProperty("throughputPerSecond", Double.toString(run.throughputPerSecond()));
        try (var writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
            fields.store(writer, "Phase09 HTTP summary; no token or payload");
        }
        // SOLUTION-END
    }

    static HttpRun readRun(Path input) throws IOException {
        // SOLUTION-BEGIN throw B1
        if (input == null) throw unavailable();
        Properties fields = new Properties();
        try (var reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) { fields.load(reader); }
        Set<String> keys = Set.of("run", "setting", "start", "end", "samples", "successes", "errors",
                "elapsedNanos", "p50Nanos", "p95Nanos", "throughputPerSecond");
        if (!fields.stringPropertyNames().equals(keys)
                || !fields.getProperty("throughputPerSecond").matches("(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?")) throw unavailable();
        try {
            return new HttpRun(fields.getProperty("run"), fields.getProperty("setting"),
                    utc(fields.getProperty("start")), utc(fields.getProperty("end")),
                    Integer.parseInt(fields.getProperty("samples")), Long.parseLong(fields.getProperty("successes")),
                    Long.parseLong(fields.getProperty("errors")), Long.parseLong(fields.getProperty("elapsedNanos")),
                    Long.parseLong(fields.getProperty("p50Nanos")), Long.parseLong(fields.getProperty("p95Nanos")),
                    Double.parseDouble(fields.getProperty("throughputPerSecond")));
        } catch (RuntimeException malformed) { throw unavailable(); }
        // SOLUTION-END
    }
    static List<MetricSnapshot> parseCsv(Reader input) throws IOException {
        // SOLUTION-BEGIN throw B1
        if (input == null) throw unavailable();
        BufferedReader reader = input instanceof BufferedReader buffered ? buffered : new BufferedReader(input);
        if (!"run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds".equals(reader.readLine())) throw unavailable();
        List<MetricSnapshot> rows = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (rows.size() == 4 || line.contains("\"") || line.isBlank()) throw unavailable();
            String[] fields = line.split(",", -1);
            if (fields.length != 7 || !fields[5].matches("[0-9]+")) throw unavailable();
            try {
                rows.add(new MetricSnapshot(fields[0], fields[1], utc(fields[2]), fields[3], fields[4],
                        Long.parseLong(fields[5]), Double.parseDouble(fields[6])));
            } catch (RuntimeException malformed) { throw unavailable(); }
        }
        checkRows(rows);
        return List.copyOf(rows);
        // SOLUTION-END
    }

    static AcquisitionResult validateRun(List<MetricSnapshot> rows, String run, Instant requestStart, Instant requestEnd) {
        // SOLUTION-BEGIN throw B1
        checkRows(rows);
        if (!isRun(run) || requestStart == null || requestEnd == null || !requestStart.isBefore(requestEnd)
                || Duration.between(requestStart, requestEnd).compareTo(Duration.ofSeconds(300)) > 0) throw unavailable();
        MetricSnapshot start = null, end = null;
        for (MetricSnapshot row : rows) {
            if (row.run().equals(run)) {
                if (row.snapshot().equals("start")) start = row; else end = row;
            }
        }
        if (start == null || end == null || start.capturedAtUtc().isAfter(requestStart)
                || start.capturedAtUtc().isBefore(requestStart.minusSeconds(5))
                || end.capturedAtUtc().isBefore(requestEnd) || end.capturedAtUtc().isAfter(requestEnd.plusSeconds(5))) throw unavailable();
        long deltaCount = end.count() - start.count();
        double deltaSeconds = end.totalTimeSeconds() - start.totalTimeSeconds();
        if (deltaCount <= 0 || deltaSeconds < 0) throw unavailable();
        return new AcquisitionResult(run, deltaCount, deltaSeconds / deltaCount);
        // SOLUTION-END
    }
    static HttpRun measureRun(String run, URI baseUri, String bearerToken, int concurrency, int requests,
                              Duration timeout, Duration duration, String settingLabel) throws InterruptedException {
        // SOLUTION-BEGIN throw B1
        if (!isRun(run) || !nonblank(settingLabel) || baseUri == null || bearerToken == null
                || !bearerToken.matches("[A-Za-z0-9._~+/=-]+") || concurrency < 1 || concurrency > 16
                || requests < 1 || requests > 1000 || timeout == null || timeout.isNegative() || timeout.isZero()
                || timeout.compareTo(Duration.ofSeconds(10)) > 0 || duration == null || duration.isNegative()
                || duration.isZero() || duration.compareTo(Duration.ofSeconds(30)) > 0) throw unavailable();
        String host = baseUri.getHost();
        if (!"http".equalsIgnoreCase(baseUri.getScheme()) || host == null
                || !(host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1") || host.equals("[::1]") || host.equals("::1"))
                || baseUri.getRawUserInfo() != null || baseUri.getRawQuery() != null || baseUri.getRawFragment() != null
                || !(baseUri.getRawPath().isEmpty() || baseUri.getRawPath().equals("/"))
                || baseUri.getPort() == 0 || baseUri.getPort() > 65535) throw unavailable();
        URI endpoint = baseUri.resolve("/api/products?page=0&size=20");
        var workers = Executors.newFixedThreadPool(concurrency);
        // Separate bounded HTTP callbacks prevent blocking send workers from starving their own I/O.
        var httpExecutor = Executors.newFixedThreadPool(concurrency + 1);
        HttpClient client = HttpClient.newBuilder().connectTimeout(timeout).followRedirects(HttpClient.Redirect.NEVER)
                .executor(httpExecutor).proxy(new ProxySelector() {
                    @Override public List<Proxy> select(URI uri) { return List.of(Proxy.NO_PROXY); }
                    @Override public void connectFailed(URI uri, SocketAddress address, IOException failure) {}
                }).build();
        try {
            for (int warmup = 0; warmup < 2; warmup++) {
                HttpRequest request = HttpRequest.newBuilder(endpoint).timeout(timeout)
                        .header("Authorization", "Bearer " + bearerToken).GET().build();
                try {
                    int status = client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
                    if (status < 200 || status >= 300) throw unavailable();
                } catch (IOException failure) { throw unavailable(); }
            }
            List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
            AtomicInteger next = new AtomicInteger(), successes = new AtomicInteger();
            Instant start = Instant.now();
            long startedNanos = System.nanoTime(), durationNanos = duration.toNanos();
            for (int worker = 0; worker < concurrency; worker++) {
                workers.submit(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        long remaining = durationNanos - (System.nanoTime() - startedNanos);
                        if (remaining <= 0 || next.getAndIncrement() >= requests) break;
                        long before = System.nanoTime();
                        try {
                            HttpRequest request = HttpRequest.newBuilder(endpoint)
                                    .timeout(Duration.ofNanos(Math.min(timeout.toNanos(), remaining)))
                                    .header("Authorization", "Bearer " + bearerToken).GET().build();
                            int status = client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
                            if (status >= 200 && status < 300) successes.incrementAndGet();
                        } catch (IOException | RuntimeException failure) {
                            // Count as error; never retain exception text or response payload.
                        } catch (InterruptedException interrupted) {
                            Thread.currentThread().interrupt();
                        } finally { latencies.add(System.nanoTime() - before); }
                    }
                });
            }
            workers.shutdown();
            if (!workers.awaitTermination(duration.toNanos() + timeout.toNanos() + 2_000_000_000L, TimeUnit.NANOSECONDS)) throw unavailable();
            long elapsed = System.nanoTime() - startedNanos;
            Instant end = Instant.now();
            if (latencies.isEmpty()) throw unavailable();
            List<Long> sorted = new ArrayList<>(latencies); Collections.sort(sorted);
            int samples = sorted.size();
            return new HttpRun(run, settingLabel, start, end, samples, successes.get(), samples - successes.get(), elapsed,
                    sorted.get((samples + 1) / 2 - 1), sorted.get((int) Math.ceil(samples * 0.95) - 1),
                    successes.get() * 1_000_000_000.0 / elapsed);
        } finally {
            workers.shutdownNow(); client.shutdownNow(); httpExecutor.shutdownNow();
            if (!workers.awaitTermination(2, TimeUnit.SECONDS)
                    || !client.awaitTermination(Duration.ofSeconds(2))
                    || !httpExecutor.awaitTermination(2, TimeUnit.SECONDS)) throw unavailable();
        }
        // SOLUTION-END
    }

    static ComparisonReport compare(HttpRun baseline, HttpRun changed, Path telemetryCsv) throws IOException {
        // SOLUTION-BEGIN throw B1
        if (baseline == null || changed == null || telemetryCsv == null
                || !baseline.run().equals("baseline") || !changed.run().equals("changed")
                || baseline.errors() != 0 || changed.errors() != 0) throw unavailable();
        try (var reader = Files.newBufferedReader(telemetryCsv, StandardCharsets.UTF_8)) {
            List<MetricSnapshot> rows = parseCsv(reader);
            return new ComparisonReport(baseline, changed,
                    validateRun(rows, "baseline", baseline.start(), baseline.end()),
                    validateRun(rows, "changed", changed.start(), changed.end()));
        }
        // SOLUTION-END
    }

    private static void checkRows(List<MetricSnapshot> rows) {
        // SOLUTION-BEGIN throw B1
        if (rows == null || rows.size() != 4) throw unavailable();
        Set<String> keys = new HashSet<>(); String pool = null;
        for (MetricSnapshot row : rows) {
            if (row == null || !isRun(row.run()) || !("start".equals(row.snapshot()) || "end".equals(row.snapshot()))
                    || row.capturedAtUtc() == null || !"hikaricp.connections.acquire".equals(row.metricName())
                    || !nonblank(row.pool()) || row.count() < 0 || !Double.isFinite(row.totalTimeSeconds())
                    || row.totalTimeSeconds() < 0 || !keys.add(row.run() + "/" + row.snapshot())) throw unavailable();
            if (pool == null) pool = row.pool(); else if (!pool.equals(row.pool())) throw unavailable();
        }
        for (String run : List.of("baseline", "changed")) {
            MetricSnapshot start = null, end = null;
            for (MetricSnapshot row : rows) if (row.run().equals(run)) {
                if (row.snapshot().equals("start")) start = row; else end = row;
            }
            if (start == null || end == null || !start.capturedAtUtc().isBefore(end.capturedAtUtc())
                    || end.count() <= start.count() || end.totalTimeSeconds() < start.totalTimeSeconds()) throw unavailable();
        }
        // SOLUTION-END
    }

    private static Instant utc(String value) {
        // SOLUTION-BEGIN throw B1
        if (value == null || !value.endsWith("Z")) throw unavailable();
        try { return Instant.parse(value); } catch (RuntimeException malformed) { throw unavailable(); }
        // SOLUTION-END
    }

    private static boolean isRun(String value) {
        // SOLUTION-BEGIN throw B1
        return "baseline".equals(value) || "changed".equals(value);
        // SOLUTION-END
    }

    private static boolean nonblank(String value) {
        // SOLUTION-BEGIN throw B1
        return value != null && !value.isBlank() && value.chars().noneMatch(Character::isISOControl);
        // SOLUTION-END
    }

    private static IllegalArgumentException unavailable() {
        // SOLUTION-BEGIN throw B1
        return new IllegalArgumentException("UNAVAILABLE: invalid measurement contract");
        // SOLUTION-END
    }

    private static void printRun(HttpRun run, AcquisitionResult acquisition, PrintStream out) {
        // SOLUTION-BEGIN throw B1
        out.println("HTTP run=" + run.run() + " start=" + run.start() + " end=" + run.end()
                + " samples=" + run.samples() + " successes=" + run.successes() + " errors=" + run.errors()
                + " p50Nanos=" + run.p50Nanos() + " p95Nanos=" + run.p95Nanos()
                + " throughputPerSecond=" + run.throughputPerSecond());
        out.println("POOL run=" + acquisition.run() + " deltaCount=" + acquisition.deltaCount()
                + " meanAcquisitionSeconds=" + acquisition.meanAcquisitionSeconds()
                + " (pool wait + acquisition overhead; not lease time or HTTP latency)");
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Index hỗ trợ filter/join/order phù hợp prefix và selectivity; kiểm tra query plan, không hứa mọi query đều nhanh.
 * Mỗi insert/update/delete phải duy trì index, tăng I/O, write amplification và storage; index dư làm write chậm.
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * Primary đã commit nhưng replica asynchronous chưa apply, hoặc replica bị lag sau outage/tải cao.
 * Read-after-write có thể thấy bản cũ; dùng primary hoặc cơ chế đọc chờ version khi invariant cần dữ liệu mới.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Key lệch theo hot tenant hoặc tăng theo thời gian có thể dồn hầu hết write/read vào một partition.
 * Tổng capacity còn rảnh nhưng hot shard hết CPU/I/O; chọn theo access pattern, đo skew và kế hoạch rebalance.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Khi network partition ngăn replica liên lạc, operation cần linearizability phải từ chối/chờ một phía
 * thay vì phục vụ mọi request bằng dữ liệu có thể mâu thuẫn. Không phải chọn hai trong ba cho mọi thời điểm.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Availability là khả năng phục vụ request; durability là giữ write đã xác nhận qua failure theo cam kết.
 * DB có thể offline nhưng write vẫn bền trên disk; async failover có thể phục vụ lại nhưng mất write chưa replicate.
 * Backup/restore và replication cần kiểm chứng RPO/RTO, không thay thế lẫn nhau.
 * SOLUTION-END
 */
/* MODEL B1:
 * SOLUTION-BEGIN
 * Trước tiên tối ưu query/index và kiểm tra bottleneck; chọn đổi pool=4 thành pool=8, giữ dữ liệu/page/concurrency/cap/duration.
 * Export COUNT/TOTAL_TIME cùng pool trước warmup và sau đo, trong 5s margin; acquisition có thể gồm traffic khác/warmup.
 * Báo cáo HTTP samples, success/error rate, p50/p95, throughput riêng với delta acquisition mean và timestamps.
 * Không suy ra p95 acquisition từ mean; tăng pool có thể đẩy tải sang DB. Lặp có kiểm soát, ghi JIT/cache/noise và môi trường.
 * Phase05 app chưa được xây: actual measurement NOT RUN; chỉ hợp đồng harness được kiểm bằng JDK HttpServer nhỏ.
 * SOLUTION-END
 */
