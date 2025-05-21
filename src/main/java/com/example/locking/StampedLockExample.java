package com.example.locking;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class StampedLockExample {
    // A point class with mutable x,y coordinates
    static class Point {
        private double x, y;
        private final StampedLock lock = new StampedLock();
        
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        // Write method - exclusive access
        public void move(double deltaX, double deltaY) {
            // Acquire write lock
            long stamp = lock.writeLock();
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        
        // Read method with normal read lock
        public double distanceFromOrigin() {
            long stamp = lock.readLock();
            try {
                return Math.sqrt(x * x + y * y);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        
        // Optimistic read method
        public double distanceFromOriginOptimistic() {
            // Try optimistic read first
            long stamp = lock.tryOptimisticRead();
            double currentX = x;
            double currentY = y;
            
            // Check if the stamp is still valid (no writes occurred)
            if (!lock.validate(stamp)) {
                // Optimistic read failed, fall back to regular read lock
                stamp = lock.readLock();
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    lock.unlockRead(stamp);
                }
            }
            
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }
        
        // Convert read lock to write lock (upgrade)
        public void moveIfAtOrigin(double newX, double newY) {
            // Read lock - optimistic first
            long stamp = lock.tryOptimisticRead();
            double currentX = x;
            double currentY = y;
            
            // Check if the point is at origin (0,0) and validate stamp
            if ((currentX != 0.0 || currentY != 0.0) || !lock.validate(stamp)) {
                // Need to check again with read lock
                stamp = lock.readLock();
                try {
                    currentX = x;
                    currentY = y;
                    if (currentX != 0.0 || currentY != 0.0) {
                        // Not at origin, return without changing
                        return;
                    }
                    
                    // Try to convert read lock to write lock
                    long writeStamp = lock.tryConvertToWriteLock(stamp);
                    if (writeStamp != 0L) {
                        // Conversion successful
                        stamp = writeStamp;
                        x = newX;
                        y = newY;
                    } else {
                        // Could not upgrade - release read lock and acquire write lock
                        lock.unlockRead(stamp);
                        stamp = lock.writeLock();
                        try {
                            // Check again in case another thread changed the values
                            if (x == 0.0 && y == 0.0) {
                                x = newX;
                                y = newY;
                            }
                        } finally {
                            lock.unlockWrite(stamp);
                            return;
                        }
                    }
                } finally {
                    // Only unlock if we still have a read lock
                    if (StampedLock.isReadLockStamp(stamp)) {
                        lock.unlockRead(stamp);
                    } else if (StampedLock.isWriteLockStamp(stamp)) {
                        lock.unlockWrite(stamp);
                    }
                }
            } else if (currentX == 0.0 && currentY == 0.0) {
                // We found the point at origin with optimistic read, now we need write lock
                stamp = lock.writeLock();
                try {
                    // Check again to be sure
                    if (x == 0.0 && y == 0.0) {
                        x = newX;
                        y = newY;
                    }
                } finally {
                    lock.unlockWrite(stamp);
                }
            }
        }
        
        @Override
        public String toString() {
            long stamp = lock.readLock();
            try {
                return "Point(" + x + ", " + y + ")";
            } finally {
                lock.unlockRead(stamp);
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("StampedLock Example");
        System.out.println("===================");
        System.out.println("This example demonstrates StampedLock with three modes:");
        System.out.println("1. Write lock: exclusive access");
        System.out.println("2. Read lock: shared access");
        System.out.println("3. Optimistic read: allows reading without locking\n");
        
        // Create a point at origin
        Point point = new Point(0.0, 0.0);
        
        // Create a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // Run a mix of readers and writers
        for (int i = 0; i < 5; i++) {
            // Writers - move the point
            executor.submit(() -> {
                for (int j = 0; j < 3; j++) {
                    double deltaX = ThreadLocalRandom.current().nextDouble(-10, 10);
                    double deltaY = ThreadLocalRandom.current().nextDouble(-10, 10);
                    
                    point.move(deltaX, deltaY);
                    System.out.println(Thread.currentThread().getName() + 
                                      " moved point by (" + deltaX + ", " + deltaY + 
                                      ") to " + point);
                    
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            
            // Readers - using normal read lock
            executor.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    double distance = point.distanceFromOrigin();
                    System.out.println(Thread.currentThread().getName() + 
                                      " read distance (read lock): " + distance);
                    
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            
            // Optimistic readers
            executor.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    double distance = point.distanceFromOriginOptimistic();
                    System.out.println(Thread.currentThread().getName() + 
                                      " read distance (optimistic): " + distance);
                    
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Also test lock conversion
        executor.submit(() -> {
            point.moveIfAtOrigin(100.0, 100.0);
            System.out.println(Thread.currentThread().getName() + 
                              " tried to move point if at origin: " + point);
        });
        
        // Shutdown the executor
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        System.out.println("\nFinal point state: " + point);
        
        System.out.println("\nExplanation:");
        System.out.println("StampedLock offers three modes of locking:");
        System.out.println("1. Write lock: Similar to exclusive lock in ReentrantLock");
        System.out.println("2. Read lock: Similar to shared lock in ReadWriteLock");
        System.out.println("3. Optimistic read: A new mode that doesn't block writers");
        System.out.println("   - Optimistic reading allows readers to check if data");
        System.out.println("     has been modified by a writer since reading began");
        System.out.println("   - If not modified, proceed without locking, otherwise");
        System.out.println("     fall back to regular read lock");
        System.out.println("StampedLock is NOT reentrant and does not support conditions.");
        System.out.println("It provides better throughput than ReentrantReadWriteLock");
        System.out.println("in high-contention scenarios with frequent reads.");
    }
} 