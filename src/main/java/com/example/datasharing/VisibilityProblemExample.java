package com.example.datasharing;

public class VisibilityProblemExample {
    private static boolean flag = false;
    private static int value = 0;
    
    // Adding volatile will fix the visibility issue
    // private static volatile boolean flag = false;
    // private static volatile int value = 0;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Memory Visibility Problem Example");
        System.out.println("================================");
        
        Thread writerThread = new Thread(() -> {
            System.out.println("Writer thread starting...");
            value = 42;  // Step 1
            flag = true;  // Step 2
            System.out.println("Writer thread: value = " + value + ", flag = " + flag);
        });
        
        Thread readerThread = new Thread(() -> {
            System.out.println("Reader thread starting...");
            // Try for a short period to detect visibility issues
            long startTime = System.currentTimeMillis();
            long endTime = startTime + 5000; // 5 seconds timeout
            
            while (!flag && System.currentTimeMillis() < endTime) {
                // Busy-wait until flag becomes true or timeout
                Thread.yield();
            }
            
            if (flag) {
                System.out.println("Reader thread: flag is true, value = " + value);
                if (value != 42) {
                    System.out.println("VISIBILITY ISSUE DETECTED: Read 'true' flag but incorrect value!");
                }
            } else {
                System.out.println("Reader thread: timed out waiting for flag");
            }
        });
        
        // Give reader a chance to start first
        readerThread.start();
        Thread.sleep(500);
        writerThread.start();
        
        // Wait for both threads to finish
        writerThread.join();
        readerThread.join();
        
        System.out.println("\nExplanation:");
        System.out.println("Without proper synchronization or the 'volatile' keyword,");
        System.out.println("the reader thread might see 'flag = true' but an outdated value of 0 instead of 42.");
        System.out.println("This is because each CPU core may cache variables, and changes made by one thread");
        System.out.println("may not be immediately visible to other threads.");
        System.out.println("\nUncomment the volatile declarations at the top of the class to fix this issue.");
    }
} 