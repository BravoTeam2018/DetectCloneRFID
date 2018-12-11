package com.cit;

import com.cit.config.ServicesConfig;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *  this is just a test which calls the main class
 */
@Category(IntegrationTests.class)
@ContextConfiguration(classes = ServicesConfig.class)
public class DetectCloneApplicationCoverTests {

	@Test
	public void applicationStarts() {

//		assertThrows(Exception.class,
//				()->{
//					RfidclonespyApplication.main(new String[]{});
//				});

		DetectCloneRFIDApplication.main(new String[]{});


	}



}
