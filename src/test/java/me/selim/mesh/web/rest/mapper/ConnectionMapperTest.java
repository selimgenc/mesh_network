package me.selim.mesh.web.rest.mapper;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.infrastructure.NodeRepository;
import me.selim.mesh.web.rest.model.ConnectionDto;
import me.selim.mesh.web.rest.model.NodeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectionMapperTest {

    @Mock
    private NodeRepository nodeRepository;

    @InjectMocks
    private ConnectionMapper mapper;


    @Test
    void mapWithNameOnly() {
        Connection c1_2 = new Connection(1L, 2L, 10);
        Connection c1_3 = new Connection(1L, 3L, 20);
        Connection c2_3 = new Connection(2L, 3L, 5);
        Node node1 = new Node(1L, "N1", Set.of(c1_2, c1_3));
        Node node2 = new Node(2L, "N2", Set.of(c2_3, c1_2));

        when(nodeRepository.findById(1L)).thenReturn(Optional.of(node1));
        when(nodeRepository.findById(2L)).thenReturn(Optional.of(node2));

        ConnectionDto connectionDto = mapper.mapWithNameOnly(c1_2);
        assertTrue(connectionDto.nodes().contains(new NodeDto(1L,"N1")));
        assertTrue(connectionDto.nodes().contains(new NodeDto(2L,"N2")));
        assertEquals(10, connectionDto.distance());

    }
}