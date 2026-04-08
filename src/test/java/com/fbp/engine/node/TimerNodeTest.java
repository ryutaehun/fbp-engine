package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TimerNodeTest {

    Connection con;
    TimerNode timerNode;

    @BeforeEach
    void setup(){
        timerNode = new TimerNode("timer-node", 500);
        con = new Connection(10);
        timerNode.getOutputPort("out").connect(con);
    }

    @DisplayName("initialize 후 메시지 생성 확인")
    @Test
    void one() throws InterruptedException {
        timerNode.initialize();

        Message msg = con.poll();
        Assertions.assertNotNull(msg);
        Assertions.assertEquals(0, (Integer) msg.get("tick"));

        timerNode.shutdown();
    }

    @DisplayName("tick 값이 순차적으로 증가하는지 확인")
    @Test
    void two() throws InterruptedException {
        timerNode.initialize();

        Message firstMsg = con.poll();
        int firstTick = firstMsg.get("tick");

        Message secondMsg = con.poll();
        int secondTick = secondMsg.get("tick");

        Assertions.assertEquals(0, firstTick);
        Assertions.assertEquals(1, secondTick);
        Assertions.assertTrue(secondTick > firstTick);

        timerNode.shutdown();
    }

    @DisplayName("shutdown 후 정지")
    @Test
    void three() throws InterruptedException {
        timerNode.initialize();
        Thread.sleep(100);
        Assertions.assertTrue(con.getBufferSize() > 0);
        timerNode.shutdown();
        int count = con.getBufferSize();

        Thread.sleep(1000);

        Assertions.assertEquals(count, con.getBufferSize());

    }

    @DisplayName("주기 확인")
    @Test
    void four() throws InterruptedException {
        timerNode.initialize();

        Thread.sleep(2100);

        timerNode.shutdown();

        int count = con.getBufferSize();
        System.out.println(count);
        Assertions.assertTrue(count >= 4 && count <= 5);

    }
}
