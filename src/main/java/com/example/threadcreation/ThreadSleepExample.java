package com.example.threadcreation;

public class ThreadSleepExample {
    public static void main(String[] args) {
        System.out.println("Thread Sleep and Interruption Example");
        System.out.println("===================================");
        
        Thread sleepingThread = new Thread(() -> {
            try {
                System.out.println("Thread going to sleep for 10 seconds");
                Thread.sleep(10000);
                System.out.println("Thread woke up normally");
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted while sleeping");
            }
        });

        sleepingThread.start();
        
        // Let the thread sleep for a bit before interrupting
        try {
            System.out.println("Main thread waiting 2 seconds before interrupting");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Interrupt the sleeping thread
        System.out.println("Main thread interrupting the sleeping thread");
        sleepingThread.interrupt();
        
        try {
            // Wait for the sleeping thread to finish
            sleepingThread.join();
            System.out.println("Sleeping thread has terminated");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
} 