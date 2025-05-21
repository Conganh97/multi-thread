package com.example.datasharing;

public class SynchronizedExample {
    private int counter = 0;
    private static int staticCounter = 0;
    private final Object lock = new Object();
    
    // Synchronized instance method - uses 'this' as the lock
    public synchronized void incrementCounter() {
        counter++;
    }
    
    // Synchronized static method - uses class object as the lock
    public static synchronized void incrementStaticCounter() {
        staticCounter++;
    }
    
    // Synchronized block on a specific object
    public void incrementWithLock() {
        synchronized (lock) {
            counter++;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Synchronized Example");
        System.out.println("===================");
        
        SynchronizedExample example = new SynchronizedExample();
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                example.incrementCounter();
                incrementStaticCounter();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                example.incrementWithLock();
                incrementStaticCounter();
            }
        });
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        System.out.println("Counter (incrementCounter + incrementWithLock): " + example.counter + 
                          " (expected: 20000)");
        System.out.println("Static counter: " + staticCounter + " (expected: 20000)");
        
        // Compare with unsynchronized counters
        System.out.println("\nComparing with non-synchronized counters:");
        
        UnsynchronizedCounter unsyncExample = new UnsynchronizedCounter();
        
        Thread thread3 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                unsyncExample.increment();
            }
        });
        
        Thread thread4 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                unsyncExample.increment();
            }
        });
        
        thread3.start();
        thread4.start();
        
        thread3.join();
        thread4.join();
        
        System.out.println("Unsynchronized counter: " + unsyncExample.getValue() + 
                          " (expected: 20000, but likely less due to race conditions)");
    }
    
    // A class with non-synchronized methods for comparison
    static class UnsynchronizedCounter {
        private int count = 0;
        
        public void increment() {
            count++;
        }
        
        public int getValue() {
            return count;
        }
    }
} 