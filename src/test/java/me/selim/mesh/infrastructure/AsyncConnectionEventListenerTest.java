package me.selim.mesh.infrastructure;

import me.selim.mesh.domain.Node;
import me.selim.mesh.events.NodeCreatedEvent;
import me.selim.mesh.events.NodeDeletedEvent;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AsyncConnectionEventListenerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @MockBean
    private AsyncNodeEventListener listener;

    @Test
    void handleNewNodeCreation() {
        Node node = new Node("N1_SOME_45644");
        final NodeCreatedEvent event = new NodeCreatedEvent(this, node);

        publisher.publishEvent(event);
        // Wait for the async method to complete using awaitility
        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->
                verify(listener, times(1)).handleNewNodeCreation(event)
        );
    }

    @Test
    void handleNodeDeleted() {
        Node node = new Node("N1_SOME_456432");
        NodeDeletedEvent event = new NodeDeletedEvent(this, node);
        publisher.publishEvent(event);

        // Wait for the async method to complete using awaitility
        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->
                verify(listener, times(1)).handleNodeDeleted(event)
        );
    }
}