package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.core.DefaultOutputPort;
import com.fbp.engine.core.Node;
import com.fbp.engine.core.OutputPort;
import com.fbp.engine.message.Message;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class GeneratorNode extends AbstractNode {

    public GeneratorNode(String id) {
        super(id);
        addOutputPort("out");
    }

    @Override
    protected void onProcess(Message message) {
    }

    public void generate(String key, Object value){
        Map<String, Object> data = new HashMap<>();
        data.put(key, value);
        getOutputPort("out").send(new Message(data));
    }
}
