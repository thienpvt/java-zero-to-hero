package phase02.d19_capstone;

import java.math.BigDecimal;

/**
 * Bài tích hợp — Account (rút tiền có race)
 * <p>
 * Nguồn: 02-jvm-concurrency.md, mục Practical Exercise.
 * Cần làm trước: d07_race.
 * Cách làm: đọc bước B1. File này cho sẵn, đừng sửa. Chạy RaceDemoTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * B1 [THÍ NGHIỆM] {@code withdraw} so số dư với số tiền rút rồi mới gán số dư mới, không có khóa
 *     giữa hai bước. Đây là bản có race.
 *   Bắt đầu   : mở method {@code withdraw} bên dưới (Ctrl+N → Account, Ctrl+F12 → withdraw).
 *               Giữ nguyên thân method. Đừng thêm {@code synchronized} vào file này.
 *   Kiểm chứng: chạy {@code RaceDemo.main} hoặc b01_experimentRuns. F7 vào {@code withdraw}
 *               để thấy lần {@code compareTo} và lần {@code subtract} là hai bước riêng.
 *               Đừng lấy một số dư in ra làm đáp án.
 *   Hoàn thành khi: viết xong khối OBSERVATION B1 trong {@code RaceDemo.java}, nêu một cách
 *               hai thread xen kẽ làm mất một lần rút.
 */
public class Account {

    private BigDecimal balance;

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal balance() {
        return balance;
    }

    /** Bản Practical Exercise. Giữ nguyên thân method này. */
    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
        }
    }
}
