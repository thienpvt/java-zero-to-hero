package phase09.d02_array_string;

/**
 * Array và String.
 * Q1: Truy cập array theo index và chèn vào đầu có chi phí thế nào? [TỰ TRẢ LỜI]
 * Q2: Vì sao `String.length()` không phải lúc nào cũng là số ký tự người dùng nhìn thấy? [TỰ TRẢ LỜI]
 * Q3: Khi nào dùng code point thay vì `char`? [TỰ TRẢ LỜI]
 * Q4: Cộng hai index hoặc tính `n * n` có thể sai thế nào khi vượt giới hạn `int`? [TỰ TRẢ LỜI]
 * Q5: Chuỗi rỗng và null có cùng ý nghĩa không? [TỰ TRẢ LỜI]
 * B1: Viết hàm xử lý chuỗi theo một quy tắc rõ ràng. Test chuỗi rỗng, Unicode ngoài BMP,
 * ký tự lặp, null theo contract và độ dài sát giới hạn đầu vào.
 */
class Ex01_ArrayString {
    static String reverseCodePoints(String value) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(value, "value");
        return new StringBuilder(value.length()).append(value).reverse().toString();
        // SOLUTION-END
    }

    static int checkedArea(int width, int height) {
        // SOLUTION-BEGIN throw B1
        if (width < 0 || height < 0) throw new IllegalArgumentException("dimensions must be nonnegative");
        return Math.multiplyExact(width, height);
        // SOLUTION-END
    }
}
