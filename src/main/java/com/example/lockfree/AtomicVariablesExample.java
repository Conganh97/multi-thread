package com.example.lockfree;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicVariablesExample {
    // Regular variables (not thread-safe)
    private static int unsafeCounter = 0;
    private static boolean unsafeFlag = false;
    private static int[] unsafeArray = new int[10];
    private static String unsafeReference = "initial";
    
    // Atomic variables (thread-safe)
    private static AtomicInteger atomicCounter = new AtomicInteger(0);
    private static AtomicBoolean atomicFlag = new AtomicBoolean(false);
    private static AtomicIntegerArray atomicArray = new AtomicIntegerArray(10);
    private static AtomicReference<String> atomicReference = new AtomicReference<>("initial");
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Atomic Variables Example");
        System.out.println("=======================");
        System.out.println("This example demonstrates the use of atomic variables from");
        System.out.println("java.util.concurrent.atomic for thread-safe operations without locks.\n");
        
        System.out.println("Part 1: Comparing unsafe and atomic counters");
        compareCounters();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 2: Operations on atomic variables");
        demonstrateAtomicOperations();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 3: Atomic references and arrays");
        demonstrateAtomicReferences();
        
        System.out.println("\nExplanation:");
        System.out.println("Atomic variables provide a way to perform thread-safe operations without using locks:");
        System.out.println("- Based on processor-specific atomic instructions (like Compare-And-Swap)");
        System.out.println("- More efficient than locking for simple operations");
        System.out.println("- Support atomic read-modify-write operations (increment, update, etc.)");
        System.out.println("- Guarantee visibility of changes across threads (memory ordering)");
        System.out.println("- Common classes: AtomicInteger, AtomicLong, AtomicBoolean, AtomicReference, etc.");
    }
    
    // Part 1: Compare regular counter with atomic counter
    private static void compareCounters() throws InterruptedException {
        int numThreads = 10;
        int incrementsPerThread = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        // Reset counters
        unsafeCounter = 0;
        atomicCounter.set(0);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    // Unsafe increment - prone to lost updates
                    unsafeCounter++;
                    
                    // Atomic increment - thread-safe
                    atomicCounter.incrementAndGet();
                }
            });
        }
        
        // Wait for all tasks to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        // Print results
        System.out.println("Expected count: " + (numThreads * incrementsPerThread));
        System.out.println("Unsafe counter: " + unsafeCounter + 
                          " (likely less than expected due to race conditions)");
        System.out.println("Atomic counter: " + atomicCounter.get() + 
                          " (correct because operations are atomic)");
    }
    
    // Part 2: Demonstrate various atomic operations
    private static void demonstrateAtomicOperations() {
        // 1. Basic operations
        AtomicInteger value = new AtomicInteger(5);
        System.out.println("Initial value: " + value.get());
        
        // Set and Get
        value.set(10);
        System.out.println("After set(10): " + value.get());
        
        // getAndIncrement (post-increment) - returns old value, then increments
        int oldValue = value.getAndIncrement();
        System.out.println("getAndIncrement returned: " + oldValue + ", new value: " + value.get());
        
        // incrementAndGet (pre-increment) - increments, then returns new value
        int newValue = value.incrementAndGet();
        System.out.println("incrementAndGet returned: " + newValue);
        
        // getAndAdd - adds a delta and returns old value
        oldValue = value.getAndAdd(5);
        System.out.println("getAndAdd(5) returned: " + oldValue + ", new value: " + value.get());
        
        // addAndGet - adds a delta and returns new value
        newValue = value.addAndGet(3);
        System.out.println("addAndGet(3) returned: " + newValue);
        
        // getAndSet - returns old value and sets new value
        oldValue = value.getAndSet(20);
        System.out.println("getAndSet(20) returned: " + oldValue + ", new value: " + value.get());
        
        // 2. Conditional updates
        System.out.println("\nConditional updates:");
        
        // compareAndSet - updates only if current value matches expected
        boolean success = value.compareAndSet(20, 25);
        System.out.println("compareAndSet(20, 25): " + success + ", new value: " + value.get());
        
        // Try again with wrong expected value
        success = value.compareAndSet(20, 30); // Won't update because current is 25, not 20
        System.out.println("compareAndSet(20, 30): " + success + ", value still: " + value.get());
        
        // updateAndGet - perform arbitrary update function
        newValue = value.updateAndGet(x -> x * 2);
        System.out.println("updateAndGet(x -> x * 2) returned: " + newValue);
        
        // getAndUpdate - perform arbitrary update function, return old value
        oldValue = value.getAndUpdate(x -> x + 5);
        System.out.println("getAndUpdate(x -> x + 5) returned: " + oldValue + ", new value: " + value.get());
        
        // accumulateAndGet - combine with another value using a function
        newValue = value.accumulateAndGet(10, (x, y) -> x + y);
        System.out.println("accumulateAndGet(10, (x, y) -> x + y) returned: " + newValue);
    }
    
    // Part 3: Demonstrate AtomicReference and AtomicIntegerArray
    private static void demonstrateAtomicReferences() throws InterruptedException {
        // Create threads that update atomic reference and array
        int numThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                // 1. Update atomic reference
                String currentValue;
                do {
                    currentValue = atomicReference.get();
                } while (!atomicReference.compareAndSet(currentValue, currentValue + "-" + threadId));
                
                System.out.println("Thread " + threadId + " updated reference to: " + atomicReference.get());
                
                // 2. Update atomic array at thread-specific index
                int currentArrayValue = atomicArray.getAndIncrement(threadId % atomicArray.length());
                System.out.println("Thread " + threadId + " updated array[" + 
                                  (threadId % atomicArray.length()) + "] from " + 
                                  currentArrayValue + " to " + 
                                  atomicArray.get(threadId % atomicArray.length()));
                
                // 3. Use getAndUpdate on an array element
                currentArrayValue = atomicArray.getAndUpdate(threadId % atomicArray.length(), x -> x + 5);
                System.out.println("Thread " + threadId + " added 5 to array[" + 
                                  (threadId % atomicArray.length()) + "], old value: " + 
                                  currentArrayValue + ", new value: " + 
                                  atomicArray.get(threadId % atomicArray.length()));
            });
        }
        
        // Wait for all tasks to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        // Print final values
        System.out.println("\nFinal atomic reference value: " + atomicReference.get());
        
        System.out.println("Final atomic array values:");
        for (int i = 0; i < atomicArray.length(); i++) {
            System.out.println("array[" + i + "] = " + atomicArray.get(i));
        }
        
        // Demonstrate AtomicReference with objects
        System.out.println("\nUsing AtomicReference with objects:");
        
        class User {
            private final String name;
            private final int age;
            
            User(String name, int age) {
                this.name = name;
                this.age = age;
            }
            
            String getName() { return name; }
            int getAge() { return age; }
            
            User withAge(int newAge) {
                return new User(name, newAge);
            }
            
            @Override
            public String toString() {
                return "User(name='" + name + "', age=" + age + ")";
            }
        }
        
        AtomicReference<User> userRef = new AtomicReference<>(new User("Alice", 25));
        System.out.println("Initial user: " + userRef.get());
        
        // Thread-safe update of the user's age
        User oldUser, newUser;
        do {
            oldUser = userRef.get();
            newUser = oldUser.withAge(oldUser.getAge() + 1);
        } while (!userRef.compareAndSet(oldUser, newUser));
        
        System.out.println("After update: " + userRef.get());
    }
} 