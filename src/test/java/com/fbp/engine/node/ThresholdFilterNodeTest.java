package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ThresholdFilterNodeTest {
    ThresholdFilterNode thresholdFilterNode;
    Connection connection1;
    Connection connection2;

    @BeforeEach
    void setup(){
        thresholdFilterNode = new ThresholdFilterNode("filter", "test", 30);
        connection1 = new Connection();
        connection2 = new Connection();
        thresholdFilterNode.getOutputPort("alert").connect(connection1);
        thresholdFilterNode.getOutputPort("normal").connect(connection2);
    }

    @DisplayName("초과 -> alert 포트")
    @Test
    void one(){
        Map<String, Object> data = new HashMap<>();
        data.put("test", 45);
        thresholdFilterNode.onProcess(new Message(data));

        Assertions.assertEquals(1, connection1.getBufferSize());
        Assertions.assertEquals(0, connection2.getBufferSize());
    }

    @DisplayName("이하 -> normal 포트")
    @Test
    void two(){
        Map<String, Object> data = new HashMap<>();
        data.put("test", 28);
        thresholdFilterNode.onProcess(new Message(data));

        Assertions.assertEquals(0, connection1.getBufferSize());
        Assertions.assertEquals(1, connection2.getBufferSize());
    }

    @DisplayName("경계값 (정확히 같은 값)")
    @Test
    void three(){
        Map<String, Object> data = new HashMap<>();
        data.put("test", 30);
        thresholdFilterNode.onProcess(new Message(data));

        Assertions.assertEquals(0, connection1.getBufferSize());
        Assertions.assertEquals(1, connection2.getBufferSize());
    }

    @DisplayName("키 없는 메시지")
    @Test
    void four(){
        Map<String, Object> data = new HashMap<>();
        data.put("no", 30);

        Assertions.assertDoesNotThrow(() -> thresholdFilterNode.onProcess(new Message(data)));
    }

    @DisplayName("양쪽 동시 검증")
    @Test
    void five(){
         //TODO #1 Collector Node 만들어서 구현
    }
}
