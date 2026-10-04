package phase09.d18_database_scaling;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static phase09.d18_database_scaling.Ex01_DatabaseScaling.*;

class Ex01_DatabaseScalingTest {
    @TempDir Path temp;
    private static final Instant START = Instant.parse("2026-10-04T12:00:01Z");
    private static final String HEADER = "run,snapshot,capturedAtUtc,metricName,pool,count,totalTimeSeconds\n";
    private static final String CSV = HEADER
            + "baseline,start,2026-10-04T12:00:00Z,hikaricp.connections.acquire,main,10,1.0\n"
            + "baseline,end,2026-10-04T12:00:07Z,hikaricp.connections.acquire,main,14,1.8\n"
            + "changed,start,2026-10-04T12:01:00Z,hikaricp.connections.acquire,main,0,0.0\n"
            + "changed,end,2026-10-04T12:01:07Z,hikaricp.connections.acquire,main,8,0.8\n";

    private HttpRun baseline() {
        return new HttpRun("baseline", "pool=4", START, START.plusSeconds(5),
                10, 10, 0, 5_000_000_000L, 10_000L, 30_000L, 2.0);
    }

    private HttpRun changed() {
        return new HttpRun("changed", "pool=8", START.plusSeconds(60), START.plusSeconds(65),
                10, 10, 0, 5_000_000_000L, 10_000L, 20_000L, 2.0);
    }

    @Test void b1_propertiesRoundTripHasExactKeysAndNoCredential() throws Exception {
        var run = baseline();
        Path output = temp.resolve("baseline.properties");
        writeRun(run, output);
        assertEquals(run, readRun(output));
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(output)) { properties.load(reader); }
        assertEquals(11, properties.size());
        assertEquals("pool=4", properties.getProperty("setting"));
        assertFalse(Files.readString(output).contains("Authorization"));
        assertThrows(java.nio.file.FileAlreadyExistsException.class, () -> writeRun(run, output));
    }

    @Test void b1_propertiesRejectMissingUnknownMalformedAndInconsistentFields() throws Exception {
        Path valid = temp.resolve("valid.properties");
        writeRun(baseline(), valid); // TODO must escape on learner before invalid-input assertions.
        Properties original = new Properties();
        try (var reader = Files.newBufferedReader(valid)) { original.load(reader); }
        String[][] mutations = {
                {"run", ""}, {"setting", " "}, {"start", "not-an-instant"},
                {"end", "2026-10-04T12:00:00Z"}, {"samples", "1.5"}, {"samples", "-1"},
                {"successes", "9"}, {"errors", "-1"}, {"elapsedNanos", "0"},
                {"elapsedNanos", "1000"}, {"p50Nanos", "-1"}, {"p50Nanos", "40000"},
                {"p95Nanos", "6000000000"}, {"throughputPerSecond", "NaN"},
                {"throughputPerSecond", "Infinity"}, {"throughputPerSecond", "-1"},
                {"throughputPerSecond", "3"}, {"successes", "9223372036854775808"}
        };
        for (String[] mutation : mutations) {
            Properties broken = new Properties(); broken.putAll(original);
            broken.setProperty(mutation[0], mutation[1]);
            Path file = temp.resolve("broken.properties");
            try (var writer = Files.newBufferedWriter(file)) { broken.store(writer, null); }
            assertThrows(IllegalArgumentException.class, () -> readRun(file), mutation[0] + "=" + mutation[1]);
        }
        for (String key : original.stringPropertyNames()) {
            Properties broken = new Properties(); broken.putAll(original); broken.remove(key);
            Path file = temp.resolve("missing.properties");
            try (var writer = Files.newBufferedWriter(file)) { broken.store(writer, null); }
            assertThrows(IllegalArgumentException.class, () -> readRun(file), key);
        }
        original.setProperty("token", "never-accepted");
        Path extra = temp.resolve("extra.properties");
        try (var writer = Files.newBufferedWriter(extra)) { original.store(writer, null); }
        assertThrows(IllegalArgumentException.class, () -> readRun(extra));
    }

    @Test void b1_httpRunChecksClockUnitsCountsAndPercentilesBeforeWrite() throws Exception {
        writeRun(baseline(), temp.resolve("valid.properties"));
        assertThrows(IllegalArgumentException.class, () -> new HttpRun("baseline", "pool=4", START,
                START.plusSeconds(5), 10, Long.MAX_VALUE, Long.MAX_VALUE, 5_000_000_000L, 1, 2, 2));
        assertThrows(IllegalArgumentException.class, () -> new HttpRun("baseline", "pool=4", START,
                START.plusSeconds(301), 10, 10, 0, 301_000_000_000L, 1, 2, 10.0 / 301));
        assertThrows(IllegalArgumentException.class, () -> writeRun(null, temp.resolve("absent.properties")));
        assertFalse(Files.exists(temp.resolve("absent.properties")));
    }

    @Test void b1_csvComputesMeanAndAllowsCounterResetBetweenRuns() throws Exception {
        var rows = parseCsv(new StringReader(CSV));
        assertEquals(4, rows.size());
        var acquisition = validateRun(rows, "baseline", START, START.plusSeconds(5));
        assertEquals(4, acquisition.deltaCount());
        assertEquals(0.2, acquisition.meanAcquisitionSeconds(), 1e-12);
        Path csv = temp.resolve("telemetry.csv"); Files.writeString(csv, CSV, StandardCharsets.UTF_8);
        var comparison = compare(baseline(), changed(), csv);
        assertEquals(0.1, comparison.changedAcquisition().meanAcquisitionSeconds(), 1e-12);
        assertEquals(baseline(), comparison.baseline());
    }

    @Test void b1_csvRejectsMalformedHeaderRowsNumbersAndUtc() throws Exception {
        parseCsv(new StringReader(CSV));
        List<String> invalid = List.of(
                CSV.replace("totalTimeSeconds", "seconds"), CSV.replace("main,10,1.0", "main,1.5,1.0"),
                CSV.replace("main,10,1.0", "main,-1,1.0"), CSV.replace("main,10,1.0", "main,10,-1.0"),
                CSV.replace("main,10,1.0", "main,10,NaN"), CSV.replace("main,10,1.0", "main,10,Infinity"),
                CSV.replace("12:00:00Z", "12:00:00+00:00"), CSV.replace("12:00:00Z", "bad"),
                CSV.replace("baseline,start", "unknown,start"), CSV.replace("baseline,start", "baseline,middle"),
                CSV.replace("hikaricp.connections.acquire", "hikaricp.connections.usage"),
                CSV.replace("main,10,1.0", "\"main\",10,1.0"),
                CSV.replace("main,10,1.0", ",10,1.0"), CSV + "\n");
        for (String input : invalid) {
            assertThrows(IllegalArgumentException.class, () -> parseCsv(new StringReader(input)));
        }
    }

    @Test void b1_csvRejectsDuplicatesMissingMismatchesAndDecreasingCounters() throws Exception {
        var rows = parseCsv(new StringReader(CSV));
        validateRun(rows, "baseline", START, START.plusSeconds(5));
        List<String> invalid = List.of(
                CSV.replace("baseline,end", "baseline,start"),
                CSV.substring(0, CSV.lastIndexOf("changed,end")),
                CSV.replace("main,14,1.8", "other,14,1.8"),
                CSV.replace("main,14,1.8", "main,9,1.8"),
                CSV.replace("main,14,1.8", "main,14,0.8"),
                CSV.replace("main,14,1.8", "main,10,1.8"),
                CSV.replace("changed,start", "baseline,start"));
        for (String input : invalid) {
            assertThrows(IllegalArgumentException.class, () -> {
                var parsed = parseCsv(new StringReader(input));
                validateRun(parsed, "baseline", START, START.plusSeconds(5));
            });
        }
        var direct = new ArrayList<>(rows);
        direct.set(0, new MetricSnapshot("baseline", "start", START.minusSeconds(1),
                "hikaricp.connections.acquire", "wrong", 10, 1.0));
        assertThrows(IllegalArgumentException.class, () -> validateRun(direct, "baseline", START, START.plusSeconds(5)));
    }

    @Test void b1_telemetryMustBracketRequestWithinFiveSeconds() throws Exception {
        var rows = parseCsv(new StringReader(CSV));
        validateRun(rows, "baseline", START, START.plusSeconds(5));
        for (String input : List.of(CSV.replace("12:00:00Z", "11:59:55Z"),
                CSV.replace("12:00:00Z", "12:00:02Z"),
                CSV.replace("12:00:07Z", "12:00:05Z"), CSV.replace("12:00:07Z", "12:00:12Z"))) {
            assertThrows(IllegalArgumentException.class, () -> validateRun(parseCsv(new StringReader(input)),
                    "baseline", START, START.plusSeconds(5)));
        }
        assertThrows(IllegalArgumentException.class, () -> validateRun(rows, "baseline", START, START.plusSeconds(301)));
        assertThrows(IllegalArgumentException.class, () -> validateRun(rows, "baseline", START, START));
    }

    @Test void b1_comparisonRejectsWrongRunsFailedHttpAndWrongPoolAcrossRuns() throws Exception {
        Path csv = temp.resolve("telemetry.csv"); Files.writeString(csv, CSV);
        compare(baseline(), changed(), csv);
        assertThrows(IllegalArgumentException.class, () -> compare(changed(), baseline(), csv));
        var failed = new HttpRun("baseline", "pool=4", START, START.plusSeconds(5),
                10, 9, 1, 5_000_000_000L, 1, 2, 1.8);
        assertThrows(IllegalArgumentException.class, () -> compare(failed, changed(), csv));
        Files.writeString(csv, CSV.replace("acquire,main,0", "acquire,other,0")
                .replace("acquire,main,8", "acquire,other,8"));
        assertThrows(IllegalArgumentException.class, () -> compare(baseline(), changed(), csv));
        assertThrows(java.io.IOException.class, () -> compare(baseline(), changed(), temp.resolve("missing.csv")));
    }

    @Test void b1_measureSendsFixedGetBearerAndExcludesTwoWarmupRequests() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        AtomicInteger invalid = new AtomicInteger();
        HttpServer server = server();
        server.createContext("/api/products", exchange -> {
            calls.incrementAndGet();
            if (!"GET".equals(exchange.getRequestMethod())
                    || !"page=0&size=20".equals(exchange.getRequestURI().getRawQuery())
                    || !"Bearer local-test-token".equals(exchange.getRequestHeaders().getFirst("Authorization"))) {
                invalid.incrementAndGet();
            }
            byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (var stream = exchange.getResponseBody()) { stream.write(body); }
        });
        server.start();
        try {
            var run = measureRun("baseline", uri(server), "local-test-token", 2, 5,
                    Duration.ofSeconds(2), Duration.ofSeconds(5), "pool=4");
            assertEquals(5, run.samples()); assertEquals(5, run.successes()); assertEquals(0, run.errors());
            assertEquals(7, calls.get()); assertEquals(0, invalid.get());
            assertTrue(run.p50Nanos() <= run.p95Nanos());
            assertEquals(run.successes() * 1_000_000_000.0 / run.elapsedNanos(), run.throughputPerSecond(), 1e-9);
        } finally { server.stop(0); }
    }

    @Test void b1_measureRejectsBadInputBeforeAnyNetworkRequest() throws Exception {
        parseCsv(new StringReader(CSV));
        AtomicInteger calls = new AtomicInteger(); HttpServer server = server();
        server.createContext("/", exchange -> { calls.incrementAndGet(); exchange.sendResponseHeaders(200, -1); exchange.close(); });
        server.start();
        try {
            for (String address : List.of("https://localhost", "http://example.com", "http://127.0.0.2",
                    "http://user@localhost", "http://localhost/?secret=yes", "http://localhost/#fragment",
                    "http://localhost/api", "http://localhost:0", "http://2130706433")) {
                assertThrows(IllegalArgumentException.class, () -> measureRun("baseline", URI.create(address),
                        "local-test-token", 4, 5, Duration.ofSeconds(2), Duration.ofSeconds(5), "pool=4"));
            }
            for (int concurrency : new int[]{0, 17}) {
                assertThrows(IllegalArgumentException.class, () -> measureRun("baseline", uri(server),
                        "local-test-token", concurrency, 5, Duration.ofSeconds(2), Duration.ofSeconds(5), "pool=4"));
            }
            for (int requests : new int[]{0, 1001}) {
                assertThrows(IllegalArgumentException.class, () -> measureRun("baseline", uri(server),
                        "local-test-token", 4, requests, Duration.ofSeconds(2), Duration.ofSeconds(5), "pool=4"));
            }
            for (Duration timeout : List.of(Duration.ZERO, Duration.ofSeconds(11))) {
                assertThrows(IllegalArgumentException.class, () -> measureRun("baseline", uri(server),
                        "local-test-token", 4, 5, timeout, Duration.ofSeconds(5), "pool=4"));
            }
            for (Duration duration : List.of(Duration.ZERO, Duration.ofSeconds(31))) {
                assertThrows(IllegalArgumentException.class, () -> measureRun("baseline", uri(server),
                        "local-test-token", 4, 5, Duration.ofSeconds(2), duration, "pool=4"));
            }
            for (String token : List.of("", "secret\r\nInjected: header")) {
                assertThrows(IllegalArgumentException.class, () -> measureRun("baseline", uri(server),
                        token, 4, 5, Duration.ofSeconds(2), Duration.ofSeconds(5), "pool=4"));
            }
            assertEquals(0, calls.get());
        } finally { server.stop(0); }
    }

    @Test void b1_redirectIsCountedAsErrorAndNeverFollowed() throws Exception {
        AtomicInteger calls = new AtomicInteger(); AtomicInteger followed = new AtomicInteger();
        HttpServer server = server();
        server.createContext("/api/products", exchange -> {
            int call = calls.incrementAndGet();
            if (call > 2) exchange.getResponseHeaders().add("Location", uri(server) + "/redirect-target");
            exchange.sendResponseHeaders(call <= 2 ? 200 : 302, -1); exchange.close();
        });
        server.createContext("/redirect-target", exchange -> { followed.incrementAndGet(); exchange.sendResponseHeaders(200, -1); exchange.close(); });
        server.start();
        try {
            var run = measureRun("baseline", uri(server), "local-test-token", 1, 3,
                    Duration.ofSeconds(2), Duration.ofSeconds(5), "pool=4");
            assertEquals(3, run.errors()); assertEquals(0, run.successes()); assertEquals(0, followed.get());
        } finally { server.stop(0); }
    }

    @Test void b1_timeoutCountsErrorAndDeadlineBoundsStartedRequests() throws Exception {
        AtomicInteger calls = new AtomicInteger(); CountDownLatch release = new CountDownLatch(1);
        var serverExecutor = Executors.newFixedThreadPool(2);
        HttpServer server = server(); server.setExecutor(serverExecutor);
        server.createContext("/api/products", exchange -> {
            int call = calls.incrementAndGet();
            if (call > 2) {
                try { release.await(3, TimeUnit.SECONDS); }
                catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); }
            }
            try { exchange.sendResponseHeaders(200, -1); }
            finally { exchange.close(); }
        });
        server.start();
        try {
            var run = measureRun("baseline", uri(server), "local-test-token", 1, 100,
                    Duration.ofMillis(100), Duration.ofMillis(150), "pool=4");
            assertTrue(run.samples() >= 1 && run.samples() <= 2);
            assertEquals(run.samples(), run.errors()); assertEquals(0, run.successes());
        } finally {
            release.countDown(); server.stop(0); serverExecutor.shutdownNow();
            assertTrue(serverExecutor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    @Test void b1_cliMissingTelemetryFailsUnavailableWithoutSecretOrPayload() throws Exception {
        Path first = temp.resolve("baseline.properties"), second = temp.resolve("changed.properties");
        writeRun(baseline(), first); writeRun(changed(), second);
        var bytes = new java.io.ByteArrayOutputStream();
        try (var out = new java.io.PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            assertEquals(1, cli(new String[]{"compare", first.toString(), second.toString(),
                    temp.resolve("missing.csv").toString()}, "local-test-token", out));
            assertEquals(1, cli(new String[]{"run", "baseline", "http://example.com", "4", "5", "5",
                    "pool=4", "--output", temp.resolve("absent.properties").toString()}, "local-test-token", out));
        }
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("UNAVAILABLE"));
        assertFalse(bytes.toString(StandardCharsets.UTF_8).contains("local-test-token"));
        assertFalse(Files.exists(temp.resolve("absent.properties")));
    }

    @Test void b1_cliComparisonReportsSeparateHttpAndAcquisitionUnits() throws Exception {
        Path first = temp.resolve("baseline.properties"), second = temp.resolve("changed.properties"), csv = temp.resolve("actual.csv");
        writeRun(baseline(), first); writeRun(changed(), second); Files.writeString(csv, CSV);
        var bytes = new java.io.ByteArrayOutputStream();
        try (var out = new java.io.PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            assertEquals(0, cli(new String[]{"compare", first.toString(), second.toString(), csv.toString()}, null, out));
        }
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("p95Nanos=30000"));
        assertTrue(output.contains("deltaCount=4"));
        assertTrue(output.contains("meanAcquisitionSeconds="));
    }

    @Test void b1_propertiesRequireBaseTenNumbers() throws Exception {
        Path valid = temp.resolve("decimal.properties"); writeRun(baseline(), valid);
        Properties fields = new Properties();
        try (var reader = Files.newBufferedReader(valid)) { fields.load(reader); }
        fields.setProperty("throughputPerSecond", "0x1.0p1");
        Path invalid = temp.resolve("hex.properties");
        try (var writer = Files.newBufferedWriter(invalid)) { fields.store(writer, null); }
        assertThrows(IllegalArgumentException.class, () -> readRun(invalid));
    }

    @Test void b1_cliFailedHttpDoesNotWriteSummaryAndMissingEnvTokenDoesNotConnect() throws Exception {
        AtomicInteger calls = new AtomicInteger(); HttpServer server = server();
        server.createContext("/api/products", exchange -> {
            int call = calls.incrementAndGet();
            exchange.sendResponseHeaders(call <= 2 ? 200 : 503, -1); exchange.close();
        });
        server.start();
        try (var out = new java.io.PrintStream(new java.io.ByteArrayOutputStream())) {
            String[] args = {"run", "baseline", uri(server).toString(), "1", "3", "5", "pool=4", "--output",
                    temp.resolve("failed.properties").toString()};
            assertEquals(1, cli(args, null, out)); assertEquals(0, calls.get());
            assertEquals(1, cli(args, "local-test-token", out)); assertEquals(5, calls.get());
            assertFalse(Files.exists(temp.resolve("failed.properties")));
        } finally { server.stop(0); }
    }

    @Test void b1_cliDefaultsKeepRequestCountExplicit() throws Exception {
        AtomicInteger calls = new AtomicInteger(); HttpServer server = server();
        server.createContext("/api/products", exchange -> {
            calls.incrementAndGet(); exchange.sendResponseHeaders(200, -1); exchange.close();
        });
        server.start();
        try (var out = new java.io.PrintStream(new java.io.ByteArrayOutputStream())) {
            Path output = temp.resolve("explicit-cap.properties");
            assertEquals(0, cli(new String[]{"run", "baseline", uri(server).toString(), "3", "pool=4",
                    "--output", output.toString()}, "local-test-token", out));
            assertEquals(3, readRun(output).samples()); assertEquals(5, calls.get());
        } finally { server.stop(0); }
    }

    private static HttpServer server() throws Exception {
        return HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
    }

    private static URI uri(HttpServer server) {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort());
    }
}
