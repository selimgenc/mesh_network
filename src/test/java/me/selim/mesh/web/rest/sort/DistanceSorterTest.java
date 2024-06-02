package me.selim.mesh.web.rest.sort;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.web.rest.model.SortType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DistanceSorterTest {

    private List<Node> nodes;
    Connection c1_2 = new Connection(1L, 2L, 10);
    Connection c1_3 = new Connection(1L, 3L, 20);

    Connection c2_3 = new Connection(2L, 3L, 5);
    Connection c2_4 = new Connection(2L, 4L, 5);
    Connection c3_4 = new Connection(3L, 4L, 15);

    @BeforeEach
    public void setUp() {
        Node node1 = new Node(1L, "N1", Set.of(c1_2, c1_3));
        Node node2 = new Node(2L, "N2", Set.of(c2_3, c1_2, c2_4));
        Node node3 = new Node(3L, "N3", Set.of(c1_3, c2_3, c3_4));
        Node node4 = new Node(4L, "N4", Set.of(c2_4,c3_4));
        //put in random order and see the result in sorted order
        nodes = List.of(node2, node3, node1, node4);
    }

    @Test
    @DisplayName("Test Distance with sort type")
    void sort() {
        DistanceSorter sorter = new DistanceSorter();
        List<Connection> sortConnections = sorter.sort(nodes, SortType.ASC);
        //check connections are ordered asc in result set

        Assertions.assertEquals(5, sortConnections.get(0).getDistance());
        Assertions.assertEquals(5, sortConnections.get(1).getDistance());
        Assertions.assertEquals(10, sortConnections.get(2).getDistance());
        Assertions.assertEquals(15, sortConnections.get(3).getDistance());
        Assertions.assertEquals(20, sortConnections.get(4).getDistance());

        sortConnections = sorter.sort(nodes, SortType.DESC);

        Assertions.assertEquals(20, sortConnections.get(0).getDistance());
        Assertions.assertEquals(15, sortConnections.get(1).getDistance());
        Assertions.assertEquals(10, sortConnections.get(2).getDistance());
        Assertions.assertEquals(5, sortConnections.get(3).getDistance());
        Assertions.assertEquals(5, sortConnections.get(4).getDistance());


    }
}