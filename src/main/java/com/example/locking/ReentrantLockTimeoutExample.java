package com.example.locking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTimeoutExample {
    private static final Lock lock = new ReentrantLock();
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("ReentrantLock Timeout Example");
        System.out.println("============================");
        System.out.println("This example demonstrates ReentrantLock's timed and interruptible lock acquisition.\n");
        
        System.out.println("Part 1: Timed Lock Acquisition");
        System.out.println("----------------------------");
        demonstrateTimedLock();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 2: Interruptible Lock Acquisition");
        System.out.println("------------------------------------");
        demonstrateInterruptibleLock();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 3: Non-blocking tryLock");
        System.out.println("---------------------------");
        demonstrateTryLock();
        
        System.out.println("\nExplanation:");
        System.out.println("ReentrantLock provides several advanced features not available with synchronized:");
        System.out.println("1. Timed Lock Acquisition: Attempt to acquire a lock with a timeout");
        System.out.println("2. Interruptible Lock Acquisition: Allow thread to be interrupted while waiting");
        System.out.println("3. Non-blocking tryLock(): Try to acquire the lock without blocking");
        System.out.println("These features are valuable for preventing deadlocks and improving responsiveness.");
    }
    
    private static void demonstrateTimedLock() throws InterruptedException {
        // Thread that holds the lock for a long time
        Thread lockHolder = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("Lock holder acquired the lock and will hold it for 3 seconds");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                System.out.println("Lock holder releasing the lock");
                lock.unlock();
            }
        });
        
        // Thread that tries to acquire the lock with a timeout
        Thread timedWaiter = new Thread(() -> {
            try {
                System.out.println("Timed waiter attempting to acquire lock with 1 second timeout");
                boolean acquired = lock.tryLock(1, TimeUnit.SECONDS);
                
                if (acquired) {
                    try {
                        System.out.println("Timed waiter acquired the lock (unexpected!)");
                    } finally {
                        lock.unlock();
                    }
                } else {
                    System.out.println("Timed waiter could not acquire the lock within timeout");
                }
                
                // Try again with a longer timeout
                System.out.println("Timed waiter trying again with 5 second timeout");
                acquired = lock.tryLock(5, TimeUnit.SECONDS);
                
                if (acquired) {
                    try {
                        System.out.println("Timed waiter acquired the lock on second attempt");
                    } finally {
                        lock.unlock();
                    }
                } else {
                    System.out.println("Timed waiter could not acquire the lock even with longer timeout");
                }
            } catch (InterruptedException e) {
                System.out.println("Timed waiter was interrupted while waiting for lock");
            }
        });
        
        lockHolder.start();
        // Give lock holder a chance to acquire the lock
        Thread.sleep(100);
        timedWaiter.start();
        
        lockHolder.join();
        timedWaiter.join();
    }
    
    private static void demonstrateInterruptibleLock() throws InterruptedException {
        // Thread that holds the lock
        Thread lockHolder = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("Lock holder acquired the lock and will hold it for 3 seconds");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                System.out.println("Lock holder releasing the lock");
                lock.unlock();
            }
        });
        
        // Thread that tries to acquire the lock but can be interrupted
        Thread interruptibleWaiter = new Thread(() -> {
            System.out.println("Interruptible waiter attempting to acquire lock (interruptibly)");
            try {
                lock.lockInterruptibly();
                try {
                    System.out.println("Interruptible waiter acquired the lock (this shouldn't happen)");
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println("Interruptible waiter was interrupted while waiting for lock");
            }
        });
        
        lockHolder.start();
        // Give lock holder a chance to acquire the lock
        Thread.sleep(100);
        interruptibleWaiter.start();
        
        // Interrupt the waiter after 1 second
        Thread.sleep(1000);
        System.out.println("Main thread interrupting the interruptible waiter");
        interruptibleWaiter.interrupt();
        
        lockHolder.join();
        interruptibleWaiter.join();
    }
    
    private static void demonstrateTryLock() throws InterruptedException {
        // Thread that holds the lock
        Thread lockHolder = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("Lock holder acquired the lock and will hold it for 2 seconds");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                System.out.println("Lock holder releasing the lock");
                lock.unlock();
            }
        });
        
        // Thread that tries to acquire the lock without blocking
        Thread nonBlockingWaiter = new Thread(() -> {
            System.out.println("Non-blocking waiter attempting to acquire lock without blocking");
            boolean acquired = lock.tryLock();
            
            if (acquired) {
                try {
                    System.out.println("Non-blocking waiter acquired the lock (unexpected!)");
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println("Non-blocking waiter could not acquire the lock, continuing with other work");
            }
            
            // Try again after the lock holder should have released the lock
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("Non-blocking waiter trying again");
            acquired = lock.tryLock();
            
            if (acquired) {
                try {
                    System.out.println("Non-blocking waiter acquired the lock on second attempt");
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println("Non-blocking waiter could not acquire the lock (unexpected)");
            }
        });
        
        lockHolder.start();
        // Give lock holder a chance to acquire the lock
        Thread.sleep(100);
        nonBlockingWaiter.start();
        
        lockHolder.join();
        nonBlockingWaiter.join();
    }
} 