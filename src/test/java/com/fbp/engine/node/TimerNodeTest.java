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
    Thread timerThread;

    @BeforeEach
    void setup(){
        timerNode = new TimerNode("timer-node", 500);
        con = new Connection(10);
        timerNode.getOutputPort("out").connect(con);
    }

    @DisplayName("메시지 생성 확인")
    @Test
    void one() throws InterruptedException {
        timerThread = new Thread(timerNode);
        timerThread.start();

        Message msg = con.poll();
        Assertions.assertNotNull(msg);
        Assertions.assertEquals(0, (Integer) msg.get("tick"));

        timerThread.interrupt();
    }

    @DisplayName("tick 값이 순차적으로 증가하는지 확인")
    @Test
    void two() throws InterruptedException {
        timerThread = new Thread(timerNode);
        timerThread.start();

        Message firstMsg = con.poll();
        int firstTick = firstMsg.get("tick");

        Message secondMsg = con.poll();
        int secondTick = secondMsg.get("tick");

        Assertions.assertEquals(0, firstTick);
        Assertions.assertEquals(1, secondTick);
        Assertions.assertTrue(secondTick > firstTick);

        timerThread.interrupt();
    }

    @DisplayName("shutdown 후 정지")
    @Test
    void three() throws InterruptedException {
        timerThread = new Thread(timerNode);
        timerThread.start();

        Thread.sleep(100);
        Assertions.assertTrue(con.getBufferSize() > 0);

        timerNode.shutdown();
        timerThread.interrupt();

        int count = con.getBufferSize();
        Thread.sleep(1000);

        Assertions.assertEquals(count, con.getBufferSize());
    }
}
