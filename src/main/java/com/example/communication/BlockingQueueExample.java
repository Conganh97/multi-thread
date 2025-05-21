package com.example.communication;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("BlockingQueue Example");
        System.out.println("====================");
        System.out.println("This example demonstrates BlockingQueue for implementing");
        System.out.println("the producer-consumer pattern without explicit locks.\n");
        
        // Create a bounded blocking queue with capacity of 5
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
        
        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    String item = "Item-" + i;
                    
                    // put() will block if queue is full
                    queue.put(item);
                    System.out.println("Produced: " + item + ", Queue size: " + queue.size());
                    
                    // Simulate varying production rates
                    Thread.sleep((long) (Math.random() * 100));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");
        
        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    // Wait for up to 2 seconds for an item
                    String item = queue.poll(2, TimeUnit.SECONDS);
                    
                    if (item == null) {
                        // No more items available within timeout
                        System.out.println("Consumer timed out, assuming production complete");
                        break;
                    }
                    
                    System.out.println("Consumed: " + item + ", Queue size: " + queue.size());
                    
                    // Simulate varying consumption rates
                    Thread.sleep((long) (Math.random() * 200));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");
        
        // Start the threads
        producer.start();
        consumer.start();
        
        // Wait for completion
        producer.join();
        consumer.join();
        
        System.out.println("\nDemonstrating different BlockingQueue implementations:");
        demonstrateQueueTypes();
        
        System.out.println("\nExplanation:");
        System.out.println("BlockingQueue provides a thread-safe queue with blocking operations:");
        System.out.println("- put(e): Adds an element, blocking if the queue is full");
        System.out.println("- take(): Retrieves and removes an element, blocking if the queue is empty");
        System.out.println("- offer(e, time, unit): Adds an element, waiting up to the specified time if needed");
        System.out.println("- poll(time, unit): Retrieves and removes an element, waiting up to the specified time if needed");
        System.out.println("Java provides several BlockingQueue implementations for different use cases.");
    }
    
    private static void demonstrateQueueTypes() {
        System.out.println("Java provides several BlockingQueue implementations:");
        
        System.out.println("1. ArrayBlockingQueue - A bounded queue backed by an array");
        // Elements are ordered FIFO
        // Must specify capacity at creation time
        // Optionally supports fairness (FIFO access for threads)
        BlockingQueue<String> arrayQueue = new ArrayBlockingQueue<>(10, true); // fair
        
        System.out.println("2. LinkedBlockingQueue - An optionally bounded queue backed by linked nodes");
        // Elements are ordered FIFO
        // Optional capacity bound (defaults to Integer.MAX_VALUE if not specified)
        // Often higher throughput than ArrayBlockingQueue but less predictable performance
        BlockingQueue<String> linkedQueue = new java.util.concurrent.LinkedBlockingQueue<>(10);
        
        System.out.println("3. PriorityBlockingQueue - An unbounded priority queue");
        // Elements are ordered by priority (natural ordering or Comparator)
        // No capacity limit (grows as needed)
        BlockingQueue<String> priorityQueue = new java.util.concurrent.PriorityBlockingQueue<>();
        
        System.out.println("4. DelayQueue - A queue where elements can only be taken when their delay has expired");
        // Elements must implement Delayed interface
        // Good for scheduling tasks
        BlockingQueue<java.util.concurrent.Delayed> delayQueue = new java.util.concurrent.DelayQueue<>();
        
        System.out.println("5. SynchronousQueue - A queue with no internal capacity");
        // Each put() must wait for a corresponding take()
        // Good for direct handoffs between threads
        BlockingQueue<String> synchronousQueue = new java.util.concurrent.SynchronousQueue<>();
        
        // Example of using multiple producers and consumers with a queue
        demonstrateMultipleProducersConsumers();
    }
    
    private static void demonstrateMultipleProducersConsumers() {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        
        System.out.println("\nDemonstrating multiple producers and consumers...");
        
        // Create multiple producer threads
        for (int i = 0; i < 3; i++) {
            final int producerId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < 5; j++) {
                        String item = "P" + producerId + "-" + j;
                        queue.put(item);
                        System.out.println("Producer " + producerId + " added: " + item);
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Producer-" + i).start();
        }
        
        // Create multiple consumer threads
        for (int i = 0; i < 2; i++) {
            final int consumerId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < 7; j++) {
                        String item = queue.take();
                        System.out.println("Consumer " + consumerId + " processed: " + item);
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Consumer-" + i).start();
        }
        
        // No need to join here - this is just a demonstration
    }
} 