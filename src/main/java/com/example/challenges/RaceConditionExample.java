package com.example.challenges;

import java.util.concurrent.atomic.AtomicInteger;

public class RaceConditionExample {
    // Unsafe counter without synchronization
    private static int unsafeCounter = 0;
    
    // Safe counter using AtomicInteger
    private static AtomicInteger safeCounter = new AtomicInteger(0);
    
    // Safe counter using synchronization
    private static int synchronizedCounter = 0;
    private static final Object lock = new Object();
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Race Condition Example");
        System.out.println("=====================");
        
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // 1. Unsafe increment - prone to race conditions
                    unsafeCounter++; // This is a read-modify-write operation
                    
                    // 2. Safe increment using AtomicInteger
                    safeCounter.incrementAndGet(); // Atomic operation
                    
                    // 3. Safe increment using synchronization
                    synchronized (lock) {
                        synchronizedCounter++;
                    }
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Expected count: " + (10 * 1000));
        System.out.println("Unsafe counter: " + unsafeCounter);
        System.out.println("Safe counter (atomic): " + safeCounter.get());
        System.out.println("Safe counter (synchronized): " + synchronizedCounter);
        
        System.out.println("\nExplanation:");
        System.out.println("Race conditions occur when multiple threads access and modify");
        System.out.println("shared data simultaneously, leading to unpredictable results.");
        System.out.println("The unsafe counter is likely less than expected due to lost updates.");
        System.out.println("Both atomic variables and synchronization solve this problem.");
    }
} 