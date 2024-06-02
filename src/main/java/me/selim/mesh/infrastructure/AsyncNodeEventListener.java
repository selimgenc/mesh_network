package me.selim.mesh.infrastructure;

import me.selim.mesh.events.NodeCreatedEvent;
import me.selim.mesh.events.NodeDeletedEvent;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncNodeEventListener {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AsyncNodeEventListener.class);

    @Async
    @EventListener()
    public void handleNewNodeCreation(NodeCreatedEvent event) {
        log.info("New Node created {}", event.getNode());
    }

    @Async
    @EventListener()
    public void handleNodeDeleted(NodeDeletedEvent event) {
        log.info("Node with id deleted {}", event.getNode().getId());
    }

}
