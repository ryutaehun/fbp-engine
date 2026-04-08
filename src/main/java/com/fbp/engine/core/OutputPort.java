package com.fbp.engine.core;

import com.fbp.engine.message.Message;

public interface OutputPort {
    String getName();
    void connect(Connection con);
    void send(Message message);
}
