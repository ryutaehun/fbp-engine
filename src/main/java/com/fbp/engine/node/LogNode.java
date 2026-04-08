package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogNode extends AbstractNode {

    public LogNode(String id){
        super(id);
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    protected void onProcess(Message message) {
        String now = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(LocalDateTime.now());

        System.out.printf("[%s][%s] %s%n",now , getId(), message.toString());
        send("out", message);
    }
}
