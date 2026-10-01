package phase03.d03_lsp;

/**
 * LSP — Bài 2: hợp đồng hẹp
 * <p>
 * Nguồn: 03-clean-code-design-patterns.md, mục 3, câu 4.
 * Cần làm trước: Ex01_RectangleSquare.
 * Cách làm: chạy test trong Ex02_FatContractTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q4 [DỰ ĐOÁN + CODE] Khi nào composition tốt hơn inheritance?
 *   Bắt đầu   : điền Q4_COMPOSITION_OVER_INHERITANCE, rồi cài {@code DocumentPrinter.print}
 *               bằng cách gọi {@code printer.print} (composition), không kéo dài class cha.
 *   Kiểm chứng: chạy q04_prediction và q04_printerDelegates.
 *   Hoàn thành khi: q04_prediction xanh và q04_printerDelegates xanh; ANSWER Q4 nêu has-a thay is-a.
 */
public class Ex02_FatContract {

    /** Khe cắm in, hợp đồng hẹp. */
    public interface Printer {

        String print(String text);
    }

    /** Khe cắm quét, tách khỏi {@code Printer}. */
    public interface Scanner {

        String scan();
    }

    public static final class ConsolePrinter implements Printer {

        @Override
        public String print(String text) {
            return "print:" + text;
        }
    }

    // Q4 — composition tốt hơn inheritance khi quan hệ chỉ là has-a.
    static final Boolean Q4_COMPOSITION_OVER_INHERITANCE = true; // SOLUTION-VALUE

    /** Dùng composition: giữ một {@code Printer}, không extends. */
    public static final class DocumentPrinter {

        private final Printer printer;

        public DocumentPrinter(Printer printer) {
            this.printer = printer;
        }

        public String print(String text) {
            // SOLUTION-BEGIN throw Q4
            return printer.print(text);
            // SOLUTION-END
        }
    }
}

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Composition tốt hơn khi quan hệ là has-a: DocumentPrinter có một Printer, không phải là một Printer.
 * extends sẽ kéo theo mọi method và field của cha, kể cả phần không liên quan.
 * Giữ một field cho phép đổi implementation lúc runtime và thay bằng fake trong test.
 * SOLUTION-END
 */
