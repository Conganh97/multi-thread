package com.example.threadcreation;

public class ThreadStateExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Thread State Monitoring Example");
        System.out.println("==============================");
        
        Object lock = new Object();
        
        Thread thread = new Thread(() -> {
            try {
                synchronized (lock) {
                    System.out.println("Thread acquired lock, now waiting...");
                    lock.wait();
                    System.out.println("Thread notified!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        System.out.println("Before starting: " + thread.getState());
        thread.start();
        
        Thread.sleep(100); // Give thread time to start and wait
        System.out.println("After wait() call: " + thread.getState());
        
        synchronized (lock) {
            lock.notify();
            System.out.println("After notify(): " + thread.getState());
        }
        
        thread.join();
        System.out.println("After completion: " + thread.getState());
        
        // Demonstrate other thread states
        Thread sleepingThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        sleepingThread.start();
        Thread.sleep(100);
        System.out.println("Sleeping thread state: " + sleepingThread.getState());
        
        Object blockLock = new Object();
        Thread blockingThread = new Thread(() -> {
            synchronized (blockLock) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        synchronized (blockLock) {
            blockingThread.start();
            Thread.sleep(100);
            System.out.println("Blocking thread state: " + blockingThread.getState());
        }
        
        // Wait for all threads to finish
        sleepingThread.join();
        blockingThread.join();
    }
} 