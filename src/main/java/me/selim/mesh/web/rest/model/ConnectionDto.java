package me.selim.mesh.web.rest.model;

import java.util.List;

public record ConnectionDto(List<NodeDto> nodes, int distance) {

}
