package com.example.performance;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExample {
    public static void main(String[] args) {
        System.out.println("Scheduled Thread Pool Example");
        System.out.println("============================");
        
        // Create a scheduled thread pool with 2 threads
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        
        System.out.println("Starting scheduled executor examples...");
        
        // Schedule a task to run after 2 seconds
        scheduler.schedule(() -> {
            System.out.println("Delayed task executed after 2 seconds by " + 
                              Thread.currentThread().getName());
            return "Delayed task result";
        }, 2, TimeUnit.SECONDS);
        
        // Schedule a task to run every 1 second, after an initial delay of 0 seconds
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Fixed rate task executed by " + 
                              Thread.currentThread().getName() + 
                              " at " + System.currentTimeMillis());
            // If this task takes longer than the period, the next execution will start immediately
            try {
                Thread.sleep(500); // Simulate work that takes 500ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        // Schedule a task to run every 1 second after the previous execution completes
        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("Fixed delay task executed by " + 
                              Thread.currentThread().getName() + 
                              " at " + System.currentTimeMillis());
            // The next execution will start 1 second after this completes
            try {
                Thread.sleep(500); // Simulate work that takes 500ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        // Keep the program running for 10 seconds
        try {
            System.out.println("Main thread sleeping for 10 seconds...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Shutdown the scheduler
        System.out.println("Shutting down the scheduler...");
        scheduler.shutdown();
        
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        
        System.out.println("Scheduler terminated");
    }
} 