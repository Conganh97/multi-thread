package com.example.datasharing;

public class ThreadLocalExample {
    // Thread-local variable - each thread has its own copy
    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);
    
    // Shared variable for comparison
    private static int sharedCounter = 0;
    
    // Method to access thread-local variable
    public static int get() {
        return threadLocal.get();
    }
    
    // Method to update thread-local variable
    public static void set(int value) {
        threadLocal.set(value);
    }
    
    // Method to clean up thread-local variable
    public static void remove() {
        threadLocal.remove();
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Thread-Local Storage Example");
        System.out.println("===========================");
        
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            
            // Get the current thread's value
            int value = get();
            System.out.println(threadName + " initial value: " + value);
            
            // Modify the thread-local value
            set(value + 100);
            System.out.println(threadName + " after increment: " + get());
            
            // Modify the shared counter for comparison
            sharedCounter++;
            System.out.println(threadName + " shared counter: " + sharedCounter);
            
            // Do some processing
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Value persists throughout thread execution
            System.out.println(threadName + " final value: " + get());
            System.out.println(threadName + " shared counter after sleep: " + sharedCounter);
            
            // Clean up to prevent memory leaks in thread pools
            remove();
        };
        
        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        // Main thread has its own separate value
        System.out.println("Main thread value: " + get());
        System.out.println("Final shared counter value: " + sharedCounter);
        
        System.out.println("\nExplanation:");
        System.out.println("Thread-local variables provide a way for each thread to have its own private copy");
        System.out.println("of a variable, avoiding synchronization and ensuring thread isolation.");
        System.out.println("This is useful for thread-specific context like user IDs, transaction IDs, etc.");
    }
} 