package com.example.challenges;

public class MemoryConsistencyExample {
    // These variables are not volatile, so updates may not be visible to other threads
    private static boolean ready = false;
    private static int number = 0;
    
    // For the solution part
    private static volatile boolean volatileReady = false;
    private static volatile int volatileNumber = 0;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Memory Consistency Example");
        System.out.println("==========================");
        System.out.println("This example demonstrates memory consistency errors that can");
        System.out.println("occur when multiple threads have inconsistent views of shared data.\n");
        
        System.out.println("Part 1: With memory consistency error (not using volatile or synchronized)");
        System.out.println("--------------------------------------------------------------------");
        demonstrateMemoryConsistencyError();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 2: With volatile variables (fixing the issue)");
        System.out.println("---------------------------------------------------");
        demonstrateVolatileSolution();
        
        System.out.println("\nPart 3: With synchronized blocks (alternative solution)");
        System.out.println("--------------------------------------------------------");
        demonstrateSynchronizedSolution();
        
        System.out.println("\nExplanation:");
        System.out.println("Memory consistency errors occur when threads have inconsistent views");
        System.out.println("of shared memory due to caching, compiler optimizations, or");
        System.out.println("instruction reordering. Two solutions are:");
        System.out.println("1. Volatile variables: Ensure reads/writes go directly to main memory");
        System.out.println("2. Synchronized blocks: Establish happens-before relationships");
    }
    
    private static void demonstrateMemoryConsistencyError() throws InterruptedException {
        // Reset the shared variables
        ready = false;
        number = 0;
        
        Thread writerThread = new Thread(() -> {
            number = 42;  // Write number
            ready = true;  // Set flag
        });
        
        Thread readerThread = new Thread(() -> {
            while (!ready) {
                Thread.yield();  // Wait until ready
            }
            System.out.println("Reader thread read number: " + number);
            // Without proper synchronization, might print 0 instead of 42!
        });
        
        readerThread.start();
        Thread.sleep(100); // Give reader time to start
        writerThread.start();
        
        readerThread.join();
        writerThread.join();
    }
    
    private static void demonstrateVolatileSolution() throws InterruptedException {
        // Reset the shared variables
        volatileReady = false;
        volatileNumber = 0;
        
        Thread writerThread = new Thread(() -> {
            volatileNumber = 42;  // Write number
            volatileReady = true;  // Set flag
        });
        
        Thread readerThread = new Thread(() -> {
            while (!volatileReady) {
                Thread.yield();  // Wait until ready
            }
            System.out.println("Reader thread read volatile number: " + volatileNumber);
            // With volatile, always prints 42
        });
        
        readerThread.start();
        Thread.sleep(100); // Give reader time to start
        writerThread.start();
        
        readerThread.join();
        writerThread.join();
    }
    
    private static void demonstrateSynchronizedSolution() throws InterruptedException {
        // Reset the shared variables
        ready = false;
        number = 0;
        
        // Use a lock object for synchronization
        final Object lock = new Object();
        
        Thread writerThread = new Thread(() -> {
            synchronized (lock) {
                number = 42;  // Write number
                ready = true;  // Set flag
            }
        });
        
        Thread readerThread = new Thread(() -> {
            while (true) {
                boolean isReady;
                int value;
                
                synchronized (lock) {
                    isReady = ready;
                    value = number;
                }
                
                if (isReady) {
                    System.out.println("Reader thread read synchronized number: " + value);
                    break;
                }
                
                Thread.yield();  // Wait until ready
            }
        });
        
        readerThread.start();
        Thread.sleep(100); // Give reader time to start
        writerThread.start();
        
        readerThread.join();
        writerThread.join();
    }
} 