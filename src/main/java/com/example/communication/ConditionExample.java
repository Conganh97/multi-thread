package com.example.communication;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionExample {
    private final Lock lock = new ReentrantLock();
    private final Condition dataAvailable = lock.newCondition();
    private final Condition spaceAvailable = lock.newCondition();
    private final String[] buffer = new String[10];
    private int count = 0, putIndex = 0, takeIndex = 0;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Condition Variables Example");
        System.out.println("==========================");
        System.out.println("This example demonstrates Condition variables with a bounded buffer");
        System.out.println("implementation where producers wait when the buffer is full and");
        System.out.println("consumers wait when the buffer is empty.\n");
        
        // Create the buffer
        ConditionExample buffer = new ConditionExample();
        
        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    String item = "Item-" + i;
                    System.out.println("Producing: " + item);
                    buffer.put(item);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Consumer thread - consumes more slowly than producer
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    String item = buffer.take();
                    System.out.println("Consumed: " + item);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Start the threads
        producer.start();
        Thread.sleep(100); // Give producer a head start
        consumer.start();
        
        // Wait for completion
        producer.join();
        consumer.join();
        
        System.out.println("\nDemonstrating await with timeout:");
        demonstrateTimeout();
        
        System.out.println("\nExplanation:");
        System.out.println("Condition variables provide more flexible thread coordination than wait/notify:");
        System.out.println("- One lock can have multiple conditions (wait sets)");
        System.out.println("- await() is similar to wait() but for condition variables");
        System.out.println("- signal() is similar to notify() but for condition variables");
        System.out.println("- signalAll() is similar to notifyAll() but for condition variables");
        System.out.println("- Conditions support timed waits (await with timeout)");
        System.out.println("- FIFO (first-in-first-out) wait queues when using fair locks");
    }
    
    // Put an item into the buffer
    public void put(String item) throws InterruptedException {
        lock.lock();
        try {
            // Wait until space is available
            while (count == buffer.length) {
                System.out.println("Buffer full, producer waiting...");
                spaceAvailable.await();
            }
            
            // Add item to buffer
            buffer[putIndex] = item;
            putIndex = (putIndex + 1) % buffer.length;
            count++;
            
            System.out.println("Added to buffer: " + item + ", count: " + count);
            
            // Signal to consumer that data is available
            dataAvailable.signal();
        } finally {
            lock.unlock();
        }
    }
    
    // Take an item from the buffer
    public String take() throws InterruptedException {
        lock.lock();
        try {
            // Wait until data is available
            while (count == 0) {
                System.out.println("Buffer empty, consumer waiting...");
                dataAvailable.await();
            }
            
            // Remove item from buffer
            String item = buffer[takeIndex];
            takeIndex = (takeIndex + 1) % buffer.length;
            count--;
            
            System.out.println("Removed from buffer: " + item + ", count: " + count);
            
            // Signal to producer that space is available
            spaceAvailable.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
    
    private static void demonstrateTimeout() throws InterruptedException {
        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        
        Thread waiter = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("Waiter: Waiting with 2 second timeout...");
                boolean notTimedOut = condition.await(2, java.util.concurrent.TimeUnit.SECONDS);
                if (notTimedOut) {
                    System.out.println("Waiter: Condition was signaled!");
                } else {
                    System.out.println("Waiter: Timed out waiting for condition!");
                }
            } catch (InterruptedException e) {
                System.out.println("Waiter: Interrupted while waiting");
            } finally {
                lock.unlock();
            }
        });
        
        waiter.start();
        
        // Simulate a decision whether to signal or not
        Thread.sleep(1000);
        
        // Randomly decide to signal or let it timeout
        boolean shouldSignal = Math.random() < 0.5;
        if (shouldSignal) {
            lock.lock();
            try {
                System.out.println("Main: Signaling condition");
                condition.signal();
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("Main: Not signaling, will let waiter timeout");
        }
        
        waiter.join();
    }
} 