package com.example.locking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {
    private static final Lock lock = new ReentrantLock();
    private static int counter = 0;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("ReentrantLock Example");
        System.out.println("====================");
        System.out.println("This example demonstrates basic usage of ReentrantLock for thread synchronization.\n");
        
        // Create and start multiple threads using the lock
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                // Always put lock acquisition in a try block and release in a finally block
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " acquired the lock");
                    counter++;
                    
                    // Simulate some work
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    // Always release the lock in a finally block
                    lock.unlock();
                    System.out.println(Thread.currentThread().getName() + " released the lock");
                }
            });
            
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\nFinal counter value: " + counter);
        
        // Demonstrate reentrancy (same thread acquiring the lock multiple times)
        demonstrateReentrancy();
    }
    
    private static void demonstrateReentrancy() {
        System.out.println("\nDemonstrating lock reentrancy:");
        
        // First level of lock acquisition
        lock.lock();
        try {
            System.out.println("First level lock acquisition");
            
            // Second level of lock acquisition (reentrant)
            lock.lock();
            try {
                System.out.println("Second level lock acquisition (reentrant)");
                
                // Third level of lock acquisition (reentrant)
                lock.lock();
                try {
                    System.out.println("Third level lock acquisition (reentrant)");
                } finally {
                    lock.unlock();
                }
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
        
        System.out.println("All locks released");
        
        System.out.println("\nExplanation:");
        System.out.println("ReentrantLock provides the same mutual exclusion and memory visibility");
        System.out.println("guarantees as synchronized blocks but with additional features:");
        System.out.println("1. Reentrance: Same thread can acquire the lock multiple times");
        System.out.println("2. Explicit locking/unlocking with try-finally pattern");
        System.out.println("3. Better performance under high contention");
        System.out.println("4. Greater flexibility with features like timeouts and interruptibility");
    }
} 