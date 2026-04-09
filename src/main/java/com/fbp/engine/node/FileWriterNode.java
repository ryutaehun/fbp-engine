package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterNode extends AbstractNode {
   private final String filePath;
   private BufferedWriter writer;

    public FileWriterNode(String id, String filePath) {
        super(id);
        addInputPort("in");
        this.filePath = filePath;
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            this.writer = new BufferedWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onProcess(Message message) {
        String msg = message.toString();
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if(writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
