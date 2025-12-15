package com.example.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for App
 */
public class AppTest {
    
    @Test
    public void testGetGreeting() {
        App app = new App();
        String greeting = app.getGreeting();
        
        assertNotNull(greeting, "Greeting should not be null");
        assertTrue(greeting.contains("Hello"), "Greeting should contain 'Hello'");
        assertTrue(greeting.contains("Java Maven App"), "Greeting should contain app name");
    }
    
    @Test
    public void testAppNotNull() {
        App app = new App();
        assertNotNull(app, "App instance should not be null");
    }
}
