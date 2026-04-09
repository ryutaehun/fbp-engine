package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

import java.util.HashMap;
import java.util.Map;

public class HumiditySensorNode extends AbstractNode {
    private final double min;
    private final double max;

    public HumiditySensorNode(String id, double min, double max) {
        super(id);
        addOutputPort("out");
        addInputPort("trigger");
        this.min = min;
        this.max = max;
    }

    @Override
    protected void onProcess(Message message) {
        double humidity = Math.round((min + Math.random() * (max - min)) * 10) / 10.0;
        Map<String, Object> payload = new HashMap<>();
        payload.put("sensorId", getId());
        payload.put("humidity", humidity);
        payload.put("unit", "%");
        payload.put("timestamp", System.currentTimeMillis());
        Message msg = new Message(payload);

        send("out", msg);
    }
}
