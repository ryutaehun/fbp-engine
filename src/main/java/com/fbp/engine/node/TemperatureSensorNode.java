package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TemperatureSensorNode extends AbstractNode {
    private final double min;
    private final double max;

    public TemperatureSensorNode(String id, double min, double max) {
        super(id);
        addInputPort("trigger");
        addOutputPort("out");
        this.min = min;
        this.max = max;
    }

    @Override
    protected void onProcess(Message message) {
        double temperature = Math.round((min + Math.random() * (max - min)) * 10) / 10.0;
        Map<String, Object> payload = new HashMap<>();
        payload.put("sensorId", getId());
        payload.put("temperature", temperature);
        payload.put("unit", "°C");
        payload.put("timestamp", System.currentTimeMillis());
        Message data = new Message(payload);

        send("out", data);
    }
}
