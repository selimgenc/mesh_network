package me.selim.mesh.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    @DisplayName("Test Node comparability in alphabetical order")
    void test_nodes_alphabetical_comparability() {
        Node n1 = new Node("A");
        Node n2 = new Node("B");
        Node n3 = new Node("C");
        Node n4 = new Node("c");
        Node n5 = new Node("b");

        assertTrue(n1.compareTo(n2) < 0);
        assertTrue(n2.compareTo(n3) < 0);
        assertTrue(n1.compareTo(n3) < 0);
        assertEquals(0, n3.compareTo(n4));
        assertTrue(n3.compareTo(n5) > 0);
    }

    @Test
    @DisplayName("Test Node Equality")
    void test_nodes_equality() {
        Node n1 = new Node("A");
        Node n2 = new Node("A");
        Node n3 = new Node("B");

        assertNotEquals(n1, n2);
        assertNotEquals(n1, n3);

        //Only Nodes with same Id are equal
        n1.setId(1L);
        n2.setId(1L);
        n3.setId(2L);
        assertEquals(n1, n2);
        assertNotEquals(n1, n3);
    }

    @Test
    @DisplayName("Adding connection when node is locked should throw exception")
    void adding_connection_when_node_is_locked_should_throw_exception() throws InterruptedException {
        Node node1 = new Node(1L, "A", Set.of());
        Node node2 = new Node(2L, "B", Set.of());
        Connection connection = new Connection(node1.getId(), node2.getId(), 1);

        Thread thread = new Thread(node1::lockForConnectionOperations);
        thread.start();
        thread.join();
        assertThrows(IllegalStateException.class, () -> node1.addConnection(connection));
    }

    @Test
    @DisplayName("Adding connection when non related node is locked, should succeed ")
    void adding_connection_when_non_related_node_is_locked_should_succeed() throws InterruptedException {
        Node node1 = new Node(1L, "A", Set.of());
        Node node2 = new Node(2L, "B", Set.of());
        Connection connection = new Connection(node1.getId(), node2.getId(), 1);

        Thread thread = new Thread(node2::lockForConnectionOperations);
        thread.start();
        thread.join();
        assertDoesNotThrow(() -> node1.addConnection(connection));
    }

    @Test
    @DisplayName("Unlocking a node after locking should allow adding connection")
    void unlocking_a_node_after_locking_should_allow_adding_connection() throws InterruptedException {
        Node node1 = new Node(1L, "A", Set.of());
        Node node2 = new Node(2L, "B", Set.of());
        Connection connection = new Connection(node1.getId(), node2.getId(), 1);

        Thread thread = new Thread(() -> {
            node1.lockForConnectionOperations();
            node1.unLock();
        });
        thread.start();
        thread.join();
        assertDoesNotThrow(() -> node1.addConnection(connection));
    }

    @Test
    @DisplayName("Locking or unlocking a node twice by same thread should pass should throw exception")
    void locking_a_node_twice_should_throw_exception() {
        Node node = new Node("A");
        node.lockForConnectionOperations();
        node.lockForConnectionOperations();

        node.unLock();
        node.unLock();
    }

    @Test
    @DisplayName("Adding connection to a node that is not part of the connection should throw exception")
    void adding_connection_to_node_not_part_of_connection_should_throw_exception() {
        Node node1 = new Node(1L, "A", Set.of());
        Node node2 = new Node(2L, "B", Set.of());
        Node node3 = new Node(3L, "C", Set.of());
        Connection connection = new Connection(node2.getId(), node3.getId(), 1);

        assertThrows(IllegalArgumentException.class, () -> node1.addConnection(connection));
    }

    @Test
    @DisplayName("Adding connection to a node that is part of the connection should not throw exception")
    void adding_connection_to_node_part_of_connection_should_not_throw_exception() {
        Node node1 = new Node(1L, "A", Set.of());
        Node node2 = new Node(2L, "B", Set.of());
        Connection connection = new Connection(node1.getId(), node2.getId(), 1);

        assertDoesNotThrow(() -> node1.addConnection(connection));
    }

}