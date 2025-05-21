package com.example.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPerConnectionExample {
    private static final int PORT = 8080;
    private static final AtomicInteger connectionCount = new AtomicInteger(0);
    private static volatile boolean serverRunning = true;
    
    public static void main(String[] args) {
        System.out.println("Thread-Per-Connection Model Example");
        System.out.println("==================================");
        System.out.println("This example demonstrates the Thread-Per-Connection model for handling");
        System.out.println("multiple client connections in a server application.\n");
        
        System.out.println("In this example, we'll simulate a server that creates a new thread");
        System.out.println("for each incoming client connection.\n");
        
        // This is a simulation - we're not actually starting the server
        simulateServer();
        
        System.out.println("\nExplanation:");
        System.out.println("Thread-Per-Connection Model Characteristics:");
        System.out.println("- Simple to implement and understand");
        System.out.println("- Direct mapping between connections and threads");
        System.out.println("- Limited scalability (typically <1000 connections)");
        System.out.println("- High memory overhead for many connections");
        System.out.println("- Thread context switching overhead becomes significant");
        System.out.println("- Good for simple servers with moderate connection load");
    }
    
    private static void simulateServer() {
        System.out.println("Starting simulated server on port " + PORT);
        System.out.println("(This is a simulation - no actual server is running)\n");
        
        // Show server code (for educational purposes)
        System.out.println("Server implementation would look like this:");
        System.out.println("```java");
        System.out.println("public void startServer() throws IOException {");
        System.out.println("    ServerSocket serverSocket = new ServerSocket(" + PORT + ");");
        System.out.println("    System.out.println(\"Server started on port \" + " + PORT + ");");
        System.out.println("    ");
        System.out.println("    while (serverRunning) {");
        System.out.println("        try {");
        System.out.println("            // This blocks until a client connects");
        System.out.println("            Socket clientSocket = serverSocket.accept();");
        System.out.println("            ");
        System.out.println("            // Create a new thread for each connection");
        System.out.println("            Thread clientThread = new Thread(() -> {");
        System.out.println("                handleClient(clientSocket);");
        System.out.println("            }, \"Client-\" + connectionCount.incrementAndGet());");
        System.out.println("            ");
        System.out.println("            // Start the thread to handle this client");
        System.out.println("            clientThread.start();");
        System.out.println("        } catch (IOException e) {");
        System.out.println("            if (serverRunning) {");
        System.out.println("                System.err.println(\"Error accepting client connection: \" + e.getMessage());");
        System.out.println("            }");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```\n");
        
        // Simulate client connections
        simulateClientConnections();
    }
    
    private static void simulateClientConnections() {
        System.out.println("Simulating client connections and thread creation:\n");
        
        // Simulate 10 clients connecting
        for (int i = 1; i <= 10; i++) {
            int connectionId = i;
            System.out.println("Client " + i + " connected");
            System.out.println("Creating thread 'Client-" + i + "' to handle this connection");
            
            // Simulate thread processing time
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\nServer now has 10 active threads, one per client connection");
        System.out.println("Each thread is blocked waiting for input from its client.");
        
        System.out.println("\nScalability discussion:");
        System.out.println("- What happens when 1,000 clients connect? 10,000 clients?");
        System.out.println("- Each thread consumes memory (typically 1MB+ for thread stack)");
        System.out.println("- Context switching overhead increases with thread count");
        System.out.println("- Resource contention increases (CPU, memory, OS thread limits)");
    }
    
    // Client handler method (not actually called in this simulation)
    private static void handleClient(Socket clientSocket) {
        try (
            clientSocket;
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            // This thread will be dedicated to this client for the duration of the connection
            while ((bytesRead = input.read(buffer)) != -1) {
                // Echo data back to client
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
            
            System.out.println(Thread.currentThread().getName() + ": Client disconnected");
        } catch (IOException e) {
            System.err.println(Thread.currentThread().getName() + ": Error handling client: " + e.getMessage());
        } finally {
            // Thread will terminate after client disconnects
        }
    }
    
    // Start server method (not actually called in this demo)
    private static void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
        
        while (serverRunning) {
            try {
                // This blocks until a client connects
                Socket clientSocket = serverSocket.accept();
                
                // Create a new thread for each connection
                Thread clientThread = new Thread(() -> {
                    handleClient(clientSocket);
                }, "Client-" + connectionCount.incrementAndGet());
                
                // Start the thread to handle this client
                clientThread.start();
            } catch (IOException e) {
                if (serverRunning) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
        
        serverSocket.close();
    }
} 