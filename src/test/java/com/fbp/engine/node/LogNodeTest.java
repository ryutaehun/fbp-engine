package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.core.Connection;
import com.fbp.engine.core.DefaultInputPort;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class LogNodeTest {

    LogNode logNode;
    Connection con;

    @BeforeEach
    void setup(){
        logNode = new LogNode("log-node");
        con = new Connection();
    }

    @DisplayName("메시지 통과 전달")
    @Test
    void one(){
        Map<String, Object> data = new HashMap<>();
        data.put("test", 33);
        Message message = new Message(data);
        logNode.getOutputPort("out").connect(con);
        logNode.process(message);

        Assertions.assertEquals(message, con.poll());
    }

    class SpyNode extends AbstractNode {
        private Message lastReceivedMessage;

        public SpyNode(String id) {
            super(id);
            addInputPort("in"); // 입구 하나 뚫어주기
        }

        @Override
        protected void onProcess(Message message) {
            // 메시지를 받으면 보관함에 저장!
            this.lastReceivedMessage = message;
        }

        public Message getLastReceivedMessage() {
            return lastReceivedMessage;
        }
    }

    @DisplayName("중간 삽입 가능")
    @Test
    void two() throws InterruptedException {
        LogNode logNode = new LogNode("log-node");
        SpyNode spyNode = new SpyNode("spy-node");
        Connection conn = new Connection(10);

        logNode.getOutputPort("out").connect(conn);
        ((DefaultInputPort)spyNode.getInputPort("in")).setConnection(conn);

        Message sendMsg = new Message(Map.of("info", "test-data"));
        logNode.process(sendMsg);

        Message receivedMsg = spyNode.getInputPort("in").receive();

        Assertions.assertNotNull(receivedMsg);
        Assertions.assertEquals(sendMsg.getId(), receivedMsg.getId());
        Assertions.assertEquals("test-data", receivedMsg.get("info"));
    }
}
