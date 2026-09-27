package phase00.d05_encapsulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import phase00.support.Compiles;

/**
 * Đóng gói — Bài 1: Constructor, static và getter
 *
 * Nguồn: 00-java-basics-review.md, mục 5 (Class, object và đóng gói), câu 1, 2, 4.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_ConstructorAndGetterTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * <p>
 * Q1 [DỰ ĐOÁN] Constructor có kiểu trả về không? Nếu không khai báo constructor thì điều gì được tạo?
 *   Bắt đầu   : đọc {@code NoCtor} và {@code OnlyArg}. Điền ba hằng Q1_*. Với
 *               Q1_DEFAULT_CTOR_AFTER_CUSTOM, bỏ comment {@code new OnlyArg()} trong test, xem lỗi đỏ, comment lại.
 *   Kiểm chứng: Ctrl+F12 trên {@code OnlyArg} — danh sách constructor không có kiểu trả về.
 *               Chạy q01_prediction.
 *   Hoàn thành khi: q01_prediction xanh; nói được constructor mặc định chỉ xuất hiện khi mình không khai báo constructor nào.
 * <p>
 * Q2 [DỰ ĐOÁN] {@code static} method có truy cập trực tiếp instance field không? Vì sao?
 *   Bắt đầu   : đọc comment trong {@code Box}. Điền Q2_STATIC_READS_INSTANCE_FIELD.
 *               Bỏ comment method {@code read} để xem lỗi đỏ, rồi comment lại.
 *   Kiểm chứng: đọc thông báo IDE trên dòng {@code return value}. Static không gắn với một instance.
 *   Hoàn thành khi: q02_prediction xanh.
 * <p>
 * Q4 [DỰ ĐOÁN + CODE] Một getter trả về trực tiếp {@code List} nội bộ có thể gây lỗi gì?
 *   Bắt đầu   : đọc {@code LeakyBag} (cho sẵn), điền Q4_LEAKY_GETTER_SEES_CALLER_ADD, rồi cài {@code Bag.tags}.
 *   Kiểm chứng: Debug q04_leakyGetter, xem {@code size} trước và sau {@code view.add}. Ctrl+Q trên {@code List.copyOf}.
 *   Code      : {@code Bag.tags()} trả một list không cho sửa. Caller {@code add} không đổi {@code size()} nội bộ.
 *   Hoàn thành khi: q04_* xanh; nói được vì sao không trả đúng field {@code tags}.
 */
public class Ex01_ConstructorAndGetter {

    static final class NoCtor {
    }

    static final class OnlyArg {
        final int n;

        OnlyArg(int n) {
            this.n = n;
        }
    }

    static final Boolean Q1_CONSTRUCTOR_HAS_RETURN_TYPE = null;
    static final Boolean Q1_NO_CTOR_CAN_BE_NEW = null;
    static final Compiles Q1_DEFAULT_CTOR_AFTER_CUSTOM = null;

    static final class Box {
        int value = 1;

        // static int read() {
        //     return value;
        // }
    }

    static final Compiles Q2_STATIC_READS_INSTANCE_FIELD = null;

    static final class LeakyBag {
        private final List<String> tags;

        LeakyBag(List<String> tags) {
            this.tags = new ArrayList<>(tags);
        }

        List<String> tags() {
            return tags;
        }

        int size() {
            return tags.size();
        }
    }

    // Q4 — caller add vào list do LeakyBag.tags() trả về, size nội bộ có tăng không?
    static final Boolean Q4_LEAKY_GETTER_SEES_CALLER_ADD = null;

    static final class Bag {
        private final List<String> tags;

        Bag(List<String> tags) {
            this.tags = new ArrayList<>(Objects.requireNonNull(tags, "tags"));
        }

        List<String> tags() {
            throw new UnsupportedOperationException("TODO Q4");
        }

        int size() {
            return tags.size();
        }
    }
}
