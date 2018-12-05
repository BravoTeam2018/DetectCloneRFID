package com.cit.controllers;


import com.cit.config.SwaggerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;


public class SwaggerControllerTest {

    @Test
    void swagger() {
        SwaggerController swaggerController =  new SwaggerController();

        assertEquals("redirect:/swagger-ui.html",swaggerController.swagger());
    }
}