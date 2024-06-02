package me.selim.mesh.infrastructure;

import me.selim.mesh.events.ConnectionEstablishedEvent;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncConnectionEventListener {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AsyncConnectionEventListener.class);

    @EventListener(ConnectionEstablishedEvent.class)
    @Async
    public void handleConnectionEstablished(ConnectionEstablishedEvent event) {
        log.info("New connection established: {}", event.getConnection());
    }

}
