package phase09.d07_linked_list;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Ex01_LinkedListTest {
    @Test
    @DisplayName("B1 reverse preserves node identity and order")
    void b01ReversePreservesIdentity() {
        Ex01_LinkedList.Node first = new Ex01_LinkedList.Node(4);
        Ex01_LinkedList.Node second = new Ex01_LinkedList.Node(4);
        Ex01_LinkedList.Node third = new Ex01_LinkedList.Node(7);
        first.next = second;
        second.next = third;

        Ex01_LinkedList.Node reversed = Ex01_LinkedList.reverse(first);
        assertSame(third, reversed);
        assertSame(second, reversed.next);
        assertSame(first, reversed.next.next);
        org.junit.jupiter.api.Assertions.assertNull(first.next);
        assertNull(Ex01_LinkedList.reverse(null));
    }

    @Test
    @DisplayName("B1 reverse từ chối cycle trước khi thay đổi liên kết")
    void b01ReverseRejectsCycleWithoutMutation() {
        Ex01_LinkedList.Node first = new Ex01_LinkedList.Node(1);
        Ex01_LinkedList.Node second = new Ex01_LinkedList.Node(2);
        first.next = second;
        second.next = first;
        assertThrows(IllegalArgumentException.class, () -> Ex01_LinkedList.reverse(first));
        assertSame(second, first.next);
        assertSame(first, second.next);
    }

    @Test
    @DisplayName("B1 cycleEntry dùng identity cho cycle giữa danh sách")
    void b01FindsCycleEntryByIdentity() {
        Ex01_LinkedList.Node first = new Ex01_LinkedList.Node(5);
        Ex01_LinkedList.Node second = new Ex01_LinkedList.Node(5);
        Ex01_LinkedList.Node entry = new Ex01_LinkedList.Node(8);
        Ex01_LinkedList.Node last = new Ex01_LinkedList.Node(8);
        first.next = second;
        second.next = entry;
        entry.next = last;
        last.next = entry;

        assertSame(entry, Ex01_LinkedList.cycleEntry(first));
        Ex01_LinkedList.Node selfCycle = firstWithSelfCycle();
        assertSame(selfCycle, Ex01_LinkedList.cycleEntry(selfCycle));
        assertNull(Ex01_LinkedList.cycleEntry(null));
        assertNull(Ex01_LinkedList.cycleEntry(new Ex01_LinkedList.Node(1)));
    }

    @Test
    @DisplayName("B1 Floyd cycleEntry khớp oracle identity trên danh sách sinh có seed")
    void b01CycleEntryMatchesOracle() {
        Random random = new Random(907);
        for (int trial = 0; trial < 200; trial++) {
            int size = 1 + random.nextInt(20);
            List<Ex01_LinkedList.Node> nodes = new ArrayList<>(size);
            for (int i = 0; i < size; i++) nodes.add(new Ex01_LinkedList.Node(random.nextInt(4)));
            for (int i = 0; i + 1 < size; i++) nodes.get(i).next = nodes.get(i + 1);
            int entryIndex = random.nextInt(size + 1);
            if (entryIndex < size) nodes.get(size - 1).next = nodes.get(entryIndex);

            Ex01_LinkedList.Node expected = entryIndex == size ? null : nodes.get(entryIndex);
            assertSame(expected, Ex01_LinkedList.cycleEntry(nodes.get(0)));
        }
    }

    private Ex01_LinkedList.Node firstWithSelfCycle() {
        Ex01_LinkedList.Node node = new Ex01_LinkedList.Node(1);
        node.next = node;
        return node;
    }
}
