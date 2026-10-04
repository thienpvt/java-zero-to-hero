package phase09.d01_complexity;

/**
 * Độ phức tạp và cách kiểm chứng.
 * Q1: Vì sao hai vòng lặp lồng nhau không phải lúc nào cũng O(n²)? [TỰ TRẢ LỜI]
 * Q2: Amortized complexity khác expected complexity thế nào? [TỰ TRẢ LỜI]
 * Q3: HashMap có bảo đảm lookup worst-case O(1) không? [TỰ TRẢ LỜI]
 * Q4: O(n log n) tăng thế nào so với O(n²) khi n lớn? [TỰ TRẢ LỜI]
 * Q5: Khi nào benchmark không đủ để chứng minh thuật toán phù hợp? [TỰ TRẢ LỜI]
 * B1: So sánh hai cách giải cùng bài toán với input nhỏ và lớn. Ghi thời gian,
 * bộ nhớ, phân tích complexity và giới hạn mà mỗi cách còn dùng được.
 */
class Ex01_Complexity {
    static long countPairsQuadratic(int[] values, int target) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(values, "values");
        long count = 0;
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                if ((long) values[i] + values[j] == target) count++;
            }
        }
        return count;
        // SOLUTION-END
    }

    static long countPairsLinear(int[] values, int target) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(values, "values");
        java.util.Map<Integer, Long> seen = new java.util.HashMap<>();
        long pairs = 0;
        for (int value : values) {
            long complement = (long) target - value;
            if (complement >= Integer.MIN_VALUE && complement <= Integer.MAX_VALUE) {
                pairs += seen.getOrDefault((int) complement, 0L);
            }
            seen.merge(value, 1L, Long::sum);
        }
        return pairs;
        // SOLUTION-END
    }

    static String experimentReport(int n) {
        // SOLUTION-BEGIN throw B1
        if (n < 0) throw new IllegalArgumentException("n must be nonnegative");
        long quadratic = (long) n * (n - 1) / 2;
        return "n=" + n + "; quadratic=" + quadratic + "; linear=" + n;
        // SOLUTION-END
    }
}
