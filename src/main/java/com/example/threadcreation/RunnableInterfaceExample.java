package com.example.threadcreation;

public class RunnableInterfaceExample implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread is running: " + Thread.currentThread().getName());
        // Your thread logic here
    }

    public static void main(String[] args) {
        System.out.println("Runnable Interface Example");
        System.out.println("=========================");
        
        Thread thread1 = new Thread(new RunnableInterfaceExample(), "Custom-Thread-1");
        thread1.start();
        
        // Using lambda (Java 8+)
        Thread thread2 = new Thread(() -> {
            System.out.println("Lambda thread is running: " + Thread.currentThread().getName());
        }, "Lambda-Thread");
        thread2.start();
        
        System.out.println("Main thread continues execution...");
        System.out.println("Main thread: " + Thread.currentThread().getName());
    }
} 