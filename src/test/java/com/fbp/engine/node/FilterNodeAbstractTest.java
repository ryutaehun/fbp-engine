package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class FilterNodeAbstractTest {

    FilterNode filterNode;

    @BeforeEach
    void setup(){
        filterNode = new FilterNode("filter-node", "test", 30);
    }

    @DisplayName("조건 만족 -> send 호출")
    @Test
    void one(){
        Map<String, Object> data = new HashMap<>();
        data.put("test", 35);
        Message message = new Message(data);
        Connection con = new Connection();

        filterNode.getOutputPort("out").connect(con);
        filterNode.process(message);

        Assertions.assertEquals(1, con.getBufferSize());
    }

    @DisplayName("조건 미달 -> 차단")
    @Test
    void two(){
        Map<String, Object> data = new HashMap<>();
        data.put("test", 29);
        Message message = new Message(data);
        Connection con = new Connection();

        filterNode.getOutputPort("out").connect(con);
        filterNode.process(message);

        Assertions.assertEquals(0, con.getBufferSize());
    }

    @DisplayName("포트 구성 확인")
    @Test
    void three(){
        Assertions.assertNotNull(filterNode.getInputPort("in"));
        Assertions.assertNotNull(filterNode.getOutputPort("out"));
    }
}
