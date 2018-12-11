package com.cit.controllers;


import com.cit.UnitTests;
import com.cit.config.SwaggerConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Category(UnitTests.class)
@ContextConfiguration(classes = {SwaggerConfig.class})
@SpringBootTest
@RunWith(SpringRunner.class)
@WebAppConfiguration
@EnableWebMvc
public class SwaggerControllerITTest {

    @Autowired
    SwaggerController swaggerController;

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        swaggerController = new SwaggerController();
    }


    @Test
    public void testSwaggerControllerRedirect() throws Exception {

        assertEquals("redirect:/swagger-ui.html",this.swaggerController.swagger());

    }


}