package com.example.performance;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelStreamsExample {
    public static void main(String[] args) {
        System.out.println("Parallel Streams Example");
        System.out.println("=======================");
        
        // Create a large list of numbers
        List<Integer> numbers = IntStream.rangeClosed(1, 10_000_000)
                                       .boxed()
                                       .collect(Collectors.toList());
        
        System.out.println("Processing " + numbers.size() + " numbers");
        
        // Sequential stream
        long startSeq = System.currentTimeMillis();
        long sumSeq = numbers.stream()
                          .mapToLong(ParallelStreamsExample::computeExpensive)
                          .sum();
        long endSeq = System.currentTimeMillis();
        
        System.out.println("Sequential sum: " + sumSeq);
        System.out.println("Sequential time: " + (endSeq - startSeq) + "ms");
        
        // Parallel stream
        long startPar = System.currentTimeMillis();
        long sumPar = numbers.parallelStream()
                          .mapToLong(ParallelStreamsExample::computeExpensive)
                          .sum();
        long endPar = System.currentTimeMillis();
        
        System.out.println("Parallel sum: " + sumPar);
        System.out.println("Parallel time: " + (endPar - startPar) + "ms");
        System.out.println("Speedup: " + String.format("%.2f", (double)(endSeq - startSeq) / (endPar - startPar)) + "x");
        
        // Example of parallel stream operations with custom collector
        System.out.println("\nExample of parallel processing with strings:");
        List<String> words = Arrays.asList(
            "Java", "Concurrent", "Parallel", "Stream", "Processing", 
            "Multithreading", "Performance", "Example"
        );
        
        // Sequential processing
        startSeq = System.currentTimeMillis();
        String resultSeq = words.stream()
                             .map(String::toUpperCase)
                             .collect(Collectors.joining(", "));
        endSeq = System.currentTimeMillis();
        
        // Parallel processing
        startPar = System.currentTimeMillis();
        String resultPar = words.parallelStream()
                             .map(String::toUpperCase)
                             .collect(Collectors.joining(", "));
        endPar = System.currentTimeMillis();
        
        System.out.println("Sequential result: " + resultSeq);
        System.out.println("Sequential time: " + (endSeq - startSeq) + "ms");
        System.out.println("Parallel result: " + resultPar);
        System.out.println("Parallel time: " + (endPar - startPar) + "ms");
        
        System.out.println("\nNote: For small workloads, parallel streams might not be faster due to overhead.");
        System.out.println("Parallel streams work best when:");
        System.out.println("1. The data source is large");
        System.out.println("2. Operations are CPU-intensive");
        System.out.println("3. The source can be efficiently split (e.g., ArrayList, not LinkedList)");
    }
    
    private static long computeExpensive(int n) {
        // Simulate a more expensive computation
        double result = 0;
        for (int i = 0; i < 10; i++) {
            result += Math.sqrt(n * Math.sin(i));
        }
        return (long)result;
    }
} 