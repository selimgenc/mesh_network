package me.selim.mesh.service;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.error.ResourceDoesNotExistException;
import me.selim.mesh.events.ConnectionDropEvent;
import me.selim.mesh.events.ConnectionEstablishedEvent;
import me.selim.mesh.events.NodeDeletedEvent;
import me.selim.mesh.infrastructure.NodeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class provides the implementation for the NodeService interface.
 * It provides methods to connect nodes, drop connections between nodes, and delete nodes with their connections.
 */
@Component
public class NodeServiceImpl implements NodeService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NodeServiceImpl.class);

    private final NodeRepository nodeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public NodeServiceImpl(NodeRepository nodeRepository, ApplicationEventPublisher eventPublisher) {
        this.nodeRepository = nodeRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Connects two nodes with a specified distance.
     *
     * @param firstNodeId  the id of the first node.
     * @param secondNodeId the id of the second node.
     * @param distance     the distance between the two nodes.
     * @return the connection created between the two nodes.
     * @throws ResourceDoesNotExistException if either of the nodes does not exist.
     * @throws IllegalArgumentException      if either of the nodes has no empty slots for connections.
     */
    @Override
    public Connection connectNodes(Long firstNodeId, Long secondNodeId, int distance) {
        Optional<Node> firstNodeOpt = nodeRepository.findById(firstNodeId);
        if (firstNodeOpt.isEmpty()) {
            throw new ResourceDoesNotExistException("Node with id: " + firstNodeId + " does not exist");
        }
        Optional<Node> secondNodeOpt = nodeRepository.findById(secondNodeId);
        if (secondNodeOpt.isEmpty()) {
            throw new ResourceDoesNotExistException("Node with id: " + secondNodeId + " does not exist");
        }
        Node firstNode = firstNodeOpt.get();
        Node secondNode = secondNodeOpt.get();
        Connection connection = new Connection(firstNodeId, secondNodeId, distance);
        try {
            //lock Nodes for connection, so no other thread can connect them
            firstNode.lockForConnectionOperations();
            secondNode.lockForConnectionOperations();

            if (!firstNode.hasEmptySlot()) {
                throw new IllegalArgumentException("Node " + firstNode.getId() + " has no empty slots for connections");
            }
            if (!secondNode.hasEmptySlot()) {
                throw new IllegalArgumentException("Node " + secondNode.getId() + " has no empty slots for connections");
            }

            firstNode.addConnection(connection);
            secondNode.addConnection(connection);

            eventPublisher.publishEvent(new ConnectionEstablishedEvent(this, connection));
        } finally {
            //unlock Nodes
            firstNode.unLock();
            secondNode.unLock();
        }
        return connection;
    }

    /**
     * Drops the connection between two nodes.
     *
     * @param firstNodeId  the id of the first node.
     * @param secondNodeId the id of the second node.
     * @throws ResourceDoesNotExistException if either of the nodes does not exist.
     * @throws IllegalArgumentException      if the nodes are not connected.
     */
    @Override
    public void dropConnection(Long firstNodeId, Long secondNodeId) {
        Optional<Node> firstNodeOpt = nodeRepository.findById(firstNodeId);
        if (firstNodeOpt.isEmpty()) {
            throw new ResourceDoesNotExistException("Node with id: " + firstNodeId + " does not exist");
        }
        Optional<Node> secondNodeOpt = nodeRepository.findById(secondNodeId);
        if (secondNodeOpt.isEmpty()) {
            throw new ResourceDoesNotExistException("Node with id: " + secondNodeId + " does not exist");
        }
        Node firstNode = firstNodeOpt.get();
        Node secondNode = secondNodeOpt.get();

        Optional<Connection> connectionOpt = firstNode.getConnectionTo(secondNode);
        if (connectionOpt.isEmpty()) {
            throw new IllegalArgumentException("Nodes are not connected");
        }
        Connection connection = connectionOpt.get();

        try {
            //lock Nodes for connection, so no other thread can connect them
            firstNode.lockForConnectionOperations();
            secondNode.lockForConnectionOperations();

            firstNode.dropConnection(connection);
            secondNode.dropConnection(connection);
            eventPublisher.publishEvent(new ConnectionDropEvent(this, connection));
        } catch (Exception e) {
            log.error("Error dropping connection between nodes with ids: {} and {}", firstNodeId, secondNodeId, e);
            firstNode.addConnection(connection);
            secondNode.addConnection(connection);
            throw e;
        } finally {
            //unlock Nodes
            firstNode.unLock();
            secondNode.unLock();
        }

    }

    /**
     * Deletes a node and all its connections.
     *
     * @param nodeId the id of the node to be deleted.
     * @throws ResourceDoesNotExistException if the node does not exist.
     */
    @Override
    public void deleteNodeWithConnections(Long nodeId) {
        Optional<Node> optionalNode = nodeRepository.findById(nodeId);
        if (optionalNode.isEmpty()) {
            throw new ResourceDoesNotExistException("Node with id: " + nodeId + " does not exist");
        }
        Node node = optionalNode.get();
        deleteNodeWithConnections(node);
    }

    /**
     * Deletes a node and all its connections.
     *
     * @param node the node to be deleted.
     */
    void deleteNodeWithConnections(Node node) {
        boolean rollback = false;
        List<Connection> connections = node.getConnections();
        Map<Node, Connection> connectedNodes = new HashMap<>();
        try {
            node.lockForConnectionOperations();
            log.info("Deleting node with connections {}", node);
            for (Connection connection : connections) {
                Long connectedNodeId = connection.getNodes().stream()
                        .filter(t -> !Objects.equals(t, node.getId())).findFirst().get();
                Optional<Node> connectedNodeOpt = nodeRepository.findById(connectedNodeId);
                if (connectedNodeOpt.isEmpty()) {
                    throw new ResourceDoesNotExistException("Connected node with id: " + connectedNodeId + " does not exist");
                }
                Node connectedNode = connectedNodeOpt.get();
                connectedNode.lockForConnectionOperations();

                connectedNode.dropConnection(connection);
                connectedNodes.put(connectedNode, connection);
            }
            nodeRepository.deleteById(node.getId());
            log.info("Node with id: {} deleted", node.getId());
        } catch (Exception e) {
            rollback = true;
            throw e;
        } finally {
            if (rollback) {
                log.error("Rolling back node deletion {}", node);
                connectedNodes.forEach(Node::addConnection);
                log.info("Node with id: {} restored", node.getId());
            }
            node.unLock();
            connectedNodes.keySet().forEach(Node::unLock);
        }
    }
}
