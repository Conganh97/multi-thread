package com.example.communication;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("CyclicBarrier Example");
        System.out.println("====================");
        System.out.println("This example demonstrates CyclicBarrier for coordinating multiple threads");
        System.out.println("to wait for each other at synchronization points.\n");
        
        System.out.println("Example 1: Basic CyclicBarrier usage");
        basicExample();
        
        Thread.sleep(1000);
        
        System.out.println("\nExample 2: Multi-phase calculation with barrier action");
        multiPhaseExample();
        
        Thread.sleep(1000);
        
        System.out.println("\nExample 3: Demonstrating barrier reuse (cyclic nature)");
        reusableBarrierExample();
        
        System.out.println("\nExplanation:");
        System.out.println("CyclicBarrier is a synchronization aid that allows a set of threads to wait for");
        System.out.println("each other to reach a common barrier point. Key features:");
        System.out.println("- Reusable: Automatically resets after all threads arrive");
        System.out.println("- Barrier action: Can execute a runnable when the barrier trips");
        System.out.println("- Thread coordination: All threads must reach the barrier to proceed");
        System.out.println("- Common uses: Algorithms with synchronous phases, concurrent simulations");
        System.out.println("- Differs from CountDownLatch: Reusable and all participating threads must wait");
    }
    
    // Example 1: Basic usage of CyclicBarrier
    private static void basicExample() throws InterruptedException {
        final int NUM_THREADS = 3;
        
        // Create a CyclicBarrier with a barrier action
        CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS, () -> {
            System.out.println("All threads reached the barrier, executing barrier action!");
        });
        
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            final int workerId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Worker " + workerId + " is doing the first part of work");
                    
                    // Simulate some work
                    Thread.sleep(1000 + (int)(Math.random() * 1000));
                    
                    System.out.println("Worker " + workerId + " reached the barrier");
                    
                    // Wait for all threads to reach this point
                    barrier.await();
                    
                    System.out.println("Worker " + workerId + " continuing after the barrier");
                } catch (InterruptedException | BrokenBarrierException e) {
                    System.err.println("Worker " + workerId + " interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Cleanup
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
    
    // Example 2: Using CyclicBarrier for multi-phase calculations
    private static void multiPhaseExample() throws InterruptedException {
        final int NUM_WORKERS = 3;
        final int ROWS = 6;
        final int COLUMNS = 8;
        
        // Create a 2D array for a matrix
        final double[][] matrix = new double[ROWS][COLUMNS];
        
        // Initialize the matrix with random values
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                matrix[i][j] = Math.random() * 10;
            }
        }
        
        // Shared array to store row sums
        final double[] rowSums = new double[ROWS];
        
        // Create a barrier that executes after each phase
        CyclicBarrier barrier = new CyclicBarrier(NUM_WORKERS, () -> {
            System.out.println("Phase complete, checking intermediate results...");
        });
        
        ExecutorService executor = Executors.newFixedThreadPool(NUM_WORKERS);
        
        // Assign rows to workers
        for (int i = 0; i < NUM_WORKERS; i++) {
            final int workerId = i;
            executor.submit(() -> {
                try {
                    // Phase 1: Calculate row sums
                    for (int row = workerId; row < ROWS; row += NUM_WORKERS) {
                        double sum = 0;
                        for (int col = 0; col < COLUMNS; col++) {
                            sum += matrix[row][col];
                        }
                        rowSums[row] = sum;
                        System.out.printf("Worker %d calculated sum for row %d: %.2f%n", workerId, row, sum);
                    }
                    
                    // Wait for all workers to complete Phase 1
                    System.out.println("Worker " + workerId + " waiting at barrier for Phase 1");
                    barrier.await();
                    
                    // Phase 2: Calculate normalized values
                    double totalSum = 0;
                    for (double sum : rowSums) {
                        totalSum += sum;
                    }
                    
                    for (int row = workerId; row < ROWS; row += NUM_WORKERS) {
                        double percentContribution = (rowSums[row] / totalSum) * 100;
                        System.out.printf("Worker %d: Row %d contributes %.2f%% to total%n", 
                                workerId, row, percentContribution);
                    }
                    
                    // Wait for all workers to complete Phase 2
                    System.out.println("Worker " + workerId + " waiting at barrier for Phase 2");
                    barrier.await();
                    
                    System.out.println("Worker " + workerId + " completed all phases");
                    
                } catch (InterruptedException | BrokenBarrierException e) {
                    System.err.println("Worker " + workerId + " failed: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Cleanup
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
    
    // Example 3: Demonstrating the reusable nature of CyclicBarrier
    private static void reusableBarrierExample() throws InterruptedException {
        final int NUM_THREADS = 3;
        final int ITERATIONS = 3;
        
        CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS, () -> {
            System.out.println("\nAll threads reached iteration checkpoint!");
        });
        
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int iteration = 0; iteration < ITERATIONS; iteration++) {
                        System.out.println("Thread " + threadId + " starting iteration " + iteration);
                        
                        // Simulate some work
                        Thread.sleep(500 + (int)(Math.random() * 500));
                        
                        System.out.println("Thread " + threadId + " waiting at barrier in iteration " + iteration);
                        
                        // Wait for all threads to reach this point
                        int arrivalIndex = barrier.await();
                        
                        System.out.println("Thread " + threadId + " passed barrier " + 
                                "(arrival index: " + arrivalIndex + ") in iteration " + iteration);
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    System.err.println("Thread " + threadId + " interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        
        // No need for executor here
        // Just wait for threads to complete their iterations
        Thread.sleep(10000);
    }
} 