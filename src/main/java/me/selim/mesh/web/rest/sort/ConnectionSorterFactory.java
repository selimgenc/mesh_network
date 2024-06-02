package me.selim.mesh.web.rest.sort;

import me.selim.mesh.web.rest.model.SortCriteria;

public class ConnectionSorterFactory {
    public static ConnectionSorter getSorter(SortCriteria sortCriteria) {
        return switch (sortCriteria) {
            case NODE -> new NodeSorter();
            case DISTANCE -> new DistanceSorter();
        };
    }
}
