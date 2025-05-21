package com.example.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CompletableFutureIOExample {
    public static void main(String[] args) {
        System.out.println("CompletableFuture for Asynchronous IO Example");
        System.out.println("===========================================");
        System.out.println("This example demonstrates how to use CompletableFuture for");
        System.out.println("asynchronous IO operations in Java.\n");
        
        try {
            // Create a temporary file for the demonstration
            Path testFile = Files.createTempFile("completablefuture-test", ".txt");
            
            // Write some content to the file
            Files.write(testFile, "Hello, this is a test file for CompletableFuture IO operations!".getBytes());
            
            System.out.println("Created temporary test file: " + testFile);
            System.out.println("\nPart 1: Basic Asynchronous File Read");
            basicAsyncRead(testFile);
            
            Thread.sleep(1000);
            
            System.out.println("\nPart 2: Chaining Asynchronous Operations");
            chainingAsyncOperations(testFile);
            
            Thread.sleep(1000);
            
            System.out.println("\nPart 3: Handling Errors");
            handleErrors(testFile);
            
            Thread.sleep(1000);
            
            System.out.println("\nPart 4: Combining Multiple Asynchronous Operations");
            combineAsyncOperations(testFile);
            
            // Clean up the test file when done
            Files.deleteIfExists(testFile);
            
            System.out.println("\nExplanation:");
            System.out.println("CompletableFuture provides a powerful way to work with asynchronous operations:");
            System.out.println("- Declarative style for composing asynchronous operations");
            System.out.println("- Handles exceptions across asynchronous boundaries");
            System.out.println("- Supports various completion stages and transformations");
            System.out.println("- Can be combined with NIO for powerful asynchronous IO");
            System.out.println("- Better supports functional programming patterns than traditional callbacks");
            System.out.println("- No blocking required for coordination of multiple async operations");
            
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.err.println("Error in demonstration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Part 1: Basic asynchronous file read using CompletableFuture
    private static void basicAsyncRead(Path file) throws IOException, InterruptedException, ExecutionException {
        System.out.println("Reading file asynchronously using CompletableFuture...");
        
        // Create a CompletableFuture to read a file asynchronously
        CompletableFuture<String> readFuture = readFileAsync(file);
        
        // We can do other work while the file is being read
        System.out.println("Main thread continues executing while file is being read...");
        System.out.println("Doing some other work in the main thread...");
        
        // Simulate some work in the main thread
        for (int i = 0; i < 3; i++) {
            Thread.sleep(200);
            System.out.println("Main thread: working...");
        }
        
        // Wait for the file read to complete and get the result
        String content = readFuture.get();
        System.out.println("File content read asynchronously: " + content);
    }
    
    // Part 2: Chaining asynchronous operations
    private static void chainingAsyncOperations(Path file) throws ExecutionException, InterruptedException {
        System.out.println("Demonstrating chaining of asynchronous operations...");
        
        // Start with reading the file asynchronously
        CompletableFuture<String> readFuture = readFileAsync(file);
        
        // Chain transformations: convert to uppercase, count chars, then print
        CompletableFuture<Void> processChain = readFuture
            .thenApply(content -> {
                System.out.println("Transforming to uppercase...");
                return content.toUpperCase();
            })
            .thenApply(upperContent -> {
                System.out.println("Calculating length...");
                return "Length of uppercase content: " + upperContent.length();
            })
            .thenAccept(result -> {
                System.out.println("Final result: " + result);
            });
        
        // Wait for all operations to complete
        processChain.get();
        System.out.println("All chained operations completed");
    }
    
    // Part 3: Handling errors in asynchronous operations
    private static void handleErrors(Path file) {
        System.out.println("Demonstrating error handling in asynchronous operations...");
        
        // Try to read a non-existent file
        Path nonExistentFile = Paths.get("non-existent-file.txt");
        
        CompletableFuture<String> errorFuture = readFileAsync(nonExistentFile)
            .thenApply(content -> {
                System.out.println("This won't be reached if there's an error");
                return content.toUpperCase();
            })
            .exceptionally(ex -> {
                System.out.println("Handling error: " + ex.getMessage());
                return "Error occurred: " + ex.getCause().getMessage();
            });
        
        try {
            String result = errorFuture.get();
            System.out.println("Result with error handling: " + result);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("This shouldn't happen because we handled the error: " + e.getMessage());
        }
    }
    
    // Part 4: Combining multiple asynchronous operations
    private static void combineAsyncOperations(Path file) throws IOException, ExecutionException, InterruptedException {
        System.out.println("Demonstrating combining multiple asynchronous operations...");
        
        // Create another temporary file
        Path secondFile = Files.createTempFile("completablefuture-second", ".txt");
        Files.write(secondFile, "This is the second file for testing combined operations.".getBytes());
        
        try {
            // Read both files asynchronously
            CompletableFuture<String> readFirst = readFileAsync(file);
            CompletableFuture<String> readSecond = readFileAsync(secondFile);
            
            // Combine results when both reads complete
            CompletableFuture<String> combined = readFirst.thenCombine(readSecond, 
                    (content1, content2) -> {
                        System.out.println("Both files read, combining results...");
                        return "Combined content length: " + 
                               (content1.length() + content2.length());
                    });
            
            // Wait for the combined result
            String result = combined.get();
            System.out.println("Combined result: " + result);
            
            // Run multiple operations in parallel
            System.out.println("\nRunning multiple operations in parallel...");
            CompletableFuture<Void> allOf = CompletableFuture.allOf(
                processFileAsync(file, "Operation 1"),
                processFileAsync(file, "Operation 2"),
                processFileAsync(secondFile, "Operation 3")
            );
            
            // Wait for all parallel operations to complete
            allOf.get();
            System.out.println("All parallel operations completed");
            
        } finally {
            // Clean up the second test file
            Files.deleteIfExists(secondFile);
        }
    }
    
    // Helper method to read a file asynchronously and return a CompletableFuture
    private static CompletableFuture<String> readFileAsync(Path path) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        try {
            AsynchronousFileChannel channel = 
                AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            
            channel.read(buffer, 0, buffer, new java.nio.channels.CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    try {
                        if (result > 0) {
                            attachment.flip();
                            byte[] data = new byte[attachment.remaining()];
                            attachment.get(data);
                            future.complete(new String(data, StandardCharsets.UTF_8));
                        } else {
                            future.complete("");
                        }
                        channel.close();
                    } catch (IOException e) {
                        future.completeExceptionally(e);
                    }
                }
                
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.completeExceptionally(exc);
                    try {
                        channel.close();
                    } catch (IOException e) {
                        // Already completing exceptionally, just log
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    // Helper method to simulate processing a file with a given operation name
    private static CompletableFuture<Void> processFileAsync(Path path, String operationName) {
        return CompletableFuture.runAsync(() -> {
            try {
                System.out.println(operationName + ": Starting on thread " + Thread.currentThread().getName());
                // Simulate processing time
                Thread.sleep((long) (Math.random() * 1000));
                String content = Files.readString(path);
                System.out.println(operationName + ": Processed file with " + content.length() + " characters");
            } catch (Exception e) {
                System.err.println(operationName + ": Error - " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
} 