package phase04.d01_schema;

import java.util.List;

/**
 * Mô hình quan hệ — Bài 1: tạo schema cho đơn hàng.
 * <p>Nguồn: 04-database-persistence.md, mục 1. Cần làm trước: kiến thức SQL và khóa quan hệ.
 * Cách làm: chạy {@code Ex01_RelationalSchemaTest}; migration được áp dụng lên PostgreSQL dùng riêng.
 * Hoàn thành khi B1 chứng minh FK, unique và giá snapshot bằng thao tác DB.
 * Q1 [TỰ TRẢ LỜI] Primary key khác unique constraint ở điểm nào?
 * Bắt đầu: viết ANSWER Q1; hoàn thành khi phân biệt định danh hàng và tính duy nhất của giá trị.
 * Q2 [TỰ TRẢ LỜI] Khi nào một cột nên cho phép {@code NULL}, và {@code NULL} khác giá trị rỗng thế nào?
 * Bắt đầu: viết ANSWER Q2; hoàn thành khi giải thích được trạng thái chưa biết/không có và chuỗi rỗng.
 * Q3 [THÍ NGHIỆM] Vì sao đơn hàng cần giữ đơn giá tại thời điểm mua?
 * Bắt đầu: B1 cập nhật giá product sau khi tạo item; hoàn thành khi giá item vẫn giữ nguyên.
 * Q4 [THÍ NGHIỆM] Foreign key bảo vệ được lỗi nào mà validation ở Java không đủ bảo vệ?
 * Bắt đầu: B1 chèn order tham chiếu customer không tồn tại; hoàn thành khi DB từ chối.
 * Q5 [TỰ TRẢ LỜI] Khi nào denormalization đáng cân nhắc và chi phí nào phát sinh?
 * Bắt đầu: viết ANSWER Q5; hoàn thành khi nêu workload/lý do và rủi ro đồng bộ dữ liệu.
 */
public class Ex01_RelationalSchema {
    /** Trả về migration SQL cho bốn bảng domain; giá tiền dùng USD trong lab. */
    static List<String> migrations() {
        throw new UnsupportedOperationException("TODO B1");
    }
}

/* ANSWER Q1:
 *
 */
/* ANSWER Q2:
 *
 */
/* OBSERVATION Q3 / ANSWER Q3:
 *
 */
/* OBSERVATION Q4 / ANSWER Q4:
 *
 */
/* ANSWER Q5:
 *
 */
