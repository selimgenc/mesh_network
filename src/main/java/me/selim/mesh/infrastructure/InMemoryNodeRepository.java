package me.selim.mesh.infrastructure;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import me.selim.mesh.domain.Node;
import me.selim.mesh.events.NodeCreatedEvent;
import me.selim.mesh.events.NodeDeletedEvent;
import me.selim.mesh.service.IdGenerator;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
@Validated
public class InMemoryNodeRepository implements NodeRepository {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(InMemoryNodeRepository.class);
    private final Set<Node> nodes = new ConcurrentSkipListSet<>();

    private final IdGenerator<Long> idGenerator;
    private final ApplicationEventPublisher eventPublisher;

    public InMemoryNodeRepository(IdGenerator<Long> idGenerator, ApplicationEventPublisher eventPublisher) {
        this.idGenerator = idGenerator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Node save(@NotNull Node node) {
        //assign ID to the node, if not only save
        if (node.getId() == null) {
            node.setId(idGenerator.next());
        }
        boolean add = nodes.add(node);
        if (!add) {
            log.info("Node already exists, updating {}", node);
        }
        eventPublisher.publishEvent(new NodeCreatedEvent(this, node));
        return node;
    }

    @Override
    public List<Node> saveAll(@NotEmpty Iterable<Node> Node) {
        List<Node> savedNodes = new ArrayList<>();
        for (Node node : Node) {
            savedNodes.add(save(node));
        }
        return savedNodes;
    }

    @Override
    public Optional<Node> findById(@NotNull Long id) {
        return nodes.stream().filter(node -> node.getId().equals(id)).findFirst();
    }

    @Override
    public List<Node> findAll() {
        return nodes.stream().toList();
    }

    @Override
    public boolean deleteById(@NotNull Long id) {
        Optional<Node> byId = findById(id);
        if (byId.isEmpty()) {
            return false;
        }
        boolean removed = nodes.removeIf(node -> node.getId().equals(id));
        if (removed) {
            eventPublisher.publishEvent(new NodeDeletedEvent(this, byId.get()));
        }
        return removed;
    }
}
