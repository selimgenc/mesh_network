package me.selim.mesh.web.rest.sort;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.web.rest.model.SortType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class NodeSorter implements ConnectionSorter {

    @Override
    public List<Connection> sort(List<Node> allNodes, SortType sortType) {
        Comparator<Node> comparator = Objects.requireNonNull(sortType) == SortType.DESC ?
                Comparator.reverseOrder() : Comparator.naturalOrder();

        List<Node> nodes = new ArrayList<>(allNodes);
        nodes.sort(comparator);
        List<Connection> orderedConnections = new ArrayList<>();
        for (Node node : nodes) {
            for (Connection nodeConnection : node.getConnections()) {
                if (!orderedConnections.contains(nodeConnection)) {
                    orderedConnections.add(nodeConnection);
                }
            }
        }
        return orderedConnections;
    }
}
