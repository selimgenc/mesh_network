package me.selim.mesh.events;

import me.selim.mesh.domain.Connection;
import org.springframework.context.ApplicationEvent;

public class ConnectionDropEvent extends ApplicationEvent {
    private final Connection connection;

    public ConnectionDropEvent(Object source, Connection connection) {
        super(source);
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
