package com.example.threadcreation;

public class ThreadPriorityExample {
    public static void main(String[] args) {
        System.out.println("Thread Priority Example");
        System.out.println("======================");
        
        // Print priority range
        System.out.println("Priority range: MIN_PRIORITY=" + Thread.MIN_PRIORITY + 
                          ", NORM_PRIORITY=" + Thread.NORM_PRIORITY + 
                          ", MAX_PRIORITY=" + Thread.MAX_PRIORITY);
        
        Thread highPriorityThread = new Thread(() -> {
            System.out.println("High priority thread running");
            long count = 0;
            for (long i = 0; i < 1_000_000_000L; i++) {
                count++;
            }
            System.out.println("High priority thread finished. Count: " + count);
        });
        
        Thread lowPriorityThread = new Thread(() -> {
            System.out.println("Low priority thread running");
            long count = 0;
            for (long i = 0; i < 1_000_000_000L; i++) {
                count++;
            }
            System.out.println("Low priority thread finished. Count: " + count);
        });
        
        highPriorityThread.setPriority(Thread.MAX_PRIORITY);  // 10
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);   // 1
        
        System.out.println("Starting threads...");
        System.out.println("High priority thread priority: " + highPriorityThread.getPriority());
        System.out.println("Low priority thread priority: " + lowPriorityThread.getPriority());
        
        lowPriorityThread.start();
        highPriorityThread.start();
        
        try {
            highPriorityThread.join();
            lowPriorityThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Both threads completed");
        System.out.println("Note: The actual execution order may vary based on OS scheduling and JVM implementation.");
    }
} 