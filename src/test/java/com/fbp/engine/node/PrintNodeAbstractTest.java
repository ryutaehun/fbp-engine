package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class PrintNodeAbstractTest {

    PrintNode printNode;

    @BeforeEach
    void setup(){
        printNode = new PrintNode("print-node");
    }

    @DisplayName("포트 구성 확인")
    @Test
    void one(){
        Assertions.assertNotNull(printNode.getInputPort("in"));
    }

    @DisplayName("process 정상 동작")
    @Test
    void two(){
        Assertions.assertDoesNotThrow(() -> printNode.process(new Message(new HashMap<>())));
    }

    @DisplayName("AbstractNode 상속 확인")
    @Test
    void three(){
        Assertions.assertInstanceOf(AbstractNode.class, printNode);
    }
}
