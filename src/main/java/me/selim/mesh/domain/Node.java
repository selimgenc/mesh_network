package me.selim.mesh.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class represents a node in a graph.
 * Each node has a unique id, a name, and a set of connections to other nodes.
 * The class implements Comparable interface to allow sorting of nodes based on their names.
 */
public class Node implements Comparable<Node> {

    public static final int MAX_CONNECTION_COUNT = 4;

    private Long id;

    private final String name;

    private final Set<Connection> connections = new HashSet<>();

    /**
     * Constructs a new Node object with the given name.
     *
     * @param name The name of the node.
     */
    public Node(String name) {
        this.name = name;
    }

    /**
     * Constructs a new Node object with the given id, name, and connections.
     *
     * @param id          The id of the node.
     * @param name        The name of the node.
     * @param connections The connections of the node.
     */
    public Node(@NotNull Long id, @NotEmpty String name,
                @Size(max = MAX_CONNECTION_COUNT) Set<Connection> connections) {
        this.id = id;
        this.name = name;
        this.connections.addAll(connections);
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalArgumentException("Node id cannot be overwritten");
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a copy of the connections set. This is to prevent direct modification of the connections set.
     *
     * @return a copy of the connections set.
     */
    public List<Connection> getConnections() {
        return connections.stream().sorted().toList();
    }

    /**
     * Compares this node with the specified node for order.
     * Returns a negative integer, zero, or a positive integer as this node
     * is less than, equal to, or greater than the specified node.
     *
     * @param o The node to be compared.
     * @return a negative integer, zero, or a positive integer as this node
     * is less than, equal to, or greater than the specified node.
     */
    @Override
    public int compareTo(Node o) {
        //Compare by name alphabetically, ignore case (this is an assumption)
        return this.name.compareToIgnoreCase(o.name);
    }

    /**
     * Checks if the given object is equal to this node.
     * Two nodes are equal if their IDs are equal.
     *
     * @param o The object to compare.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        //Two nodes are equal if their IDs are equal otherwise they are not
        return getId() != null && getId().equals(node.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", connections=" + connections +
                '}';
    }

    /**
     * Checks if this node is connected to the given node.
     *
     * @param to The node to check connection with.
     * @return true if this node is connected to the given node, false otherwise.
     */
    public boolean isConnectedTo(Node to) {
        return connections.stream().anyMatch(c -> c.getNodes().containsAll(Set.of(this.getId(), to.getId())));
    }

    /**
     * Return the connection between this node and the given node.
     *
     * @param to The node to check connection with.
     * @return the connection between this node and the given node.
     */
    public Optional<Connection> getConnectionTo(Node to) {
        return connections.stream().filter(c -> c.getNodes().containsAll(Set.of(this.getId(), to.getId()))).findFirst();
    }


    /**
     * Checks if this node has an empty slot for a new connection.
     *
     * @return true if this node has an empty slot, false otherwise.
     */
    public boolean hasEmptySlot() {
        return connections.size() < MAX_CONNECTION_COUNT;
    }

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Unlocks the node if it is locked by the current thread.
     */
    public void unLock() {
        if (!lock.isLocked() || !lock.isHeldByCurrentThread()) {
            return;
        }
        lock.unlock();
    }

    /**
     * Locks the node for connection operations.
     *
     * @throws IllegalStateException if the node is already locked by another thread.
     */
    public void lockForConnectionOperations() {
        if (lock.isLocked() && !lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("Node is already being on connection operations");
        }
        lock.lock();
    }

    /**
     * Adds a connection to this node.
     *
     * @param connection The connection to add.
     * @return true if the connection was added, false otherwise.
     * @throws IllegalStateException if the node is locked by another thread.
     */
    public boolean addConnection(Connection connection) {
        if (lock.isLocked() && !lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("Node is already being on connection operations");
        }
        if (!connection.getNodes().contains(this.getId())) {
            throw new IllegalArgumentException("Connection must contain this node");
        }
        return connections.add(connection);
    }

    /**
     * Drops a connection from this node.
     *
     * @param connection The connection to drop.
     * @return true if the connection was dropped, false otherwise.
     * @throws IllegalStateException if the node is locked by another thread.
     */
    public boolean dropConnection(Connection connection) {
        if (lock.isLocked() && !lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("Node is already being on connection operations");
        }

        if (!connections.contains(connection)) {
            return false;
        }
        return connections.remove(connection);
    }
}
