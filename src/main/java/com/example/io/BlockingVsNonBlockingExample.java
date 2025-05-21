package com.example.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BlockingVsNonBlockingExample {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Blocking vs Non-Blocking IO Example");
        System.out.println("==================================");
        System.out.println("This example demonstrates the difference between blocking and non-blocking IO models.\n");
        
        System.out.println("Part 1: Demonstrating Blocking IO");
        explainBlockingIO();
        
        System.out.println("\nPart 2: Demonstrating Non-Blocking IO");
        explainNonBlockingIO();
        
        System.out.println("\nPart 3: Comparing IO Models");
        compareIOModels();
        
        System.out.println("\nExplanation:");
        System.out.println("Blocking IO:");
        System.out.println("- Thread waits (blocks) until IO operation completes");
        System.out.println("- Simple programming model but inefficient resource utilization");
        System.out.println("- One thread per active connection limits scalability");
        System.out.println("\nNon-Blocking IO:");
        System.out.println("- Thread can continue processing while IO operations are pending");
        System.out.println("- More complex programming model but better resource utilization");
        System.out.println("- Can handle thousands of connections with fewer threads");
        System.out.println("- Uses Java NIO (New IO) features like channels, selectors, and buffers");
    }
    
    // Explain blocking IO with a simulated example
    private static void explainBlockingIO() {
        System.out.println("In blocking IO:");
        System.out.println("1. Thread initiates IO operation");
        System.out.println("2. Thread blocks until operation completes");
        System.out.println("3. Thread cannot do anything else while waiting");
        
        // Simulate blocking read
        simulateBlockingRead();
    }
    
    // Explain non-blocking IO with a simulated example
    private static void explainNonBlockingIO() {
        System.out.println("In non-blocking IO:");
        System.out.println("1. Thread initiates IO operation and specifies interest in completion notification");
        System.out.println("2. Thread can continue doing other work (or handle other connections)");
        System.out.println("3. Thread is notified when operation is ready to proceed or complete");
        
        // Simulate non-blocking read
        simulateNonBlockingRead();
    }
    
    // Simulate a blocking read operation
    private static void simulateBlockingRead() {
        System.out.println("\nSimulating blocking read operation:");
        
        // Create a thread pool with 2 threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Submit tasks representing blocking IO operations
        executor.submit(() -> {
            System.out.println("Thread-1: Starting IO operation (blocking)");
            try {
                // Simulate IO operation that takes time
                Thread.sleep(2000);
                System.out.println("Thread-1: IO operation completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        executor.submit(() -> {
            System.out.println("Thread-2: Starting IO operation (blocking)");
            try {
                // Simulate IO operation that takes time
                Thread.sleep(3000);
                System.out.println("Thread-2: IO operation completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Simulate starting a third operation, but no threads are available
        System.out.println("Main: Would like to start a third operation, but no threads available");
        System.out.println("Main: With blocking IO, we would need to create a new thread or wait");
        
        // Shutdown the executor and wait for completion
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Simulate a non-blocking read operation
    private static void simulateNonBlockingRead() {
        System.out.println("\nSimulating non-blocking read operation:");
        
        // Simulate a selector with multiple channels
        System.out.println("Main: Creating a selector to monitor multiple channels");
        System.out.println("Main: Registering 3 channels with the selector (simulated)");
        
        // Simulate the selector loop
        for (int i = 0; i < 3; i++) {
            System.out.println("\nMain: Checking for IO events (select call)");
            
            // Simulate some events becoming ready
            System.out.println("Main: Channel " + (i+1) + " is ready for IO");
            System.out.println("Main: Processing Channel " + (i+1) + " IO operation without blocking");
            
            // Simulate processing multiple channels in a single thread
            try {
                Thread.sleep(500); // Simulate short processing time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\nMain: All channels processed with a single thread");
        System.out.println("Main: Non-blocking IO allowed handling multiple operations efficiently");
    }
    
    // Compare the two IO models with code examples (not executed)
    private static void compareIOModels() {
        System.out.println("Code comparison between blocking and non-blocking IO:");
        
        System.out.println("\nBlocking IO example (pseudocode):");
        System.out.println("  // Each connection needs its own thread");
        System.out.println("  ServerSocket serverSocket = new ServerSocket(8080);");
        System.out.println("  while (true) {");
        System.out.println("      Socket socket = serverSocket.accept(); // Blocks until connection");
        System.out.println("      new Thread(() -> {");
        System.out.println("          InputStream in = socket.getInputStream();");
        System.out.println("          byte[] data = new byte[1024];");
        System.out.println("          int bytesRead = in.read(data); // Blocks until data available");
        System.out.println("          // Process data...");
        System.out.println("      }).start();");
        System.out.println("  }");
        
        System.out.println("\nNon-blocking IO example (pseudocode):");
        System.out.println("  // One thread can handle many connections");
        System.out.println("  Selector selector = Selector.open();");
        System.out.println("  ServerSocketChannel serverChannel = ServerSocketChannel.open();");
        System.out.println("  serverChannel.configureBlocking(false);");
        System.out.println("  serverChannel.register(selector, SelectionKey.OP_ACCEPT);");
        System.out.println("  ");
        System.out.println("  while (true) {");
        System.out.println("      selector.select(); // Blocks until any channel is ready");
        System.out.println("      Set<SelectionKey> selectedKeys = selector.selectedKeys();");
        System.out.println("      for (SelectionKey key : selectedKeys) {");
        System.out.println("          if (key.isAcceptable()) {");
        System.out.println("              // Accept new connection");
        System.out.println("          } else if (key.isReadable()) {");
        System.out.println("              // Read data from connection");
        System.out.println("              SocketChannel channel = (SocketChannel) key.channel();");
        System.out.println("              ByteBuffer buffer = ByteBuffer.allocate(1024);");
        System.out.println("              channel.read(buffer); // Non-blocking read");
        System.out.println("              // Process data...");
        System.out.println("          }");
        System.out.println("      }");
        System.out.println("  }");
    }
    
    // Example of blocking read (not called in this demo)
    static void blockingRead(SocketChannel channel, ByteBuffer buffer) throws IOException {
        // Thread blocks until data is available or EOF
        int bytesRead = channel.read(buffer);
        if (bytesRead > 0) {
            // Process data
            buffer.flip();
            // ... process buffer contents ...
            buffer.clear();
        }
    }
    
    // Example of non-blocking read (not called in this demo)
    static void nonBlockingRead(SocketChannel channel, ByteBuffer buffer) throws IOException {
        // Configure channel for non-blocking mode
        channel.configureBlocking(false);
        
        // Read available data, returns immediately
        int bytesRead = channel.read(buffer);
        if (bytesRead > 0) {
            // Process data
            buffer.flip();
            // ... process buffer contents ...
            buffer.clear();
        } else if (bytesRead == 0) {
            // No data available at the moment
            // Do other work and try again later
        }
    }
} 