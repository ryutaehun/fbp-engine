package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.core.InputPort;
import com.fbp.engine.message.Message;

import java.util.HashMap;
import java.util.Map;

public class MergeNode extends AbstractNode {
    private Message pending1;
    private Message pending2;
    private final Object lock = new Object();

    public MergeNode(String id) {
        super(id);
        addInputPort("in-1");
        addInputPort("in-2");
        addOutputPort("out");
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                InputPort in1 = getInputPort("in-1");
                InputPort in2 = getInputPort("in-2");

                if (pending1 == null) {
                    pending1 = in1.receive();
                }

                if (pending2 == null) {
                    pending2 = in2.receive();
                }

                synchronized (lock) {
                    if (pending1 != null && pending2 != null) {
                        Map<String, Object> mergedPayload = new HashMap<>(pending1.getPayload());
                        mergedPayload.putAll(pending2.getPayload());

                        Message mergedMessage = new Message(mergedPayload);
                        send("out", mergedMessage);

                        pending1 = null;
                        pending2 = null;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdown();
        }
    }

    @Override
    protected void onProcess(Message message) {
    }
}
