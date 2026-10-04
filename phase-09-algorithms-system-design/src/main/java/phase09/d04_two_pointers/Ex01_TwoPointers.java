package phase09.d04_two_pointers;

/**
 * Two Pointers và Sliding Window.
 * Q1: Điều kiện nào giúp sliding window duy trì được kết quả khi co/mở cửa sổ? [TỰ TRẢ LỜI]
 * Q2: Vì sao tổng đoạn con với số âm có thể không dùng được cách co cửa sổ thông thường? [TỰ TRẢ LỜI]
 * Q3: Trong two pointers trên array đã sort, vì sao có thể di chuyển một pointer mà không bỏ sót nghiệm? [TỰ TRẢ LỜI]
 * Q4: Invariant của cửa sổ là gì? [TỰ TRẢ LỜI]
 * Q5: Cần kiểm tra trường hợp input rỗng nào? [TỰ TRẢ LỜI]
 * B1: Giải một bài tìm cặp trên array đã sort và một bài cửa sổ con với dữ liệu không âm.
 * Viết thêm brute force cho input nhỏ để đối chiếu.
 */
class Ex01_TwoPointers {
    static int[] pairSumSorted(int[] values, int target) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(values, "values");
        for (int i = 1; i < values.length; i++) {
            if (values[i] < values[i - 1]) throw new IllegalArgumentException("values must be ascending");
        }
        int left = 0, right = values.length - 1;
        while (left < right) {
            long sum = (long) values[left] + values[right];
            if (sum == target) {
                int first = left + 1;
                while ((long) values[left] + values[first] < target) first++;
                return new int[]{left, first};
            }
            if (sum < target) left++;
            else right--;
        }
        return new int[]{-1, -1};
        // SOLUTION-END
    }

    static int minWindowLength(int[] values, long target) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(values, "values");
        if (target <= 0) throw new IllegalArgumentException("target must be positive");
        for (int value : values) {
            if (value < 0) throw new IllegalArgumentException("values must be nonnegative");
        }
        int start = 0, minimum = Integer.MAX_VALUE;
        long sum = 0;
        for (int end = 0; end < values.length; end++) {
            sum += values[end];
            while (sum >= target) {
                minimum = Math.min(minimum, end - start + 1);
                sum -= values[start++];
            }
        }
        return minimum == Integer.MAX_VALUE ? 0 : minimum;
        // SOLUTION-END
    }
}
