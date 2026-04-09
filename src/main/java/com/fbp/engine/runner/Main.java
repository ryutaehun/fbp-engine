package com.fbp.engine.runner;

import com.fbp.engine.core.Flow;
import com.fbp.engine.core.FlowEngine;
import com.fbp.engine.node.*;

public class Main {
    public static void main(String[] args) {
        FlowEngine flowEngine = new FlowEngine();
        Flow flow = new Flow("flow");
        flow.addNode(new TimerNode("timer", 1000))
                .addNode(new TemperatureSensorNode("temperature", 15, 45))
                .addNode(new ThresholdFilterNode("filter", "temperature", 30))
                .addNode(new AlertNode("alert"))
                .addNode(new LogNode("log"));

        flow.connect("timer", "out", "temperature", "trigger")
                .connect("temperature", "out", "filter", "in")
                .connect("filter", "alert", "alert", "in")
                .connect("filter", "normal", "log", "in");

        flowEngine.register(flow);
        flowEngine.startFlow("flow");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        flowEngine.stopFlow("flow");
    }
}