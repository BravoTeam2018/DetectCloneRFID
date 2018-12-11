package com.cit.exceptions;

import com.cit.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Category(UnitTests.class)
public class PanelIdExceptionTest {

    @Test
    void testCardIdException() {
        assertThrows( PanelIdException.class, () -> {
                    throw new PanelIdException("Panel Id not found");
                }
        );

    }


}