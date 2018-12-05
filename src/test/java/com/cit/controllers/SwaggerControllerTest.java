package com.cit.controllers;


import com.cit.UnitTests;
import com.cit.config.SwaggerConfig;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;


@Category(UnitTests.class)
public class SwaggerControllerTest {

    @Test
    void swagger() {
        SwaggerController swaggerController =  new SwaggerController();

        assertEquals("redirect:/swagger-ui.html",swaggerController.swagger());
    }
}