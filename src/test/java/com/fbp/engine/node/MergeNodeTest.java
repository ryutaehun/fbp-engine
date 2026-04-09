package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.core.DefaultInputPort;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MergeNodeTest {
    MergeNode node;
    Connection outConn;

    @BeforeEach
    void setUp() {
        node = new MergeNode("merge-node");
        outConn = new Connection();
        node.getOutputPort("out").connect(outConn);
    }

    @Test
    @DisplayName("포트 구성 확인")
    void one() {
        assertNotNull(node.getInputPort("in-1"));
        assertNotNull(node.getInputPort("in-2"));
        assertNotNull(node.getOutputPort("out"));
    }

    @Test
    @DisplayName("한쪽만 도착 시 대기 확인")
    void two() {
        Connection inConn = new Connection();
        ((DefaultInputPort)node.getInputPort("in-1")).setConnection(inConn);
        Thread thread = new Thread(node);
        thread.start();

        inConn.deliver(new Message(Map.of("port", "1", "temp", 25.5)));

        assertEquals(0, outConn.getBufferSize());
    }

    @Test
    @DisplayName("양쪽 입력 수신 및 메시지 합치기 확인")
    void testMergingMessages() throws InterruptedException {
        Connection inConn1 = new Connection();
        Connection inConn2 = new Connection();
        ((DefaultInputPort)node.getInputPort("in-1")).setConnection(inConn1);
        ((DefaultInputPort)node.getInputPort("in-2")).setConnection(inConn2);

        Thread thread = new Thread(node);
        thread.start();

        inConn1.deliver(new Message(Map.of("temp", 25.5)));
        inConn2.deliver(new Message(Map.of("humi", 60.0)));

        Message result = outConn.poll();

        assertNotNull(result);
        assertEquals(25.5, result.get("temp"));
        assertEquals(60.0, result.get("humi"));

        thread.interrupt();
    }
}