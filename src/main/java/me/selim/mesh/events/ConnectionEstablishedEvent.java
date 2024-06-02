package me.selim.mesh.events;

import me.selim.mesh.domain.Connection;
import org.springframework.context.ApplicationEvent;

public class ConnectionEstablishedEvent extends ApplicationEvent {
    private final Connection connection;

    public ConnectionEstablishedEvent(Object source, Connection connection) {
        super(source);
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
