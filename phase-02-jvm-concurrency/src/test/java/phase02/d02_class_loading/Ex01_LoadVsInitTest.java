package phase02.d02_class_loading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static phase02.support.Predictions.assertPrediction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex01_LoadVsInitTest {

    private static final String HINT_LOAD =
            "forName với initialize = false không chạy static initializer. Đếm touch() trên ProbeUninit, "
                    + "class mà hitsAfterInit không đụng tới. Ctrl+Q trên Class.forName.";

    private static final String HINT_INIT =
            "Initialization chạy khối static đúng một lần. touch() chỉ chạy khi Probe được initialize, "
                    + "không chạy lại ở lần forName sau. Đặt breakpoint tại touch() rồi F7.";

    @Test
    @DisplayName("Q3: load ProbeUninit không initialize thì số lần touch là 0")
    void q03_hitsAfterLoadWithoutInit_returnsZero() throws ClassNotFoundException {
        int actual = Ex01_LoadVsInit.hitsAfterLoadWithoutInit();

        assertEquals(0, actual, "ProbeUninit chưa initialize: touch() không được chạy.");
        assertPrediction("Q3_HITS_AFTER_LOAD_WITHOUT_INIT", actual,
                Ex01_LoadVsInit.Q3_HITS_AFTER_LOAD_WITHOUT_INIT, HINT_LOAD);
    }

    @Test
    @DisplayName("Q3: initialize Probe thì touch() chạy một lần")
    void q03_hitsAfterInit_returnsOne() throws ClassNotFoundException {
        int actual = Ex01_LoadVsInit.hitsAfterInit();

        assertEquals(1, actual, "Initialize Probe phải chạy touch() đúng một lần.");
        assertPrediction("Q3_HITS_AFTER_INIT", actual, Ex01_LoadVsInit.Q3_HITS_AFTER_INIT, HINT_INIT);
    }

    @Test
    @DisplayName("Q3: Probe đã init rồi load không initialize thì số lần touch không về 0")
    void q03_initThenLoadWithoutInit_onInitializedClass_notZero() throws ClassNotFoundException {
        int initialized = Ex01_LoadVsInit.hitsAfterInit();
        ClassLoader loader = Ex01_LoadVsInit.Probe.class.getClassLoader();
        Class.forName(Ex01_LoadVsInit.Probe.class.getName(), false, loader);

        assertNotEquals(0, Ex01_LoadVsInit.HITS.get(),
                "Probe đã được init: forName với initialize = false không đưa số lần touch về 0.");
        assertEquals(initialized, Ex01_LoadVsInit.HITS.get(),
                "Load không initialize không được chạy touch() thêm lần nữa.");
    }
}
