package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DelayNodeTest {

    DelayNode delayNode;
    Connection connection;

    @BeforeEach
    void setup(){
        delayNode = new DelayNode("delay", 500);
        connection = mock(Connection.class);
        delayNode.getOutputPort("out").connect(connection);
    }

    @DisplayName("지연 후 전달")
    @Test
    void one(){
        Message message = new Message(new HashMap<>());

        long startTime = System.currentTimeMillis();

        delayNode.process(message);

        long duration = System.currentTimeMillis() - startTime;

        assertTrue(duration >= 500);

        verify(connection, times(1)).deliver(any(Message.class));
    }

    @DisplayName("메시지 내용 보존")
    @Test
    void two(){
        Message message = new Message(Map.of("data", "imp"));
        delayNode.process(message);

        verify(connection).deliver(argThat(msg ->
                msg.get("data").equals("imp") && msg.getId().equals(message.getId())));
    }
}
