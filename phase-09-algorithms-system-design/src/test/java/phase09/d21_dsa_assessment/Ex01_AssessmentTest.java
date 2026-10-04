package phase09.d21_dsa_assessment;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_AssessmentTest {
    @Test
    void shortestPathHandlesEmptyDuplicateMalformedAndBoundaryCases() {
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_Assessment.shortestPath(new int[0][], 0, 0, 0, 0));
        assertEquals(0, Ex01_Assessment.shortestPath(new int[][]{{1}}, 0, 0, 0, 0));
        assertEquals(3, Ex01_Assessment.shortestPath(new int[][]{{1, 1, 1}, {0, 0, 1}}, 0, 0, 1, 2));
        assertEquals(-1, Ex01_Assessment.shortestPath(new int[][]{{1, 0}, {0, 1}}, 0, 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> Ex01_Assessment.shortestPath(null, 0, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> Ex01_Assessment.shortestPath(new int[][]{{1}, {1, 1}}, 0, 0, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> Ex01_Assessment.shortestPath(new int[][]{{1, 2}}, 0, 0, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> Ex01_Assessment.shortestPath(new int[][]{{1}}, -1, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> Ex01_Assessment.shortestPath(new int[][]{{1}}, 0, 0, 0, 1));
    }

    @Test
    void shortestPathMatchesSeededBruteForceOracle() {
        Random random = new Random(2109);
        for (int trial = 0; trial < 200; trial++) {
            int rows = random.nextInt(1, 6), columns = random.nextInt(1, 6);
            int[][] grid = new int[rows][columns];
            for (int[] row : grid) for (int column = 0; column < columns; column++) row[column] = random.nextInt(2);
            int start = random.nextInt(rows * columns), goal = random.nextInt(rows * columns);
            int expected = brute(grid, start, goal);
            assertEquals(expected, Ex01_Assessment.shortestPath(grid, start / columns, start % columns,
                    goal / columns, goal % columns));
        }
    }

    private static int brute(int[][] grid, int start, int goal) {
        int columns = grid[0].length;
        if (grid[start / columns][start % columns] == 0 || grid[goal / columns][goal % columns] == 0) return -1;
        int[] distance = new int[grid.length * columns];
        Arrays.fill(distance, -1);
        distance[start] = 0;
        for (int depth = 0; depth < distance.length; depth++) {
            for (int cell = 0; cell < distance.length; cell++) {
                if (distance[cell] != depth) continue;
                if (cell == goal) return depth;
                int row = cell / columns, column = cell % columns;
                for (int[] delta : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                    int nextRow = row + delta[0], nextColumn = column + delta[1];
                    if (nextRow >= 0 && nextRow < grid.length && nextColumn >= 0 && nextColumn < columns
                            && grid[nextRow][nextColumn] == 1) {
                        int next = nextRow * columns + nextColumn;
                        if (distance[next] < 0) distance[next] = depth + 1;
                    }
                }
            }
        }
        return -1;
    }
}
