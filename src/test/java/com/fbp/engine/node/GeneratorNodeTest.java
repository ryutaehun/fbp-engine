package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorNodeTest {

    private GeneratorNode gen;
    private Connection outConn;

    @BeforeEach
    public void setup() {
        gen = new GeneratorNode("gen");
        outConn = new Connection();

        gen.getOutputPort("out").connect(outConn);
    }

    @DisplayName("generate 메시지 생성")
    @Test
    void one() {
        gen.generate("key", "value");

        assertEquals(1, outConn.getBufferSize());
    }

    @DisplayName("메시지 내용 확인")
    @Test
    void two() {
        gen.generate("key", "value");

        assertEquals("value", outConn.poll().get("key"));
    }

    @DisplayName("OutputPort 조회")
    @Test
    void three() {
        assertNotNull(gen.getOutputPort("out"));
    }

    @DisplayName("다수 generate 호출")
    @Test
    void four() {
        gen.generate("key", "value1");
        gen.generate("key", "value2");
        gen.generate("key", "value3");

        assertEquals(3, outConn.getBufferSize());
    }
}