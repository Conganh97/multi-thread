package com.example.challenges;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class DeadlockPreventionExample {
    // For lock ordering approach
    private static final Object RESOURCE_A = new Object();
    private static final Object RESOURCE_B = new Object();
    
    // For lock timeout approach
    private static final Lock LOCK_A = new ReentrantLock();
    private static final Lock LOCK_B = new ReentrantLock();
    
    public static void main(String[] args) {
        System.out.println("Deadlock Prevention Example");
        System.out.println("==========================");
        
        System.out.println("1. Prevention using consistent lock ordering\n");
        demonstrateLockOrdering();
        
        System.out.println("\n\n2. Prevention using lock timeouts\n");
        demonstrateLockTimeout();
        
        System.out.println("\n\n3. Prevention using tryLock with global ordering\n");
        demonstrateGlobalOrdering();
    }
    
    // Method 1: Consistent lock ordering
    private static void demonstrateLockOrdering() {
        System.out.println("Both threads will acquire locks in the same order: A then B");
        
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1: Following lock ordering protocol");
            synchronized (RESOURCE_A) {
                System.out.println("Thread 1: Acquired lock A");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                synchronized (RESOURCE_B) {
                    System.out.println("Thread 1: Acquired lock B");
                    System.out.println("Thread 1: Working with both resources");
                }
            }
            System.out.println("Thread 1: Released all resources");
        });
        
        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2: Following lock ordering protocol");
            synchronized (RESOURCE_A) {
                System.out.println("Thread 2: Acquired lock A");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                synchronized (RESOURCE_B) {
                    System.out.println("Thread 2: Acquired lock B");
                    System.out.println("Thread 2: Working with both resources");
                }
            }
            System.out.println("Thread 2: Released all resources");
        });
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Method 2: Lock timeout
    private static void demonstrateLockTimeout() {
        System.out.println("Using explicit locks with timeouts to prevent indefinite waiting");
        
        Thread thread1 = new Thread(() -> {
            try {
                boolean gotBothLocks = false;
                
                while (!gotBothLocks) {
                    // Try to acquire first lock with timeout
                    boolean gotLockA = LOCK_A.tryLock(500, TimeUnit.MILLISECONDS);
                    
                    if (gotLockA) {
                        try {
                            System.out.println("Thread 1: Acquired lock A");
                            
                            // Try to acquire second lock with timeout
                            boolean gotLockB = LOCK_B.tryLock(500, TimeUnit.MILLISECONDS);
                            
                            if (gotLockB) {
                                try {
                                    // Got both locks, do work
                                    System.out.println("Thread 1: Acquired lock B");
                                    System.out.println("Thread 1: Working with both resources");
                                    gotBothLocks = true;
                                } finally {
                                    LOCK_B.unlock();
                                    System.out.println("Thread 1: Released lock B");
                                }
                            } else {
                                // Release first lock if couldn't get second
                                System.out.println("Thread 1: Couldn't get lock B, releasing lock A and retrying");
                            }
                        } finally {
                            if (!gotBothLocks) {
                                LOCK_A.unlock();
                            }
                        }
                    } else {
                        System.out.println("Thread 1: Couldn't get lock A, retrying");
                    }
                    
                    // Add a small random delay before retrying to reduce contention
                    if (!gotBothLocks) {
                        Thread.sleep((long) (Math.random() * 100));
                    }
                }
                
                // Release final lock
                LOCK_A.unlock();
                System.out.println("Thread 1: Released lock A");
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            try {
                boolean gotBothLocks = false;
                
                while (!gotBothLocks) {
                    // Thread 2 tries to acquire locks in opposite order to demonstrate prevention
                    boolean gotLockB = LOCK_B.tryLock(500, TimeUnit.MILLISECONDS);
                    
                    if (gotLockB) {
                        try {
                            System.out.println("Thread 2: Acquired lock B");
                            
                            boolean gotLockA = LOCK_A.tryLock(500, TimeUnit.MILLISECONDS);
                            
                            if (gotLockA) {
                                try {
                                    System.out.println("Thread 2: Acquired lock A");
                                    System.out.println("Thread 2: Working with both resources");
                                    gotBothLocks = true;
                                } finally {
                                    LOCK_A.unlock();
                                    System.out.println("Thread 2: Released lock A");
                                }
                            } else {
                                System.out.println("Thread 2: Couldn't get lock A, releasing lock B and retrying");
                            }
                        } finally {
                            if (!gotBothLocks) {
                                LOCK_B.unlock();
                            }
                        }
                    } else {
                        System.out.println("Thread 2: Couldn't get lock B, retrying");
                    }
                    
                    if (!gotBothLocks) {
                        Thread.sleep((long) (Math.random() * 100));
                    }
                }
                
                LOCK_B.unlock();
                System.out.println("Thread 2: Released lock B");
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Method 3: Global lock ordering based on object identity
    private static void demonstrateGlobalOrdering() {
        // Helper method to ensure consistent lock ordering by identity hash code
        class ResourceOrderer {
            public void acquireLocksInOrder(Object resource1, Object resource2, Runnable criticalSection) {
                Object firstLock, secondLock;
                
                // Order locks based on their hash codes
                if (System.identityHashCode(resource1) < System.identityHashCode(resource2)) {
                    firstLock = resource1;
                    secondLock = resource2;
                } else {
                    firstLock = resource2;
                    secondLock = resource1;
                }
                
                // Acquire locks in the determined order
                synchronized (firstLock) {
                    System.out.println(Thread.currentThread().getName() + ": Acquired first lock");
                    synchronized (secondLock) {
                        System.out.println(Thread.currentThread().getName() + ": Acquired second lock");
                        // Execute the critical section
                        criticalSection.run();
                    }
                }
            }
        }
        
        ResourceOrderer orderer = new ResourceOrderer();
        
        // Even though threads try to acquire resources in different orders,
        // the ResourceOrderer will enforce a consistent global ordering
        
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1: Using resource orderer to prevent deadlock");
            // Thread 1 wants to lock A then B
            orderer.acquireLocksInOrder(RESOURCE_A, RESOURCE_B, () -> {
                System.out.println("Thread 1: Working with both resources");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Thread 1: Released all resources");
        });
        
        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2: Using resource orderer to prevent deadlock");
            // Thread 2 wants to lock B then A (opposite order)
            orderer.acquireLocksInOrder(RESOURCE_B, RESOURCE_A, () -> {
                System.out.println("Thread 2: Working with both resources");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Thread 2: Released all resources");
        });
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\nExplanation:");
        System.out.println("To prevent deadlocks, we can use several strategies:");
        System.out.println("1. Lock ordering: Always acquire locks in the same order in all threads");
        System.out.println("2. Lock timeout: Use timed lock acquisition to avoid indefinite waiting");
        System.out.println("3. Global ordering: Use a system like resource hash codes to determine lock order");
        System.out.println("All these strategies help break at least one of the four conditions necessary for deadlock.");
    }
} 