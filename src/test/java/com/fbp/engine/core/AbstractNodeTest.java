package com.fbp.engine.core;

import static org.junit.jupiter.api.Assertions.*;

import com.fbp.engine.message.Message;
import com.fbp.engine.node.LogNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class AbstractNodeTest {

    private AbstractNode node;

    @BeforeEach
    void setup(){
        node = new LogNode("log-node");
    }

    @DisplayName("getId 반환")
    @Test
    void one(){
        assertEquals("log-node", node.getId());
    }

    @DisplayName("addInputPort & addOutputPort 등록 & 미등록 포트 조회")
    @Test
    void two(){
        assertNotNull(node.getInputPort("in"));
        assertNotNull(node.getOutputPort("out"));
        assertNull(node.getInputPort("nop"));
    }

    @DisplayName("process() 호출 시 하위 클래스의 onProcess()가 실행됨")
    @Test
    void three(){
        TestNode test = new TestNode("test-node");
        Message message = new Message(new HashMap<>());
        test.process(message);

        assertTrue(test.isOnProcessCalled);
    }

    // three four 테스트용
    static class TestNode extends AbstractNode {
        boolean isOnProcessCalled = false;
        Message receivedMessage;

        public TestNode(String id) {
            super(id);
            addInputPort("in");
        }

        @Override
        protected void onProcess(Message message) {
            this.isOnProcessCalled = true;
            this.receivedMessage = message;
        }
    }

    @DisplayName("send로 메시지 전달")
    @Test
    void four(){
        DefaultOutputPort outputPort = new DefaultOutputPort("out");
        Connection con = new Connection(10);
        outputPort.connect(con);

        Message msg = new Message(Map.of("test", "value"));
        outputPort.send(msg);

        Message polledMsg = con.poll();

        assertNotNull(polledMsg);
        assertEquals(msg.getId(), polledMsg.getId());
        assertEquals("value", polledMsg.get("test"));
    }
}
