package com.example.virtualthread;

import java.util.concurrent.*;

public class StructuredConcurrencyExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Structured Concurrency Example");
        System.out.println("=============================");
        System.out.println("This example demonstrates structured concurrency with virtual threads,");
        System.out.println("which ensures that tasks started in a scope complete before leaving that scope.\n");

        try {
            System.out.println("Part 1: Traditional Approach vs Structured Approach");
            traditionalVsStructured();

            Thread.sleep(1000);

            System.out.println("\nPart 2: ShutdownOnFailure Strategy");
            shutdownOnFailure();

            Thread.sleep(1000);

            System.out.println("\nPart 3: ShutdownOnSuccess Strategy");
            shutdownOnSuccess();

            Thread.sleep(1000);

            System.out.println("\nPart 4: Exception Handling");
            try {
                exceptionHandling();
            } catch (ExecutionException e) {
                System.out.println("Main thread caught ExecutionException: " + e.getCause().getMessage());
            }

        } catch (Exception e) {
            System.err.println("Example Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nExplanation:");
        System.out.println("Structured Concurrency Benefits:");
        System.out.println("- Improved reliability: Ensures tasks don't leak");
        System.out.println("- Better error propagation: Propagates exceptions from subtasks");
        System.out.println("- Cleaner lifecycle management: Ties subtask lifetime to a parent scope");
        System.out.println("- Simplified cancellation: Cancels all subtasks when the scope is cancelled");
        System.out.println("- Easier reasoning about concurrent code: More structured approach");
    }

    // Part 1: Compare traditional approach with structured approach
    private static void traditionalVsStructured()
            throws InterruptedException, ExecutionException {
        System.out.println("Traditional approach (using ExecutorService and Future):");
        traditionalApproach();

        System.out.println("\nStructured approach (using StructuredTaskScope):");
        structuredApproach();
    }

    // Traditional approach using ExecutorService and Future
    private static void traditionalApproach() throws InterruptedException {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> userFuture = executor.submit(() -> fetchUser(1));
            Future<String> orderFuture = executor.submit(() -> fetchOrder(1));

            try {
                String user = userFuture.get();
                String order = orderFuture.get();
                System.out.println("Combined result: " + user + " - " + order);
            } catch (ExecutionException e) {
                System.out.println("Error: " + e.getCause().getMessage());
                // Note: if one fails, we should cancel the other, but this is often forgotten
                userFuture.cancel(true);
                orderFuture.cancel(true);
            }
        }
    }

    // Structured approach using StructuredTaskScope
    private static void structuredApproach()
            throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            StructuredTaskScope.Subtask<String> userTask = scope.fork(() -> fetchUser(1));
            StructuredTaskScope.Subtask<String> orderTask = scope.fork(() -> fetchOrder(1));

            // Wait for both tasks to complete or one to fail
            scope.join();
            // Propagate exceptions
            scope.throwIfFailed();

            // Extract results
            String user = userTask.get();
            String order = orderTask.get();
            System.out.println("Combined result: " + user + " - " + order);
        }
        // When scope closes, all subtasks are guaranteed to be complete
    }

    // Part 2: Demonstrate ShutdownOnFailure strategy
    private static void shutdownOnFailure() throws InterruptedException {
        System.out.println("Using ShutdownOnFailure strategy:");
        System.out.println("- All tasks are started");
        System.out.println("- If any task fails, all others are cancelled");
        System.out.println("- Useful for tasks that depend on all operations succeeding");

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Fork multiple tasks
            StructuredTaskScope.Subtask<String> task1 = scope.fork(() -> {
                System.out.println("Task 1 starting");
                Thread.sleep(500);
                System.out.println("Task 1 completed successfully");
                return "Result 1";
            });

            StructuredTaskScope.Subtask<String> task2 = scope.fork(() -> {
                System.out.println("Task 2 starting");
                Thread.sleep(300);
                // This task will fail
                throw new RuntimeException("Task 2 failed!");
            });

            StructuredTaskScope.Subtask<String> task3 = scope.fork(() -> {
                System.out.println("Task 3 starting");
                // This sleep is longer, but task will be interrupted
                try {
                    Thread.sleep(2000);
                    System.out.println("Task 3 completed successfully");
                    return "Result 3";
                } catch (InterruptedException e) {
                    System.out.println("Task 3 was interrupted");
                    throw e;
                }
            });

            // Join all tasks
            scope.join();

            // Check if any tasks failed
            try {
                scope.throwIfFailed();
                System.out.println("All tasks succeeded");
            } catch (ExecutionException e) {
                System.out.println("At least one task failed: " + e.getCause().getMessage());
                System.out.println("Task 1 state: " + (task1.state() == StructuredTaskScope.Subtask.State.SUCCESS ?
                        "SUCCESS" : "FAILED or CANCELLED"));
                System.out.println("Task 2 state: " + (task2.state() == StructuredTaskScope.Subtask.State.SUCCESS ?
                        "SUCCESS" : "FAILED or CANCELLED"));
                System.out.println("Task 3 state: " + (task3.state() == StructuredTaskScope.Subtask.State.SUCCESS ?
                        "SUCCESS" : "FAILED or CANCELLED"));
            }
        }
    }

    // Part 3: Demonstrate ShutdownOnSuccess strategy
    private static void shutdownOnSuccess() throws InterruptedException {
        System.out.println("Using ShutdownOnSuccess strategy:");
        System.out.println("- All tasks are started");
        System.out.println("- When any task succeeds, all others are cancelled");
        System.out.println("- Useful for finding the first successful result (e.g., querying multiple services)");

        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            // Fork multiple tasks
            scope.fork(() -> {
                System.out.println("Service 1 starting (will succeed after 700ms)");
                Thread.sleep(700);
                System.out.println("Service 1 succeeded");
                return "Result from Service 1";
            });

            scope.fork(() -> {
                System.out.println("Service 2 starting (will succeed after 300ms)");
                Thread.sleep(300);
                System.out.println("Service 2 succeeded");
                return "Result from Service 2";
            });

            scope.fork(() -> {
                System.out.println("Service 3 starting (will take 2000ms)");
                try {
                    Thread.sleep(2000);
                    System.out.println("Service 3 succeeded (but too late)");
                    return "Result from Service 3";
                } catch (InterruptedException e) {
                    System.out.println("Service 3 was cancelled because another service succeeded first");
                    throw e;
                }
            });

            // Wait for first successful result or all tasks to fail
            scope.join();

            try {
                // Get the successful result
                String result = scope.result();
                System.out.println("Got first successful result: " + result);
            } catch (ExecutionException e) {
                System.out.println("All services failed: " + e.getCause().getMessage());
            }
        }
    }

    // Part 4: Demonstrate exception handling with structured concurrency
    private static void exceptionHandling() throws InterruptedException, ExecutionException {
        System.out.println("Demonstrating exception handling with structured concurrency:");

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Fork a task that will succeed
            scope.fork(() -> {
                System.out.println("Task A running");
                Thread.sleep(200);
                return "Success";
            });

            // Fork a task that will throw an exception
            scope.fork(() -> {
                System.out.println("Task B running");
                Thread.sleep(100);
                throw new RuntimeException("Simulated error in Task B");
            });

            // Join and process exceptions
            scope.join();

            // This will throw an ExecutionException wrapping the RuntimeException from Task B
            System.out.println("Calling throwIfFailed() - this will throw an exception...");
            scope.throwIfFailed();

            // This code will never be reached
            System.out.println("This line won't be executed");
        }
    }

    // Simulated service methods
    private static String fetchUser(int userId) throws InterruptedException {
        System.out.println("Fetching user " + userId);
        Thread.sleep(200);
        return "User" + userId;
    }

    private static String fetchOrder(int orderId) throws InterruptedException {
        System.out.println("Fetching order " + orderId);
        Thread.sleep(300);
        return "Order" + orderId;
    }
} 