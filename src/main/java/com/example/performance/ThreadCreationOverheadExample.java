package com.example.performance;

public class ThreadCreationOverheadExample {
    private static final int NUM_THREADS = 1000;
    
    public static void main(String[] args) {
        System.out.println("Thread Creation Overhead Example");
        System.out.println("==============================");
        
        // Measure time to create many threads directly
        long startDirect = System.currentTimeMillis();
        
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            final int taskId = i;
            threads[i] = new Thread(() -> {
                // Simple task
                double result = 0;
                for (int j = 0; j < 1000; j++) {
                    result += Math.sqrt(j);
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        long endDirect = System.currentTimeMillis();
        
        System.out.println("Time to create and run " + NUM_THREADS + 
                          " individual threads: " + (endDirect - startDirect) + "ms");
        System.out.println();
        System.out.println("This example shows the overhead of thread creation.");
        System.out.println("In practice, thread pools are used to avoid this overhead.");
        System.out.println("(See ThreadPoolExample and CachedThreadPoolExample)");
    }
} 