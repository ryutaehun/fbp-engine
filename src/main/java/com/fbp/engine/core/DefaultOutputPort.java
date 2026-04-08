package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DefaultOutputPort implements OutputPort {

    @Getter
    private final List<Connection> connections = new ArrayList<>();
    private final String name;

    public DefaultOutputPort(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void connect(Connection con) {
        connections.add(con);
    }

    @Override
    public void send(Message message) {
        for(Connection c : connections){
            c.deliver(message);
        }
    }
}