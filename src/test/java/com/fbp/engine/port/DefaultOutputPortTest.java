package com.fbp.engine.port;

import com.fbp.engine.core.Connection;
import com.fbp.engine.core.DefaultOutputPort;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultOutputPortTest {

    private DefaultOutputPort outputPort;
    private Message message;

    @BeforeEach
    void setup() {
        outputPort = new DefaultOutputPort("out");
        message = new Message(Map.of("data", "test"));
    }

    @DisplayName("단일 Connection 전달")
    @Test
    void one() {
        Connection conn = new Connection();
        outputPort.connect(conn);

        outputPort.send(message);

        assertEquals(1, conn.getBufferSize());
        assertEquals(message, conn.poll());
    }

    @DisplayName("다중 Connection 전달 (1:N)")
    @Test
    void two() {
        Connection conn1 = new Connection();
        Connection conn2 = new Connection();

        outputPort.connect(conn1);
        outputPort.connect(conn2);

        outputPort.send(message);

        assertEquals(1, conn1.getBufferSize());
        assertEquals(1, conn2.getBufferSize());
    }

    @DisplayName("Connection 미연결 시 예외 없음")
    @Test
    void three() {
        assertDoesNotThrow(() -> outputPort.send(message));
    }
}