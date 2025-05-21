package com.example.performance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CachedThreadPoolExample {
    public static void main(String[] args) {
        // Create a cached thread pool that creates new threads as needed and reuses idle ones
        ExecutorService executor = Executors.newCachedThreadPool();
        
        System.out.println("Starting to submit tasks to cached thread pool...");
        
        // Submit 20 tasks with varying delays to demonstrate thread creation and reuse
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " started on " + 
                                  Thread.currentThread().getName());
                try {
                    // Simulate varying workload
                    Thread.sleep(taskId % 3 == 0 ? 1000 : 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task " + taskId + " completed on " + 
                                  Thread.currentThread().getName());
                return taskId;
            });
            
            // Small delay between task submissions to better observe thread creation
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("All tasks submitted, waiting for completion...");
        
        // Proper shutdown
        executor.shutdown();
        try {
            // Wait for all tasks to complete or timeout after 60 seconds
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                System.out.println("Some tasks did not complete before timeout");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        
        System.out.println("All tasks completed");
    }
} 