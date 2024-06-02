package me.selim.mesh.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryNodeIdGeneratorTest {

    @Test
    @DisplayName("next() should return a new NodeId")
    void next() {
        InMemoryNodeIdGenerator generator = new InMemoryNodeIdGenerator();
        Long nodeId1 = generator.next();
        Long nodeId2 = generator.next();

        assertNotEquals(nodeId1, nodeId2);
    }
}