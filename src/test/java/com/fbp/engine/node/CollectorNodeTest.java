package com.fbp.engine.node;

import com.fbp.engine.core.Flow;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CollectorNodeTest {
    CollectorNode collector;

    @BeforeEach
    void setUp() {
        collector = new CollectorNode("collector");
    }

    @Test
    @DisplayName("초기 상태 및 포트 확인")
    void one() {
        assertTrue(collector.getCollected().isEmpty());
        assertNotNull(collector.getInputPort("in"));
    }

    @Test
    @DisplayName("메시지 수집 및 순서 보존 확인")
    void two() {
        Message msg1 = new Message(Map.of("id", 1));
        Message msg2 = new Message(Map.of("id", 2));

        collector.onProcess(msg1);
        collector.onProcess(msg2);

        List<Message> results = collector.getCollected();
        assertEquals(2, results.size());
        assertEquals(1, (Integer) results.get(0).get("id"));
        assertEquals(2, (Integer) results.get(1).get("id"));
    }

    @Test
    @DisplayName("파이프라인 연결 검증")
    void three() throws InterruptedException {
        Flow flow = new Flow("test-flow");

        GeneratorNode generator = new GeneratorNode("gen", 5);
        flow.addNode(generator).addNode(collector);
        flow.connect("gen", "out", "collector", "in");

        flow.initialize();

        Thread.sleep(500);
        flow.shutdown();

        assertEquals(5, collector.getCollected().size());
    }
}