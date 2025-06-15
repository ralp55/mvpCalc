package com.example;

import com.example.demo.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = DemoApplication.class)
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testConstructor() {
		new DemoApplication();
	}

	@Test
	void testMainMethod() {
		String[] args = {};
		DemoApplication.main(args);
	}
}
