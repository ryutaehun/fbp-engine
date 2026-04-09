package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

public class ThresholdFilterNode extends AbstractNode {
    private final String fieldName;
    private final double threshold;

    public ThresholdFilterNode(String id, String fieldName, double threshold) {
        super(id);
        addInputPort("in");
        addOutputPort("alert");
        addOutputPort("normal");
        this.fieldName = fieldName;
        this.threshold = threshold;
    }

    @Override
    protected void onProcess(Message message) {
        Object val = message.get(fieldName);
        if (val instanceof Number) {
            double value = ((Number) val).doubleValue();

            if (value > threshold) {
                send("alert", message);
            } else {
                send("normal", message);
            }
        }
    }
}
