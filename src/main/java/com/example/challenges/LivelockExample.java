package com.example.challenges;

public class LivelockExample {
    static class Worker {
        private String name;
        private boolean active;

        public Worker(String name, boolean active) {
            this.name = name;
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }

        public void work(Worker otherWorker, Object commonResource) {
            while (active) {
                // Wait while the other worker is active
                if (otherWorker.isActive()) {
                    System.out.println(name + ": Waiting for " + otherWorker.getName() + " to complete");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                // If we reach here, the other worker is not active
                System.out.println(name + ": Working on " + commonResource);
                active = false;
                System.out.println(name + ": Completed work on " + commonResource);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Livelock Example");
        System.out.println("===============");
        
        final Worker worker1 = new Worker("Worker 1", true);
        final Worker worker2 = new Worker("Worker 2", true);
        final Object resource = "Shared Resource";

        System.out.println("This example demonstrates a livelock where both workers keep");
        System.out.println("responding to each other but neither makes progress.\n");
        
        // Both workers will keep waiting for each other
        Thread thread1 = new Thread(() -> worker1.work(worker2, resource));
        Thread thread2 = new Thread(() -> worker2.work(worker1, resource));
        
        thread1.start();
        thread2.start();
        
        // Let the livelock run for a while
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\nExplanation:");
        System.out.println("In a livelock, threads are actively running (not blocked)");
        System.out.println("but cannot make progress because they keep responding to");
        System.out.println("each other's actions in a way that prevents either from completing.");
        System.out.println("Unlike deadlocks, the threads are not waiting for resources,");
        System.out.println("but are stuck in a cycle of responding to each other.");
        
        // Attempt to break the livelock by terminating the program
        System.exit(0);
    }
} 