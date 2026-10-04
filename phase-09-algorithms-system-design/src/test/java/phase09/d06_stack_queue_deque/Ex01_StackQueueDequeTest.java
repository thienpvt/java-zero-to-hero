package phase09.d06_stack_queue_deque;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Ex01_StackQueueDequeTest {
    @Test
    @DisplayName("B1 kiểm tra ngoặc chỉ chấp nhận ba cặp ký tự")
    void b01BalancedBrackets() {
        assertTrue(Ex01_StackQueueDeque.balancedBrackets(""));
        assertTrue(Ex01_StackQueueDeque.balancedBrackets("{[()]}"));
        assertFalse(Ex01_StackQueueDeque.balancedBrackets("([)]"));
        assertFalse(Ex01_StackQueueDeque.balancedBrackets("(x)"));
        assertFalse(Ex01_StackQueueDeque.balancedBrackets("("));
        assertThrows(NullPointerException.class, () -> Ex01_StackQueueDeque.balancedBrackets(null));
    }

    @Test
    @DisplayName("B1 BFS lưới khớp oracle queue trên lưới nhỏ có seed")
    void b01GridPathMatchesQueueOracle() {
        Random random = new Random(906);
        for (int trial = 0; trial < 200; trial++) {
            int rows = 1 + random.nextInt(6), columns = 2 + random.nextInt(6);
            char[][] grid = new char[rows][columns];
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) grid[row][column] = random.nextInt(4) == 0 ? '#' : '.';
            }
            int start = random.nextInt(rows * columns);
            int end = random.nextInt(rows * columns - 1);
            if (end >= start) end++;
            grid[start / columns][start % columns] = 'S';
            grid[end / columns][end % columns] = 'E';
            assertEquals(gridOracle(grid), Ex01_StackQueueDeque.shortestGridPath(grid));
        }
    }

    @Test
    @DisplayName("B1 BFS lưới đi bốn hướng, không xuyên tường và không đi chéo")
    void b01ShortestGridPath() {
        assertEquals(1, Ex01_StackQueueDeque.shortestGridPath(new char[][]{{'S', 'E'}}));
        assertEquals(4, Ex01_StackQueueDeque.shortestGridPath(new char[][]{
                {'S', '.', '.'}, {'#', '#', '.'}, {'.', '.', 'E'}}));
        assertEquals(-1, Ex01_StackQueueDeque.shortestGridPath(new char[][]{{'S', '#', 'E'}}));
        assertEquals(-1, Ex01_StackQueueDeque.shortestGridPath(new char[][]{{'S', '#'}, {'#', 'E'}}));
        assertThrows(IllegalArgumentException.class, () -> Ex01_StackQueueDeque.shortestGridPath(new char[0][]));
        assertThrows(IllegalArgumentException.class, () -> Ex01_StackQueueDeque.shortestGridPath(new char[][]{{'S'}, {'.', 'E'}}));
        assertThrows(IllegalArgumentException.class, () -> Ex01_StackQueueDeque.shortestGridPath(new char[][]{{'S', 'S', 'E'}}));
        assertThrows(IllegalArgumentException.class, () -> Ex01_StackQueueDeque.shortestGridPath(new char[][]{{'S', 'x', 'E'}}));
        assertThrows(IllegalArgumentException.class, () -> Ex01_StackQueueDeque.shortestGridPath(new char[][]{{'S'}}));
        assertThrows(NullPointerException.class, () -> Ex01_StackQueueDeque.shortestGridPath(null));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_StackQueueDeque.shortestGridPath(new char[][]{{'S', '.', 'E'}, null}));
    }

    private int gridOracle(char[][] grid) {
        int rows = grid.length, columns = grid[0].length;
        int[][] distance = new int[rows][columns];
        for (int[] row : distance) java.util.Arrays.fill(row, -1);
        Queue<int[]> queue = new ArrayDeque<>();
        outer:
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (grid[row][column] == 'S') {
                    queue.add(new int[]{row, column});
                    distance[row][column] = 0;
                    break outer;
                }
            }
        }
        int[] dr = {-1, 1, 0, 0}, dc = {0, 0, -1, 1};
        while (!queue.isEmpty()) {
            int[] current = queue.remove();
            for (int direction = 0; direction < 4; direction++) {
                int row = current[0] + dr[direction], column = current[1] + dc[direction];
                if (row < 0 || row >= rows || column < 0 || column >= columns
                        || distance[row][column] >= 0 || grid[row][column] == '#') continue;
                distance[row][column] = distance[current[0]][current[1]] + 1;
                if (grid[row][column] == 'E') return distance[row][column];
                queue.add(new int[]{row, column});
            }
        }
        return -1;
    }
}
