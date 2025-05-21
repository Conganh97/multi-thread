package com.example.virtualthread;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadPerformanceExample {
    private static final int TASK_COUNT = 10_000;
    private static final Object LOCK = new Object();
    private static final AtomicInteger counter = new AtomicInteger();
    
    public static void main(String[] args) throws Exception {
        System.out.println("Virtual Thread Performance Example");
        System.out.println("================================");
        System.out.println("This example demonstrates performance considerations when using virtual threads.\n");
        
        System.out.println("Part 1: Demonstrating Thread Pinning Impact");
        demonstratePinning();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 2: Scaling with Number of Tasks");
        demonstrateScaling();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 3: IO Operations and Yielding");
        demonstrateIOYielding();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 4: Memory Consumption");
        demonstrateMemoryUsage();
        
        System.out.println("\nPerformance Considerations Summary:");
        System.out.println("1. Avoid thread pinning (long synchronized blocks)");
        System.out.println("2. Virtual threads excel with IO-bound workloads");
        System.out.println("3. Virtual threads automatically yield during blocking operations");
        System.out.println("4. Memory usage grows with the number of active virtual threads");
        System.out.println("5. The number of carrier threads typically matches CPU core count");
    }
    
    // Part 1: Demonstrate impact of thread pinning
    private static void demonstratePinning() throws InterruptedException {
        System.out.println("Demonstrating the impact of pinning on virtual thread performance:");
        int numTasks = 500;
        
        // Test with pinning (long synchronized blocks)
        long pinnedStart = System.currentTimeMillis();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numTasks; i++) {
                executor.submit(() -> {
                    synchronized (LOCK) {
                        try {
                            // This operation pins the virtual thread to its carrier thread
                            Thread.sleep(10);
                            counter.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            }
        }
        
        long pinnedDuration = System.currentTimeMillis() - pinnedStart;
        System.out.println("Time with pinning: " + pinnedDuration + "ms");
        
        // Reset counter
        counter.set(0);
        
        // Test without pinning (short synchronized blocks)
        long unpinnedStart = System.currentTimeMillis();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numTasks; i++) {
                executor.submit(() -> {
                    try {
                        // Do the blocking operation outside the synchronized block
                        Thread.sleep(10);
                        
                        // Only synchronize the small critical section
                        synchronized (LOCK) {
                            counter.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        
        long unpinnedDuration = System.currentTimeMillis() - unpinnedStart;
        System.out.println("Time without pinning: " + unpinnedDuration + "ms");
        System.out.println("Performance improvement: " + 
                          String.format("%.2fx", (double)pinnedDuration / unpinnedDuration));
        
        System.out.println("\nExplanation:");
        System.out.println("When a virtual thread executes a synchronized block, it becomes 'pinned'");
        System.out.println("to its carrier thread and cannot yield during blocking operations.");
        System.out.println("With many pinned virtual threads, we effectively limit concurrency to the");
        System.out.println("number of carrier threads (typically equal to CPU cores).");
    }
    
    // Part 2: Demonstrate scaling with different numbers of tasks
    private static void demonstrateScaling() throws InterruptedException {
        System.out.println("Comparing scaling of virtual threads vs platform threads:");
        
        // Define task counts to test
        int[] taskCounts = {100, 1000, 5000};
        
        // Runnable that simulates an IO-bound task
        Runnable ioTask = () -> {
            try {
                // Simulate IO operation
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        System.out.printf("%-10s %-15s %-15s %-10s%n", 
                         "Tasks", "Platform (ms)", "Virtual (ms)", "Ratio");
        System.out.println("-----------------------------------------------");
        
        for (int taskCount : taskCounts) {
            // Measure platform threads
            long platformStart = System.currentTimeMillis();
            try (var executor = 
                     Executors.newFixedThreadPool(100)) { // Limited platform threads
                
                for (int i = 0; i < taskCount; i++) {
                    executor.submit(ioTask);
                }
            }
            long platformDuration = System.currentTimeMillis() - platformStart;
            
            // Measure virtual threads
            long virtualStart = System.currentTimeMillis();
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < taskCount; i++) {
                    executor.submit(ioTask);
                }
            }
            long virtualDuration = System.currentTimeMillis() - virtualStart;
            
            // Calculate ratio and print results
            double ratio = (double) platformDuration / virtualDuration;
            System.out.printf("%-10d %-15d %-15d %-10.2fx%n", 
                             taskCount, platformDuration, virtualDuration, ratio);
        }
    }
    
    // Part 3: Demonstrate how virtual threads yield during IO operations
    private static void demonstrateIOYielding() throws InterruptedException {
        System.out.println("Demonstrating how virtual threads yield during IO operations:");
        
        // Create a small number of virtual threads to make the output readable
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 3; i++) {
                final int id = i;
                executor.submit(() -> {
                    try {
                        System.out.println("Thread " + id + ": Starting first IO operation");
                        // First IO operation - thread will yield
                        Thread.sleep(100);
                        System.out.println("Thread " + id + ": Completed first IO, starting second");
                        // Second IO operation - thread will yield again
                        Thread.sleep(100);
                        System.out.println("Thread " + id + ": Completed second IO");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        
        System.out.println("\nExplanation:");
        System.out.println("During blocking operations like Thread.sleep(), network or file IO,");
        System.out.println("virtual threads automatically yield their carrier thread.");
        System.out.println("This allows the carrier thread to be reused by other virtual threads,");
        System.out.println("enabling high throughput with a limited number of system threads.");
        System.out.println("Common yielding points include:");
        System.out.println("- Thread.sleep() and Thread.yield()");
        System.out.println("- BlockingQueue operations");
        System.out.println("- Object.wait() and Condition.await()");
        System.out.println("- Socket IO operations");
        System.out.println("- File IO operations");
    }
    
    // Part 4: Demonstrate memory usage patterns
    private static void demonstrateMemoryUsage() throws InterruptedException {
        System.out.println("Demonstrating memory efficiency of virtual threads:");
        
        Runtime runtime = Runtime.getRuntime();
        System.gc(); // Request garbage collection
        
        // Measure baseline memory
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Initial memory usage: " + (initialMemory / 1024 / 1024) + " MB");
        
        // Create many virtual threads to demonstrate memory scaling
        int[] threadCounts = {1000, 5000, 10000};
        
        for (int count : threadCounts) {
            System.gc();
            long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // Create threads
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < count; i++) {
                    executor.submit(() -> {
                        try {
                            Thread.sleep(500); // Keep thread alive
                            return "Done";
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return "Interrupted";
                        }
                    });
                }
                
                // Let threads initialize but not complete
                Thread.sleep(200);
                
                System.gc();
                long withThreadsMemory = runtime.totalMemory() - runtime.freeMemory();
                long increase = withThreadsMemory - beforeMemory;
                
                System.out.printf("%,d virtual threads: %d MB (increase of %d MB, ~%d KB per thread)%n",
                                 count, 
                                 withThreadsMemory / 1024 / 1024,
                                 increase / 1024 / 1024,
                                 (increase / count) / 1024);
            }
        }
        
        System.out.println("\nFor comparison, each platform thread typically uses ~1MB of memory");
        System.out.println("for its stack, making it impractical to create tens of thousands");
        System.out.println("of platform threads on most systems.");
    }
} 