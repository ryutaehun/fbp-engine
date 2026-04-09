package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class CollectorNode extends AbstractNode {
    @Getter
    private List<Message> collected = new ArrayList<>();

    public CollectorNode(String id) {
        super(id);
        addInputPort("in");
    }

    @Override
    protected void onProcess(Message message) {
        collected.add(message);
    }
}
