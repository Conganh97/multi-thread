package com.example.performance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SingleThreadExecutorExample {
    public static void main(String[] args) {
        System.out.println("Single-Threaded Executor Example");
        System.out.println("===============================");
        
        // Create a single-threaded executor
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        System.out.println("Starting to submit tasks to single thread executor...");
        
        // Submit 5 tasks to be executed sequentially
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Starting task " + taskId + " on " + 
                                  Thread.currentThread().getName());
                try {
                    // Simulate work
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Completed task " + taskId + " on " + 
                                 Thread.currentThread().getName());
                return "Task " + taskId + " result";
            });
        }
        
        System.out.println("All tasks submitted. They will execute sequentially.");
        
        // Submit an additional task that tries to interrupt processing
        executor.submit(() -> {
            System.out.println("Priority task submitted on " + 
                              Thread.currentThread().getName());
            System.out.println("Note: Despite the name, this task will wait for previous tasks");
        });
        
        // Proper shutdown
        executor.shutdown();
        try {
            // Wait for all tasks to complete or timeout after 60 seconds
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        
        System.out.println("All tasks completed");
        System.out.println("Note: All tasks executed on the same thread sequentially");
    }
} 