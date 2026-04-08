package com.fbp.engine.node;

import com.fbp.engine.core.Node;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class PrintNodeTest {
    PrintNode printNode = new PrintNode("print-1");

    @DisplayName("getId 반환")
    @Test
    void one(){
        Assertions.assertEquals("print-1", printNode.getId());
    }

    @DisplayName("process 정상 동작")
    @Test
    void two(){
        Map<String, Object> data = new HashMap<>();
        Message message =  new Message(data);
        Assertions.assertDoesNotThrow(() -> printNode.process(message));
    }

    @DisplayName("Node 인터페이스 구현")
    @Test
    void three(){
        Node myNode = printNode;

        Assertions.assertInstanceOf(Node.class, myNode);
    }
}
