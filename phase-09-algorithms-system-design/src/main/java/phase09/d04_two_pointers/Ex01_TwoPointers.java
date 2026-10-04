package phase09.d04_two_pointers;

/**
 * Two Pointers và Sliding Window.
 * Nguồn: 09-algorithms-system-design.md, mục 4, câu 1–5.
 * Cần làm trước: ôn mảng đã sắp xếp, vòng lặp và tính chất số không âm.
 * Cách làm: trả lời từng câu trong ANSWER tương ứng; chạy test B1 và so oracle vét cạn.
 *
 * Q1 [TỰ TRẢ LỜI] Điều kiện nào giúp sliding window duy trì được kết quả khi co/mở cửa sổ?
 *   Bắt đầu: xác định cách thay đổi cửa sổ ảnh hưởng đơn điệu đến điều kiện cần đạt.
 *   Tra cứu: mục 4 tài liệu nguồn; thử mở rộng/co cửa sổ trên dữ liệu có điều kiện.
 *   Hoàn thành khi: nêu được invariant và vì sao mỗi lần co/mở không bỏ qua nghiệm tốt hơn.
 * Q2 [TỰ TRẢ LỜI] Vì sao tổng đoạn con với số âm có thể không dùng được cách co cửa sổ thông thường?
 *   Bắt đầu: thêm một số âm vào cửa sổ và theo dõi tổng.
 *   Tra cứu: mục 4 tài liệu nguồn; so sánh tổng trước/sau khi thêm hoặc bỏ phần tử âm.
 *   Hoàn thành khi: giải thích được tổng không còn đơn điệu theo thao tác co/mở.
 * Q3 [TỰ TRẢ LỜI] Trong two pointers trên array đã sort, vì sao có thể di chuyển một pointer mà không bỏ sót nghiệm?
 *   Bắt đầu: xét tổng hai đầu khi tổng nhỏ hơn hoặc lớn hơn target.
 *   Tra cứu: mục 4 tài liệu nguồn; chứng minh bằng thứ tự tăng của array.
 *   Hoàn thành khi: chứng minh được dịch đầu trái lên hoặc đầu phải xuống loại bỏ vùng không thể có nghiệm.
 * Q4 [TỰ TRẢ LỜI] Invariant của cửa sổ là gì?
 *   Bắt đầu: viết điều kiện luôn đúng sau mỗi bước mở/co cửa sổ.
 *   Tra cứu: định nghĩa loop invariant và mục 4 tài liệu nguồn.
 *   Hoàn thành khi: phát biểu được invariant cụ thể cho bài cửa sổ đang xét.
 * Q5 [TỰ TRẢ LỜI] Cần kiểm tra trường hợp input rỗng nào?
 *   Bắt đầu: xác định kết quả hợp lệ khi không có phần tử để xét.
 *   Tra cứu: contract của pairSumSorted/minWindowLength và test B1.
 *   Hoàn thành khi: nêu được kết quả rỗng đúng contract và không truy cập phần tử ngoài biên.
 * B1 [CODE]: cài pairSumSorted và minWindowLength; chạy b01FindsPairsAndMinimumWindow cùng hai test oracle/validation.
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
class Ex01_TwoPointers {
    static int[] pairSumSorted(int[] values, int target) {
        throw new UnsupportedOperationException("TODO B1");
    }

    static int minWindowLength(int[] values, long target) {
        throw new UnsupportedOperationException("TODO B1");
    }
}
