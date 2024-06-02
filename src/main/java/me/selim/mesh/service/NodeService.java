package me.selim.mesh.service;

import me.selim.mesh.domain.Connection;

public interface NodeService {
    Connection connectNodes(Long firstNodeId, Long secondNodeId, int distance);
    void dropConnection(Long firstNodeId, Long secondNodeId);
    void deleteNodeWithConnections(Long nodeId);
}
