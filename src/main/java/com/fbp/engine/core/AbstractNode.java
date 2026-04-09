package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNode implements Node, Runnable {
    @Getter
    private final String id;
    private final Map<String, InputPort> inputPorts = new HashMap<>();
    @Getter
    private final Map<String, OutputPort> outputPorts = new HashMap<>();

    public AbstractNode(String id) {
        this.id = id;
    }

    @Override
    public void process(Message message) {
        System.out.printf("[%s] processing message...%n", id);
        onProcess(message);
        System.out.printf("[%s] done message...%n", id);
    }

    protected abstract void onProcess(Message message);

    protected void addInputPort(String name) {
        inputPorts.put(name, new DefaultInputPort(name, this));
    }

    protected void addOutputPort(String name) {
        outputPorts.put(name, new DefaultOutputPort(name));
    }

    @Override
    public void initialize() { System.out.printf("[%s] initialized%n", id); }

    @Override
    public void shutdown() { System.out.printf("[%s] shutdown%n", id); }

    public InputPort getInputPort(String name) { return inputPorts.get(name); }

    public OutputPort getOutputPort(String name) { return outputPorts.get(name); }

    protected void send(String portName, Message message) {
        OutputPort op = outputPorts.get(portName);
        if (op != null) op.send(message);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                for (InputPort port : inputPorts.values()) {
                    Message message = port.receive();
                    if (message != null) {
                        process(message);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println(getId() + " error: " + e.getMessage());
        }
    }
}