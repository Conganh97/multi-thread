package com.example.lockfree;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class CASExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Compare-And-Swap (CAS) Operations Example");
        System.out.println("=======================================");
        System.out.println("This example demonstrates CAS operations, which are the foundation");
        System.out.println("of lock-free algorithms in Java's atomic classes.\n");
        
        System.out.println("Part 1: Implementing a lock-free counter with CAS");
        casCounter();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 2: CAS-based stack implementation");
        casStack();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 3: Java's internal CAS mechanisms");
        internalCAS();
        
        System.out.println("\nExplanation:");
        System.out.println("Compare-And-Swap (CAS) is an atomic operation used in concurrent algorithms:");
        System.out.println("- Atomically compares current value to expected value");
        System.out.println("- If they match, updates to a new value and returns true");
        System.out.println("- If they don't match, leaves value unchanged and returns false");
        System.out.println("- Implemented using CPU-specific instructions like CMPXCHG");
        System.out.println("- Forms the basis for lock-free and wait-free algorithms");
        System.out.println("- Used internally by all Java's atomic classes");
    }
    
    // Part 1: Implementing a counter using CAS
    private static void casCounter() throws InterruptedException {
        // A class that implements a counter using CAS
        class CASCounter {
            private AtomicReference<Integer> value = new AtomicReference<>(0);
            
            public int get() {
                return value.get();
            }
            
            public int incrementAndGet() {
                while (true) {
                    Integer current = value.get();
                    Integer next = current + 1;
                    if (value.compareAndSet(current, next)) {
                        return next;
                    }
                    // If we get here, someone else updated the value
                    // So we try again with the new current value
                }
            }
            
            public int addAndGet(int delta) {
                while (true) {
                    Integer current = value.get();
                    Integer next = current + delta;
                    if (value.compareAndSet(current, next)) {
                        return next;
                    }
                }
            }
        }
        
        // Test the CAS counter with multiple threads
        final CASCounter counter = new CASCounter();
        int numThreads = 5;
        int incrementsPerThread = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.incrementAndGet();
                }
            });
        }
        
        // Wait for all tasks to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        System.out.println("Expected count: " + (numThreads * incrementsPerThread));
        System.out.println("Actual count: " + counter.get());
        System.out.println("Our CAS counter is thread-safe without using locks!");
    }
    
    // Part 2: Implementing a lock-free stack using CAS
    private static void casStack() throws InterruptedException {
        // A class that implements a lock-free stack using CAS
        class Node<T> {
            final T item;
            Node<T> next;
            
            Node(T item) {
                this.item = item;
            }
        }
        
        class CASStack<T> {
            private AtomicReference<Node<T>> top = new AtomicReference<>();
            
            public void push(T item) {
                Node<T> newHead = new Node<>(item);
                Node<T> oldHead;
                do {
                    oldHead = top.get();
                    newHead.next = oldHead;
                } while (!top.compareAndSet(oldHead, newHead));
            }
            
            public T pop() {
                Node<T> oldHead;
                Node<T> newHead;
                do {
                    oldHead = top.get();
                    if (oldHead == null) {
                        return null;
                    }
                    newHead = oldHead.next;
                } while (!top.compareAndSet(oldHead, newHead));
                return oldHead.item;
            }
            
            public boolean isEmpty() {
                return top.get() == null;
            }
        }
        
        // Test the CAS stack with multiple threads
        final CASStack<Integer> stack = new CASStack<>();
        
        // Push some initial items
        for (int i = 1; i <= 5; i++) {
            stack.push(i);
            System.out.println("Pushed: " + i);
        }
        
        // Create multiple producer and consumer threads
        int numProducers = 3;
        int numConsumers = 3;
        int itemsPerProducer = 10;
        
        ExecutorService executor = Executors.newFixedThreadPool(numProducers + numConsumers);
        
        // Producers
        for (int i = 0; i < numProducers; i++) {
            final int producerId = i;
            executor.submit(() -> {
                for (int j = 0; j < itemsPerProducer; j++) {
                    int value = (producerId * 100) + j;
                    stack.push(value);
                    System.out.println("Producer " + producerId + " pushed: " + value);
                    
                    try {
                        Thread.sleep((int)(Math.random() * 10));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Consumers
        for (int i = 0; i < numConsumers; i++) {
            final int consumerId = i;
            executor.submit(() -> {
                int itemsPopped = 0;
                while (itemsPopped < (numProducers * itemsPerProducer / numConsumers)) {
                    Integer value = stack.pop();
                    if (value != null) {
                        System.out.println("Consumer " + consumerId + " popped: " + value);
                        itemsPopped++;
                    }
                    
                    try {
                        Thread.sleep((int)(Math.random() * 20));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Wait for all tasks to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        // Pop any remaining items
        System.out.println("\nRemaining items in stack:");
        while (!stack.isEmpty()) {
            System.out.println("Remaining: " + stack.pop());
        }
    }
    
    // Part 3: Demonstrating Java's internal CAS mechanisms
    private static void internalCAS() {
        System.out.println("Java's atomic classes internally use Unsafe.compareAndSwapInt and similar methods.");
        System.out.println("These methods directly access hardware CAS instructions.");
        System.out.println("The main atomic operations include:");
        System.out.println("- compareAndSet(expectedValue, newValue)");
        System.out.println("- weakCompareAndSet(expectedValue, newValue)");
        
        // Demonstrate CAS with AtomicReference
        AtomicReference<String> ref = new AtomicReference<>("initial");
        boolean success = ref.compareAndSet("initial", "updated");
        System.out.println("\nAtomicReference update succeeded: " + success);
        System.out.println("New value: " + ref.get());
        
        // Demonstrate failed CAS
        success = ref.compareAndSet("initial", "another update");
        System.out.println("AtomicReference update succeeded: " + success + " (expected to fail)");
        System.out.println("Value remains: " + ref.get());
        
        // Demonstrate CAS retry pattern
        int retries = 0;
        while (true) {
            String currentValue = ref.get();
            if (currentValue.equals("updated")) {
                if (ref.compareAndSet(currentValue, "final update")) {
                    System.out.println("Update succeeded after " + retries + " retries");
                    break;
                }
                retries++;
            } else {
                System.out.println("Unexpected value: " + currentValue);
                break;
            }
        }
        
        System.out.println("Final value: " + ref.get());
        
        // Introduce AtomicStampedReference for ABA problem (covered in next example)
        System.out.println("\nJava also provides AtomicStampedReference to solve the ABA problem");
        System.out.println("(which will be covered in the ABA Problem example)");
    }
} 