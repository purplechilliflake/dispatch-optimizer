package com.freightfox.dispatchoptimizer; // <-- Correct package

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // Tells Spring to find the main application and start a context
class DispatchOptimizerApplicationTests {

    @Test // A standard JUnit 5 test
    void contextLoads() {
        // This test is simple: if the application context can start up
        // without crashing, the test passes. It's a great sanity check.
    }

}