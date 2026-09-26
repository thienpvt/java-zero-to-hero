package phase01.d05_equals_hashcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static phase01.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d05_equals_hashcode.Ex01_EqualsContract.CaseInsensitiveName;
import phase01.d05_equals_hashcode.Ex01_EqualsContract.Money;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_EqualsContractTest {

    private static final String HINT_Q1 =
            "So amountMinor bằng == và currency bằng Objects.equals; dùng Objects.hash(amountMinor, currency).";
    private static final String HINT_Q5 =
            "Ctrl+B vào Integer.valueOf(int) để xem ngưỡng cache IntegerCache (-128..127).";

    @Test
    @DisplayName("Q1 reflexive: money.equals(money) luôn true")
    void q01_reflexive() {
        Money money = new Money(1000, "USD");
        assertTrue(money.equals(money), "Một object phải equals với chính nó.");
    }

    @Test
    @DisplayName("Q1 symmetric: a.equals(b) và b.equals(a) phải cùng kết quả")
    void q01_symmetric() {
        Money a = new Money(1000, "USD");
        Money b = new Money(1000, "USD");
        assertTrue(a.equals(b), "a và b có cùng amountMinor/currency nên phải equals.");
        assertTrue(b.equals(a), "Nếu a.equals(b) true thì b.equals(a) cũng phải true.");
    }

    @Test
    @DisplayName("Q1 transitive: a equals b, b equals c thì a equals c")
    void q01_transitive() {
        Money a = new Money(1000, "USD");
        Money b = new Money(1000, "USD");
        Money c = new Money(1000, "USD");
        assertTrue(a.equals(b), "a phải equals b.");
        assertTrue(b.equals(c), "b phải equals c.");
        assertTrue(a.equals(c), "Vì a equals b và b equals c nên a phải equals c.");
    }

    @Test
    @DisplayName("Q1 consistent: gọi equals nhiều lần cho cùng kết quả")
    void q01_consistentAcrossRepeatedCalls() {
        Money a = new Money(1000, "USD");
        Money b = new Money(1000, "USD");
        for (int i = 0; i < 10; i++) {
            assertTrue(a.equals(b), "Lần gọi thứ " + i + " phải vẫn trả về true như các lần trước.");
        }
    }

    @Test
    @DisplayName("Q1 equals(null) phải trả về false")
    void q01_equalsNullIsFalse() {
        Money a = new Money(1000, "USD");
        assertFalse(a.equals(null), "equals(null) không được ném NPE và phải trả về false.");
    }

    @Test
    @DisplayName("Q1 equals với kiểu khác phải trả về false")
    void q01_equalsDifferentTypeIsFalse() {
        Money a = new Money(1000, "USD");
        assertFalse(a.equals("1000 USD"), "So với một String không phải Money phải trả về false.");
    }

    @Test
    @DisplayName("Q1 khác currency thì không equals")
    void q01_differentCurrencyIsFalse() {
        Money a = new Money(1000, "USD");
        Money b = new Money(1000, "EUR");
        assertFalse(a.equals(b), "Cùng amountMinor nhưng khác currency thì không được equals.");
    }

    @Test
    @DisplayName("Q1 khác amount thì không equals")
    void q01_differentAmountIsFalse() {
        Money a = new Money(1000, "USD");
        Money b = new Money(2000, "USD");
        assertFalse(a.equals(b), "Cùng currency nhưng khác amountMinor thì không được equals.");
    }

    @Test
    @DisplayName("Q1 hai Money equals thì phải cùng hashCode")
    void q01_equalImpliesSameHashCode() {
        Money a = new Money(1000, "USD");
        Money b = new Money(1000, "USD");
        assertTrue(a.equals(b), "Điều kiện tiên quyết: a và b phải equals.");
        assertEquals(a.hashCode(), b.hashCode(), HINT_Q1);
    }

    @Test
    @DisplayName("Q1 dự đoán: CaseInsensitiveName.equals(String) và chiều ngược lại")
    void q01_prediction_nameEqualsStringIsNotSymmetric() {
        CaseInsensitiveName name = new CaseInsensitiveName("Alice");
        boolean nameEqualsString = name.equals("alice");
        boolean stringEqualsName = "alice".equals(name);

        assertPrediction("Q1_NAME_EQUALS_STRING", nameEqualsString,
                Ex01_EqualsContract.Q1_NAME_EQUALS_STRING,
                "CaseInsensitiveName.equals nhận nhánh instanceof String và so equalsIgnoreCase.");
        assertPrediction("Q1_STRING_EQUALS_NAME", stringEqualsName,
                Ex01_EqualsContract.Q1_STRING_EQUALS_NAME,
                "String.equals(Object) chỉ trả true khi tham số cũng là String, nên luôn false ở đây.");
    }

    @Test
    @DisplayName("Q5 dự đoán: new String(\"hi\") == new String(\"hi\")")
    void q05_newStringsDoubleEquals() {
        String a = new String("hi");
        String b = new String("hi");
        assertPrediction("Q5_NEW_STRINGS_DOUBLE_EQUALS", a == b,
                Ex01_EqualsContract.Q5_NEW_STRINGS_DOUBLE_EQUALS, HINT_Q5);
    }

    @Test
    @DisplayName("Q5 dự đoán: hai literal String \"hi\" có cùng == không")
    void q05_stringLiteralsDoubleEquals() {
        String a = "hi";
        String b = "hi";
        assertPrediction("Q5_STRING_LITERALS_DOUBLE_EQUALS", a == b,
                Ex01_EqualsContract.Q5_STRING_LITERALS_DOUBLE_EQUALS, HINT_Q5);
    }

    @Test
    @DisplayName("Q5 dự đoán: Integer 127 có dùng cache (== true) không")
    void q05_integer127DoubleEquals() {
        Integer a = 127;
        Integer b = 127;
        assertPrediction("Q5_INTEGER_127_DOUBLE_EQUALS", a == b,
                Ex01_EqualsContract.Q5_INTEGER_127_DOUBLE_EQUALS, HINT_Q5);
    }

    @Test
    @DisplayName("Q5 dự đoán: Integer 128 có dùng cache (== true) không")
    void q05_integer128DoubleEquals() {
        Integer a = 128;
        Integer b = 128;
        assertPrediction("Q5_INTEGER_128_DOUBLE_EQUALS", a == b,
                Ex01_EqualsContract.Q5_INTEGER_128_DOUBLE_EQUALS, HINT_Q5);
    }
}
