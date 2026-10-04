package phase09.d10_graph_bfs_dfs;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ex01_GraphBfsDfsTest {
    @Test
    void countsUndirectedComponentsIncludingIsolatesAndSelfLoops() {
        var graph = new Ex01_GraphBfsDfs.Graph(Set.of(1, 2, 3, 4),
                Map.of(1, List.of(2, 1)), false);
        assertEquals(3, Ex01_GraphBfsDfs.componentCount(graph));
        assertEquals(0, Ex01_GraphBfsDfs.componentCount(
                new Ex01_GraphBfsDfs.Graph(Set.of(), Map.of(), false)));
        assertEquals(1, Ex01_GraphBfsDfs.componentCount(
                new Ex01_GraphBfsDfs.Graph(Set.of(8), Map.of(), false)));
    }

    @Test
    void shortestPathHandlesDisconnectedAndSameVertex() {
        var graph = new Ex01_GraphBfsDfs.Graph(Set.of(1, 2, 3, 4),
                Map.of(1, List.of(2), 2, List.of(1, 3), 3, List.of(2)), false);
        assertEquals(2, Ex01_GraphBfsDfs.shortestPath(graph, 1, 3));
        assertEquals(-1, Ex01_GraphBfsDfs.shortestPath(graph, 1, 4));
        assertEquals(0, Ex01_GraphBfsDfs.shortestPath(graph, 2, 2));
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_GraphBfsDfs.shortestPath(graph, 1, 99));
        var directed = new Ex01_GraphBfsDfs.Graph(Set.of(1, 2), Map.of(1, List.of(2)), true);
        assertThrows(IllegalArgumentException.class,
                () -> Ex01_GraphBfsDfs.shortestPath(directed, 1, 2));
    }

    @Test
    void graphDeepCopiesInputsAndRejectsUndeclaredEndpoints() {
        Set<Integer> vertices = new HashSet<>(Set.of(1, 2));
        List<Integer> neighbors = new ArrayList<>(List.of(2));
        Map<Integer, List<Integer>> edges = new HashMap<>();
        edges.put(1, neighbors);
        var graph = new Ex01_GraphBfsDfs.Graph(vertices, edges, false);
        vertices.clear();
        neighbors.clear();
        edges.clear();
        assertEquals(Set.of(1, 2), graph.vertices());
        assertEquals(Map.of(1, List.of(2)), graph.edges());
        assertThrows(UnsupportedOperationException.class, () -> graph.vertices().clear());
        assertThrows(UnsupportedOperationException.class, () -> graph.edges().get(1).clear());
        assertEquals(1, Ex01_GraphBfsDfs.componentCount(graph));
        assertThrows(IllegalArgumentException.class, () -> new Ex01_GraphBfsDfs.Graph(
                Set.of(1), Map.of(1, List.of(2)), false));
    }
}
