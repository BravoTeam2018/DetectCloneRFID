package com.cit;

import com.cit.config.ServicesConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@ContextConfiguration(classes = ServicesConfig.class)
@AutoConfigureMockMvc
@RunWith(value = SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = {DetectCloneRFIDApplication.class})
class DetectCloneApplicationITTests {

	@Test
	void applicationStarts() {

	}



}
