package com.example.communication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("CountDownLatch Example");
        System.out.println("=====================");
        System.out.println("This example demonstrates CountDownLatch for coordinating multiple threads.\n");
        
        System.out.println("Example 1: Starting main work after initialization tasks complete");
        initializationExample();
        
        Thread.sleep(1000);
        
        System.out.println("\nExample 2: Controlling the start of multiple threads");
        startingGunExample();
        
        Thread.sleep(1000);
        
        System.out.println("\nExample 3: Waiting with timeout");
        timeoutExample();
        
        System.out.println("\nExplanation:");
        System.out.println("CountDownLatch is a synchronization aid that allows one or more threads to wait until");
        System.out.println("a set of operations being performed in other threads completes. Key features:");
        System.out.println("- One-time use: Cannot be reset once count reaches zero");
        System.out.println("- Counting down: Threads call countDown() to decrement the counter");
        System.out.println("- Waiting threads: Threads wait for counter to reach zero with await()");
        System.out.println("- No ownership: Any thread can count down, any thread can wait");
        System.out.println("- Common uses: Thread coordination, implementing barriers, startup signaling");
    }
    
    // Example 1: Using CountDownLatch for thread initialization
    private static void initializationExample() throws InterruptedException {
        // Create a CountDownLatch with count of 3 (for 3 initialization tasks)
        CountDownLatch initLatch = new CountDownLatch(3);
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    // Simulate initialization task
                    System.out.println("Task " + taskId + " initializing...");
                    Thread.sleep(1000 + (int)(Math.random() * 1000));
                    System.out.println("Task " + taskId + " initialized");
                    
                    // Signal that this task is complete
                    initLatch.countDown();
                    System.out.println("Task " + taskId + " counted down, remaining: " + initLatch.getCount());
                    
                    // Continue with additional work after initialization
                    Thread.sleep(500);
                    System.out.println("Task " + taskId + " continuing with work");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Main thread waits for all initialization tasks to complete
        System.out.println("Main thread waiting for initialization...");
        initLatch.await();
        System.out.println("All initialization tasks complete, main thread continuing");
        
        // Cleanup
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    
    // Example 2: Using CountDownLatch as a starting gun for multiple threads
    private static void startingGunExample() throws InterruptedException {
        // This latch is used to signal all threads to start simultaneously
        CountDownLatch startSignal = new CountDownLatch(1);
        // This latch is used to wait for all threads to finish
        CountDownLatch doneSignal = new CountDownLatch(5);
        
        // Create and start threads
        for (int i = 0; i < 5; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    System.out.println("Worker " + workerId + " ready and waiting for start signal");
                    
                    // Wait for start signal from main thread
                    startSignal.await();
                    
                    // Begin work simultaneously with other threads
                    System.out.println("Worker " + workerId + " started");
                    Thread.sleep((int)(Math.random() * 1000));
                    System.out.println("Worker " + workerId + " finished");
                    
                    // Signal that this worker is done
                    doneSignal.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Worker-" + i).start();
        }
        
        // Give threads time to initialize and wait
        Thread.sleep(500);
        
        // Send start signal to all waiting threads
        System.out.println("\nMain thread firing the starting gun...");
        startSignal.countDown();
        
        // Wait for all workers to finish
        System.out.println("Main thread waiting for all workers to finish");
        doneSignal.await();
        System.out.println("All workers finished");
    }
    
    // Example 3: Using CountDownLatch with timeout
    private static void timeoutExample() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        
        // Start only 2 threads but latch count is 3
        for (int i = 0; i < 2; i++) {
            final int taskId = i;
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    System.out.println("Task " + taskId + " counting down");
                    latch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        
        System.out.println("Main thread waiting with 2 second timeout...");
        
        // Wait with timeout
        boolean completed = latch.await(2, TimeUnit.SECONDS);
        
        if (completed) {
            System.out.println("All tasks completed within the timeout");
        } else {
            System.out.println("Timeout elapsed before all tasks completed");
            System.out.println("Remaining count: " + latch.getCount());
        }
    }
} 