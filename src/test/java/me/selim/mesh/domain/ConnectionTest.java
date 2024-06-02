package me.selim.mesh.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionTest {

    @Test
    @DisplayName("Compare Connections when they are equal")
    void test_compareTo_equals() {
        Connection con1 = new Connection(1L, 2L, 1);
        Connection con2 = new Connection(1L, 2L, 1);
        assertEquals(0, con1.compareTo(con2));
    }

    @Test
    @DisplayName("Compare Connections when the first one lighter")
    void test_compareTo_less() {
        Connection con1 = new Connection(1L, 2L, 1);
        Connection con2 = new Connection(1L, 2L, 3);
        assertTrue(con1.compareTo(con2) < 0);
    }

    @Test
    @DisplayName("Compare Connections when the second one lighter")
    void test_compareTo_greater() {
        Connection con1 = new Connection(1L, 2L, 2);
        Connection con2 = new Connection(1L, 2L, 1);
        assertTrue(con1.compareTo(con2) > 0);
    }

    @Test
    @DisplayName("Compare Connections when they are not equal")
    void test_same_connection_with_different_order() {
        Connection conAtoB = new Connection(1L, 2L, 1);
        Connection conBtoA = new Connection(2L, 1L, 1);
        assertEquals(conBtoA, conAtoB);

        //assert hash codes
        assertEquals(conBtoA.hashCode(), conAtoB.hashCode());

    }

    @Test
    @DisplayName("Compare Connections when they are not equal but different distance or creation time")
    void test_same_connection_with_different_weight_time() {
        Connection conAtoB = new Connection(1L, 2L, 1, Instant.now().minusSeconds(100));
        Connection conBtoA = new Connection(2L, 1L, 2, Instant.now());
        assertEquals(conBtoA, conAtoB);

        //assert hash codes
        assertEquals(conBtoA.hashCode(), conAtoB.hashCode());

    }

    @Test
    @DisplayName("Connections with different pairs are not equal")
    void test_different_pairs_are_not_equal() {
        Connection conAtoB = new Connection(1L, 2L, 1);
        Connection conBtoC = new Connection(2L, 3L, 1);
        Assertions.assertThat(conBtoC).isNotEqualTo(conAtoB);
    }
}