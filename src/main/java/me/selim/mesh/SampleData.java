package me.selim.mesh;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.infrastructure.NodeRepository;
import me.selim.mesh.service.NodeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@ConfigurationProperties(prefix = "sample")
public class SampleData {
    private boolean disable;

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    private final static AtomicBoolean dataInit = new AtomicBoolean(false);
    @Bean
    CommandLineRunner initDatabase(NodeRepository repository, NodeService service) {
        return args -> {
            if (disable) {
                return;
            }
            if (dataInit.get()){
                return;
            }
            dataInit.set(true);
            createNodes(repository);
            createConnections(service);
        };
    }

    /**
     * Create following graph for testing
     * (2)----(4)----(6)
     * /  \   / |    / \
     * /    \ /  |   /   \
     * (1)----(3) (7)--(9) (11)
     * | \   /    |    \  / \
     * |  \ /    |      \/   \
     * (5) (8)  (10)----(12) (14)
     * |   /    |     /      |
     * |  /     |    /      |
     * (13)----(15) (17)--(19)
     * \    /  |   /    /
     * \  /   |  /    /
     * (16)---(18)--(20)
     *
     * @param repository
     */
    void createNodes(NodeRepository repository) {
        for (int i = 1; i < 21; i++) {
            repository.save(new Node("Node " + i));
        }
    }

    void createConnections(NodeService nodeService) {
        // Create connections with distances based on the number of dashes
        Connection c1_2 = new Connection(1L, 2L, 2);
        Connection c1_3 = new Connection(1L, 3L, 4);
        Connection c1_5 = new Connection(1L, 5L, 2);
        Connection c1_8 = new Connection(1L, 8L, 2);
        addConnectionsToNodes(nodeService, List.of(c1_2, c1_3, c1_5, c1_8));

        Connection c2_3 = new Connection(2L, 3L, 3);
        Connection c2_4 = new Connection(2L, 4L, 4);
        addConnectionsToNodes(nodeService, List.of(c2_3, c2_4));

        Connection c3_4 = new Connection(3L, 4L, 2);
        Connection c3_8 = new Connection(3L, 8L, 2);
        addConnectionsToNodes(nodeService, List.of(c3_4, c3_8));

        Connection c4_6 = new Connection(4L, 6L, 4);
        Connection c4_7 = new Connection(4L, 7L, 2);
        addConnectionsToNodes(nodeService, List.of(c4_6, c4_7));

        Connection c5_13 = new Connection(5L, 13L, 2);
        addConnectionsToNodes(nodeService, List.of(c5_13));

        Connection c6_9 = new Connection(6L, 9L, 2);
        Connection c6_11 = new Connection(6L, 11L, 2);
        addConnectionsToNodes(nodeService, List.of(c6_9, c6_11));

        Connection c7_9 = new Connection(7L, 9L, 2);
        addConnectionsToNodes(nodeService, List.of(c7_9));

        Connection c8_13 = new Connection(8L, 13L, 2);
        addConnectionsToNodes(nodeService, List.of(c8_13));

        Connection c9_12 = new Connection(9L, 12L, 2);
        addConnectionsToNodes(nodeService, List.of(c9_12));

        Connection c10_12 = new Connection(10L, 12L, 4);
        Connection c10_15 = new Connection(10L, 15L, 2);
        addConnectionsToNodes(nodeService, List.of(c10_12, c10_15));

        Connection c11_12 = new Connection(11L, 12L, 2);
        Connection c11_14 = new Connection(11L, 14L, 2);
        addConnectionsToNodes(nodeService, List.of(c11_12, c11_14));

        Connection c12_17 = new Connection(12L, 17L, 2);
        addConnectionsToNodes(nodeService, List.of(c12_17));

        Connection c13_15 = new Connection(13L, 15L, 4);
        Connection c13_16 = new Connection(13L, 16L, 2);
        addConnectionsToNodes(nodeService, List.of(c13_15, c13_16));

        Connection c14_19 = new Connection(14L, 19L, 2);
        addConnectionsToNodes(nodeService, List.of(c14_19));

        Connection c15_16 = new Connection(15L, 16L, 2);
        Connection c15_18 = new Connection(15L, 18L, 2);
        addConnectionsToNodes(nodeService, List.of(c15_16, c15_18));

        Connection c16_18 = new Connection(16L, 18L, 3);
        addConnectionsToNodes(nodeService, List.of(c16_18));

        Connection c17_18 = new Connection(17L, 18L, 2);
        Connection c17_19 = new Connection(17L, 19L, 2);
        addConnectionsToNodes(nodeService, List.of(c17_18, c17_19));

        Connection c18_20 = new Connection(18L, 20L, 2);
        Connection c19_20 = new Connection(19L, 20L, 2);
        addConnectionsToNodes(nodeService, List.of(c18_20, c19_20));
    }


    private static void addConnectionsToNodes(NodeService nodeService, List<Connection> connections) {
        for (Connection connection : connections) {
            List<Long> nodes = new ArrayList<>(connection.getNodes());
            Long firstNodeId = nodes.get(0);
            Long secondNodeId = nodes.get(1);
            nodeService.connectNodes(firstNodeId, secondNodeId, connection.getDistance());
        }
    }
}

