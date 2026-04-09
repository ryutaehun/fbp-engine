package com.fbp.engine;

import com.fbp.engine.core.Flow;
import com.fbp.engine.core.FlowEngine;
import com.fbp.engine.message.Message;
import com.fbp.engine.node.CollectorNode;
import com.fbp.engine.node.TemperatureSensorNode;
import com.fbp.engine.node.ThresholdFilterNode;
import com.fbp.engine.node.TimerNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemperatureMonitoringTest {
    @Test
    @DisplayName("온도 모니터링 통합 플로우 테스트")
    void testMonitoringFlow() throws InterruptedException {
        FlowEngine engine = new FlowEngine();
        Flow flow = new Flow("integration-test");

        TimerNode timer = new TimerNode("timer", 100);
        TemperatureSensorNode sensor = new TemperatureSensorNode("sensor", 10, 50);
        ThresholdFilterNode filter = new ThresholdFilterNode("filter", "temperature", 30);

        CollectorNode alertCollector = new CollectorNode("alert-collector");
        CollectorNode normalCollector = new CollectorNode("normal-collector");

        flow.addNode(timer).addNode(sensor).addNode(filter)
                .addNode(alertCollector).addNode(normalCollector);

        flow.connect("timer", "out", "sensor", "trigger")
                .connect("sensor", "out", "filter", "in")
                .connect("filter", "alert", "alert-collector", "in")
                .connect("filter", "normal", "normal-collector", "in");

        engine.register(flow);
        engine.startFlow("integration-test");

        Thread.sleep(2000);
        engine.stopFlow("integration-test");

        List<Message> alerts = alertCollector.getCollected();
        List<Message> normals = normalCollector.getCollected();

        for (Message m : alerts) {
            double temp = m.get("temperature");
            assertTrue(temp > 30.0, "Alert 메시지는 30도보다 커야 함: " + temp);
        }

        for (Message m : normals) {
            double temp = m.get("temperature");
            assertTrue(temp <= 30.0, "Normal 메시지는 30도 이하여야 함: " + temp);
        }

        int total = alerts.size() + normals.size();
        assertTrue(total > 10, "2초 동안 최소 10개 이상의 메시지가 처리되어야 함");
    }
}
