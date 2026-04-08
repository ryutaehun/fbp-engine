package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageTest {

    Map<String, Object> data = new HashMap<>();
    Message message;

    @BeforeEach
    void setup(){
        data.put("temperature", 38.8);
        message = new Message(data);
    }

    @DisplayName("생성 시 ID 자동 할당")
    @Test
    void one(){
        UUID id = message.getId();
        Assertions.assertAll(
                () -> {
                    Assertions.assertNotNull(id);
                    Assertions.assertFalse(id.toString().isBlank());
                }
        );
    }

    @DisplayName("생성 시 timestamp 자동 기록")
    @Test
    void two(){
        Assertions.assertTrue(message.getTimeStamp() > 0);
    }

    @DisplayName("페이로드 조회")
    @Test
    void three(){
        Object temperature = message.get("temperature");
        Assertions.assertEquals(38.8, temperature);
    }

    @DisplayName("제네릭 get 타입 캐스팅")
    @Test
    void four(){
        double temperature = message.get("temperature");
        Assertions.assertEquals(38.8, temperature);
    }

    @DisplayName("존재하지 않는 키 조회")
    @Test
    void five(){
        Object value = message.get("없는키");
        Assertions.assertNull(value);
    }

    @DisplayName("페이로드 불변 - 외부 수정 차단")
    @Test
    void six(){
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                message.getPayload().put("test", 1));
    }

    @DisplayName("페이로드 불변 - 원본 Map 수정 차단")
    @Test
    void seven(){
        data.put("temperature", 40.3);
        Assertions.assertEquals(38.8, message.get("temperature"));
    }

    @DisplayName("withEntry - 새 객체 반환")
    @Test
    void eight(){
        Message newMessage = message.withEntry("temperature", 38.8);
        Assertions.assertNotEquals(newMessage, message);
    }

    @DisplayName("withEntry - 원본 불변")
    @Test
    void nine(){
        message.withEntry("newKey", 100);
        Assertions.assertFalse(message.hasKey("newKey"));
    }

    @DisplayName("withEntry — 새 메시지에 값 존재")
    @Test
    void ten(){
        Message newMessage = message.withEntry("newKey", 40.3);
        Assertions.assertEquals(40.3, newMessage.get("newKey"));
    }

    @DisplayName("hasKey — 존재하는 키")
    @Test
    void eleven(){
        Assertions.assertTrue(message.hasKey("temperature"));
    }

    @DisplayName("hasKey — 없는 키")
    @Test
    void twelve(){
        Assertions.assertFalse(message.hasKey("없는 키"));
    }

    @DisplayName("withoutKey — 키 제거 확인")
    @Test
    void thirteen(){
        Message newMessage = message.withoutKey("temperature");
        Assertions.assertFalse(newMessage.hasKey("temperature"));
    }

    @DisplayName("withoutKey — 원본 불변")
    @Test
    void fourteen(){
        message.withoutKey("temperature");
        Assertions.assertTrue(message.hasKey("temperature"));
    }

    @DisplayName("toString 포맷")
    @Test
    void fifteen(){
        String string = message.toString();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(string),
                () -> Assertions.assertTrue(string.contains("38.8"))
        );
    }

}
