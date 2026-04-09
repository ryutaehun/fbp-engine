package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HumiditySensorNodeTest {
    HumiditySensorNode humiditySensorNode;
    Connection connection;

    @BeforeEach
    void setup(){
        humiditySensorNode = new HumiditySensorNode("humidity", 30, 90);
        connection = new Connection();
        humiditySensorNode.getOutputPort("out").connect(connection);
    }

    @DisplayName("습도 범위 확인")
    @Test
    void one(){
        Message trigger = new Message(Map.of("command", "start"));
        humiditySensorNode.onProcess(trigger);

        Message result = connection.poll();
        assertNotNull(result);

        double humidity = result.get("humidity");
        assertTrue(humidity >= 30.0 && humidity <= 90.0);
    }

    @DisplayName("필수 키 포함")
    @Test
    void two(){
        Message trigger = new Message(Map.of("command", "start"));
        humiditySensorNode.onProcess(trigger);

        Message result = connection.poll();
        assertNotNull(result);
        assertEquals("humidity", result.get("sensorId"));
        assertEquals("%", result.get("unit"));
        assertNotNull(result.get("timestamp"));
    }


    @DisplayName("트리거마다 생성")
    @Test
    void three(){
        humiditySensorNode.onProcess(new Message(Map.of()));
        humiditySensorNode.onProcess(new Message(Map.of()));
        humiditySensorNode.onProcess(new Message(Map.of()));

        assertEquals(3, connection.getBufferSize());
    }
}
