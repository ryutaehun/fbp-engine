package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class CounterNodeTest {

    CounterNode counterNode;
    Connection connection;

    @BeforeEach
    void setup() {
        counterNode = new CounterNode("카운트");
        connection = new Connection();
        counterNode.getOutputPort("out").connect(connection);
    }

    @DisplayName("count 키 추가")
    @Test
    void one() {
        Message message = new Message(new HashMap<>());
        counterNode.process(message);

        Assertions.assertEquals(1, counterNode.getCount());

        Message outMsg = connection.poll();
        Assertions.assertEquals(1, (int) outMsg.get("count"));
    }

    @DisplayName("count 누적")
    @Test
    void two() {
        Message m1 = new Message(new HashMap<>());
        Message m2 = new Message(new HashMap<>());

        counterNode.process(m1);
        counterNode.process(m2);

        Assertions.assertEquals(2, counterNode.getCount());

        connection.poll();
        Message secondOut = connection.poll();
        Assertions.assertEquals(2, (int) secondOut.get("count"));
    }

    @DisplayName("원본 키 유지")
    @Test
    void three() {
        Map<String, Object> data = new HashMap<>();
        data.put("origin", "value");
        Message message = new Message(data);

        counterNode.process(message);

        Message outMsg = connection.poll();
        Assertions.assertEquals("value", outMsg.get("origin"));
        Assertions.assertEquals(1, (int) outMsg.get("count"));
    }
}