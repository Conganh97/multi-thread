package com.example.datasharing;

public class SharedMemoryExample {
    // Shared variable (class/static variable)
    private static int staticCounter = 0;
    
    // Instance variable - shared when the object is shared
    private int instanceCounter = 0;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Shared Memory Example");
        System.out.println("====================");
        
        // Local variable - not shared between threads
        final SharedMemoryExample example = new SharedMemoryExample();
        
        Runnable incrementTask = () -> {
            // Local variable - each thread has its own copy
            int localCounter = 0;
            
            for (int i = 0; i < 10000; i++) {
                // Increment shared static variable
                staticCounter++;  // This operation is not atomic
                
                // Increment shared instance variable
                example.instanceCounter++;  // This operation is not atomic
                
                // Increment thread-local variable
                localCounter++;  // This is safe and doesn't need synchronization
            }
            
            System.out.println(Thread.currentThread().getName() + 
                              " local counter: " + localCounter);
        };
        
        Thread thread1 = new Thread(incrementTask, "Thread-1");
        Thread thread2 = new Thread(incrementTask, "Thread-2");
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        // The result is likely less than 20000 due to race conditions
        System.out.println("Final static counter value: " + staticCounter);
        System.out.println("Final instance counter value: " + example.instanceCounter);
        System.out.println("Note: Due to race conditions, the shared counters are likely less than 20000");
        System.out.println("This demonstrates that non-synchronized shared variables can lead to data corruption");
    }
} 