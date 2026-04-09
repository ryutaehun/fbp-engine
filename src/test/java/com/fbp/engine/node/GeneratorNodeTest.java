package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorNodeTest {

    private GeneratorNode gen;
    private Connection outConn;

    @BeforeEach
    public void setup() {
        gen = new GeneratorNode("gen", 5);
        outConn = new Connection(10);
        gen.getOutputPort("out").connect(outConn);
    }

    @DisplayName("스레드 실행 후 메시지 생성 확인")
    @Test
    void one() throws InterruptedException {
        Thread thread = new Thread(gen);
        thread.start();

        Message msg = outConn.poll();

        assertNotNull(msg);
        assertEquals(0, (Integer) msg.get("count"));
        assertEquals("data-0", msg.get("value"));

        thread.interrupt();
    }

    @DisplayName("지정된 개수만큼 메시지 생성 확인")
    @Test
    void two() throws InterruptedException {
        Thread thread = new Thread(gen);
        thread.start();

        for (int i = 0; i < 5; i++) {
            Message msg = outConn.poll();
            assertNotNull(msg);
            assertEquals(i, (Integer) msg.get("count"));
        }

        Thread.sleep(100);
        assertEquals(0, outConn.getBufferSize());

        thread.interrupt();
    }

    @DisplayName("중간에 정지(shutdown) 시 생성 중단 확인")
    @Test
    void three() throws InterruptedException {
        GeneratorNode longGen = new GeneratorNode("long-gen", 100);
        longGen.getOutputPort("out").connect(outConn);

        Thread thread = new Thread(longGen);
        thread.start();

        Thread.sleep(50);

        longGen.shutdown();
        thread.interrupt();

        int countAtStop = outConn.getBufferSize();

        Thread.sleep(200);
        assertEquals(countAtStop, outConn.getBufferSize());
    }
}