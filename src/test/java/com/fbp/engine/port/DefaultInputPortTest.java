package com.fbp.engine.port;

import com.fbp.engine.core.Connection;
import com.fbp.engine.core.DefaultInputPort;
import com.fbp.engine.core.Node;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class DefaultInputPortTest {

    @DisplayName("포트 이름 확인 및 메시지 수신/보관 테스트")
    @Test
    void one() throws InterruptedException {
        DefaultInputPort inputPort = new DefaultInputPort("test-in", mock(Node.class));
        Connection connection = new Connection(10);

        inputPort.setConnection(connection);

        assertEquals("test-in", inputPort.getName());

        Message msg = new Message(Map.of("k", "v"));
        connection.deliver(msg);

        Message received = inputPort.receive();

        assertNotNull(received);
        assertEquals(msg.getId(), received.getId());
    }
}