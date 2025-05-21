package com.example.locking;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    // The shared resource: a cache with read-write access pattern
    private static final Map<String, String> cache = new HashMap<>();

    // ReadWriteLock allows multiple readers but exclusive writers
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    // Statistics counters
    private static int reads = 0;
    private static int writes = 0;
    private static final Object statsLock = new Object();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("ReadWriteLock Example");
        System.out.println("=====================");
        System.out.println("This example demonstrates how ReadWriteLock allows multiple readers");
        System.out.println("to access shared data simultaneously while ensuring exclusive access");
        System.out.println("for writers.\n");

        // Add some initial data to the cache
        initializeCache();

        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Submit a mix of reader and writer tasks
        for (int i = 0; i < 100; i++) {
            final int id = i;
            // 80% read operations, 20% write operations to simulate a read-heavy workload
            if (i % 5 == 0) {
                // Write operation
                executor.submit(() -> performWrite("Key-" + (id % 10), "Updated-" + id));
            } else {
                // Read operation
                executor.submit(() -> performRead("Key-" + (id % 10)));
            }
        }

        // Shutdown the executor and wait for tasks to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Print statistics
        System.out.println("\nOperation Statistics:");
        System.out.println("Total reads: " + reads);
        System.out.println("Total writes: " + writes);
        System.out.println("Final cache size: " + cache.size());
        System.out.println("Final cache contents:");
        for (Map.Entry<String, String> entry : cache.entrySet()) {
            System.out.println("  " + entry.getKey() + " = " + entry.getValue());
        }

        // Demonstrate lock downgrading (write → read)
        System.out.println("\nDemonstrating Lock Downgrading (write -> read):");
        demonstrateLockDowngrading("Key-0", "Downgraded-Value");

        System.out.println("\nExplanation:");
        System.out.println("ReadWriteLock provides two locks: a read lock and a write lock");
        System.out.println("- Read lock: Multiple threads can hold the read lock simultaneously");
        System.out.println("- Write lock: Only one thread can hold the write lock, and no read locks can be held");
        System.out.println("- This is optimal for read-heavy workloads where data is read frequently but updated rarely");
        System.out.println("- Lock downgrading (write -> read) is supported, but not upgrading (read -> write)");
    }

    private static void initializeCache() {
        for (int i = 0; i < 10; i++) {
            cache.put("Key-" + i, "Value-" + i);
        }
    }

    public static String performRead(String key) {
        // First try to read with read lock
        rwLock.readLock().lock();
        try {
            String value = cache.get(key);
            // Simulate some read processing time
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            incrementReads();
            System.out.println(Thread.currentThread().getName() +
                    " read: " + key + " = " + value);
            return value;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public static void performWrite(String key, String value) {
        // Get write lock for exclusive access
        rwLock.writeLock().lock();
        try {
            // Simulate some write processing time
            try {
                Thread.sleep(50);  // Writes are typically slower than reads
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            cache.put(key, value);
            incrementWrites();
            System.out.println(Thread.currentThread().getName() +
                    " wrote: " + key + " = " + value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // Demonstrate lock downgrading (write → read)
    public static void demonstrateLockDowngrading(String key, String newValue) {
        rwLock.writeLock().lock(); // Get write lock
        try {
            // Update the value
            cache.put(key, newValue);
            incrementWrites();
            System.out.println(Thread.currentThread().getName() +
                    " updated cache: " + key + " = " + newValue);

            // Downgrade by acquiring read lock before releasing write lock
            rwLock.readLock().lock();
            try {
                // Now we can release the write lock but still have read access
                rwLock.writeLock().unlock();
                System.out.println(Thread.currentThread().getName() +
                        " downgraded to read lock");

                // Read the value we just wrote (with only a read lock)
                String value = cache.get(key);
                incrementReads();
                System.out.println(Thread.currentThread().getName() +
                        " read with downgraded lock: " + key + " = " + value);
            } finally {
                rwLock.readLock().unlock();
            }
        } finally {
            // In case we didn't downgrade
            if (((ReentrantLock) rwLock.writeLock()).isHeldByCurrentThread()) {
                rwLock.writeLock().unlock();
            }
        }
    }

    private static void incrementReads() {
        synchronized (statsLock) {
            reads++;
        }
    }

    private static void incrementWrites() {
        synchronized (statsLock) {
            writes++;
        }
    }
} 