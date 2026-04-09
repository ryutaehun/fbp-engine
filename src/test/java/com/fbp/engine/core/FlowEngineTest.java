package com.fbp.engine.core;

import com.fbp.engine.node.PrintNode;
import com.sun.source.tree.AssertTree;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class FlowEngineTest {
    FlowEngine flowEngine;
    Flow flow;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @BeforeEach
    void setup(){
        flowEngine = new FlowEngine();
        flow = new Flow("flow");
        AbstractNode node = new PrintNode("print-node");
        flow.addNode(node);
        flowEngine.register(flow);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @DisplayName("초기 상태")
    @Test
    void one(){
        Assertions.assertEquals(FlowEngine.State.INITIALIZED, flowEngine.getState());
    }

    @DisplayName("플로우 등록")
    @Test
    void two(){
        Assertions.assertTrue(flowEngine.getFlows().containsKey("flow"));
    }

    @DisplayName("startFlow 정상")
    @Test
    void three(){
        flowEngine.startFlow("flow");
        Assertions.assertEquals(Flow.State.RUNNING, flow.getState());
    }

    @DisplayName("startFlow - 없는 ID")
    @Test
    void four(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> flowEngine.startFlow("hi"));
    }

    @DisplayName("startFlow - 유효성 실패")
    @Test
    void five(){
        Flow flow1 = new Flow("flow1");
        flowEngine.register(flow1);
        Assertions.assertThrows(IllegalStateException.class, () -> flowEngine.startFlow("flow1"));
    }

    @DisplayName("stopFlow 정상")
    @Test
    void six(){
        flowEngine.startFlow("flow");
        flowEngine.stopFlow("flow");

        Assertions.assertEquals(Flow.State.STOPPED, flow.getState());
    }

    @DisplayName("shutdown 전체")
    @Test
    void seven(){
        flowEngine.shutdown();

        Assertions.assertEquals(FlowEngine.State.STOPPED, flowEngine.getState());
    }

    @DisplayName("다중 플로우 독립 동작")
    @Test
    void eight(){
        Flow flow1 = new Flow("flow1");
        AbstractNode node = new PrintNode("node1");
        flow1.addNode(node);
        flowEngine.register(flow1);

        flowEngine.startFlow("flow");
        flowEngine.startFlow("flow1");

        flowEngine.stopFlow("flow");

        Assertions.assertEquals(Flow.State.RUNNING, flow1.getState());
    }

    @DisplayName("listFlows 출력")
    @Test
    void nine(){
        Flow flow1 = new Flow("flow1");
        AbstractNode node = new PrintNode("node1");
        flow1.addNode(node);
        flowEngine.register(flow1);

        flowEngine.listFlows();

        String output = outputStreamCaptor.toString().trim();

        Assertions.assertTrue(output.contains("flow1"));
        Assertions.assertTrue(output.contains("INITIALIZED"));
    }

}
