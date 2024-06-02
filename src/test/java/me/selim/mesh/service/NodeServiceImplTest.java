package me.selim.mesh.service;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.error.ResourceDoesNotExistException;
import me.selim.mesh.infrastructure.NodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceImplTest {

    @Mock
    private NodeRepository nodeRepository;

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("Connect two nodes")
    void connect_two_nodes() throws InterruptedException {
        Node node1 = Mockito.spy(new Node(1L, "A", Set.of()));
        Node node2 = Mockito.spy(new Node(2L, "B", Set.of()));
        when(nodeRepository.findById(node1.getId())).thenReturn(Optional.of(node1));
        when(nodeRepository.findById(node2.getId())).thenReturn(Optional.of(node2));

        Connection connection = nodeService.connectNodes(node1.getId(), node2.getId(), 10);

        verify(node1).addConnection(connection);
        verify(node2).addConnection(connection);
    }

    @Test
    @DisplayName("Adding connection when node is locked should throw exception")
    void connect_should_fail_when_another_thread_lock_one_of_node() throws InterruptedException {
        Node node1 = new Node(1L, "A", Set.of());
        Node node2 = new Node(2L, "B", Set.of());
        when(nodeRepository.findById(node1.getId())).thenReturn(Optional.of(node1));
        when(nodeRepository.findById(node2.getId())).thenReturn(Optional.of(node2));

        Thread thread = new Thread(node1::lockForConnectionOperations);
        thread.start();
        thread.join();

        assertThrows(IllegalStateException.class, () -> nodeService.connectNodes(node1.getId(), node2.getId(), 10));

    }

    @Test
    @DisplayName("Connect should fail if one of the node does not exist in the repository")
    void shouldThrowExceptionWhenFirstNodeDoesNotExist() {
        Long firstNodeId = 1L;
        Long secondNodeId = 2L;
        when(nodeRepository.findById(firstNodeId)).thenReturn(Optional.empty());

        assertThrows(ResourceDoesNotExistException.class, () -> nodeService.connectNodes(firstNodeId, secondNodeId, 10));

        Node firstNode = mock(Node.class);
        when(nodeRepository.findById(firstNodeId)).thenReturn(Optional.of(firstNode));
        when(nodeRepository.findById(secondNodeId)).thenReturn(Optional.empty());

        assertThrows(ResourceDoesNotExistException.class, () -> nodeService.connectNodes(firstNodeId, secondNodeId, 10));

    }

    @Test
    @DisplayName("Connect should fail if one of the node does not have empty connection slot")
    void shouldThrowExceptionWhenNodeHasNoEmptySlots() {
        Node firstNode = Mockito.spy(new Node(1L, "A", Set.of()));
        Node secondNode = Mockito.spy(new Node(2L, "B", Set.of()));
        when(nodeRepository.findById(firstNode.getId())).thenReturn(Optional.of(firstNode));
        when(nodeRepository.findById(secondNode.getId())).thenReturn(Optional.of(secondNode));
        when(firstNode.hasEmptySlot()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> nodeService.connectNodes(firstNode.getId(), secondNode.getId(), 10));
    }

    @Test
    @DisplayName("Drop connection between two nodes")
    void drop_connection_between_two_nodes() {
        Node node1 = Mockito.spy(new Node(1L, "A", Set.of()));
        Node node2 = Mockito.spy(new Node(2L, "B", Set.of()));
        Connection connection = new Connection(node1.getId(), node2.getId(), 10);
        node1.addConnection(connection);
        node2.addConnection(connection);
        when(nodeRepository.findById(node1.getId())).thenReturn(Optional.of(node1));
        when(nodeRepository.findById(node2.getId())).thenReturn(Optional.of(node2));

        nodeService.dropConnection(node1.getId(), node2.getId());

        verify(node1).dropConnection(connection);
        verify(node2).dropConnection(connection);
    }

    @Test
    @DisplayName("Drop connection should fail if one of the node does not exist in the repository")
    void dropConnection_shouldThrowExceptionWhenNodeDoesNotExist() {
        Long firstNodeId = 1L;
        Long secondNodeId = 2L;
        when(nodeRepository.findById(firstNodeId)).thenReturn(Optional.empty());

        assertThrows(ResourceDoesNotExistException.class, () -> nodeService.dropConnection(firstNodeId, secondNodeId));

        Node firstNode = mock(Node.class);
        when(nodeRepository.findById(firstNodeId)).thenReturn(Optional.of(firstNode));
        when(nodeRepository.findById(secondNodeId)).thenReturn(Optional.empty());

        assertThrows(ResourceDoesNotExistException.class, () -> nodeService.dropConnection(firstNodeId, secondNodeId));
    }

    @Test
    @DisplayName("Drop connection should fail if nodes are not connected")
    void dropConnection_shouldThrowExceptionWhenNodesAreNotConnected() {
        Node firstNode = Mockito.spy(new Node(1L, "A", Set.of()));
        Node secondNode = Mockito.spy(new Node(2L, "B", Set.of()));
        when(nodeRepository.findById(firstNode.getId())).thenReturn(Optional.of(firstNode));
        when(nodeRepository.findById(secondNode.getId())).thenReturn(Optional.of(secondNode));

        assertThrows(IllegalArgumentException.class, () -> nodeService.dropConnection(firstNode.getId(), secondNode.getId()));
    }

    @Test
    @DisplayName("Delete node with connections")
    void delete_node_with_connections() {
        Node node1 = Mockito.spy(new Node(1L, "A", Set.of()));
        Node node2 = Mockito.spy(new Node(2L, "B", Set.of()));
        Connection connection = new Connection(node1.getId(), node2.getId(), 10);
        node1.addConnection(connection);
        node2.addConnection(connection);
        when(nodeRepository.findById(node2.getId())).thenReturn(Optional.of(node2));

        nodeService.deleteNodeWithConnections(node1);

        verify(node2).dropConnection(connection);
        verify(nodeRepository).deleteById(node1.getId());
    }

    @Test
    @DisplayName("Delete node with connections should fail if one of the connected node does not exist in the repository")
    void deleteNodeWithConnections_shouldThrowExceptionWhenConnectedNodeDoesNotExist() {
        Node node1 = Mockito.spy(new Node(1L, "A", Set.of()));
        Node node2 = Mockito.spy(new Node(2L, "B", Set.of()));
        Connection connection = new Connection(node1.getId(), node2.getId(), 10);
        node1.addConnection(connection);
        node2.addConnection(connection);
        when(nodeRepository.findById(node1.getId())).thenReturn(Optional.of(node1));
        when(nodeRepository.findById(node2.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceDoesNotExistException.class, () -> nodeService.deleteNodeWithConnections(node1.getId()));
    }

    @Test
    @DisplayName("Delete node with connections should rollback if an exception occurs")
    void deleteNodeWithConnections_shouldRollbackWhenExceptionOccurs() {
        Node node1 = Mockito.spy(new Node(1L, "A", Set.of()));
        Node node2 = Mockito.spy(new Node(2L, "B", Set.of()));
        Connection connection = new Connection(node1.getId(), node2.getId(), 10);
        node1.addConnection(connection);
        node2.addConnection(connection);
        when(nodeRepository.findById(node2.getId())).thenReturn(Optional.of(node2));

        when(nodeRepository.deleteById(node1.getId())).
                thenThrow(new RuntimeException("Error deleting node"));

        assertThrows(RuntimeException.class, () -> nodeService.deleteNodeWithConnections(node1));

        verify(node1).addConnection(connection);
        verify(node2, times(2)).addConnection(connection);

        assertTrue(node2.getConnections().contains(connection));
        assertEquals(1, node2.getConnections().size());
    }
}
