package com.example.challenges;

public class ThreadSafePatternExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Thread-Safe Design Patterns Example");
        System.out.println("==================================");
        System.out.println("This example demonstrates various thread-safe design patterns.\n");
        
        System.out.println("Part 1: Monitor Pattern (Thread-safe Bank Account)");
        System.out.println("------------------------------------------------");
        monitorPatternExample();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 2: Immutable Object Pattern");
        System.out.println("--------------------------------");
        immutableObjectExample();
        
        System.out.println("\nExplanation:");
        System.out.println("Thread-safe design patterns help create components that can be safely");
        System.out.println("used in multi-threaded environments. Common patterns include:");
        System.out.println("1. Monitor Pattern: Encapsulate data with synchronized access methods");
        System.out.println("2. Immutable Objects: Objects that cannot be modified after creation");
        System.out.println("3. Thread-Local Storage: Give each thread its own private copy of data");
    }
    
    // Monitor Pattern Example
    private static void monitorPatternExample() throws InterruptedException {
        BankAccount account = new BankAccount(1000);
        
        Thread depositThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.deposit(100);
                System.out.println("Deposited 100, new balance: " + account.getBalance());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        Thread withdrawThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    account.withdraw(50);
                    System.out.println("Withdrew 50, new balance: " + account.getBalance());
                } catch (InsufficientFundsException e) {
                    System.out.println("Could not withdraw: " + e.getMessage());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        depositThread.start();
        withdrawThread.start();
        
        depositThread.join();
        withdrawThread.join();
        
        System.out.println("Final balance: " + account.getBalance());
    }
    
    // Immutable Object Pattern Example
    private static void immutableObjectExample() {
        // Create an immutable person
        ImmutablePerson person = new ImmutablePerson("John", 30);
        
        // Start multiple threads that access the immutable object
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                // Reading from immutable objects is always thread-safe
                System.out.println(Thread.currentThread().getName() + 
                                  " read: " + person.getName() + ", " + person.getAge());
                
                // Create a new immutable person (doesn't modify the original)
                ImmutablePerson olderPerson = person.withAge(person.getAge() + 1);
                System.out.println(Thread.currentThread().getName() + 
                                  " created: " + olderPerson.getName() + ", " + olderPerson.getAge());
            });
            
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Original person remains unchanged
        System.out.println("Original person after all threads: " + 
                          person.getName() + ", " + person.getAge());
    }
    
    // Monitor Pattern implementation - Thread-safe bank account
    static class BankAccount {
        private double balance;
        
        public BankAccount(double initialBalance) {
            this.balance = initialBalance;
        }
        
        // Synchronized accessor methods form a monitor
        public synchronized double getBalance() {
            return balance;
        }
        
        public synchronized void deposit(double amount) {
            if (amount < 0) {
                throw new IllegalArgumentException("Cannot deposit negative amount");
            }
            double newBalance = balance + amount;
            // Simulate some processing time
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            balance = newBalance;
        }
        
        public synchronized void withdraw(double amount) throws InsufficientFundsException {
            if (amount < 0) {
                throw new IllegalArgumentException("Cannot withdraw negative amount");
            }
            if (balance < amount) {
                throw new InsufficientFundsException("Insufficient funds for withdrawal");
            }
            double newBalance = balance - amount;
            // Simulate some processing time
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            balance = newBalance;
        }
    }
    
    // Exception for failed withdrawals
    static class InsufficientFundsException extends Exception {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }
    
    // Immutable Object Pattern implementation
    static final class ImmutablePerson {
        private final String name;
        private final int age;
        
        public ImmutablePerson(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public String getName() {
            return name;
        }
        
        public int getAge() {
            return age;
        }
        
        // Instead of setters, return a new instance with the modified value
        public ImmutablePerson withName(String newName) {
            return new ImmutablePerson(newName, this.age);
        }
        
        public ImmutablePerson withAge(int newAge) {
            return new ImmutablePerson(this.name, newAge);
        }
    }
} 