package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Connection {

    private String id;
    private final BlockingQueue<Message> buffer;

    @Setter
    @Getter
    private InputPort target;

    public Connection() {
        this(100);
    }

    public Connection(int capacity) {
        this.buffer = new LinkedBlockingQueue<>(capacity);
    }

    public void deliver(Message message) {
        try {
            buffer.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Message poll() {
        try {
            return buffer.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public int getBufferSize() {
        return buffer.size();
    }
}