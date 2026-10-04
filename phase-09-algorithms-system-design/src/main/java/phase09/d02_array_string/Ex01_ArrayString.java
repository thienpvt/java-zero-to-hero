package phase09.d02_array_string;

/**
 * Array và String.
 * Nguồn: 09-algorithms-system-design.md, mục 2, câu 1–5.
 * Cần làm trước: ôn mảng, UTF-16, String và giới hạn kiểu int.
 * Cách làm: trả lời từng câu trong ANSWER tương ứng; chạy test B1 với chuỗi biên.
 *
 * Q1 [TỰ TRẢ LỜI] Truy cập array theo index và chèn vào đầu có chi phí thế nào?
 *   Bắt đầu: đếm thao tác đọc index và số phần tử cần dịch khi chèn đầu.
 *   Tra cứu: mục 2 tài liệu nguồn; dùng một array nhỏ để liệt kê vị trí dịch.
 *   Hoàn thành khi: giải thích được truy cập index O(1), chèn đầu O(n).
 * Q2 [TỰ TRẢ LỜI] Vì sao `String.length()` không phải lúc nào cũng là số ký tự người dùng nhìn thấy?
 *   Bắt đầu: so sánh UTF-16 code unit, code point và grapheme.
 *   Tra cứu: Java 21 String API, codePointCount và Unicode grapheme documentation.
 *   Hoàn thành khi: nêu được supplementary character dùng hai char và grapheme có thể gồm nhiều code point.
 * Q3 [TỰ TRẢ LỜI] Khi nào dùng code point thay vì `char`?
 *   Bắt đầu: xác định bài toán xử lý từng Unicode scalar hay code unit.
 *   Tra cứu: Java 21 String API về codePointAt/codePoints và mục 2 tài liệu nguồn.
 *   Hoàn thành khi: chọn code point khi cần xử lý ký tự ngoài BMP như một đơn vị.
 * Q4 [TỰ TRẢ LỜI] Cộng hai index hoặc tính `n * n` có thể sai thế nào khi vượt giới hạn `int`?
 *   Bắt đầu: tính thử phép cộng và nhân quanh Integer.MAX_VALUE.
 *   Tra cứu: Java Language Specification về integer operations; Math.multiplyExact.
 *   Hoàn thành khi: mô tả overflow số nguyên và nêu cách nâng kiểu trước phép tính hoặc kiểm tra overflow.
 * Q5 [TỰ TRẢ LỜI] Chuỗi rỗng và null có cùng ý nghĩa không?
 *   Bắt đầu: phân biệt giá trị String tồn tại nhưng rỗng với reference không trỏ tới String.
 *   Tra cứu: contract API đang xét và mục 2 tài liệu nguồn.
 *   Hoàn thành khi: giải thích được chuỗi rỗng là giá trị hợp lệ, còn null cần contract xử lý riêng.
 * B1 [CODE]: cài reverseCodePoints và checkedArea; chạy b01ReversesCodePoints, b01RejectsInvalidInputs.
 */

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * Đọc phần tử bằng index có chi phí O(1).
 * Chèn ở đầu cần dời tối đa n phần tử để tạo chỗ, nên chi phí O(n).
 * SOLUTION-END
 */
/* ANSWER Q2:
 * SOLUTION-BEGIN
 * String.length() đếm UTF-16 code unit, không đếm grapheme người dùng nhìn như một ký tự.
 * Code point ngoài BMP chiếm hai code unit; một grapheme như chữ kèm dấu có thể gồm nhiều code point.
 * SOLUTION-END
 */
/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Dùng code point khi thao tác cần xem ký tự ngoài BMP như một đơn vị thay vì hai char UTF-16.
 * Dùng grapheme segmentation khi yêu cầu là ký tự hiển thị theo ngôn ngữ người dùng.
 * SOLUTION-END
 */
/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Phép toán int tràn sẽ wrap quanh miền giá trị, nên index hoặc diện tích có thể thành số âm/sai.
 * Nâng toán hạng lên long trước phép tính hoặc dùng Math.*Exact để phát hiện overflow.
 * SOLUTION-END
 */
/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Chuỗi rỗng là đối tượng String hợp lệ có độ dài 0.
 * Null là reference không có giá trị String; API phải quy định chấp nhận hay từ chối riêng.
 * SOLUTION-END
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
