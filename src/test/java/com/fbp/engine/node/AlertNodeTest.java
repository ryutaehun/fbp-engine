package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class AlertNodeTest {

    AlertNode alertNode;
    Connection connection;

    @BeforeEach
    void setup() {
        alertNode = new AlertNode("alert");
        connection = new Connection();
        connection.setTarget(alertNode.getInputPort("in"));
    }

    @DisplayName("정상 처리")
    @Test
    void one() {
        Map<String, Object> data = new HashMap<>();
        data.put("sensorId", "id");
        data.put("temperature", 38.3);
        Assertions.assertDoesNotThrow(() -> connection.deliver(new Message(data)));
    }

    @DisplayName("키 누락 시 처리")
    @Test
    void two(){
        Map<String, Object> data = new HashMap<>();
        data.put("sensorId", "id");
        Assertions.assertDoesNotThrow(() -> connection.deliver(new Message(data)));
    }
}
