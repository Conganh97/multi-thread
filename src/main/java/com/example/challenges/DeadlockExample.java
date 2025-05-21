package com.example.challenges;

public class DeadlockExample {
    private static final Object RESOURCE_A = new Object();
    private static final Object RESOURCE_B = new Object();
    
    public static void main(String[] args) {
        System.out.println("Deadlock Example");
        System.out.println("===============");
        System.out.println("This example demonstrates how two threads can deadlock");
        System.out.println("when they acquire resources in different orders.\n");
        
        // Thread 1 tries to lock resources in order: A -> B
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1: Attempting to lock resource A");
            synchronized (RESOURCE_A) {
                System.out.println("Thread 1: Locked resource A");
                
                try {
                    Thread.sleep(100); // Delay to increase deadlock probability
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Now try to lock resource B
                System.out.println("Thread 1: Attempting to lock resource B");
                synchronized (RESOURCE_B) {
                    System.out.println("Thread 1: Locked resource B");
                    System.out.println("Thread 1: Working with both resources");
                }
                System.out.println("Thread 1: Released resource B");
            }
            System.out.println("Thread 1: Released resource A");
        }, "Thread-1");
        
        // Thread 2 tries to lock resources in order: B -> A
        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2: Attempting to lock resource B");
            synchronized (RESOURCE_B) {
                System.out.println("Thread 2: Locked resource B");
                
                try {
                    Thread.sleep(100); // Delay to increase deadlock probability
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Now try to lock resource A
                System.out.println("Thread 2: Attempting to lock resource A");
                synchronized (RESOURCE_A) {
                    System.out.println("Thread 2: Locked resource A");
                    System.out.println("Thread 2: Working with both resources");
                }
                System.out.println("Thread 2: Released resource A");
            }
            System.out.println("Thread 2: Released resource B");
        }, "Thread-2");
        
        thread1.start();
        thread2.start();
        
        // To detect the deadlock, we can check if threads are still alive after some time
        try {
            Thread.sleep(2000);
            
            if (thread1.isAlive() && thread2.isAlive()) {
                System.out.println("\nDEADLOCK DETECTED!");
                System.out.println("Thread 1 state: " + thread1.getState());
                System.out.println("Thread 2 state: " + thread2.getState());
                
                System.out.println("\nExplanation:");
                System.out.println("Deadlock occurs when two or more threads wait for each other to release locks.");
                System.out.println("In this example, Thread 1 holds resource A and waits for resource B,");
                System.out.println("while Thread 2 holds resource B and waits for resource A.");
                System.out.println("This situation is called a circular wait, and it's one of the");
                System.out.println("four conditions necessary for a deadlock to occur.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
} 