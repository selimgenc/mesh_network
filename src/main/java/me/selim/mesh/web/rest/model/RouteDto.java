package me.selim.mesh.web.rest.model;

import java.util.List;

public record RouteDto(List<NodeDto> nodes, int totalDistance) {

}