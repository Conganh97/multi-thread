package com.example.datasharing;

public class VolatileExample {
    // Without volatile, the thread might never see running = false
    private static volatile boolean running = true;
    
    // For comparison, a non-volatile flag
    private static boolean nonVolatileRunning = true;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Volatile Variables Example");
        System.out.println("=========================");
        
        // Using volatile variable
        Thread volatileWorker = new Thread(() -> {
            int counter = 0;
            System.out.println("Volatile worker starting...");
            
            while (running) {
                counter++;
                // Avoid too many iterations for this example
                if (counter % 10_000_000 == 0) {
                    Thread.yield(); // Give the main thread a chance to run
                }
            }
            
            System.out.println("Volatile worker stopped. Counted to " + counter);
        });
        
        // Using non-volatile variable
        Thread nonVolatileWorker = new Thread(() -> {
            int counter = 0;
            System.out.println("Non-volatile worker starting...");
            
            while (nonVolatileRunning) {
                counter++;
                // Avoid too many iterations for this example
                if (counter % 10_000_000 == 0) {
                    Thread.yield(); // Give the main thread a chance to run
                }
            }
            
            System.out.println("Non-volatile worker stopped. Counted to " + counter);
        });
        
        // Start both worker threads
        volatileWorker.start();
        nonVolatileWorker.start();
        
        // Give the workers time to start
        Thread.sleep(500);
        
        // Signal the workers to stop
        System.out.println("Setting running flags to false");
        running = false;
        nonVolatileRunning = false;
        
        // Wait for a moment to see if the threads notice the change
        Thread.sleep(500);
        
        // If the non-volatile thread doesn't stop, interrupt it after a timeout
        if (nonVolatileWorker.isAlive()) {
            System.out.println("Non-volatile worker didn't see the flag change. Interrupting...");
            nonVolatileWorker.interrupt();
        }
        
        // Wait for both threads to finish
        volatileWorker.join();
        nonVolatileWorker.join(1000); // Wait at most 1 second more
        
        if (nonVolatileWorker.isAlive()) {
            System.out.println("Non-volatile worker is still running even after interruption!");
            System.out.println("This demonstrates why volatile is needed for flag variables in multi-threaded code.");
            // Force stop for the example
            nonVolatileWorker.stop(); // Note: stop() is deprecated but used here for demonstration
        }
        
        System.out.println("\nExplanation:");
        System.out.println("Volatile variables ensure that changes are visible across threads immediately.");
        System.out.println("Without volatile, the optimization by the JVM/compiler may cause a thread");
        System.out.println("to cache the value locally and never see updates from other threads.");
    }
} 