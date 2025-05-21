package com.example.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolServerExample {
    private static final int PORT = 8080;
    private static final int POOL_SIZE = 5;  // Fixed thread pool size
    private static final AtomicInteger connectionCount = new AtomicInteger(0);
    private static volatile boolean serverRunning = true;
    
    public static void main(String[] args) {
        System.out.println("Thread Pool Model Example");
        System.out.println("=======================");
        System.out.println("This example demonstrates the Thread Pool model for handling");
        System.out.println("multiple client connections in a server application.\n");
        
        System.out.println("In this example, we'll simulate a server that uses a fixed thread pool");
        System.out.println("to handle multiple client connections efficiently.\n");
        
        // This is a simulation - we're not actually starting the server
        simulateServer();
        
        System.out.println("\nExplanation:");
        System.out.println("Thread Pool Model Characteristics:");
        System.out.println("- Better resource management than thread-per-connection");
        System.out.println("- Controlled concurrency with configurable pool size");
        System.out.println("- Reuses threads for multiple connections");
        System.out.println("- Queues requests when all threads are busy");
        System.out.println("- Still uses blocking IO, limiting scalability");
        System.out.println("- Good for moderate concurrency (<few thousand connections)");
        System.out.println("- Recommended pool size typically cores × (1 + wait time / service time)");
    }
    
    private static void simulateServer() {
        System.out.println("Starting simulated server on port " + PORT + " with thread pool of size " + POOL_SIZE);
        System.out.println("(This is a simulation - no actual server is running)\n");
        
        // Show server code (for educational purposes)
        System.out.println("Server implementation would look like this:");
        System.out.println("```java");
        System.out.println("public void startServer() throws IOException {");
        System.out.println("    // Create a thread pool with " + POOL_SIZE + " threads");
        System.out.println("    ExecutorService executor = Executors.newFixedThreadPool(" + POOL_SIZE + ");");
        System.out.println("    ServerSocket serverSocket = new ServerSocket(" + PORT + ");");
        System.out.println("    System.out.println(\"Server started on port \" + " + PORT + ");");
        System.out.println("    ");
        System.out.println("    while (serverRunning) {");
        System.out.println("        try {");
        System.out.println("            // This blocks until a client connects");
        System.out.println("            Socket clientSocket = serverSocket.accept();");
        System.out.println("            int clientId = connectionCount.incrementAndGet();");
        System.out.println("            ");
        System.out.println("            // Submit client handling task to the thread pool");
        System.out.println("            executor.submit(() -> {");
        System.out.println("                System.out.println(\"Thread \" + Thread.currentThread().getName() + ");
        System.out.println("                                  \" handling client \" + clientId);");
        System.out.println("                handleClient(clientSocket, clientId);");
        System.out.println("            });");
        System.out.println("        } catch (IOException e) {");
        System.out.println("            if (serverRunning) {");
        System.out.println("                System.err.println(\"Error accepting client connection: \" + e.getMessage());");
        System.out.println("            }");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // Shutdown the executor service");
        System.out.println("    executor.shutdown();");
        System.out.println("    try {");
        System.out.println("        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {");
        System.out.println("            executor.shutdownNow();");
        System.out.println("        }");
        System.out.println("    } catch (InterruptedException e) {");
        System.out.println("        executor.shutdownNow();");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```\n");
        
        // Simulate client connections
        simulateClientConnections();
    }
    
    private static void simulateClientConnections() {
        System.out.println("Simulating client connections and thread pool behavior:\n");
        
        // Simulate 10 clients connecting (more than our thread pool size)
        for (int i = 1; i <= 10; i++) {
            final int clientId = i;
            
            System.out.println("Client " + clientId + " connected");
            
            if (i <= POOL_SIZE) {
                System.out.println("Worker-" + i + " handling client " + clientId + " immediately");
            } else {
                System.out.println("All worker threads busy - Client " + clientId + " queued for processing");
                System.out.println("(Will be handled when a worker thread becomes available)");
            }
            
            // Simulate thread processing time for the first 5 clients
            if (i <= POOL_SIZE) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        System.out.println("\nServer has " + POOL_SIZE + " active threads handling clients, others are queued");
        System.out.println("\nAdvantages over Thread-Per-Connection model:");
        System.out.println("- Limited number of threads regardless of client count");
        System.out.println("- Better resource utilization");
        System.out.println("- Controlled thread creation overhead");
        System.out.println("- Built-in work queueing");
        
        System.out.println("\nLimitations:");
        System.out.println("- Still uses blocking IO operations within each thread");
        System.out.println("- Long-running operations can still block the worker threads");
        System.out.println("- Queue can grow unbounded with many clients");
        System.out.println("- Need to tune pool size based on expected workload and hardware");
    }
    
    // Client handler method (not actually called in this simulation)
    private static void handleClient(Socket clientSocket, int clientId) {
        try (
            clientSocket;
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream()
        ) {
            System.out.println("Thread " + Thread.currentThread().getName() + 
                              " started handling client " + clientId);
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            // Process client data - this is a blocking operation
            while ((bytesRead = input.read(buffer)) != -1) {
                // Echo data back to client
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
            
            System.out.println("Thread " + Thread.currentThread().getName() + 
                              " finished handling client " + clientId);
        } catch (IOException e) {
            System.err.println("Error handling client " + clientId + ": " + e.getMessage());
        }
        // Thread returns to pool after client is handled
    }
    
    // Start server method (not actually called in this demo)
    private static void startServer() throws IOException {
        // Create a thread pool with fixed size
        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
        
        while (serverRunning) {
            try {
                // This blocks until a client connects
                Socket clientSocket = serverSocket.accept();
                int clientId = connectionCount.incrementAndGet();
                
                // Submit client handling task to the thread pool
                executor.submit(() -> {
                    System.out.println("Thread " + Thread.currentThread().getName() + 
                                      " handling client " + clientId);
                    handleClient(clientSocket, clientId);
                });
            } catch (IOException e) {
                if (serverRunning) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
        
        // Shutdown the executor service
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        
        serverSocket.close();
    }
} 