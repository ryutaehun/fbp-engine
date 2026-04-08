package com.fbp.engine.node;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrintNodePortTest {

    private final PrintNode printer = new PrintNode("printer");

    @DisplayName("InputPort 조회")
    @Test
    void one() {
        assertNotNull(printer.getInputPort("in"));
    }

    @DisplayName("InputPort를 통한 수신")
    @Test
    void two(){
        assertDoesNotThrow(() ->
                printer.getInputPort("in").receive());
    }
}
