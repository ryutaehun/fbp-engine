package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionTest {

    private Connection connection;
    private Message message1;
    private Message message2;

    @BeforeEach
    void setup() {
        connection = new Connection();
        message1 = new Message(Map.of("temperature", 38.8));
        message2 = new Message(Map.of("temperature", 40.0));
    }

    @DisplayName("deliver-poll 기본 동작")
    @Test
    void one() {
        connection.deliver(message1);

        Message received = connection.poll();
        assertEquals(message1, received);
    }

    @DisplayName("메시지 순서 보장")
    @Test
    void two() {
        connection.deliver(message1);
        connection.deliver(message2);

        assertEquals(message1, connection.poll());
        assertEquals(message2, connection.poll());
    }

    @DisplayName("멀티스레드 deliver-poll")
    @Test
    void testMultiThreadDeliverPoll() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Message[] received = new Message[1];

        Thread consumer = new Thread(() -> {
            received[0] = connection.poll();
            latch.countDown();
        });
        consumer.start();

        connection.deliver(message1);

        boolean isCompleted = latch.await(2, TimeUnit.SECONDS);

        assertTrue(isCompleted);
        assertEquals(message1, received[0]);
    }

    @DisplayName("poll 대기(블로킹) 동작 확인")
    @Test
    void testPollBlocking() {
        assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
            CountDownLatch latch = new CountDownLatch(1);

            Thread consumer = new Thread(() -> {
                connection.poll();
                latch.countDown();
            });
            consumer.start();

            Thread.sleep(500);
            assertEquals(1, latch.getCount());

            connection.deliver(message1);

            latch.await();
            assertEquals(0, latch.getCount());
        });
    }

    @DisplayName("버퍼 크기 제한")
    @Test
    void testBufferLimitBlocksDeliver() throws InterruptedException {
        Connection smallConn = new Connection(2);
        smallConn.deliver(message1);
        smallConn.deliver(message2);

        CountDownLatch latch = new CountDownLatch(1);

        Thread producer = new Thread(() -> {
            smallConn.deliver(new Message(Map.of("k", "v")));
            latch.countDown();
        });
        producer.start();

        Thread.sleep(500);
        assertEquals(1, latch.getCount());

        smallConn.poll();

        boolean isCompleted = latch.await(2, TimeUnit.SECONDS);
        assertTrue(isCompleted);
    }

    @DisplayName("버퍼 크기 조회")
    @Test
    void six() {
        connection.deliver(message1);
        connection.deliver(message2);

        assertEquals(2, connection.getBufferSize());
    }
}