package com.example.performance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolSizingExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Thread Pool Sizing Example");
        System.out.println("=========================");
        
        // Get number of available cores
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processor cores: " + cores);
        
        // For CPU-bound tasks
        int cpuBoundPoolSize = cores;
        System.out.println("Optimal pool size for CPU-bound tasks: " + cpuBoundPoolSize);
        
        // For IO-bound tasks (assuming 90% wait time)
        int waitTimeRatio = 9;  // 90% wait time means ratio of 9:1
        int ioBoundPoolSize = cores * (1 + waitTimeRatio);
        System.out.println("Optimal pool size for IO-bound tasks (90% wait time): " + ioBoundPoolSize);
        
        // Test with CPU-bound tasks
        System.out.println("\nTesting with CPU-bound tasks:");
        testPoolSize(cpuBoundPoolSize, true);
        
        // Test with IO-bound tasks
        System.out.println("\nTesting with IO-bound tasks:");
        testPoolSize(ioBoundPoolSize, false);
        
        // Compare with too small pool for IO-bound tasks
        System.out.println("\nTesting with small pool size for IO-bound tasks:");
        testPoolSize(cores, false);
    }
    
    private static void testPoolSize(int poolSize, boolean cpuBound) throws InterruptedException {
        System.out.println("Using thread pool with " + poolSize + " threads for " + 
                          (cpuBound ? "CPU-bound" : "IO-bound") + " tasks");
        
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        
        long startTime = System.currentTimeMillis();
        
        // Submit a number of tasks
        int taskCount = poolSize * 3; // 3 tasks per thread
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            executor.submit(() -> {
                if (cpuBound) {
                    // CPU-bound task - simulate heavy computation
                    long result = 0;
                    for (long j = 0; j < 100_000_000L; j++) {
                        result += j % 10;
                    }
                    System.out.println("CPU task " + taskId + " completed with result: " + result);
                } else {
                    // IO-bound task - simulate IO operations with sleep
                    try {
                        // Simulate 90% wait time
                        Thread.sleep(900);
                        // And 10% CPU time
                        long result = 0;
                        for (long j = 0; j < 10_000_000L; j++) {
                            result += j % 10;
                        }
                        System.out.println("IO task " + taskId + " completed with result: " + result);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                return taskId;
            });
        }
        
        // Shutdown the executor
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        
        long endTime = System.currentTimeMillis();
        System.out.println("Completed " + taskCount + " tasks in " + (endTime - startTime) + "ms");
    }
} 