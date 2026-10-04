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
 *
 */
/* ANSWER Q2:
 *
 */
/* ANSWER Q3:
 *
 */
/* ANSWER Q4:
 *
 */
/* ANSWER Q5:
 *
 */
class Ex01_ArrayString {
    static String reverseCodePoints(String value) {
        throw new UnsupportedOperationException("TODO B1");
    }

    static int checkedArea(int width, int height) {
        throw new UnsupportedOperationException("TODO B1");
    }
}
