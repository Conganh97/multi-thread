package com.example.communication;

public class WaitNotifyExample {
    private static final Object lock = new Object();
    private static boolean dataReady = false;
    private static String data = null;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Wait and Notify Example");
        System.out.println("======================");
        System.out.println("This example demonstrates the wait/notify mechanism for inter-thread communication.\n");
        
        // Consumer thread - waits for data
        Thread consumerThread = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Consumer waiting for data...");
                while (!dataReady) {
                    try {
                        lock.wait(); // Release lock and wait
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                // Process data after being notified
                System.out.println("Consumer received data: " + data);
            }
        });
        
        // Producer thread - prepares data and notifies
        Thread producerThread = new Thread(() -> {
            // Simulate some work before data is ready
            try {
                System.out.println("Producer working on data...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            
            synchronized (lock) {
                // Prepare data
                data = "Important information";
                dataReady = true;
                
                // Notify waiting thread
                System.out.println("Producer is notifying consumer...");
                lock.notify();
            }
        });
        
        // Start both threads
        consumerThread.start();
        Thread.sleep(500); // Make sure consumer starts first
        producerThread.start();
        
        // Wait for completion
        consumerThread.join();
        producerThread.join();
        
        System.out.println("\nDemonstrating lost notification problem:");
        demonstrateLostNotification();
        
        System.out.println("\nDemonstrating notifyAll for multiple waiters:");
        demonstrateNotifyAll();
        
        System.out.println("\nExplanation:");
        System.out.println("The wait() and notify() methods provide a basic mechanism for thread communication:");
        System.out.println("- wait() causes a thread to release a lock and wait until notified");
        System.out.println("- notify() wakes up a single waiting thread");
        System.out.println("- notifyAll() wakes up all waiting threads");
        System.out.println("These methods must be called from within a synchronized block on the same object.");
        System.out.println("Always use wait() in a loop to handle spurious wakeups.");
    }
    
    private static void demonstrateLostNotification() throws InterruptedException {
        // Reset
        dataReady = false;
        data = null;
        
        // Producer runs first!
        Thread producerThread = new Thread(() -> {
            synchronized (lock) {
                // Prepare data
                data = "Information that might be lost";
                dataReady = true;
                
                System.out.println("Producer notifying, but no one is waiting yet!");
                lock.notify(); // This notification is lost if no thread is waiting
            }
        });
        
        // Consumer runs second
        Thread consumerThread = new Thread(() -> {
            // Do some work before waiting
            try {
                Thread.sleep(1000); // Consumer is late!
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            synchronized (lock) {
                System.out.println("Consumer checking data...");
                if (!dataReady) { // Use if instead of while to demonstrate the problem
                    try {
                        System.out.println("Consumer waiting for data (might wait forever)...");
                        lock.wait(); // Wait for notification that was already sent!
                    } catch (InterruptedException e) {
                        System.out.println("Consumer was interrupted");
                    }
                }
                
                if (dataReady) {
                    System.out.println("Consumer received data: " + data);
                } else {
                    System.out.println("Consumer never received notification!");
                }
            }
        });
        
        producerThread.start();
        Thread.sleep(100); // Ensure producer runs first
        consumerThread.start();
        
        producerThread.join();
        
        // Interrupt consumer after 3 seconds (it might be waiting forever)
        Thread.sleep(3000);
        consumerThread.interrupt();
        consumerThread.join();
        
        System.out.println("This demonstrates the 'lost notification' problem.");
        System.out.println("Solution: Always check condition in a loop using while, not if.");
    }
    
    private static void demonstrateNotifyAll() throws InterruptedException {
        // Reset
        dataReady = false;
        data = null;
        
        // Multiple consumer threads
        Thread[] consumers = new Thread[3];
        
        for (int i = 0; i < consumers.length; i++) {
            final int id = i;
            consumers[i] = new Thread(() -> {
                synchronized (lock) {
                    System.out.println("Consumer " + id + " waiting for data...");
                    while (!dataReady) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    System.out.println("Consumer " + id + " received data: " + data);
                }
            });
            
            consumers[i].start();
        }
        
        // Let all consumers start waiting
        Thread.sleep(500);
        
        // Producer thread - notifies all consumers
        Thread producerThread = new Thread(() -> {
            synchronized (lock) {
                // Prepare data
                data = "Broadcast information";
                dataReady = true;
                
                System.out.println("Producer notifying ALL consumers...");
                lock.notifyAll(); // Wake up all waiting threads
            }
        });
        
        producerThread.start();
        
        // Wait for all threads to complete
        producerThread.join();
        for (Thread consumer : consumers) {
            consumer.join();
        }
    }
} 