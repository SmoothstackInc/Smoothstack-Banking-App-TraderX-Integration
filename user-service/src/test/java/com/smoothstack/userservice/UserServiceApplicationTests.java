package com.smoothstack.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads, indirectly testing the @SpringBootApplication annotation and related configuration
        assertNotNull(context);
    }

    @Test
    void mainMethodTestOnDifferentPort() {
        // Set the server port to a different value
        System.setProperty("server.port", "0"); // Use 0 for a random port

        try {
            UserServiceApplication.main(new String[]{});
        } finally {
            // Clean up the property to not affect other tests
            System.clearProperty("server.port");
        }
    }

}
