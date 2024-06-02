package me.selim.mesh.events;

import me.selim.mesh.domain.Node;
import org.springframework.context.ApplicationEvent;

public class NodeCreatedEvent extends ApplicationEvent {
    private final Node node;

    public NodeCreatedEvent(Object source, Node node) {
        super(source);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
