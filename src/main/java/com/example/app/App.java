package com.example.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple Java Application for DevOps CI/CD Pipeline Demo
 */
public class App {
    
    private static final String APP_NAME = "Java Maven App";
    private static final String VERSION = "1.0.0";
    
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
    
    public void run() {
        printBanner();
        printInfo();
        startServer();
    }
    
    private void printBanner() {
        System.out.println("========================================");
        System.out.println("   " + APP_NAME);
        System.out.println("   Version: " + VERSION);
        System.out.println("========================================");
    }
    
    private void printInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);
        
        System.out.println("\n✓ Application started successfully!");
        System.out.println("✓ Time: " + currentTime);
        System.out.println("✓ Java Version: " + System.getProperty("java.version"));
        System.out.println("✓ OS: " + System.getProperty("os.name"));
        System.out.println("✓ User: " + System.getProperty("user.name"));
    }
    
    private void startServer() {
        System.out.println("\n🚀 Server is running...");
        System.out.println("💡 This is a demo application for CI/CD pipeline");
        System.out.println("🔄 Press Ctrl+C to stop");
        
        // Keep the application running
        try {
            while (true) {
                Thread.sleep(10000); // Sleep for 10 seconds
                System.out.println("⏰ Application is still running... " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        } catch (InterruptedException e) {
            System.out.println("\n⚠️  Application interrupted. Shutting down...");
        }
    }
    
    public String getGreeting() {
        return "Hello from " + APP_NAME + " v" + VERSION;
    }
}
