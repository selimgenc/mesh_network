package me.selim.mesh.infrastructure;

import me.selim.mesh.domain.Connection;
import me.selim.mesh.events.ConnectionEstablishedEvent;
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
class AsyncNodeEventListenerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @MockBean
    private AsyncConnectionEventListener listener;

    @Test
    void handleConnectionEstablished() {

        Connection connection = new Connection(1L, 2L, 10);
        ConnectionEstablishedEvent event = new ConnectionEstablishedEvent(this, connection);

        publisher.publishEvent(event);
        // Wait for the async method to complete using awaitility
        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->
                verify(listener, times(1)).handleConnectionEstablished(event)
        );
    }

}