package com.example.virtualthread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadExample {
    private static final int THREAD_COUNT = 10_000;

    public static void main(String[] args) throws Exception {
        System.out.println("Virtual Thread Basics Example");
        System.out.println("===========================");
        System.out.println("This example demonstrates the basics of Java virtual threads");
        System.out.println("including creation, execution, and comparison with platform threads.\n");

        System.out.println("Part 1: Creating Virtual Threads");
        demonstrateCreation();

        Thread.sleep(1000);

        System.out.println("\nPart 2: Performance Comparison with Platform Threads");
        performanceComparison();

        Thread.sleep(1000);

        System.out.println("\nPart 3: Memory Footprint");
        memoryComparison();

        System.out.println("\nExplanation:");
        System.out.println("Virtual Threads Characteristics:");
        System.out.println("- Lightweight threads managed by the JVM rather than the OS");
        System.out.println("- Much lower memory footprint (few KB vs ~1MB for platform threads)");
        System.out.println("- Can create millions of virtual threads on modest hardware");
        System.out.println("- Same Thread API as platform threads (Thread class)");
        System.out.println("- Designed for IO-bound workloads, not CPU-intensive tasks");
        System.out.println("- Automatically yield carrier thread during blocking operations");
        System.out.println("- Number of carrier threads typically matches CPU core count");
    }

    // Part 1: Different ways to create virtual threads
    private static void demonstrateCreation() throws Exception {
        System.out.println("Creating virtual threads in different ways:");

        // Method 1: Using Thread.ofVirtual().start()
        Thread vthread1 = Thread.ofVirtual()
                .name("VirtualThread-1")
                .start(() -> {
                    System.out.println("Method 1: Running in " +
                            Thread.currentThread().getName());
                });

        // Method 2: Using Thread.startVirtualThread()
        Thread vthread2 = Thread.startVirtualThread(() -> {
            System.out.println("Method 2: Running in " + Thread.currentThread().getName());
        });

        // Method 3: Using a virtual thread builder but not starting immediately
        Thread vthread3 = Thread.ofVirtual()
                .name("VirtualThread-3")
                .unstarted(() -> {
                    System.out.println("Method 3: Running in " +
                            Thread.currentThread().getName());
                });
        vthread3.start();

        // Method 4: Using a custom virtual thread factory
        ThreadFactory factory = Thread.ofVirtual().factory();
        Thread vthread4 = factory.newThread(() -> {
            System.out.println("Method 4: Running in " + Thread.currentThread().getName());
        });
        vthread4.start();

        // Method 5: Using ExecutorService
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                System.out.println("Method 5: Running in " + Thread.currentThread().getName());
            });
        }

        // Wait for all threads to complete
        vthread1.join();
        vthread2.join();
        vthread3.join();
        vthread4.join();

        System.out.println("\nVirtual thread properties:");
        Thread vt = Thread.ofVirtual().name("TestThread").unstarted(() -> {
        });
        System.out.println("- isVirtual(): " + vt.isVirtual());
        System.out.println("- Default name: " + Thread.currentThread().getName());
        System.out.println("- toString(): " + vt);
        System.out.println("- Initial state: " + vt.getState());
    }

    // Part 2: Compare performance of virtual threads vs platform threads
    private static void performanceComparison() throws Exception {
        System.out.println("Comparing performance of virtual threads and platform threads");
        System.out.println("for IO-bound tasks (using sleep to simulate IO):\n");

        // Number of tasks to run
        final int TASK_COUNT = 1000;

        // Simulate an IO-bound task
        Runnable ioTask = () -> {
            try {
                // Simulate IO operation
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Using platform threads
        System.out.println("Running " + TASK_COUNT + " tasks using platform threads...");
        long platformStart = System.currentTimeMillis();

        try (ExecutorService executor =
                     Executors.newFixedThreadPool(100)) { // Limited number of platform threads

            for (int i = 0; i < TASK_COUNT; i++) {
                executor.submit(ioTask);
            }
        } // executor.close() called implicitly, waits for tasks

        long platformDuration = System.currentTimeMillis() - platformStart;
        System.out.println("Platform threads completed in: " + platformDuration + "ms");

        // Using virtual threads
        System.out.println("\nRunning " + TASK_COUNT + " tasks using virtual threads...");
        long virtualStart = System.currentTimeMillis();

        try (ExecutorService executor =
                     Executors.newVirtualThreadPerTaskExecutor()) { // One virtual thread per task

            for (int i = 0; i < TASK_COUNT; i++) {
                executor.submit(ioTask);
            }
        } // executor.close() called implicitly, waits for tasks

        long virtualDuration = System.currentTimeMillis() - virtualStart;
        System.out.println("Virtual threads completed in: " + virtualDuration + "ms");

        // Compare results
        System.out.println("\nPerformance comparison:");
        System.out.printf("Platform threads: %d ms\n", platformDuration);
        System.out.printf("Virtual threads: %d ms\n", virtualDuration);
        System.out.printf("Speedup: %.2fx\n", (double) platformDuration / virtualDuration);
    }

    // Part 3: Memory footprint comparison
    private static void memoryComparison() throws Exception {
        System.out.println("Demonstrating memory efficiency of virtual threads:\n");

        Runtime runtime = Runtime.getRuntime();
        System.gc(); // Request garbage collection to get more accurate memory readings

        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Initial memory usage: " + (initialMemory / 1024 / 1024) + " MB");

        List<Thread> virtualThreads = new ArrayList<>();

        System.out.println("Creating " + THREAD_COUNT + " virtual threads...");

        AtomicInteger counter = new AtomicInteger();
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread vt = Thread.ofVirtual().unstarted(() -> {
                int id = counter.incrementAndGet();
                try {
                    // Park the thread so it stays alive
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            virtualThreads.add(vt);
        }

        // Start all threads
        for (Thread vt : virtualThreads) {
            vt.start();
        }

        // Let threads initialize
        Thread.sleep(500);

        System.gc();
        long memoryWithThreads = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory usage with " + THREAD_COUNT + " virtual threads: " +
                (memoryWithThreads / 1024 / 1024) + " MB");

        long memoryPerThread = (memoryWithThreads - initialMemory) / THREAD_COUNT;
        System.out.println("Approximate memory per virtual thread: " + memoryPerThread + " bytes");

        System.out.println("\nFor comparison, platform threads would typically use:");
        System.out.println("- ~1MB stack size per thread by default");
        System.out.println("- " + THREAD_COUNT + " platform threads would need ~" +
                (THREAD_COUNT / 1024) + " GB of memory");

        // Wait for threads to finish
        for (Thread vt : virtualThreads) {
            vt.join();
        }
    }
} 