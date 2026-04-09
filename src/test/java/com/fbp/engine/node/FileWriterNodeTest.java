package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class FileWriterNodeTest {

    FileWriterNode fileWriterNode;
    Connection connection;

    @BeforeEach
    void setup(){
        fileWriterNode = new FileWriterNode("fileWriter", "/Users/taehun/IdeaProjects/fbp-engine/test.txt");
        fileWriterNode.initialize();
        connection = new Connection();
        connection.setTarget(fileWriterNode.getInputPort("in"));
    }

    @DisplayName("파일 생성")
    @Test
    void one(){
        File file = new File("/Users/taehun/IdeaProjects/fbp-engine/test.txt");
        Assertions.assertTrue(file.exists());
    }

    @DisplayName("내용 기록")
    @Test
    void two() throws IOException {
        File file = new File("/Users/taehun/IdeaProjects/fbp-engine/test.txt");
        fileWriterNode.process(new Message(new HashMap<>()));
        fileWriterNode.process(new Message(new HashMap<>()));
        fileWriterNode.process(new Message(new HashMap<>()));

        fileWriterNode.shutdown();

        List<String> lines = Files.readAllLines(file.toPath());
        Assertions.assertFalse(lines.isEmpty());
    }

    @DisplayName("shutdown 후 파일 닫힘")
    @Test
    void three(){
        fileWriterNode.process(new Message(new HashMap<>()));

        fileWriterNode.shutdown();

        Assertions.assertThrows(Exception.class, () ->fileWriterNode.process(new Message(new HashMap<>())));
    }
}
