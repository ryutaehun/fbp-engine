package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FilterNodeTest {

    private FilterNode filterNode;
    private Connection outConn;

    @BeforeEach
    void setup() {
        filterNode = new FilterNode("filter", "temp", 30.0);
        outConn = new Connection();

        filterNode.getOutputPort("out").connect(outConn);
    }

    @DisplayName("조건 만족 시 통과")
    @Test
    void one() {
        filterNode.process(new Message(Map.of("temp", 35.0)));

        assertEquals(1, outConn.getBufferSize());
    }

    @DisplayName("조건 미달 시 차단")
    @Test
    void two() {
        filterNode.process(new Message(Map.of("temp", 25.0)));

        assertEquals(0, outConn.getBufferSize());
    }

    @DisplayName("경계값 처리")
    @Test
    void three() {
        filterNode.process(new Message(Map.of("temp", 30.0)));

        assertEquals(1, outConn.getBufferSize());
    }

    @DisplayName("키 없는 메시지 예외 없이 처리")
    @Test
    void four() {
        assertDoesNotThrow(() -> filterNode.process(new Message(Map.of("humidity", 50.0))));
    }
}