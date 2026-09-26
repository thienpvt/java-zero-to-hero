package phase01.d07_comparable_comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import phase01.d07_comparable_comparator.Ex02_ComparatorComposition.Employee;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_ComparatorCompositionTest {

    @Test
    @DisplayName("Q5 byDepartmentThenAgeDescThenName: department tăng, age giảm, name tăng")
    void q05_byDepartmentThenAgeDescThenName_sortsByThreeFieldsInOrder() {
        List<Employee> employees = new ArrayList<>(List.of(
                new Employee("IT", "Bình", 30),
                new Employee("HR", "An", 25),
                new Employee("IT", "An", 30),
                new Employee("IT", "Chi", 40)));

        employees.sort(Ex02_ComparatorComposition.byDepartmentThenAgeDescThenName());

        assertEquals(List.of(
                        new Employee("HR", "An", 25),
                        new Employee("IT", "Chi", 40),
                        new Employee("IT", "An", 30),
                        new Employee("IT", "Bình", 30)),
                employees,
                "Phải sort department tăng dần; trong cùng department age giảm dần; cùng age thì name tăng dần.");
    }

    @Test
    @DisplayName("Q6 dự đoán: sort theo age có phần tử null bằng comparing() trần")
    void q06_prediction_comparingWithNullAgeThrows() {
        List<Employee> employees = new ArrayList<>(List.of(
                new Employee("IT", "An", 30),
                new Employee("IT", "Bình", null)));
        Comparator<Employee> rawComparingByAge = Comparator.comparing(Employee::age);

        String exceptionSimpleName;
        try {
            employees.sort(rawComparingByAge);
            exceptionSimpleName = "(không ném ngoại lệ)";
        } catch (RuntimeException e) {
            exceptionSimpleName = e.getClass().getSimpleName();
        }

        assertPrediction("Q6_SORT_NULL_AGE_EXCEPTION", exceptionSimpleName,
                Ex02_ComparatorComposition.Q6_SORT_NULL_AGE_EXCEPTION,
                "Comparator.comparing(Employee::age) dùng Integer.compareTo tự nhiên; so với age null gây NullPointerException.");
    }

    @Test
    @DisplayName("Q6 byAgeNullsLast: age null luôn đứng cuối")
    void q06_byAgeNullsLast_putsNullAgeLast() {
        List<Employee> employees = new ArrayList<>(List.of(
                new Employee("IT", "A", 30),
                new Employee("IT", "B", null),
                new Employee("IT", "C", 20)));

        employees.sort(Ex02_ComparatorComposition.byAgeNullsLast());

        assertEquals(Arrays.asList(20, 30, null),
                employees.stream().map(Employee::age).toList(),
                "byAgeNullsLast() phải sort age tăng dần, đưa null xuống cuối.");
    }

    @Test
    @DisplayName("Q6 byDepartmentNullsFirstThenName: department null luôn đứng đầu")
    void q06_byDepartmentNullsFirstThenName_putsNullDepartmentFirst() {
        List<Employee> employees = new ArrayList<>(List.of(
                new Employee("IT", "X", 1),
                new Employee(null, "Y", 2),
                new Employee("HR", "Z", 3)));

        employees.sort(Ex02_ComparatorComposition.byDepartmentNullsFirstThenName());

        assertEquals(Arrays.asList(null, "HR", "IT"),
                employees.stream().map(Employee::department).toList(),
                "byDepartmentNullsFirstThenName() phải đưa department null lên đầu.");
    }
}
