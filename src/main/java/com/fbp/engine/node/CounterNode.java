package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;
import lombok.Getter;

public class CounterNode extends AbstractNode {

    @Getter
    private int count = 0;

    public CounterNode(String id) {
        super(id);
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    protected void onProcess(Message message) {
        count++;
        Message newMessage = message.withEntry("count", count);
        send("out", newMessage);
    }

    @Override
    public void shutdown(){
        System.out.printf("[%s] 총 처리 메시지 : %d건%n", getId(), count);
    }
}
