package me.selim.mesh.web.rest.sort;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.web.rest.model.SortType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DistanceSorter implements ConnectionSorter {
    @Override
    public List<Connection> sort(List<Node> allNodes, SortType sortType) {
        Comparator<Integer> comparator = Objects.requireNonNull(sortType) == SortType.DESC ?
                Comparator.reverseOrder() : Comparator.naturalOrder();

        // Get all connections from all nodes
        // Sort connections by distance using the determined comparator
        return allNodes.stream()
                .flatMap(node -> node.getConnections().stream())
                .distinct()
                .sorted(Comparator.comparing(Connection::getDistance, comparator))
                .collect(Collectors.toList());
    }
}
