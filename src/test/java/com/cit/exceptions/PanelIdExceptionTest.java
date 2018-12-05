package com.cit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PanelIdExceptionTest {

    @Test
    void testCardIdException() {
        assertThrows( PanelIdException.class, () -> {
                    throw new PanelIdException("Panel Id not found");
                }
        );

    }


}