package com.example.datasharing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Immutable class example
public final class ImmutablePersonExample {
    // All fields are final
    private final String name;
    private final int age;
    private final Map<String, String> attributes;
    
    public ImmutablePersonExample(String name, int age, Map<String, String> attributes) {
        this.name = name;
        this.age = age;
        
        // Defensive copy to ensure immutability of the mutable map
        Map<String, String> tempMap = new HashMap<>();
        if (attributes != null) {
            tempMap.putAll(attributes);
        }
        this.attributes = Collections.unmodifiableMap(tempMap);
    }
    
    // Only getters, no setters
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public Map<String, String> getAttributes() {
        return attributes; // Already unmodifiable
    }
    
    public static void main(String[] args) {
        System.out.println("Immutable Objects Example");
        System.out.println("========================");
        
        Map<String, String> attrs = new HashMap<>();
        attrs.put("eyeColor", "blue");
        
        ImmutablePersonExample person = new ImmutablePersonExample("John", 30, attrs);
        
        System.out.println("Created person: " + person.getName() + ", " + person.getAge() + 
                          ", attributes: " + person.getAttributes());
        
        // Create threads that read the immutable object
        Runnable readTask = () -> {
            System.out.println(Thread.currentThread().getName() + 
                              " reading: " + person.getName() + 
                              ", " + person.getAge() +
                              ", " + person.getAttributes());
        };
        
        Thread thread1 = new Thread(readTask, "Thread-1");
        Thread thread2 = new Thread(readTask, "Thread-2");
        
        thread1.start();
        thread2.start();
        
        // This won't affect the immutable object's internal state
        System.out.println("\nModifying the original map...");
        attrs.put("hairColor", "brown");
        System.out.println("Original map now: " + attrs);
        System.out.println("Person's attributes: " + person.getAttributes());
        System.out.println("The immutable object's attributes remained unchanged");
        
        // This will throw UnsupportedOperationException
        try {
            System.out.println("\nTrying to modify the attributes map from the immutable object...");
            person.getAttributes().put("height", "180cm");
        } catch (UnsupportedOperationException e) {
            System.out.println("Couldn't modify attributes: " + e.getMessage());
            System.out.println("The unmodifiable map prevents changes");
        }
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\nExplanation:");
        System.out.println("Immutable objects cannot be changed after creation, making them inherently thread-safe.");
        System.out.println("Their state is fixed, so multiple threads can safely access them without synchronization.");
        System.out.println("Best practices for immutability include:");
        System.out.println("1. Make all fields final");
        System.out.println("2. Don't provide setters or methods that modify state");
        System.out.println("3. Make defensive copies of mutable objects in constructors and getters");
        System.out.println("4. Make the class final to prevent subclassing");
    }
} 