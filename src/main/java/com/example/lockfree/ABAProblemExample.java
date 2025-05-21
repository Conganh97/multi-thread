package com.example.lockfree;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.LockSupport;

public class ABAProblemExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("ABA Problem Example");
        System.out.println("==================");
        System.out.println("This example demonstrates the ABA problem in lock-free algorithms");
        System.out.println("and how to solve it using AtomicStampedReference.\n");
        
        System.out.println("Part 1: Demonstrating the ABA problem");
        demonstrateABAProblem();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 2: Solving the ABA problem with AtomicStampedReference");
        solveWithStampedReference();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 3: Alternative solutions to the ABA problem");
        alternativeSolutions();
        
        System.out.println("\nExplanation:");
        System.out.println("The ABA problem occurs in CAS operations when a variable changes from");
        System.out.println("value A to B and then back to A between a thread's read and CAS operations:");
        System.out.println("1. Thread 1 reads value A and gets suspended");
        System.out.println("2. Thread 2 changes value from A to B, then back to A");
        System.out.println("3. Thread 1 resumes and CAS succeeds (because value is still A)");
        System.out.println("4. However, the state is not what Thread 1 expected (structure changed)");
        System.out.println("Solutions include:");
        System.out.println("- AtomicStampedReference: Associates a version number with the reference");
        System.out.println("- AtomicMarkableReference: Associates a mark with the reference");
        System.out.println("- Immutable data structures: Values can't be modified after creation");
        System.out.println("- Hazard pointers: Mark objects that are in use by threads");
    }
    
    // Part 1: Demonstrating the ABA problem
    private static void demonstrateABAProblem() throws InterruptedException {
        // Create a shared atomic reference
        final AtomicReference<Node> head = new AtomicReference<>(new Node(1));
        // Add a few more nodes to create a linked list: 1 -> 2 -> 3
        head.get().next = new Node(2);
        head.get().next.next = new Node(3);
        
        System.out.println("Initial list state:");
        printList(head.get());
        
        // Thread 1 will simulate a pop operation but be suspended after reading the head
        Thread thread1 = new Thread(() -> {
            // Simulate a pop operation with delay to demonstrate ABA
            Node oldHead = head.get();
            System.out.println("Thread 1: Read head node with value: " + oldHead.value);
            
            // Artificially pause thread1 to allow thread2 to modify the list
            LockSupport.parkNanos(500_000_000); // 500ms
            
            // Resume and try to perform our pop with CAS
            // This will succeed even though the list structure has changed!
            boolean success = head.compareAndSet(oldHead, oldHead.next);
            System.out.println("Thread 1: CAS " + (success ? "succeeded" : "failed") + 
                              " to pop node " + oldHead.value);
            
            System.out.println("Thread 1: List after my operation:");
            printList(head.get());
        });
        
        // Thread 2 will make quick changes while Thread 1 is suspended
        Thread thread2 = new Thread(() -> {
            // Pop the first node (value 1)
            Node oldHead = head.get();
            head.compareAndSet(oldHead, oldHead.next);
            System.out.println("Thread 2: Popped node with value: " + oldHead.value);
            
            // Pop the second node (value 2)
            oldHead = head.get();
            head.compareAndSet(oldHead, oldHead.next);
            System.out.println("Thread 2: Popped node with value: " + oldHead.value);
            
            // IMPORTANT: Change the list structure but put the same value back at head
            Node nodeWithValue1 = new Node(1); // Create a new node with value 1 (same as original head)
            nodeWithValue1.next = head.get();  // Point it to the current head (node 3)
            
            // Push the new node - the head value is now 1 again, but it's a different node!
            head.compareAndSet(head.get(), nodeWithValue1);
            System.out.println("Thread 2: Pushed new node with value: " + nodeWithValue1.value);
            
            System.out.println("Thread 2: List after my operations:");
            printList(head.get());
        });
        
        // Start both threads
        thread1.start();
        
        // Let thread1 start and get suspended
        Thread.sleep(100);
        
        thread2.start();
        
        // Wait for threads to finish
        thread1.join();
        thread2.join();
        
        System.out.println("\nFinal list state:");
        printList(head.get());
        
        System.out.println("\nAnalysis: This demonstrates the ABA problem. Thread 1's CAS succeeded even");
        System.out.println("though the list changed completely while it was suspended. The reason is");
        System.out.println("that CAS only checks if the reference is the same, not if the entire state is unchanged.");
    }
    
    // Part 2: Solving the ABA problem with AtomicStampedReference
    private static void solveWithStampedReference() throws InterruptedException {
        // Create a shared atomic stamped reference with initial value and stamp
        final AtomicStampedReference<Node> head = 
            new AtomicStampedReference<>(new Node(1), 0);
        
        // Add a few more nodes to create a linked list: 1 -> 2 -> 3
        head.getReference().next = new Node(2);
        head.getReference().next.next = new Node(3);
        
        System.out.println("Initial list state:");
        printList(head.getReference());
        
        // Thread 1 will now use a stamped reference for the pop operation
        Thread thread1 = new Thread(() -> {
            // Read the head node and its stamp
            int[] stampHolder = new int[1];
            Node oldHead = head.get(stampHolder);
            int stamp = stampHolder[0];
            
            System.out.println("Thread 1: Read head node with value: " + oldHead.value + 
                              ", stamp: " + stamp);
            
            // Artificially pause thread1 to allow thread2 to modify the list
            LockSupport.parkNanos(500_000_000); // 500ms
            
            // Resume and try to perform our pop with stamped CAS
            // This will fail because the stamp has changed, even if the reference looks the same!
            boolean success = head.compareAndSet(oldHead, oldHead.next, stamp, stamp + 1);
            System.out.println("Thread 1: CAS " + (success ? "succeeded" : "failed") + 
                              " to pop node " + oldHead.value);
            
            if (!success) {
                // Since CAS failed, get the current state and try again
                oldHead = head.getReference();
                stampHolder = new int[1];
                head.get(stampHolder);
                System.out.println("Thread 1: Current head value: " + oldHead.value + 
                                  ", stamp: " + stampHolder[0]);
            }
        });
        
        // Thread 2 will make the same changes as before, but with stamps
        Thread thread2 = new Thread(() -> {
            // Pop the first node (value 1)
            int[] stampHolder = new int[1];
            Node oldHead = head.get(stampHolder);
            int stamp = stampHolder[0];
            
            head.compareAndSet(oldHead, oldHead.next, stamp, stamp + 1);
            System.out.println("Thread 2: Popped node with value: " + oldHead.value + 
                              ", new stamp: " + (stamp + 1));
            
            // Pop the second node (value 2)
            oldHead = head.getReference();
            stamp = head.getStamp();
            
            head.compareAndSet(oldHead, oldHead.next, stamp, stamp + 1);
            System.out.println("Thread 2: Popped node with value: " + oldHead.value + 
                              ", new stamp: " + (stamp + 1));
            
            // IMPORTANT: Change the list structure but put the same value back at head
            Node nodeWithValue1 = new Node(1); // Create a new node with value 1 (same as original head)
            nodeWithValue1.next = head.getReference();  // Point it to the current head (node 3)
            
            // Push the new node with updated stamp
            stamp = head.getStamp();
            head.compareAndSet(head.getReference(), nodeWithValue1, stamp, stamp + 1);
            System.out.println("Thread 2: Pushed new node with value: " + nodeWithValue1.value + 
                              ", new stamp: " + (stamp + 1));
            
            System.out.println("Thread 2: List after my operations:");
            printList(head.getReference());
        });
        
        // Start both threads
        thread1.start();
        
        // Let thread1 start and get suspended
        Thread.sleep(100);
        
        thread2.start();
        
        // Wait for threads to finish
        thread1.join();
        thread2.join();
        
        System.out.println("\nFinal list state:");
        printList(head.getReference());
        
        System.out.println("\nAnalysis: With AtomicStampedReference, Thread 1's CAS failed because");
        System.out.println("the stamp changed, even though the head node had the same value. This");
        System.out.println("protected against the ABA problem.");
    }
    
    // Part 3: Alternative solutions to the ABA problem
    private static void alternativeSolutions() {
        System.out.println("Besides AtomicStampedReference, there are other ways to handle the ABA problem:");
        
        // 1. AtomicMarkableReference
        System.out.println("\n1. AtomicMarkableReference:");
        System.out.println("   - Similar to AtomicStampedReference but uses a boolean mark instead of an integer stamp");
        System.out.println("   - Useful when you only need to mark a reference as 'logically removed'");
        System.out.println("   - Less overhead than AtomicStampedReference");
        System.out.println("   - Example: java.util.concurrent.atomic.AtomicMarkableReference");
        
        // 2. Immutable data structures
        System.out.println("\n2. Immutable data structures:");
        System.out.println("   - Create new nodes/objects instead of modifying existing ones");
        System.out.println("   - Each modification creates a new version of the data structure");
        System.out.println("   - No possibility of an object changing back to a previous state");
        System.out.println("   - Example: Functional data structures like those in persistent collections libraries");
        
        // 3. Double-width CAS
        System.out.println("\n3. Double-width CAS:");
        System.out.println("   - Use a pair of values (reference and counter) in a single atomic operation");
        System.out.println("   - Some architectures support atomic operations on wider values");
        System.out.println("   - Java's internal implementation uses this on some platforms");
        
        // 4. Hazard pointers
        System.out.println("\n4. Hazard pointers:");
        System.out.println("   - Threads register references they're currently accessing");
        System.out.println("   - Other threads check this registry before reclaiming memory");
        System.out.println("   - Prevents ABA by ensuring references in use aren't recycled");
        System.out.println("   - More complex implementation but useful for memory management");
        
        // 5. Garbage collection
        System.out.println("\n5. Garbage collection:");
        System.out.println("   - In languages with GC (like Java), ABA is less problematic for memory issues");
        System.out.println("   - GC ensures an object won't be reused until all references are gone");
        System.out.println("   - However, logical ABA issues can still occur with same-valued but different objects");
    }
    
    // Simple Node class for our demonstrations
    static class Node {
        final int value;
        Node next;
        
        Node(int value) {
            this.value = value;
        }
    }
    
    // Utility method to print a linked list
    private static void printList(Node head) {
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.value);
            if (current.next != null) {
                sb.append(" -> ");
            }
            current = current.next;
        }
        System.out.println(sb.toString());
    }
} 