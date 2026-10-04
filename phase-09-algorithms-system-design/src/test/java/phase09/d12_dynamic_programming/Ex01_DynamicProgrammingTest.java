package phase09.d12_dynamic_programming;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_DynamicProgrammingTest {
    @Test
    void minCoinsHandlesZeroImpossibleTiesAndLargeDenominations() {
        assertEquals(Optional.of(List.of()), Ex01_DynamicProgramming.minCoins(new int[]{1, 3}, 0));
        assertEquals(Optional.empty(), Ex01_DynamicProgramming.minCoins(new int[]{4, 6}, 5));
        assertEquals(Optional.of(List.of(4)), Ex01_DynamicProgramming.minCoins(new int[]{1, 3, 4}, 4));
        assertEquals(Optional.of(List.of(1, 3)), Ex01_DynamicProgramming.minCoins(new int[]{3, 1}, 4));
        assertEquals(Optional.of(List.of(2, 2, 3)), Ex01_DynamicProgramming.minCoins(new int[]{2, 3}, 7));
        assertEquals(Optional.empty(), Ex01_DynamicProgramming.minCoins(new int[]{Integer.MAX_VALUE}, 10));
        assertThrows(IllegalArgumentException.class, () -> Ex01_DynamicProgramming.minCoins(new int[0], 10_001));
        assertThrows(IllegalArgumentException.class, () -> Ex01_DynamicProgramming.minCoins(new int[33], 0));
    }

    @Test
    void minCoinsMatchesIndependentRecursiveOracleForSeededInputs() {
        Random random = new Random(1209);
        for (int trial = 0; trial < 100; trial++) {
            int[] coins = random.ints(random.nextInt(1, 6), 1, 10).distinct().toArray();
            int amount = random.nextInt(21);
            List<Integer> expected = brute(coins, amount, 0);
            assertEquals(Optional.ofNullable(expected), Ex01_DynamicProgramming.minCoins(coins, amount));
        }
    }

    @Test
    void minCoinsValidatesBeforeAllocationAndRejectsMalformedInputs() {
        assertThrows(IllegalArgumentException.class, () -> Ex01_DynamicProgramming.minCoins(new int[]{1}, 10_001));
        assertThrows(IllegalArgumentException.class, () -> Ex01_DynamicProgramming.minCoins(new int[]{0}, 1));
        assertThrows(IllegalArgumentException.class, () -> Ex01_DynamicProgramming.minCoins(new int[]{-1}, 1));
        assertThrows(IllegalArgumentException.class, () -> Ex01_DynamicProgramming.minCoins(new int[]{1, 1}, 1));
        assertThrows(NullPointerException.class, () -> Ex01_DynamicProgramming.minCoins(null, 1));
        assertThrows(IllegalArgumentException.class, () -> Ex01_DynamicProgramming.minCoins(new int[]{1}, -1));
    }

    private static List<Integer> brute(int[] coins, int remaining, int minimumCoin) {
        if (remaining == 0) return new ArrayList<>();
        List<Integer> best = null;
        for (int coin : coins) {
            if (coin < minimumCoin || coin > remaining) continue;
            List<Integer> suffix = brute(coins, remaining - coin, coin);
            if (suffix == null) continue;
            List<Integer> candidate = new ArrayList<>(suffix);
            candidate.add(0, coin);
            if (best == null || candidate.size() < best.size()
                    || (candidate.size() == best.size() && lexicographicallyBefore(candidate, best))) best = candidate;
        }
        return best;
    }

    private static boolean lexicographicallyBefore(List<Integer> left, List<Integer> right) {
        for (int i = 0; i < left.size(); i++) {
            int comparison = Integer.compare(left.get(i), right.get(i));
            if (comparison != 0) return comparison < 0;
        }
        return false;
    }
}
