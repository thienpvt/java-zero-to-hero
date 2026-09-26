package phase00.d06_polymorphism;

import phase00.support.Compiles;

/**
 * Đa hình — Bài 1: Interface, override và kế thừa một lớp
 *
 * Nguồn: 00-java-basics-review.md, mục 6 (Kế thừa, đa hình, interface và abstract class), câu 1, 2, 3.
 * Cần làm trước: không.
 * Cách làm: làm lần lượt từng câu; chạy test tương ứng trong Ex01_DispatchTest
 * bằng nút ▶ cạnh tên test (Ctrl+Shift+F10). Câu trước xanh mới sang câu sau.
 *
 * ─────────────────────────────────────────────────────────────────────
 * Q1 [DỰ ĐOÁN] Biến kiểu interface có thể trỏ đến những implementation nào?
 *   Bắt đầu   : đọc {@code Speaker}, {@code Dog}, {@code Cat}. Điền hai hằng Q1_*.
 *               Với Q1_NEW_INTERFACE, bỏ comment {@code new Speaker()} trong test, xem lỗi đỏ, comment lại.
 *   Kiểm chứng: Ctrl+Click vào {@code Speaker}. Alt+F8 gõ {@code dog.speak()} khi biến có kiểu Speaker.
 *   Hoàn thành khi: q01_prediction xanh; nói được biến interface trỏ tới mọi class implements nó, không {@code new} được chính interface.
 *
 * Q2 [DỰ ĐOÁN] Khi gọi một method đã override qua tham chiếu parent, bản nào chạy?
 *   Bắt đầu   : đọc {@code Animal} và {@code Dog}. Điền Q2_SPEAK_THROUGH_ANIMAL_REF bằng chuỗi {@code speak()} trả về
 *               khi biến kiểu {@code Animal} đang trỏ tới {@code Dog}.
 *   Kiểm chứng: Debug q02_prediction, F7 Step Into từ {@code animal.speak()}.
 *   Hoàn thành khi: q02_prediction xanh; nói được bản của object thật được chọn lúc chạy.
 *
 * Q3 [DỰ ĐOÁN] Java cho phép class {@code extends} nhiều class cùng lúc không?
 *   Bắt đầu   : điền Q3_CLASS_EXTENDS_TWO_CLASSES. Mẫu cú pháp nằm trong block comment cuối file —
 *               không bỏ comment tại chỗ vì file sẽ không còn biên dịch.
 *   Kiểm chứng: dán mẫu vào một file Java tạm, xem lỗi đỏ, rồi xóa file tạm.
 *   Hoàn thành khi: q03_prediction xanh.
 */
public class Ex01_Dispatch {

    interface Speaker {
        String speak();
    }

    static final class Dog implements Speaker {
        @Override
        public String speak() {
            return "gâu";
        }
    }

    static final class Cat implements Speaker {
        @Override
        public String speak() {
            return "meo";
        }
    }

    // Q1 — một biến Speaker gán được cả Dog lẫn Cat, và speak() chạy được?
    static final Boolean Q1_CAN_POINT_AT_DOG_AND_CAT = null;

    // Q1 — new Speaker() có biên dịch không?
    static final Compiles Q1_NEW_INTERFACE = null;

    static class Animal {
        String speak() {
            return "animal";
        }
    }

    static final class DogAnimal extends Animal {
        @Override
        String speak() {
            return "dog";
        }
    }

    // Q2 — Animal animal = new DogAnimal(); animal.speak()
    static final String Q2_SPEAK_THROUGH_ANIMAL_REF = null;

    static final Compiles Q3_CLASS_EXTENDS_TWO_CLASSES = null;

    /*
     * Mẫu không biên dịch, chỉ để đối chiếu Q3:
     * class Child extends Animal, DogAnimal {}
     */
}
