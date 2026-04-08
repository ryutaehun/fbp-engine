package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class TransformNodeTest {

    TransformNode transformNode;

    @DisplayName("변환 정상 동작")
    @Test
    void one(){
        transformNode = new TransformNode("더하기 10",msg -> msg.withEntry("test", (int)msg.get("test") + 10));
        Connection connection = mock(Connection.class);

        transformNode.getOutputPort("out").connect(connection);

        transformNode.process(new Message(Map.of("test", 30)));

        verify(connection, times(1)).deliver(any(Message.class));
    }

    @DisplayName("null 반환 시 미전달")
    @Test
    void two(){
        transformNode = new TransformNode("null 체크", msg -> null);
        Connection connection = mock(Connection.class);
        transformNode.getOutputPort("out").connect(connection);
        transformNode.process(new Message(Map.of("test", "data")));

        verify(connection, times(0)).deliver(any(Message.class));
    }

    @DisplayName("원본 메시지 불변")
    @Test
    void three(){
        transformNode = new TransformNode("f2c", msg -> {
            Double fahrenheit = msg.get("temperature");
            double celsius = (fahrenheit - 32) * 5.0 / 9.0;
            return msg.withEntry("temperature", celsius);
        });

        Map<String, Object> payload = new HashMap<>();
        payload.put("temperature", 100.0);
        Message origin = new Message(payload);

        transformNode.process(origin);

        Assertions.assertEquals(100.0, origin.get("temperature"));
    }
}
