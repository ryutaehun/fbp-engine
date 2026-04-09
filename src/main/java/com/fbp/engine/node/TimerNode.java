package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimerNode extends AbstractNode {

    private final long intervalMs;
    private int tickCount = 0;
    private ScheduledExecutorService scheduler;

    public TimerNode(String id, long intervalMs){
        super(id);
        addOutputPort("out");
        this.intervalMs = intervalMs;
    }

    @Override
    public void shutdown(){
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        super.shutdown();
    }

    @Override
    protected void onProcess(Message message) {
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                generateMessage();
                Thread.sleep(intervalMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void generateMessage() {
        Map<String, Object> data = new HashMap<>();
        data.put("tick", tickCount++);
        data.put("timestamp", System.currentTimeMillis());
        send("out", new Message(data));
    }
}
