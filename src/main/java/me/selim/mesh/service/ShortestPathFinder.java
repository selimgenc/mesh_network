package me.selim.mesh.service;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.domain.Route;
import me.selim.mesh.error.ResourceDoesNotExistException;
import me.selim.mesh.infrastructure.NodeRepository;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class implements the PathFinder interface and is responsible for finding the shortest path
 * between two nodes in a graph. It uses Dijkstra's algorithm to find the shortest path.
 * <p>
 * The class is annotated with @Component, meaning it is an object managed by the Spring container.
 * <p>
 * It uses a NodeRepository to fetch nodes from the data source. The NodeRepository is injected via the constructor.
 * <p>
 * The main public method is findOptimalRoute which takes the start and end NodeId's and returns the shortest Route.
 * It first checks if the start and end nodes exist in the repository, if not it throws a ResourceDoesNotExistException.
 * Then it calls the protected method findShortestPath to perform the actual path finding.
 * <p>
 * The findShortestPath method uses Dijkstra's algorithm to find the shortest path between the start and end nodes.
 * It maintains a PriorityQueue of nodes to visit, a Map of the shortest known distances to each node, and a Map of the previous node on the shortest path to each node.
 * It iterates over each node in the queue, updating the shortest known distances and previous nodes as it goes.
 * Once the queue is empty, it constructs the shortest path by following the previous nodes from the end node back to the start node.
 * Algorithm may not be optimal yet, but it does the job for now.
 */
@Component
public class ShortestPathFinder implements PathFinder {
    private final NodeRepository nodeRepository;

    public ShortestPathFinder(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public Route findOptimalRoute(Long start, Long end) {
        Optional<Node> firstNodeOpt = this.nodeRepository.findById(start);
        if (firstNodeOpt.isEmpty()) {
            throw new ResourceDoesNotExistException("Node with id: " + start + " does not exist");
        }
        Optional<Node> secondNodeOpt = this.nodeRepository.findById(end);
        if (secondNodeOpt.isEmpty()) {
            throw new ResourceDoesNotExistException("Node with id: " + end + " does not exist");
        }

        return findShortestPath(firstNodeOpt.get(), secondNodeOpt.get());
    }

    protected Route findShortestPath(Node start, Node end) {
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Integer> nodeCounts = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        PriorityQueue<Tuple> queue = new PriorityQueue<>(Comparator
                .comparingInt((Tuple t) -> t.distance)
                .thenComparingInt(t -> t.nodeCount));

        distances.put(start, 0);
        nodeCounts.put(start, 0);
        queue.add(new Tuple(0, start, 0));

        while (!queue.isEmpty()) {
            Tuple currentTuple = queue.poll();
            Node currentNode = currentTuple.node;
            int currentDistance = currentTuple.distance;
            int currentNodeCount = currentTuple.nodeCount;

            if (currentNode.equals(end)) {
                break;
            }

            for (Connection connection : currentNode.getConnections()) {
                Long connectedNodeId = connection.getOtherNodeId(currentNode.getId());
                Optional<Node> neighborOpt = nodeRepository.findById(connectedNodeId);
                if (neighborOpt.isEmpty()) {
                    continue;
                }
                Node neighbor = neighborOpt.get();
                int distance = connection.getDistance();
                int newDistance = currentDistance + distance;
                int newNodeCount = currentNodeCount + 1;

                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)
                        || (newDistance == distances.get(neighbor) && newNodeCount < nodeCounts.getOrDefault(neighbor, Integer.MAX_VALUE))) {
                    distances.put(neighbor, newDistance);
                    nodeCounts.put(neighbor, newNodeCount);
                    previousNodes.put(neighbor, currentNode);
                    queue.add(new Tuple(newDistance, neighbor, newNodeCount));
                }
            }
        }

        List<Node> path = new ArrayList<>();
        for (Node node = end; node != null; node = previousNodes.get(node)) {
            path.add(node);
        }
        Collections.reverse(path);

        return new Route(path, distances.get(end));
    }

    private static class Tuple {
        int distance;
        Node node;
        int nodeCount;

        Tuple(int distance, Node node, int nodeCount) {
            this.distance = distance;
            this.node = node;
            this.nodeCount = nodeCount;
        }
    }
}
