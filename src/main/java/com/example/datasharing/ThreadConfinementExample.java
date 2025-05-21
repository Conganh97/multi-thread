package com.example.datasharing;

public class ThreadConfinementExample {
    // Shared object (would need synchronization if accessed by multiple threads)
    private static StringBuilder sharedBuilder = new StringBuilder();
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Thread Confinement Example");
        System.out.println("=========================");
        
        Thread thread1 = new Thread(() -> {
            // Stack confinement - localBuilder is confined to this thread's stack
            StringBuilder localBuilder = new StringBuilder();
            
            for (int i = 0; i < 100; i++) {
                // This is thread-safe because localBuilder is confined to this thread
                localBuilder.append(i).append(",");
                
                // This is not thread-safe if multiple threads access it
                synchronized(sharedBuilder) {
                    sharedBuilder.append(i).append(",");
                }
            }
            
            System.out.println("Thread-1 local builder result: " + localBuilder.toString());
        });
        
        Thread thread2 = new Thread(() -> {
            // Another stack-confined object
            StringBuilder localBuilder = new StringBuilder();
            
            for (int i = 100; i < 200; i++) {
                // Thread-safe: stack confinement
                localBuilder.append(i).append(",");
                
                // Needs synchronization for shared object
                synchronized(sharedBuilder) {
                    sharedBuilder.append(i).append(",");
                }
            }
            
            System.out.println("Thread-2 local builder result: " + localBuilder.toString());
        });
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        System.out.println("Shared builder result: " + sharedBuilder.toString());
        
        // ThreadLocal confinement example
        System.out.println("\nThreadLocal confinement example:");
        
        ThreadLocal<StringBuilder> threadLocalBuilder = ThreadLocal.withInitial(StringBuilder::new);
        
        Thread thread3 = new Thread(() -> {
            StringBuilder builder = threadLocalBuilder.get();
            for (int i = 0; i < 100; i++) {
                builder.append(i).append(",");
            }
            System.out.println("Thread-3 ThreadLocal builder: " + builder.toString());
        });
        
        Thread thread4 = new Thread(() -> {
            StringBuilder builder = threadLocalBuilder.get();
            for (int i = 100; i < 200; i++) {
                builder.append(i).append(",");
            }
            System.out.println("Thread-4 ThreadLocal builder: " + builder.toString());
        });
        
        thread3.start();
        thread4.start();
        
        thread3.join();
        thread4.join();
        
        System.out.println("Main thread ThreadLocal builder: " + threadLocalBuilder.get().toString());
        
        System.out.println("\nExplanation:");
        System.out.println("Thread confinement is a technique where data is confined to a specific thread,");
        System.out.println("eliminating the need for synchronization. Two common approaches are:");
        System.out.println("1. Stack confinement: Variables that only exist on a thread's stack");
        System.out.println("2. ThreadLocal: Provides each thread with its own instance of an object");
    }
} 