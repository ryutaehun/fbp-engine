package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

public class AlertNode extends AbstractNode {
    public AlertNode(String id) {
        super(id);
        addInputPort("in");
    }

    @Override
    protected void onProcess(Message message) {
        String sensorId = message.get("sensorId");
        if(sensorId == null || message.get("temperature") == null){
            System.out.println("[경고] 알 수 없는 센서 데이터");
        }
        double temperature = message.get("temperature");

        System.out.printf("[경고] 센서 {%s} 온도 {%f}°C - 임계값 초과!%n", sensorId, temperature);
    }
}
