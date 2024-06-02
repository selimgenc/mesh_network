package me.selim.mesh.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.domain.Route;
import me.selim.mesh.infrastructure.NodeRepository;
import me.selim.mesh.service.NodeService;
import me.selim.mesh.service.PathFinder;
import me.selim.mesh.web.rest.mapper.ConnectionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NodeResource.class)
public class NodeResourceTest {
    private static final String NODES_URL = "/api/nodes";
    private static final String NODE_URL = "/api/nodes/{id}";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NodeRepository nodeRepository;

    @MockBean
    NodeService nodeService;
    @MockBean
    PathFinder pathFinder;

    @MockBean
    ConnectionMapper connectionMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Get All Nodes")
    void test_getAllNodes() throws Exception {
        List<Node> nodeList = List.of(new Node(1L, "N1", Set.of()), new Node(2L, "N2", Set.of()));
        when(nodeRepository.findAll()).thenReturn(nodeList);

        mockMvc.perform(get(NODES_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(nodeList)));
    }

    @Test
    @DisplayName("Get Node by Id")
    void test_getNodeById() throws Exception {
        Node node = new Node(1L, "N1", Set.of());
        when(nodeRepository.findById(1L)).thenReturn(java.util.Optional.of(node));

        mockMvc.perform(get(NODE_URL, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(node)));
    }

    @Test
    @DisplayName("Create new Node. Confirm Node is created and returned with Id")
    void test_createNode() throws Exception {
        Node savedNode = new Node(1L, "Pretty Node", Set.of());
        when(nodeRepository.save(Mockito.any(Node.class))).thenReturn(savedNode);

        mockMvc.perform(post(NODES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pretty Node\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedNode)));

    }

    @Test
    @DisplayName("Delete Node by Id")
    void test_deleteNodeById() throws Exception {
        Long nodeId = 1L;
        Node nodeToDelete = new Node(nodeId, "N1", Set.of());
        when(nodeRepository.findById(nodeId)).thenReturn(Optional.of(nodeToDelete));

        mockMvc.perform(delete(NODE_URL, 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Establish connection between two nodes")
    void test_establishConnection() throws Exception {
        Long fromId = 1L;
        Long toId = 2L;
        Node fromNode = new Node(fromId, "N1", Set.of());
        Node toNode = new Node(toId, "N2", Set.of());
        when(nodeRepository.findById(fromId)).thenReturn(Optional.of(fromNode));
        when(nodeRepository.findById(toId)).thenReturn(Optional.of(toNode));
        Connection connection = new Connection(fromId, toId, 1);
        when(nodeService.connectNodes(fromId, toId, 1)).thenReturn(connection);
        String requestStr = """
                {"distance":1}
                """;
        mockMvc.perform(post(NODE_URL + "/connect/{toId}", 1, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestStr))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Establish connection between two nodes when one of the nodes does not exist")
    void test_establishConnection_whenNodeDoesNotExist() throws Exception {
        Long fromId = 1L;
        Long toId = 2L;
        Node fromNode = new Node(fromId, "N1", Set.of());

        when(nodeRepository.findById(fromId)).thenReturn(Optional.of(fromNode));
        when(nodeRepository.findById(toId)).thenReturn(Optional.empty());
        String requestStr = """
                {"distance":1}
                """;
        mockMvc.perform(post(NODE_URL + "/connect/{toId}", 1, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestStr))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get connection between two node")
    void test_get_connection_between_two_nodes() throws Exception {
        Long fromId = 1L;
        Long toId = 2L;
        Node fromNode = new Node(fromId, "N1", Set.of());
        Node toNode = new Node(toId, "N2", Set.of());
        Connection connection = new Connection(fromId, toId, 1);
        fromNode.addConnection(connection);
        toNode.addConnection(connection);

        when(nodeRepository.findById(fromId)).thenReturn(Optional.of(fromNode));
        when(nodeRepository.findById(toId)).thenReturn(Optional.of(toNode));

        mockMvc.perform(get(NODE_URL + "/connection/{toId}", 1, 2))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Find the shortest path between two nodes")
    void test_findShortestPath() throws Exception {
        Node start = new Node(1L, "N1", Set.of());
        Node end = new Node(2L, "N2", Set.of());
        Node middle = new Node(3L, "N3", Set.of());

        when(pathFinder.findOptimalRoute(1L, 2L)).thenReturn(new Route(List.of(start, middle, end), 4));

        mockMvc.perform(get(NODE_URL + "/shortestPath/{toId}", 1, 2))
                .andExpect(status().isOk());
    }
}