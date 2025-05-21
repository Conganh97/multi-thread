package com.example.virtualthread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadBestPracticesExample {
    // Thread-local storage example
    private static final ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "Default");

    // Counter for task tracking
    private static final AtomicInteger counter = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        System.out.println("Virtual Thread Best Practices Example");
        System.out.println("==================================");
        System.out.println("This example demonstrates best practices when using virtual threads.\n");

        System.out.println("Part 1: DO - Use Virtual Threads for IO-Bound Tasks");
        demonstrateForIOTasks();

        Thread.sleep(1000);

        System.out.println("\nPart 2: DON'T - Avoid Thread Pools for Virtual Threads");
        demonstrateAvoidThreadPools();

        Thread.sleep(1000);

        System.out.println("\nPart 3: DO - Keep Synchronization Blocks Short");
        demonstrateSyncBlocks();

        Thread.sleep(1000);

        System.out.println("\nPart 4: DON'T - Avoid ThreadLocal with Virtual Threads");
        demonstrateThreadLocalIssues();

        Thread.sleep(1000);

        System.out.println("\nPart 5: DO - Use Structured Concurrency");
        demonstrateStructuredConcurrency();

        System.out.println("\nBest Practices Summary:");
        System.out.println("DO:");
        System.out.println("✓ Use virtual threads for IO-bound tasks");
        System.out.println("✓ Keep synchronization blocks short");
        System.out.println("✓ Use structured concurrency where possible");
        System.out.println("✓ Use per-task thread creation");
        System.out.println("✓ Use standard blocking IO operations");

        System.out.println("\nDON'T:");
        System.out.println("✗ Use thread pools for virtual threads");
        System.out.println("✗ Use ThreadLocal extensively with virtual threads");
        System.out.println("✗ Hold locks during blocking operations");
        System.out.println("✗ Use virtual threads for CPU-intensive tasks");
        System.out.println("✗ Pin virtual threads (keep them mounted on carriers)");
    }

    // Part 1: Using virtual threads for IO-bound tasks
    private static void demonstrateForIOTasks() throws InterruptedException {
        System.out.println("Virtual threads are designed for IO-bound operations:");

        // Simulate IO-bound and CPU-bound tasks
        Runnable ioTask = () -> {
            try {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + ": Starting IO operation");
                // Simulate IO operation (e.g., network call, database query)
                Thread.sleep(100);
                System.out.println(threadName + ": IO operation completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable cpuTask = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + ": Starting CPU-intensive operation");
            // Simulate CPU-intensive operation
            long sum = 0;
            for (long i = 0; i < 100_000_000; i++) {
                sum += i;
            }
            System.out.println(threadName + ": CPU-intensive operation completed. Sum: " + sum);
        };

        System.out.println("Running IO-bound tasks with virtual threads:");
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                executor.submit(ioTask);
            }
        }

        System.out.println("\nRunning CPU-bound tasks with virtual threads (not recommended):");
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 2; i++) {
                executor.submit(cpuTask);
            }
        }

        System.out.println("\nRecommendation for CPU-bound tasks:");
        System.out.println("For CPU-intensive tasks, use platform threads with a fixed thread pool:");
        System.out.println("ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());");
    }

    // Part 2: Avoid thread pools for virtual threads
    private static void demonstrateAvoidThreadPools() throws InterruptedException {
        System.out.println("Thread pools negate the benefits of virtual threads:");

        // INCORRECT: Creating a pool of virtual threads
        ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();
        try (ExecutorService incorrectPool = Executors.newFixedThreadPool(10, virtualThreadFactory)) {
            System.out.println("Incorrect: Fixed pool of 10 virtual threads created");
            System.out.println("This defeats the purpose - virtual threads are designed to be plentiful");

            // Submit more tasks than pool size to demonstrate the problem
            for (int i = 0; i < 20; i++) {
                final int taskId = i;
                incorrectPool.submit(() -> {
                    try {
                        System.out.println("Task " + taskId + " starting on " + Thread.currentThread());
                        Thread.sleep(100);
                        return "Result from task " + taskId;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return "Interrupted";
                    }
                });
            }
        }

        System.out.println("\nCORRECT: Using one virtual thread per task");
        try (ExecutorService correctExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            System.out.println("Correct: Virtual thread per task executor created");
            System.out.println("This allows for optimal use of virtual threads");

            // Submit the same number of tasks
            for (int i = 0; i < 20; i++) {
                final int taskId = i;
                correctExecutor.submit(() -> {
                    try {
                        Thread.sleep(100);
                        return "Result from task " + taskId;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return "Interrupted";
                    }
                });
            }
        }
    }

    // Part 3: Keep synchronization blocks short
    private static void demonstrateSyncBlocks() throws InterruptedException {
        System.out.println("Virtual threads should avoid long synchronized blocks:");

        // Bad practice: Long-held lock during IO
        Object badLock = new Object();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 3; i++) {
                executor.submit(() -> {
                    synchronized (badLock) {
                        String threadName = Thread.currentThread().getName();
                        try {
                            System.out.println(threadName + ": Acquired lock (bad practice)");
                            System.out.println(threadName + ": Performing IO operation while holding lock...");
                            // This will pin the virtual thread to its carrier thread
                            Thread.sleep(500);
                            System.out.println(threadName + ": Completed IO (still holding lock)");
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            }
        }

        Thread.sleep(1500);

        // Good practice: Short synchronization around critical section
        Object goodLock = new Object();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 3; i++) {
                executor.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    try {
                        // Perform IO operation without holding lock
                        System.out.println(threadName + ": Performing IO operation (not holding lock)");
                        Thread.sleep(500);
                        System.out.println(threadName + ": Completed IO, now acquiring lock for critical section");

                        // Only synchronize the critical section
                        synchronized (goodLock) {
                            System.out.println(threadName + ": Processing result in critical section (short)");
                            // Short operation that needs synchronization
                            counter.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }

    // Part 4: Avoid ThreadLocal with virtual threads
    private static void demonstrateThreadLocalIssues() throws InterruptedException {
        System.out.println("ThreadLocal can cause memory issues with virtual threads:");

        final int THREAD_COUNT = 1000;

        System.out.println("Creating " + THREAD_COUNT + " virtual threads that use ThreadLocal");
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int id = i;
                executor.submit(() -> {
                    // Each virtual thread gets its own copy of ThreadLocal
                    threadLocal.set("Data for thread " + id);

                    // Use the thread-local data
                    String value = threadLocal.get();

                    if (id % 100 == 0) {
                        System.out.println(Thread.currentThread().getName() +
                                " using ThreadLocal value: " + value);
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Good practice: Remove thread-local data when done
                    // But this is easy to forget!
                    threadLocal.remove();

                    return "Task " + id + " completed";
                });
            }
        }

        System.out.println("\nBetter approach: Pass context explicitly instead of using ThreadLocal");
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                final String context = "Context for task " + i;
                executor.submit(() -> processWithContext(context));
            }
        }
    }

    // Helper method for context passing
    private static String processWithContext(String context) {
        System.out.println(Thread.currentThread().getName() + ": Processing with explicit context: " + context);
        // Process using the explicitly passed context
        return "Processed: " + context;
    }

    // Part 5: Use structured concurrency
    private static void demonstrateStructuredConcurrency() throws Exception {
        System.out.println("Use structured concurrency for better lifecycle management:");

        System.out.println("Using StructuredTaskScope for coordinated task execution:");

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Fork several tasks
            List<StructuredTaskScope.Subtask<String>> tasks = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                final int id = i;
                tasks.add(scope.fork(() -> {
                    if (id == 3) {
                        Thread.sleep(100);
                        // Simulate a failure in one task
                        throw new RuntimeException("Task " + id + " failed");
                    }

                    Thread.sleep(50);
                    return "Result from task " + id;
                }));
            }

            try {
                // Wait for all tasks and propagate exceptions
                scope.join();
                scope.throwIfFailed();

                // Process results if all succeeded
                System.out.println("All tasks completed successfully");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println("Task " + i + " result: " + tasks.get(i).get());
                }
            } catch (Exception e) {
                System.out.println("Caught exception: " + e.getMessage());
                System.out.println("Structured concurrency ensures all subtasks are cancelled");

                // Check the state of each task
                for (int i = 0; i < tasks.size(); i++) {
                    var state = tasks.get(i).state();
                    System.out.println("Task " + i + " state: " + state);
                }
            }
        }
    }
} 