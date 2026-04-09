package com.fbp.engine.core;

import com.fbp.engine.node.CounterNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.node.TimerNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlowTest {
    Flow flow;

    @BeforeEach
    void setUp() {
        flow = new Flow("flow");
    }

    @Test
    @DisplayName("노드 등록")
    void one() {
        AbstractNode node = new PrintNode("printer");
        flow.addNode(node);
        assertTrue(flow.getNodes().containsKey("printer"));
    }

    @Test
    @DisplayName("메서드 체이닝")
    void two() {
        assertDoesNotThrow(() -> {
            flow.addNode(new TimerNode("timer", 1000))
                    .addNode(new PrintNode("printer"))
                    .connect("timer", "out", "printer", "in");
        });
    }

    @Test
    @DisplayName("정상 연결")
    void three() {
        flow.addNode(new TimerNode("t", 1000)).addNode(new PrintNode("p"));
        flow.connect("t", "out", "p", "in");
        assertEquals(1, flow.getConnections().size());
    }

    @Test
    @DisplayName("존재하지 않는 노드 ID")
    void fourFive() {
        flow.addNode(new TimerNode("timer", 1000));
        assertThrows(IllegalArgumentException.class, () -> flow.connect("wrong", "out", "timer", "in"));
        assertThrows(IllegalArgumentException.class, () -> flow.connect("timer", "out", "wrong", "in"));
    }

    @Test
    @DisplayName("존재하지 않는 포트")
    void sixSeven() {
        flow.addNode(new TimerNode("timer", 1000)).addNode(new PrintNode("printer"));
        assertThrows(IllegalArgumentException.class, () -> flow.connect("timer", "wrong_port", "printer", "in"));
        assertThrows(IllegalArgumentException.class, () -> flow.connect("timer", "out", "printer", "wrong_port"));
    }

    @Test
    @DisplayName("validate - 빈 Flow")
    void eight() {
        List<String> errors = flow.validate();
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("0개"));
    }

    @Test
    @DisplayName("validate - 정상 Flow")
    void nine() {
        flow.addNode(new TimerNode("t", 1000)).addNode(new PrintNode("p"));
        flow.connect("t", "out", "p", "in");

        List<String> errors = flow.validate();
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("initialize 및 shutdown 호출 확인")
    void tenEleven() {
        flow.addNode(new TimerNode("t", 1000));
        assertDoesNotThrow(() -> {
            flow.initialize();
            flow.shutdown();
        });
    }

    @Test
    @DisplayName("순환 참조 탐지")
    void twelve() {
        flow.addNode(new CounterNode("A"))
                .addNode(new CounterNode("B"));

        flow.connect("A", "out", "B", "in")
                .connect("B", "out", "A", "in");

        List<String> errors = flow.validate();
        assertFalse(errors.isEmpty(), "순환 참조가 있는데 에러가 발견되지 않았습니다.");
        assertTrue(errors.stream().anyMatch(e -> e.contains("순환") || e.contains("cycle")));
    }
}