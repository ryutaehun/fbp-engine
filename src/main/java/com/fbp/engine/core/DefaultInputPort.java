package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.Getter;
import lombok.Setter;

public class DefaultInputPort implements InputPort {

    @Setter
    private Connection connection;
    private final String name;
    @Getter
    private final Node owner;

    public DefaultInputPort(String name, Node owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void receive(Message message) {
    }

    @Override
    public Message receive() {
        if (connection != null) {
            return connection.poll();
        }
        return null;
    }
}
