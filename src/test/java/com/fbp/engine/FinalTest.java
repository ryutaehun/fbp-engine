package com.fbp.engine;

import com.fbp.engine.core.Flow;
import com.fbp.engine.core.FlowEngine;
import com.fbp.engine.message.Message;
import com.fbp.engine.node.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FinalTest {

    @TempDir
    Path tempDir;

    @Test
    void zero() throws IOException {
        FlowEngine flowEngine = new FlowEngine();
        Flow flow = new Flow("flow");
        Path logFile = tempDir.resolve("test_output.txt");

        CollectorNode alertCollector = new CollectorNode("alert-collector");
        CollectorNode normalCollector = new CollectorNode("normal-collector");

        flow.addNode(new TimerNode("타이머", 1000))
                .addNode(new TemperatureSensorNode("온도 센서", 15, 45))
                .addNode(new ThresholdFilterNode("필터", "temperature", 30))
                .addNode(new AlertNode("경고"))
                .addNode(new LogNode("로그"))
                .addNode(new FileWriterNode("파일 작성", logFile.toString()))
                .addNode(alertCollector)
                .addNode(normalCollector);

        flow.connect("타이머", "out", "온도 센서", "trigger")
                .connect("온도 센서", "out", "필터", "in")
                .connect("필터", "alert", "경고", "in")
                .connect("필터", "normal", "로그", "in")
                .connect("로그", "out", "파일 작성", "in")
                .connect("필터", "alert", "alert-collector", "in")
                .connect("로그", "out", "normal-collector", "in");

        flowEngine.register(flow);

        flowEngine.startFlow("flow");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        flowEngine.stopFlow("flow");

        assertEquals(Flow.State.STOPPED, flow.getState());

        List<Message> alertMsgs = alertCollector.getCollected();
        List<Message> normalMsgs = normalCollector.getCollected();

        for (Message m : alertMsgs) {
            double temp = m.get("temperature");
            assertTrue(temp > 30.0, "Alert 데이터는 30도 초과여야 함: " + temp);
            assertAll("메시지 형식 검증",
                    () -> assertNotNull(m.get("sensorId")),
                    () -> assertNotNull(m.get("unit")),
                    () -> assertTrue(temp >= 15.0 && temp <= 45.0)
            );
        }

        for (Message m : normalMsgs) {
            double temp = m.get("temperature");
            assertTrue(temp <= 30.0, "Normal 데이터는 30도 이하여야 함: " + temp);
        }

        if (Files.exists(logFile)) {
            long lineCount = Files.lines(logFile).count();
            assertEquals(normalMsgs.size(), (int) lineCount, "파일 줄 수와 정상 메시지 수가 일치해야 함");
        }
    }
}
