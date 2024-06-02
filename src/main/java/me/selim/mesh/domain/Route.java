package me.selim.mesh.domain;

import java.util.List;

/**
 * A record representing a route in a graph.
 * <p>
 * A route is a sequence of nodes, with a total distance associated with it.
 * The total distance is the sum of the distances between consecutive nodes in the route.
 *
 * @param nodes         The list of nodes in the route. They are ordered as they appear in the route.
 * @param totalDistance The total distance of the route.
 */
public record Route(List<Node> nodes, int totalDistance) {

}