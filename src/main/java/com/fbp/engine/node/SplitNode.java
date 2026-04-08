package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

public class SplitNode extends AbstractNode {

    private final String key;
    private final double threshold;

    public SplitNode(String id, String key, double threshold){
        super(id);
        addInputPort("in");
        addOutputPort("match");
        addOutputPort("mismatch");
        this.key = key;
        this.threshold = threshold;
    }

    @Override
    protected void onProcess(Message message) {
        Object val = message.get(key);
        if (val instanceof Number) {
            double tick = ((Number) val).doubleValue();

            if (tick >= threshold) {
                send("match", message);
            } else {
                send("mismatch", message);
            }
        }
    }
}
