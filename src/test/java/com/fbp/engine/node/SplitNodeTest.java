package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class SplitNodeTest {

    SplitNode splitNode;
    Connection connection1;
    Connection connection2;

    @BeforeEach
    void setup(){
        splitNode = new SplitNode("split", "count", 3);
        connection1 = mock(Connection.class);
        connection2 = mock(Connection.class);
        splitNode.getOutputPort("match").connect(connection1);
        splitNode.getOutputPort("mismatch").connect(connection2);
    }

    @DisplayName("조건 만족 -> match 포트")
    @Test
    void one(){
        Map<String, Object> payload = new HashMap<>();
        payload.put("count", 4);
        Message message = new Message(payload);

        splitNode.process(message);

        verify(connection1, times(1)).deliver(any(Message.class));
    }

    @DisplayName("조건 미달 -> mismatch 포트")
    @Test
    void two(){
        Map<String, Object> payload = new HashMap<>();
        payload.put("count", 2);
        Message message = new Message(payload);

        splitNode.process(message);

        verify(connection2, times(1)).deliver(any(Message.class));
    }

    @DisplayName("양쪽 동시 확인")
    @Test
    void three(){
        Map<String, Object> payload = new HashMap<>();
        payload.put("count", 4);
        Message message = new Message(payload);

        Map<String, Object> payload2 = new HashMap<>();
        payload.put("count", 2);
        Message message2 = new Message(payload);

        splitNode.process(message);
        splitNode.process(message2);

        verify(connection1, times(1)).deliver(any(Message.class));
        verify(connection2, times(1)).deliver(any(Message.class));
    }

    @DisplayName("경계값 처리")
    @Test
    void four(){
        Map<String, Object> payload = new HashMap<>();
        payload.put("count", 3);
        Message message = new Message(payload);
        splitNode.process(message);
        verify(connection1, times(1)).deliver(any(Message.class));
    }
}
