package me.selim.mesh.web.rest.sort;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.web.rest.model.SortType;

import java.util.List;


public interface ConnectionSorter {
    List<Connection> sort(List<Node> allNodes, SortType sortType);
}
