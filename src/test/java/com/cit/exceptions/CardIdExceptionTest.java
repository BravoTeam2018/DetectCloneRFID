package com.cit.exceptions;

import com.cit.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Category(UnitTests.class)
class CardIdExceptionTest {
    @Test
    void testCardIdException() {
        assertThrows( CardIdException.class, () -> {
                throw new CardIdException("Card Id not found");
            }
        );

    }
}