package phase03.d19_clean_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase03.support.Predictions.assertPrediction;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_NamingAndFunctionsTest {

    private static final String HINT_Q1 =
            "Clean code là đọc hiểu nhanh, không phải ít dòng.";
    private static final String HINT_Q2 =
            "Thước đo là số trách nhiệm và số nhánh, không phải số dòng.";
    private static final String HINT_Q3 =
            "Hai đoạn giống nhau nhưng đổi vì hai lý do khác nhau thì gộp lại sẽ phải chiều cả hai.";
    private static final String HINT_Q4 =
            "Comment lặp lại điều code đã nói thì làm nhiễu; comment giải thích why thì có giá trị.";
    private static final String HINT_Q5 =
            "Tham số boolean buộc người đọc nhớ true nghĩa là gì, và thường là hai việc trong một method.";

    private static final Ex01_NamingAndFunctions.LineItem ITEM =
            new Ex01_NamingAndFunctions.LineItem("book", 1_000, 3);

    @Test
    @DisplayName("Q1 dự đoán: clean code có nghĩa code ngắn")
    void q01_prediction() {
        assertPrediction("Q1_CLEAN_CODE_MEANS_SHORT", false,
                Ex01_NamingAndFunctions.Q1_CLEAN_CODE_MEANS_SHORT, HINT_Q1);
    }

    @Test
    @DisplayName("Q2 dự đoán: method 100 dòng có luôn sai")
    void q02_prediction() {
        assertPrediction("Q2_HUNDRED_LINE_METHOD",
                Ex01_NamingAndFunctions.Verdict.DEPENDS_ON_RESPONSIBILITIES,
                Ex01_NamingAndFunctions.Q2_HUNDRED_LINE_METHOD, HINT_Q2);
    }

    @Test
    @DisplayName("Q3 dự đoán: duplicate tốt hơn premature abstraction")
    void q03_prediction() {
        assertPrediction("Q3_DUPLICATE_BETTER_THAN_PREMATURE", true,
                Ex01_NamingAndFunctions.Q3_DUPLICATE_BETTER_THAN_PREMATURE, HINT_Q3);
    }

    @Test
    @DisplayName("Q4 dự đoán: comment nhiều có phải tốt")
    void q04_prediction() {
        assertPrediction("Q4_MORE_COMMENTS",
                Ex01_NamingAndFunctions.Verdict.DEPENDS_ON_RESPONSIBILITIES,
                Ex01_NamingAndFunctions.Q4_MORE_COMMENTS, HINT_Q4);
    }

    @Test
    @DisplayName("Q5 dự đoán: boolean parameter báo hiệu gì")
    void q05_prediction() {
        assertPrediction("Q5_BOOLEAN_PARAMETER",
                Ex01_NamingAndFunctions.Signal.METHOD_DOES_TWO_THINGS,
                Ex01_NamingAndFunctions.Q5_BOOLEAN_PARAMETER, HINT_Q5);
    }

    @Test
    @DisplayName("Q6: describe nói rõ việc và đơn vị")
    void q06_describeNamesUnit() {
        assertEquals("book x3 = 3000 cents", Ex01_NamingAndFunctions.describe(ITEM));
    }

    @Test
    @DisplayName("Q6: totalWithShipping cộng cả phí và chặn danh sách rỗng")
    void q06_totalWithShipping() {
        assertEquals(3_500, Ex01_NamingAndFunctions.totalWithShipping(List.of(ITEM), 500));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_NamingAndFunctions.totalWithShipping(List.of(), 500));
    }
}
