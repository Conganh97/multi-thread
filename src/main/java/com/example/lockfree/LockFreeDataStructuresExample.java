package com.example.lockfree;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class LockFreeDataStructuresExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Lock-Free Data Structures Example");
        System.out.println("================================");
        System.out.println("This example demonstrates lock-free data structures from");
        System.out.println("java.util.concurrent package for thread-safe collections.\n");
        
        System.out.println("Part 1: ConcurrentLinkedQueue example");
        concurrentQueueExample();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 2: ConcurrentSkipListMap example");
        concurrentMapExample();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 3: CopyOnWriteArrayList example");
        copyOnWriteExample();
        
        Thread.sleep(1000);
        
        System.out.println("\nPart 4: ConcurrentHashMap example");
        concurrentHashMapExample();
        
        System.out.println("\nExplanation:");
        System.out.println("Lock-free data structures provide thread-safe access without using locks:");
        System.out.println("- Typically use CAS (Compare-And-Swap) operations internally");
        System.out.println("- Higher throughput under high contention than locked collections");
        System.out.println("- Guarantee thread-safety without blocking threads");
        System.out.println("- Useful for concurrent applications with many threads");
        System.out.println("- Examples: ConcurrentLinkedQueue, ConcurrentHashMap, ConcurrentSkipListMap");
    }
    
    // Part 1: Demonstrate ConcurrentLinkedQueue
    private static void concurrentQueueExample() throws InterruptedException {
        // Create a lock-free queue
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        
        System.out.println("ConcurrentLinkedQueue is a thread-safe, non-blocking queue implementation");
        System.out.println("based on a linked node structure. It's suitable for high-throughput");
        System.out.println("concurrent applications with many producers and consumers.\n");
        
        // Create producer and consumer threads
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Producer tasks
        for (int i = 0; i < 2; i++) {
            final int producerId = i;
            executor.submit(() -> {
                for (int j = 0; j < 10; j++) {
                    String item = "P" + producerId + "-" + j;
                    queue.add(item);
                    System.out.println("Producer " + producerId + " added: " + item);
                    try {
                        Thread.sleep((int) (Math.random() * 10));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
        }
        
        // Consumer tasks
        for (int i = 0; i < 2; i++) {
            final int consumerId = i;
            executor.submit(() -> {
                for (int j = 0; j < 10; j++) {
                    String item;
                    // Poll until we get an item
                    while ((item = queue.poll()) == null) {
                        Thread.yield(); // Be nice to other threads
                    }
                    System.out.println("Consumer " + consumerId + " processed: " + item);
                    try {
                        Thread.sleep((int) (Math.random() * 20));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
        }
        
        // Let the simulation run for a while
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("\nRemaining items in queue: " + queue.size());
        if (!queue.isEmpty()) {
            System.out.println("Items: " + queue);
        }
    }
    
    // Part 2: Demonstrate ConcurrentSkipListMap
    private static void concurrentMapExample() throws InterruptedException {
        // Create a lock-free sorted map
        ConcurrentSkipListMap<Integer, String> skipListMap = new ConcurrentSkipListMap<>();
        
        System.out.println("ConcurrentSkipListMap is a scalable concurrent NavigableMap implementation");
        System.out.println("based on a skip list data structure. It maintains elements in sorted order");
        System.out.println("and provides efficient operations for concurrent access.\n");
        
        // Populate the map with some initial values
        for (int i = 0; i < 10; i += 2) {
            skipListMap.put(i, "Initial-" + i);
        }
        
        System.out.println("Initial map contents:");
        skipListMap.forEach((k, v) -> System.out.println("  " + k + " -> " + v));
        
        // Create threads that perform operations on the map
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Thread that adds new entries
        executor.submit(() -> {
            for (int i = 1; i < 10; i += 2) {
                skipListMap.put(i, "Added-" + i);
                System.out.println("Added: " + i + " -> Added-" + i);
                try {
                    Thread.sleep((int) (Math.random() * 20));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        // Thread that updates existing entries
        executor.submit(() -> {
            for (int i = 0; i < 10; i += 2) {
                skipListMap.compute(i, (k, v) -> {
                    System.out.println("Updating: " + k + " from " + v);
                    return "Updated-" + k;
                });
                try {
                    Thread.sleep((int) (Math.random() * 20));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        // Thread that performs range queries
        executor.submit(() -> {
            for (int i = 0; i < 3; i++) {
                int start = ThreadLocalRandom.current().nextInt(0, 5);
                int end = start + ThreadLocalRandom.current().nextInt(1, 5);
                
                // Get submap within range [start, end)
                Map<Integer, String> subMap = skipListMap.subMap(start, end);
                
                System.out.println("Range query [" + start + ", " + end + "):");
                subMap.forEach((k, v) -> System.out.println("  " + k + " -> " + v));
                
                try {
                    Thread.sleep((int) (Math.random() * 100));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        // Let the simulation run for a while
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("\nFinal map contents (naturally sorted by key):");
        skipListMap.forEach((k, v) -> System.out.println("  " + k + " -> " + v));
        
        // Demonstrate other useful operations
        System.out.println("\nDemonstrating navigational operations:");
        System.out.println("First entry: " + skipListMap.firstEntry());
        System.out.println("Last entry: " + skipListMap.lastEntry());
        System.out.println("Floor entry for 4.5: " + skipListMap.floorEntry(4));
        System.out.println("Ceiling entry for 4.5: " + skipListMap.ceilingEntry(5));
        
        // ConcurrentSkipListSet
        ConcurrentSkipListSet<Integer> skipListSet = new ConcurrentSkipListSet<>();
        System.out.println("\nConcurrentSkipListSet is similar but stores only keys, not key-value pairs");
    }
    
    // Part 3: Demonstrate CopyOnWriteArrayList
    private static void copyOnWriteExample() throws InterruptedException {
        // Create a copy-on-write array list
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        
        System.out.println("CopyOnWriteArrayList creates a fresh copy of the underlying array");
        System.out.println("for every modification operation, making it thread-safe without locks.");
        System.out.println("It's optimized for cases where reads greatly outnumber writes.\n");
        
        // Add some initial elements
        list.add("Apple");
        list.add("Banana");
        list.add("Cherry");
        
        System.out.println("Initial list: " + list);
        
        // Create a thread that reads the list while another thread modifies it
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Writer thread
        executor.submit(() -> {
            for (int i = 0; i < 3; i++) {
                String item = "Fruit-" + i;
                list.add(item);
                System.out.println("Added: " + item + ", List size: " + list.size());
                
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        // Reader thread - creates an iterator and then checks whether changes are visible
        executor.submit(() -> {
            // Create an iterator - this takes a snapshot of the list
            Iterator<String> iterator = list.iterator();
            
            System.out.println("Iterator created, will now sleep");
            try {
                // Sleep to allow writer thread to make changes
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Read using the iterator - changes made after iterator creation won't be visible
            System.out.println("Reading with iterator:");
            while (iterator.hasNext()) {
                System.out.println("  Iterator sees: " + iterator.next());
            }
            
            // Create a new iterator to see the changes
            System.out.println("Creating new iterator to see changes");
            iterator = list.iterator();
            System.out.println("Reading with new iterator:");
            while (iterator.hasNext()) {
                System.out.println("  New iterator sees: " + iterator.next());
            }
            
            // Try to modify during iteration - unlike ArrayList, this won't throw ConcurrentModificationException
            try {
                System.out.println("\nTrying direct access while iterating (no exception expected):");
                for (String s : list) {
                    System.out.println("  Reading: " + s);
                    if (s.equals("Banana")) {
                        list.remove(s);
                        System.out.println("  Removed: " + s);
                    }
                }
            } catch (Exception e) {
                System.out.println("Unexpected exception: " + e);
            }
        });
        
        // Let the simulation run for a while
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("\nFinal list contents: " + list);
    }
    
    // Part 4: Demonstrate ConcurrentHashMap
    private static void concurrentHashMapExample() throws InterruptedException {
        // Create a concurrent hash map
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        
        System.out.println("ConcurrentHashMap is a thread-safe implementation of HashMap");
        System.out.println("that offers better concurrency than Hashtable by using lock striping");
        System.out.println("(multiple locks for different segments of the map) and other techniques.\n");
        
        // Add some initial entries
        map.put("Apple", 1);
        map.put("Banana", 2);
        map.put("Cherry", 3);
        
        System.out.println("Initial map: " + map);
        
        // Create multiple threads that perform operations on the map
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Thread that adds new entries
        executor.submit(() -> {
            for (int i = 0; i < 5; i++) {
                String key = "Fruit-" + i;
                map.put(key, i + 10);
                System.out.println("Added: " + key + " -> " + (i + 10));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        // Thread that updates existing entries
        executor.submit(() -> {
            String[] keys = {"Apple", "Banana", "Cherry", "Fruit-0", "Fruit-1"};
            for (String key : keys) {
                // Wait until the key exists
                while (!map.containsKey(key)) {
                    Thread.yield();
                }
                
                // Atomic update
                map.compute(key, (k, oldValue) -> {
                    int newValue = (oldValue == null) ? 1 : oldValue * 2;
                    System.out.println("Updated: " + k + " from " + oldValue + " to " + newValue);
                    return newValue;
                });
                
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        // Thread that demonstrates atomic operations
        executor.submit(() -> {
            // putIfAbsent - only puts if key not present
            map.putIfAbsent("Orange", 5);
            System.out.println("After putIfAbsent: Orange -> " + map.get("Orange"));
            
            // Try to put again, won't change value
            map.putIfAbsent("Orange", 10);
            System.out.println("After second putIfAbsent: Orange -> " + map.get("Orange"));
            
            // replace - only replaces if key present
            map.replace("Orange", 15);
            System.out.println("After replace: Orange -> " + map.get("Orange"));
            
            // replace with condition - only replaces if current value matches
            boolean replaced = map.replace("Orange", 15, 20);
            System.out.println("Conditional replace " + (replaced ? "succeeded" : "failed") + 
                              ": Orange -> " + map.get("Orange"));
            
            // remove with condition
            boolean removed = map.remove("Apple", 1);
            System.out.println("Conditional remove " + (removed ? "succeeded" : "failed") + 
                              " for Apple (value was " + map.get("Apple") + ")");
        });
        
        // Let the simulation run for a while
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("\nFinal map contents: " + map);
        
        // Demonstrate some bulk operations
        System.out.println("\nDemonstrating parallel bulk operations (added in Java 8):");
        
        // Parallel forEach
        System.out.println("Parallel forEach:");
        map.forEach(4, (key, value) -> 
            System.out.println("  " + key + " -> " + value + " (processed by thread " + 
                              Thread.currentThread().getName() + ")"));
        
        // Compute entry values in parallel
        System.out.println("\nMultiplying all values by 10 in parallel:");
        map.replaceAll((key, value) -> value * 10);
        System.out.println("After parallel replaceAll: " + map);
        
        // Reduce entries in parallel
        int sum = map.reduceValues(2, Integer::sum);
        System.out.println("\nSum of all values (parallel reduction): " + sum);
        
        // Search in parallel
        String found = map.search(2, (key, value) -> 
            value > 100 ? key : null);
        System.out.println("Key with value > 100: " + (found != null ? found : "Not found"));
    }
} 