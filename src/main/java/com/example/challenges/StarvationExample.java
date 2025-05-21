package com.example.challenges;

public class StarvationExample {
    private static final Object sharedResource = new Object();

    public static void main(String[] args) {
        System.out.println("Starvation Example");
        System.out.println("==================");
        System.out.println("This example demonstrates starvation where low-priority threads");
        System.out.println("cannot access a resource because it's continuously held by");
        System.out.println("high-priority threads.\n");
        
        // Create a high-priority thread
        Thread highPriorityThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (sharedResource) {
                    System.out.println("High priority thread working on the resource");
                    try {
                        // Hold the lock for a long time
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                // Briefly release the lock to give other threads a chance
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "HighPriorityThread");
        
        highPriorityThread.setPriority(Thread.MAX_PRIORITY);
        
        // Create multiple low-priority threads
        for (int i = 0; i < 5; i++) {
            Thread lowPriorityThread = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    long startTime = System.currentTimeMillis();
                    synchronized (sharedResource) {
                        long waitTime = System.currentTimeMillis() - startTime;
                        System.out.println(Thread.currentThread().getName() + 
                                          " acquired the resource after waiting " + 
                                          waitTime + "ms");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "LowPriorityThread-" + i);
            
            lowPriorityThread.setPriority(Thread.MIN_PRIORITY);
            lowPriorityThread.start();
        }
        
        // Start the high-priority thread last to demonstrate starvation
        highPriorityThread.start();
        
        // Let the example run for a while
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\nExplanation:");
        System.out.println("Starvation occurs when a thread is denied access to resources");
        System.out.println("for extended periods. In this example, the high-priority thread");
        System.out.println("keeps getting CPU time and access to the shared resource,");
        System.out.println("while low-priority threads have to wait much longer to get access.");
        System.out.println("This can lead to poor performance for low-priority operations.");
    }
} 