package me.selim.mesh.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import me.selim.mesh.domain.Connection;
import me.selim.mesh.domain.Node;
import me.selim.mesh.domain.Route;
import me.selim.mesh.error.ResourceDoesNotExistException;
import me.selim.mesh.infrastructure.NodeRepository;
import me.selim.mesh.service.NodeService;
import me.selim.mesh.service.PathFinder;
import me.selim.mesh.web.rest.mapper.ConnectionMapper;
import me.selim.mesh.web.rest.model.*;
import me.selim.mesh.web.rest.sort.ConnectionSorter;
import me.selim.mesh.web.rest.sort.ConnectionSorterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Node Resources", description = "This API provides endpoints for Node operations")
@RestController
@RequestMapping("/api/nodes")
public class NodeResource {

    private static final Logger log = LoggerFactory.getLogger(NodeResource.class);

    private final NodeRepository repository;
    private final NodeService nodeService;
    private final PathFinder pathFinder;
    private final ConnectionMapper connectionMapper;

    public NodeResource(NodeRepository repository, NodeService nodeService, PathFinder pathFinder, ConnectionMapper connectionMapper) {
        this.repository = repository;
        this.nodeService = nodeService;
        this.pathFinder = pathFinder;
        this.connectionMapper = connectionMapper;
    }

    @Operation(summary = "Get all nodes")
    @ApiResponse(responseCode = "200", description = "List of all nodes", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Node.class)))
    })
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<EntityModel<Node>>> getAllNodes() {
        List<EntityModel<Node>> list = repository.findAll().stream().map(node -> EntityModel.of(node,
                        linkTo(methodOn(NodeResource.class).getNodeById((Long) node.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get a node by its ID")
    @ApiResponse(responseCode = "200", description = "Found the node",
            content = {@Content(schema = @Schema(implementation = Node.class))}
    )
    @GetMapping(path = "/{id}")
    ResponseEntity<?> getNodeById(@PathVariable @Min(1) Long id) {
        Node node = repository.findById(id)
                .orElseThrow(() -> new ResourceDoesNotExistException("Node with id: " + id + "' does not exist"));
        EntityModel<Node> entityModel = EntityModel.of(node,
                linkTo(methodOn(NodeResource.class).getNodeById(id)).withSelfRel(),
                linkTo(methodOn(NodeResource.class).getAllNodes()).withRel("nodes"));
        return ResponseEntity.ok(entityModel);
    }

    @Operation(summary = "Create a new node by name.")
    @ApiResponse(responseCode = "201", description = "Node created",
            content = {@Content(schema = @Schema(implementation = Node.class))}
    )
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<Node>> createNode(@RequestBody RestNodeCreateRequest request) {
        Node node = new Node(request.name());
        Node savedNode = repository.save(node);
        EntityModel<Node> entityModel = EntityModel.of(savedNode,
                linkTo(methodOn(NodeResource.class).getNodeById(savedNode.getId())).withSelfRel(),
                linkTo(methodOn(NodeResource.class).getAllNodes()).withRel("nodes"));

        log.info("Node with id: {} created", savedNode.getId());
        return ResponseEntity.created(
                        linkTo(methodOn(NodeResource.class).getNodeById(savedNode.getId())).toUri())
                .body(entityModel);
    }

    @Operation(summary = "Delete a node by its ID. It also delete the connections on connected Nodes")
    @ApiResponse(responseCode = "204", description = "Node deleted")
    @DeleteMapping(path = "/{id}")
    ResponseEntity<?> deleteNodeById(@PathVariable @Min(1) Long id) {
        nodeService.deleteNodeWithConnections(id);
        log.info("Node with id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Connect two nodes. If nodes are already connected, it returns 409 Conflict. A Node can have maximum 4 connection.")
    @ApiResponse(responseCode = "201", description = "Nodes connected",
            content = {@Content(schema = @Schema(implementation = Connection.class))}
    )
    @PostMapping(path = "/{id}/connect/{targetId}")
    ResponseEntity<?> connectNodes(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long targetId, @RequestBody RestConnectRequest request) {
        Node node = repository.findById(id)
                .orElseThrow(() -> new ResourceDoesNotExistException("Node with id: " + id + "' does not exist"));
        Node targetNode = repository.findById(targetId)
                .orElseThrow(() -> new ResourceDoesNotExistException("Node with id: " + targetId + "' does not exist"));

        if (node.isConnectedTo(targetNode)) {
            log.info("Nodes are already connected {} and {}", id, targetId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nodes are already connected");
        }

        Connection connection = nodeService.connectNodes(id, targetId, request.distance());
        log.info("Node: {} connected to Node: {}", id, targetId);
        EntityModel<Connection> entityModel = EntityModel.of(connection,
                linkTo(methodOn(NodeResource.class).getConnectionBetweenNodes(id, targetId)).withSelfRel());

        return ResponseEntity.created(
                linkTo(methodOn(NodeResource.class).getConnectionBetweenNodes(id, targetId)).toUri()).body(entityModel);
    }

    @Operation(summary = "Get connection between two nodes")
    @ApiResponse(responseCode = "200", description = "Connection details",
            content = {@Content(schema = @Schema(implementation = Connection.class))}
    )
    @GetMapping(path = "/{id}/connection/{targetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Connection>> getConnectionBetweenNodes(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long targetId) {
        Node node = repository.findById(id)
                .orElseThrow(() -> new ResourceDoesNotExistException("Node with id: " + id + " does not exist"));
        Node targetNode = repository.findById(targetId)
                .orElseThrow(() -> new ResourceDoesNotExistException("Node with id: " + targetId + " does not exist"));

        Optional<Connection> connectionOptional = node.getConnectionTo(targetNode);
        if (connectionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Connection connection = connectionOptional.get();
        EntityModel<Connection> entityModel = EntityModel.of(connection,
                linkTo(methodOn(NodeResource.class).getConnectionBetweenNodes(id, targetId)).withSelfRel());
        return ResponseEntity.ok(entityModel);
    }

    @Operation(summary = "Disconnect two nodes")
    @ApiResponse(responseCode = "204", description = "Nodes disconnected")
    @DeleteMapping(path = "/{id}/disconnect/{targetId}")
    ResponseEntity<?> disconnectNodes(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long targetId) {
        nodeService.dropConnection(id, targetId);
        log.info("Connection between nodes {} and {} dropped", id, targetId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Find the shortest path between two nodes")
    @ApiResponse(responseCode = "200", description = "Shortest path found",
            content = {@Content(schema = @Schema(implementation = Route.class))}
    )
    @GetMapping(path = "/{id}/shortestPath/{targetId}")
    ResponseEntity<?> findShortestPath(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long targetId) {
        Route route = pathFinder.findOptimalRoute(id, targetId);
        log.info("Shortest path between nodes {} and {} found as {} ", id, targetId, route);

        List<NodeDto> nodesDto = route.nodes().stream().map(node -> new NodeDto(node.getId(), node.getName())).toList();
        RouteDto routeDto = new RouteDto(nodesDto, route.totalDistance());

        EntityModel<RouteDto> entityModel = EntityModel.of(routeDto,
                linkTo(methodOn(NodeResource.class).getNodeById(id)).withRel("startNode"),
                linkTo(methodOn(NodeResource.class).getNodeById(targetId)).withRel("targetNode"),
                linkTo(methodOn(NodeResource.class).findShortestPath(id, targetId)).withSelfRel());

        routeDto.nodes().forEach(node ->
                entityModel.add(linkTo(methodOn(NodeResource.class).getNodeById(node.id())).withRel("node-" + node.id()))
        );
        return ResponseEntity.ok(entityModel);
    }

    @Operation(summary = "Get all connections between nodes")
    @ApiResponse(responseCode = "200", description = "List of all connections between nodes in a sorted way", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConnectionDto.class)))
    })
    @GetMapping("/connections")
    ResponseEntity<List<EntityModel<ConnectionDto>>> getAllConnections(@RequestParam SortCriteria criteria, @RequestParam SortType sortType) {
        ConnectionSorter sorter = ConnectionSorterFactory.getSorter(criteria);
        List<ConnectionDto> sortedConnections = sorter.sort(repository.findAll(), sortType)
                .stream().map(connectionMapper::mapWithNameOnly).toList();

        List<EntityModel<ConnectionDto>> sortedAndLinkedConnections = sortedConnections.stream().map(dto -> EntityModel.of(dto,
                        linkTo(methodOn(NodeResource.class).getConnectionBetweenNodes(dto.nodes().get(0).id(), dto.nodes().get(1).id()))
                                .withRel(String.format("Nodes: " + dto.nodes().get(0).name() + " and " + dto.nodes().get(1).name()))
                )
        ).toList();
        return ResponseEntity.ok(sortedAndLinkedConnections);
    }

}
