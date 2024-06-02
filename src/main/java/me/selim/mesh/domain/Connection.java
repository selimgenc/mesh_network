package me.selim.mesh.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.beans.Transient;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * This class represents a connection between two nodes in a graph.
 * Each connection has a source node, a destination node, a distance, and a connection time.
 * The connection is considered bidirectional, meaning a connection from A to B is the same as a connection from B to A.
 * The class implements Comparable interface to allow sorting of connections based on their distance.
 */
public class Connection implements Comparable<Connection> {
    private final Set<Long> nodes; //The nodes connected to each other
    private final Integer distance; // The distance of the connection
    private final Instant connectionTime; // The time when the connection was established

    /**
     * Constructs a new Connection object.
     *
     * @param from           The source node of the connection.
     * @param to             The destination node of the connection.
     * @param distance       The distance of the connection.
     * @param connectionTime The time when the connection was established.
     * @throws IllegalArgumentException if the source node and the destination node are the same.
     */
    public Connection(@NotNull Long from, @NotNull Long to, @Min(0) Integer distance, @NotNull Instant connectionTime) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("Nodes cannot be connected to themselves");
        }
        this.nodes = Set.of(from, to);
        this.distance = distance;
        this.connectionTime = connectionTime;
    }

    /**
     * Constructs a new Connection object with the current time as the connection time.
     *
     * @param from     The source node of the connection.
     * @param to       The destination node of the connection.
     * @param distance The distance of the connection.
     */
    public Connection(Long from, Long to, Integer distance) {
        this(from, to, distance, Instant.now());
    }

    /**
     * Checks if the given object is equal to this connection.
     * A connection from A to B is considered the same as a connection from B to A.
     *
     * @param o The object to compare.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Connection other)) return false;

        return this.nodes.equals(other.getNodes());
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }

    /**
     * Compares this connection with the specified connection for order.
     * Returns a negative integer, zero, or a positive integer as this connection
     * is less than, equal to, or greater than the specified connection.
     *
     * @param o The connection to be compared.
     * @return a negative integer, zero, or a positive integer as this connection
     * is less than, equal to, or greater than the specified connection.
     */
    @Override
    public int compareTo(@NotNull Connection o) {
        return Integer.compare(this.distance, o.distance);
    }

    public Set<Long> getNodes() {
        return nodes;
    }

    public Integer getDistance() {
        return distance;
    }

    public Instant getConnectionTime() {
        return connectionTime;
    }

    @Transient
    public Long getOtherNodeId(Long nodeId) {
        //if nodeId is not in the nodes set, return null
        return nodes.contains(nodeId) ? nodes.stream().filter(id -> !id.equals(nodeId)).findFirst().get() : null;
    }

    /**
     * Returns a string representation of the connection.
     *
     * @return a string representation of the connection.
     */
    @Override
    public String toString() {
        return "Connection{" +
                "nodes=" + nodes +
                ", distance=" + distance +
                ", connectionTime=" + connectionTime +
                '}';
    }
}