package com.ligafutbol.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:api-context-tests;DB_CLOSE_DELAY=-1",
		"league.import.enabled=false"
})
class ApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
