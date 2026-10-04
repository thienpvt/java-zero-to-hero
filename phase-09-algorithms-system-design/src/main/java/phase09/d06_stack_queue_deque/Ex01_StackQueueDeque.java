package phase09.d06_stack_queue_deque;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Stack, Queue và Deque.
 * <p>
 * Nguồn: 09-algorithms-system-design.md, mục 6 (Stack, Queue và Deque), câu 1–5.
 * Cần làm trước: mục 2 (Array và String), mục 4 (Two Pointers và Sliding Window).
 * Cách làm: trả lời từng câu, chạy test trong Ex01_StackQueueDequeTest bằng nút ▶ (Ctrl+Shift+F10).
 * <p>
 * Q1 [TỰ TRẢ LỜI] Bài toán nào tự nhiên dùng LIFO, bài toán nào dùng FIFO?
 *   Bắt đầu   : viết ANSWER Q1; so sánh undo với xử lý theo thứ tự đến.
 *   Tra cứu   : thứ tự lấy phần tử của stack và queue.
 *   Hoàn thành khi: ANSWER Q1 liên hệ đúng thứ tự xử lý với hai loại bài toán.
 * <p>
 * Q2 [TỰ TRẢ LỜI] Vì sao `ArrayDeque` thường phù hợp hơn `Stack`?
 *   Bắt đầu   : viết ANSWER Q2; xét API và cấu trúc đồng bộ.
 *   Tra cứu   : Java Collections Framework.
 *   Hoàn thành khi: ANSWER Q2 nêu API hiện đại và lựa chọn cấu trúc theo yêu cầu đồng bộ.
 * <p>
 * Q3 [TỰ TRẢ LỜI] Monotonic stack duy trì invariant gì?
 *   Bắt đầu   : viết ANSWER Q3; xác định chiều tăng hoặc giảm cần cho bài toán.
 *   Tra cứu   : next greater element.
 *   Hoàn thành khi: ANSWER Q3 nêu thứ tự đơn điệu và điều kiện pop.
 * <p>
 * Q4 [TỰ TRẢ LỜI] BFS cần queue để bảo đảm đặc tính nào?
 *   Bắt đầu   : viết ANSWER Q4; xét thứ tự xử lý theo từng lớp.
 *   Tra cứu   : shortest path trong graph không trọng số.
 *   Hoàn thành khi: ANSWER Q4 giải thích vì sao node gần hơn được xử lý trước.
 * <p>
 * Q5 [TỰ TRẢ LỜI] Hành vi khi deque rỗng cần được thiết kế thế nào?
 *   Bắt đầu   : viết ANSWER Q5; phân biệt thao tác kiểm tra và lấy phần tử.
 *   Tra cứu   : cặp phương thức offer/poll và add/remove.
 *   Hoàn thành khi: ANSWER Q5 nêu rõ sentinel hoặc exception của API được chọn.
 * <p>
 * B1: Kiểm tra ngoặc và tìm đường đi ngắn nhất trong lưới không trọng số.
 */
class Ex01_StackQueueDeque {
    static boolean balancedBrackets(String input) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(input, "input");
        ArrayDeque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);
            if (current == '(' || current == '[' || current == '{') {
                stack.push(current);
            } else if (current == ')' || current == ']' || current == '}') {
                if (stack.isEmpty()) return false;
                char open = stack.pop();
                if ((current == ')' && open != '(') || (current == ']' && open != '[')
                        || (current == '}' && open != '{')) return false;
            } else {
                return false;
            }
        }
        return stack.isEmpty();
        // SOLUTION-END
    }

    static int shortestGridPath(char[][] grid) {
        // SOLUTION-BEGIN throw B1
        java.util.Objects.requireNonNull(grid, "grid");
        if (grid.length == 0 || grid[0] == null || grid[0].length == 0) {
            throw new IllegalArgumentException("grid must be nonempty and rectangular");
        }
        int rows = grid.length, columns = grid[0].length;
        int startRow = -1, startColumn = -1, endCount = 0, startCount = 0;
        for (int row = 0; row < rows; row++) {
            if (grid[row] == null || grid[row].length != columns) {
                throw new IllegalArgumentException("grid must be rectangular");
            }
            for (int column = 0; column < columns; column++) {
                char cell = grid[row][column];
                if (cell == 'S') {
                    startCount++;
                    startRow = row;
                    startColumn = column;
                } else if (cell == 'E') {
                    endCount++;
                } else if (cell != '#' && cell != '.') {
                    throw new IllegalArgumentException("grid contains an invalid cell");
                }
            }
        }
        if (startCount != 1 || endCount != 1) throw new IllegalArgumentException("grid needs one S and one E");

        boolean[][] visited = new boolean[rows][columns];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{startRow, startColumn, 0});
        visited[startRow][startColumn] = true;
        int[] rowStep = {-1, 1, 0, 0};
        int[] columnStep = {0, 0, -1, 1};
        while (!queue.isEmpty()) {
            int[] current = queue.remove();
            for (int direction = 0; direction < 4; direction++) {
                int nextRow = current[0] + rowStep[direction];
                int nextColumn = current[1] + columnStep[direction];
                if (nextRow < 0 || nextRow >= rows || nextColumn < 0 || nextColumn >= columns
                        || visited[nextRow][nextColumn] || grid[nextRow][nextColumn] == '#') continue;
                if (grid[nextRow][nextColumn] == 'E') return current[2] + 1;
                visited[nextRow][nextColumn] = true;
                queue.add(new int[]{nextRow, nextColumn, current[2] + 1});
            }
        }
        return -1;
        // SOLUTION-END
    }
}

/* ANSWER Q1:
 * SOLUTION-BEGIN
 * LIFO xử lý phần tử mới nhất trước, nên phù hợp với undo, call stack và ngoặc lồng nhau.
 * FIFO xử lý phần tử đến trước trước, nên phù hợp với hàng đợi công việc và BFS theo từng lớp.
 * SOLUTION-END
 */

/* ANSWER Q2:
 * SOLUTION-BEGIN
 * ArrayDeque có API Deque hiện đại và tránh các phương thức đồng bộ kế thừa của Stack.
 * Khi không cần đồng bộ, nó thường phù hợp cho stack hoặc queue; nếu cần đồng bộ phải chọn cơ chế phù hợp riêng.
 * SOLUTION-END
 */

/* ANSWER Q3:
 * SOLUTION-BEGIN
 * Monotonic stack giữ các phần tử theo một thứ tự tăng hoặc giảm nhất quán.
 * Khi phần tử mới phá invariant, pop các phần tử bị nó giải quyết, chẳng hạn tìm next greater element.
 * Quy tắc pop phụ thuộc việc cần lớn hơn hay lớn hơn hoặc bằng.
 * SOLUTION-END
 */

/* ANSWER Q4:
 * SOLUTION-BEGIN
 * Queue FIFO xử lý các node theo thứ tự được phát hiện, tương ứng khoảng cách không giảm từ nguồn.
 * Trong graph không trọng số, BFS phát hiện đường ngắn nhất tới node khi lần đầu thăm node đó.
 * Vì vậy node ở lớp gần được xử lý trước lớp xa.
 * SOLUTION-END
 */

/* ANSWER Q5:
 * SOLUTION-BEGIN
 * Cần quy định rõ thao tác rỗng trả sentinel hay ném exception.
 * Với Deque, poll/peek trả null khi không có phần tử; remove/getFirst ném NoSuchElementException.
 * Chọn cặp API theo cách caller muốn xử lý trạng thái rỗng.
 * SOLUTION-END
 */
