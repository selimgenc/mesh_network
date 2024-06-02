package me.selim.mesh.web.rest.sort;

import me.selim.mesh.web.rest.model.SortCriteria;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class ConnectionSorterFactoryTest {

    @Test
    @DisplayName("Test getSorter factory")
    void getSorter() {

        ConnectionSorter sorterNode = ConnectionSorterFactory.getSorter(SortCriteria.NODE);
        Assertions.assertInstanceOf(NodeSorter.class, sorterNode);

        ConnectionSorter distanceSorter = ConnectionSorterFactory.getSorter(SortCriteria.DISTANCE);
        Assertions.assertInstanceOf(DistanceSorter.class, distanceSorter);
    }
}