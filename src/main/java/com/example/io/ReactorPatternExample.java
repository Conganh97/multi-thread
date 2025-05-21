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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReactorPatternExample {
    private static final int PORT = 8080;
    private static Selector selector;
    private static ConcurrentMap<SocketChannel, ByteBuffer> clientBuffers = new ConcurrentHashMap<>();
    
    public static void main(String[] args) {
        System.out.println("Reactor Pattern Example");
        System.out.println("=====================");
        System.out.println("This example demonstrates the Reactor pattern, an event-driven");
        System.out.println("architecture using non-blocking IO with a small number of threads.\n");
        
        System.out.println("In this example, we'll explore how the Reactor pattern handles");
        System.out.println("multiple connections efficiently using Java NIO.\n");
        
        // This is a simulation - we don't actually start the server
        simulateReactorPattern();
        
        System.out.println("\nExplanation:");
        System.out.println("Reactor Pattern Characteristics:");
        System.out.println("- Highly scalable, can handle thousands of connections");
        System.out.println("- Uses Java NIO's Selector for efficient IO multiplexing");
        System.out.println("- Non-blocking IO operations");
        System.out.println("- Event-driven approach to handle IO readiness");
        System.out.println("- Single-threaded event loop handles many connections");
        System.out.println("- More complex programming model than thread-per-connection");
        System.out.println("- Excellent for high-concurrency scenarios");
    }
    
    private static void simulateReactorPattern() {
        System.out.println("Simulating Reactor Pattern behavior:");
        System.out.println("(This is a simulation - no actual server is running)\n");
        
        // Show server code (for educational purposes)
        System.out.println("Reactor pattern implementation would look like this:");
        System.out.println("```java");
        System.out.println("public void runReactor() throws IOException {");
        System.out.println("    // Create a selector");
        System.out.println("    selector = Selector.open();");
        System.out.println("    ");
        System.out.println("    // Create and configure server socket channel");
        System.out.println("    ServerSocketChannel serverChannel = ServerSocketChannel.open();");
        System.out.println("    serverChannel.configureBlocking(false);");
        System.out.println("    serverChannel.bind(new InetSocketAddress(PORT));");
        System.out.println("    ");
        System.out.println("    // Register server channel with selector for accept events");
        System.out.println("    serverChannel.register(selector, SelectionKey.OP_ACCEPT);");
        System.out.println("    ");
        System.out.println("    System.out.println(\"Reactor started on port \" + PORT);");
        System.out.println("    ");
        System.out.println("    while (true) {");
        System.out.println("        // Block until there are events");
        System.out.println("        selector.select();");
        System.out.println("        ");
        System.out.println("        // Get set of ready keys");
        System.out.println("        Set<SelectionKey> selectedKeys = selector.selectedKeys();");
        System.out.println("        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();");
        System.out.println("        ");
        System.out.println("        while (keyIterator.hasNext()) {");
        System.out.println("            SelectionKey key = keyIterator.next();");
        System.out.println("            keyIterator.remove();");
        System.out.println("            ");
        System.out.println("            if (!key.isValid()) {");
        System.out.println("                continue;");
        System.out.println("            }");
        System.out.println("            ");
        System.out.println("            if (key.isAcceptable()) {");
        System.out.println("                // Accept new connection");
        System.out.println("                accept(key);");
        System.out.println("            } else if (key.isReadable()) {");
        System.out.println("                // Read data from client");
        System.out.println("                read(key);");
        System.out.println("            } else if (key.isWritable()) {");
        System.out.println("                // Write data to client");
        System.out.println("                write(key);");
        System.out.println("            }");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```\n");
        
        // Simulate the reactor in action
        simulateReactorInAction();
    }
    
    private static void simulateReactorInAction() {
        System.out.println("Simulating Reactor in action:\n");
        
        // Step 1: Init
        System.out.println("1. Initializing Reactor");
        System.out.println("   - Selector created");
        System.out.println("   - ServerSocketChannel created, configured non-blocking and bound to port " + PORT);
        System.out.println("   - ServerSocketChannel registered with Selector for ACCEPT events");
        
        // Step 2: Event loop starts
        System.out.println("\n2. Event loop starts");
        System.out.println("   - Calling selector.select() to wait for events");
        
        // Step 3: Accept a connection
        System.out.println("\n3. New client connection arrives");
        System.out.println("   - select() returns with 1 event");
        System.out.println("   - ServerSocketChannel key is ACCEPTABLE");
        System.out.println("   - Accepting new client connection (Client-1)");
        System.out.println("   - New SocketChannel configured as non-blocking");
        System.out.println("   - Registering SocketChannel with selector for READ events");
        
        // Step 4: Accept another connection
        System.out.println("\n4. Another client connection arrives");
        System.out.println("   - select() returns with 1 event");
        System.out.println("   - ServerSocketChannel key is ACCEPTABLE");
        System.out.println("   - Accepting new client connection (Client-2)");
        System.out.println("   - New SocketChannel configured as non-blocking");
        System.out.println("   - Registering SocketChannel with selector for READ events");
        
        // Step 5: Read from first client
        System.out.println("\n5. Client-1 sends data");
        System.out.println("   - select() returns with 1 event");
        System.out.println("   - Client-1 key is READABLE");
        System.out.println("   - Reading data from Client-1: 'Hello from Client-1'");
        System.out.println("   - Processing data (echo service)");
        System.out.println("   - Changing Client-1 interest to WRITE events");
        
        // Step 6: Read from second client
        System.out.println("\n6. Client-2 sends data");
        System.out.println("   - select() returns with 1 event");
        System.out.println("   - Client-2 key is READABLE");
        System.out.println("   - Reading data from Client-2: 'Hello from Client-2'");
        System.out.println("   - Processing data (echo service)");
        System.out.println("   - Changing Client-2 interest to WRITE events");
        
        // Step 7: Write to first client
        System.out.println("\n7. Ready to write to Client-1");
        System.out.println("   - select() returns with 1 event");
        System.out.println("   - Client-1 key is WRITABLE");
        System.out.println("   - Writing data to Client-1: 'Hello from Client-1'");
        System.out.println("   - Changing Client-1 interest back to READ events");
        
        // Step 8: Write to second client
        System.out.println("\n8. Ready to write to Client-2");
        System.out.println("   - select() returns with 1 event");
        System.out.println("   - Client-2 key is WRITABLE");
        System.out.println("   - Writing data to Client-2: 'Hello from Client-2'");
        System.out.println("   - Changing Client-2 interest back to READ events");
        
        // Step 9: Client-1 disconnects
        System.out.println("\n9. Client-1 disconnects");
        System.out.println("   - select() returns with 1 event");
        System.out.println("   - Client-1 key is READABLE");
        System.out.println("   - Reading data returns -1 (end of stream)");
        System.out.println("   - Closing Client-1 channel");
        System.out.println("   - Cancelling Client-1 key");
        
        System.out.println("\nReactor continues running with remaining connections...");
        System.out.println("A single thread has handled multiple connections efficiently!");
    }
    
    // These methods would be part of the actual implementation
    
    // Accept new connections
    private static void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        
        // Register client channel for reading
        clientChannel.register(selector, SelectionKey.OP_READ);
        
        // Allocate buffer for this client
        clientBuffers.put(clientChannel, ByteBuffer.allocate(1024));
        
        System.out.println("Accepted new connection from " + clientChannel.getRemoteAddress());
    }
    
    // Read data from client
    private static void read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = clientBuffers.get(clientChannel);
        buffer.clear();
        
        int bytesRead;
        try {
            bytesRead = clientChannel.read(buffer);
        } catch (IOException e) {
            // Connection was closed forcibly
            closeChannel(key);
            return;
        }
        
        if (bytesRead == -1) {
            // Client closed connection
            closeChannel(key);
            return;
        }
        
        // Echo data back to client (flip buffer to prepare for writing)
        buffer.flip();
        
        // Change interest to write (when socket is ready for writing)
        key.interestOps(SelectionKey.OP_WRITE);
    }
    
    // Write data to client
    private static void write(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = clientBuffers.get(clientChannel);
        
        // Write remaining data
        clientChannel.write(buffer);
        
        if (!buffer.hasRemaining()) {
            // Buffer completely written, switch back to reading
            key.interestOps(SelectionKey.OP_READ);
        }
    }
    
    // Close a client channel
    private static void closeChannel(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        clientBuffers.remove(clientChannel);
        clientChannel.close();
        key.cancel();
    }
    
    // Main reactor method (not actually called in this simulation)
    private static void runReactor() throws IOException {
        // Create a selector
        selector = Selector.open();
        
        // Create and configure server socket channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(PORT));
        
        // Register server channel with selector for accept events
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        System.out.println("Reactor started on port " + PORT);
        
        while (true) {
            // Block until there are events
            selector.select();
            
            // Get set of ready keys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                
                if (!key.isValid()) {
                    continue;
                }
                
                if (key.isAcceptable()) {
                    // Accept new connection
                    accept(key);
                } else if (key.isReadable()) {
                    // Read data from client
                    read(key);
                } else if (key.isWritable()) {
                    // Write data to client
                    write(key);
                }
            }
        }
    }
} 