package me.selim.mesh.web.rest.mapper;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.infrastructure.NodeRepository;
import me.selim.mesh.web.rest.model.ConnectionDto;
import me.selim.mesh.web.rest.model.NodeDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ConnectionMapper {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConnectionMapper.class);
    private final NodeRepository nodeRepository;


    public ConnectionMapper(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public ConnectionDto mapWithNameOnly(Connection connection) {
        if (connection == null) {
            return null;
        }
        List<NodeDto> connectionDto = new ArrayList<>();
        for (Long nodeId : connection.getNodes()) {
            Optional<Node> nodeOpt = this.nodeRepository.findById(nodeId);
            if (nodeOpt.isEmpty()) {
                log.warn("Node {} not found", nodeId);
                continue;
            }
            connectionDto.add(new NodeDto(nodeOpt.get().getId(), nodeOpt.get().getName()));
        }
        return new ConnectionDto(connectionDto, connection.getDistance());
    }
}
