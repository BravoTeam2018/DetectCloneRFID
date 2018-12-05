package com.cit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardIdExceptionTest {
    @Test
    void testCardIdException() {
        assertThrows( CardIdException.class, () -> {
                throw new CardIdException("Card Id not found");
            }
        );

    }
}