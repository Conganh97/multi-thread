package com.example.challenges;

import java.util.concurrent.locks.ReentrantLock;

public class StarvationPreventionExample {
    // Use a fair lock to prevent starvation
    private static final ReentrantLock fairLock = new ReentrantLock(true);
    
    // For comparison, a non-fair lock
    private static final ReentrantLock nonFairLock = new ReentrantLock(false);
    
    public static void main(String[] args) {
        System.out.println("Starvation Prevention Example");
        System.out.println("============================");
        System.out.println("This example demonstrates how to prevent thread starvation");
        System.out.println("using fair locks, which service threads in FIFO order.\n");
        
        System.out.println("Part 1: Using a fair lock (prevents starvation)");
        System.out.println("------------------------------------------");
        demonstrateLock(fairLock);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\nPart 2: Using a non-fair lock (may cause starvation)");
        System.out.println("--------------------------------------------------");
        demonstrateLock(nonFairLock);
        
        System.out.println("\nExplanation:");
        System.out.println("A fair lock ensures that threads acquire the lock in the order");
        System.out.println("they requested it (First-In-First-Out), preventing any single");
        System.out.println("thread from being starved of access to the resource.");
        System.out.println("Other starvation prevention techniques include:");
        System.out.println("1. Bounded waiting: Ensuring no thread waits indefinitely");
        System.out.println("2. Priority aging: Gradually increasing priority of waiting threads");
        System.out.println("3. Resource partitioning: Dividing resources to ensure all threads get access");
    }
    
    private static void demonstrateLock(ReentrantLock lock) {
        // Create multiple threads that all compete for the same resource
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 2; j++) {
                    long startTime = System.currentTimeMillis();
                    
                    // Try to acquire the lock
                    lock.lock();
                    try {
                        long waitTime = System.currentTimeMillis() - startTime;
                        System.out.println("Thread " + threadId + 
                                          " acquired the lock after waiting " + 
                                          waitTime + "ms");
                        
                        // Simulate work
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                    
                    // Do some work outside the lock
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Thread-" + i);
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
} 