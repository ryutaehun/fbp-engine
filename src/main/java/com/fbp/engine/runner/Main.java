package com.fbp.engine.runner;

import com.fbp.engine.core.Flow;
import com.fbp.engine.node.*;

public class Main {
    public static void main(String[] args) {
        Flow flow = new Flow();

        flow.addNode(new TimerNode("타이머", 1000))
                .addNode(new CounterNode("카운터"))
                .addNode(new FilterNode("필터", "tick", 3))
                .addNode(new PrintNode("프린트"));

        flow.connect("타이머", "out", "카운터", "in")
                .connect("카운터", "out", "필터", "in")
                .connect("필터", "out", "프린트", "in");

        flow.initialize();

        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        flow.shutdown();
    }
}