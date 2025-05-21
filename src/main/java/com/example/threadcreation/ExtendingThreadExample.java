package com.example.threadcreation;

public class ExtendingThreadExample extends Thread {
    @Override
    public void run() {
        System.out.println("Thread is running: " + Thread.currentThread().getName());
        // Your thread logic here
    }

    public static void main(String[] args) {
        System.out.println("Extending Thread Example");
        System.out.println("========================");
        
        ExtendingThreadExample thread1 = new ExtendingThreadExample();
        thread1.setName("Custom-Thread-1");
        thread1.start();  // Don't call run() directly
        
        ExtendingThreadExample thread2 = new ExtendingThreadExample();
        thread2.setName("Custom-Thread-2");
        thread2.start();
        
        System.out.println("Main thread continues execution...");
        
        // The main thread will continue executing while the child threads run
        System.out.println("Main thread: " + Thread.currentThread().getName());
    }
} 