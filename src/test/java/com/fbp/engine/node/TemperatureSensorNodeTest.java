package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TemperatureSensorNodeTest {
    TemperatureSensorNode node;
    Connection con;

    @BeforeEach
    void setup(){
        node = new TemperatureSensorNode("sensor-node", 10, 30);
        con = new Connection();
        node.getOutputPort("out").connect(con);
    }

    @DisplayName("온도 범위 확인")
    @Test
    void one(){
        node.onProcess(new Message(new HashMap<>()));
        Message message = con.poll();
        double temperature = message.get("temperature");
        Assertions.assertTrue(temperature >= 10 && temperature <= 30);
    }

    @DisplayName("필수 키 포함")
    @Test
    void two(){
        node.onProcess(new Message(new HashMap<>()));
        Message message = con.poll();

        assertEquals("sensor-node", message.get("sensorId"));
        assertEquals("°C", message.get("unit"));
        assertNotNull(message.get("timestamp"));
    }

    @DisplayName("트리거마다 생성")
    @Test
    void three(){
        node.onProcess(new Message(Map.of()));
        node.onProcess(new Message(Map.of()));
        node.onProcess(new Message(Map.of()));

        assertEquals(3, con.getBufferSize());
    }
}
