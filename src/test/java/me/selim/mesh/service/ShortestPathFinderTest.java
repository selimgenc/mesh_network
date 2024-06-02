package me.selim.mesh.service;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.domain.Route;
import me.selim.mesh.infrastructure.NodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortestPathFinderTest {

    @Mock
    NodeRepository nodeRepository;

    ShortestPathFinder shortestPathFinder;

    @BeforeEach
    void setUp() {
        shortestPathFinder = new ShortestPathFinder(nodeRepository);
    }

    @Test
    @DisplayName("Find the optimal route between two nodes")
    void findTheRoute() {
        Node start = new Node(1L, "N1", Set.of());
        Node end = new Node(2L, "N2", Set.of());
        Node middle = new Node(3L, "N3", Set.of());

        Connection conn1 = new Connection(start.getId(), middle.getId(), 3);
        start.addConnection(conn1);
        middle.addConnection(conn1);
        Connection conn2 = new Connection(middle.getId(), end.getId(), 1);
        middle.addConnection(conn2);
        end.addConnection(conn2);

        when(nodeRepository.findById(2L)).thenReturn(Optional.of(end));
        when(nodeRepository.findById(3L)).thenReturn(Optional.of(middle));
//        when(nodeRepository.findAll()).thenReturn(List.of(start, middle, end));


        Route route = shortestPathFinder.findShortestPath(start, end);

        assertEquals(4, route.totalDistance());
        assertEquals(List.of(start, middle, end), route.nodes());
    }

}