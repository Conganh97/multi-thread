package com.example.threadcreation;

public class DaemonThreadExample {
    public static void main(String[] args) {
        System.out.println("Daemon Thread Example");
        System.out.println("====================");
        
        Thread daemonThread = new Thread(() -> {
            int count = 0;
            while (true) {
                try {
                    System.out.println("Daemon thread running... Count: " + (++count));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        
        daemonThread.setDaemon(true);  // Must be set before starting
        System.out.println("Is daemon thread? " + daemonThread.isDaemon());
        daemonThread.start();
        
        // Create a non-daemon thread for comparison
        Thread normalThread = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                try {
                    System.out.println("Normal thread running... Count: " + i);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out.println("Normal thread finished");
        });
        
        normalThread.start();
        
        try {
            System.out.println("Main thread will now sleep for 5 seconds");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Main thread exiting");
        System.out.println("Note: Daemon thread will be terminated when all non-daemon threads exit");
    }
} 