package com.example.performance;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinExample {
    public static void main(String[] args) {
        System.out.println("Fork/Join Framework Example");
        System.out.println("==========================");
        
        // Create a large array of numbers
        int[] numbers = new int[100_000_000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }
        
        System.out.println("Computing sum of 1.." + numbers.length + " using Fork/Join");
        
        // Get the common fork-join pool
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        System.out.println("Fork/Join pool parallelism: " + forkJoinPool.getParallelism());
        
        long start = System.currentTimeMillis();
        // Submit the task to the pool
        long sum = forkJoinPool.invoke(new SumTask(numbers, 0, numbers.length));
        long end = System.currentTimeMillis();
        
        System.out.println("Sum: " + sum);
        System.out.println("Time taken: " + (end - start) + "ms");
        
        // Compare with sequential summing
        System.out.println("\nComputing the same sum sequentially...");
        start = System.currentTimeMillis();
        long seqSum = 0;
        for (int num : numbers) {
            seqSum += num;
        }
        end = System.currentTimeMillis();
        
        System.out.println("Sequential sum: " + seqSum);
        System.out.println("Time taken: " + (end - start) + "ms");
    }
    
    // RecursiveTask represents a task that returns a result
    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000; // Processing chunks of 10,000 elements sequentially
        private final int[] array;
        private final int start;
        private final int end;
        
        SumTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        protected Long compute() {
            int length = end - start;
            
            // If the work is small enough, compute it directly
            if (length <= THRESHOLD) {
                return computeDirectly();
            }
            
            // Otherwise, split the work
            int mid = start + length / 2;
            
            // Fork the first half
            SumTask leftTask = new SumTask(array, start, mid);
            leftTask.fork();
            
            // Compute the second half (reuse current thread)
            SumTask rightTask = new SumTask(array, mid, end);
            long rightResult = rightTask.compute();
            
            // Join the results
            long leftResult = leftTask.join();
            
            return leftResult + rightResult;
        }
        
        private long computeDirectly() {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        }
    }
} 