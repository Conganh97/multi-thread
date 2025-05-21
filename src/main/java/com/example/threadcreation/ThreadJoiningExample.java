package com.example.threadcreation;

public class ThreadJoiningExample {
    public static void main(String[] args) {
        System.out.println("Thread Joining Example");
        System.out.println("=====================");
        
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1 started");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread 1 completed");
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2 started");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread 2 completed");
        });

        thread1.start();
        thread2.start();

        System.out.println("Main thread waiting for child threads to complete...");
        
        try {
            thread1.join();  // Main thread waits for thread1 to complete
            System.out.println("Thread 1 joined");
            
            thread2.join();  // Main thread waits for thread2 to complete
            System.out.println("Thread 2 joined");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Both threads have completed execution");
    }
} 