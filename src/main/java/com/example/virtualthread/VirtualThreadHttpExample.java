package com.example.virtualthread;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpServer;

public class VirtualThreadHttpExample {
    private static final int SERVER_PORT = 8090;
    private static final int CONCURRENT_REQUESTS = 1000;
    private static HttpServer server;
    
    public static void main(String[] args) throws Exception {
        System.out.println("Virtual Threads for HTTP Services Example");
        System.out.println("=======================================");
        System.out.println("This example demonstrates virtual threads for HTTP services,");
        System.out.println("both on the client and server side.\n");
        
        try {
            // Start a simple HTTP server with error handling
            try {
                startServer();
                System.out.println("Server started successfully on port " + SERVER_PORT);
            } catch (IOException e) {
                System.err.println("Failed to start HTTP server: " + e.getMessage());
                System.err.println("This could be due to port " + SERVER_PORT + " being in use.");
                System.err.println("Try changing the SERVER_PORT value and rerun.");
                return; // Exit early if server fails to start
            }
            
            System.out.println("Part 1: HTTP Client with Virtual Threads");
            try {
                httpClientWithVirtualThreads();
            } catch (Exception e) {
                System.err.println("Error in HTTP client test: " + e.getMessage());
                e.printStackTrace();
            }
            
            Thread.sleep(1000);
            
            System.out.println("\nPart 2: Comparing HTTP Clients");
            try {
                compareHttpClients();
            } catch (Exception e) {
                System.err.println("Error in HTTP client comparison: " + e.getMessage());
                e.printStackTrace();
            }
            
            Thread.sleep(1000);
            
            System.out.println("\nPart 3: HTTP Server with Virtual Threads");
            explainServerWithVirtualThreads();
            
        } finally {
            // Stop the server
            if (server != null) {
                server.stop(0);
                System.out.println("Server stopped");
            }
        }
        
        System.out.println("\nVirtual Threads for HTTP Applications - Key Points:");
        System.out.println("- HTTP operations are IO-bound, perfect for virtual threads");
        System.out.println("- Virtual threads enable higher concurrency with less resources");
        System.out.println("- HttpClient supports virtual threads via custom executor");
        System.out.println("- Simple HTTP servers can use virtual thread pools");
        System.out.println("- Modern servers/frameworks can handle many concurrent connections");
        System.out.println("- Greatly simplifies the programming model (vs async callbacks)");
    }
    
    // Start a simple HTTP server for testing
    private static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        
        // Add a simple handler that sleeps to simulate processing time
        server.createContext("/test", exchange -> {
            try {
                // Simulate some server processing time (reduced from 100ms to 50ms for faster tests)
                Thread.sleep(50);
                
                String response = "Hello from " + Thread.currentThread().getName() + "!";
                exchange.sendResponseHeaders(200, response.length());
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                exchange.sendResponseHeaders(500, 0);
            } catch (Exception e) {
                System.err.println("Error processing request: " + e.getMessage());
                try {
                    exchange.sendResponseHeaders(500, 0);
                } catch (IOException ioe) {
                    // Ignore if we can't send error response
                }
            }
        });
        
        // Use virtual threads for the server's worker thread pool
        ThreadFactory factory = Thread.ofVirtual().name("server-", 0).factory();
        server.setExecutor(Executors.newThreadPerTaskExecutor(factory));
        
        server.start();
        System.out.println("Started HTTP server on port " + SERVER_PORT);
        System.out.println("Server using virtual threads for request handling");
    }
    
    // Part 1: Demonstrate HTTP client with virtual threads
    private static void httpClientWithVirtualThreads() throws Exception {
        System.out.println("Making multiple HTTP requests with virtual threads:");
        
        // Create an HttpClient that uses virtual threads
        HttpClient client = HttpClient.newBuilder()
                                     .executor(Executors.newVirtualThreadPerTaskExecutor())
                                     .connectTimeout(Duration.ofSeconds(5))
                                     .build();
        
        // Prepare the request
        HttpRequest request = HttpRequest.newBuilder()
                                       .uri(URI.create("http://localhost:" + SERVER_PORT + "/test"))
                                       .GET()
                                       .build();
        
        // Make a few requests to demonstrate
        List<CompletableFuture<HttpResponse<String>>> futures = new ArrayList<>();
        
        System.out.println("Sending " + 5 + " requests...");
        for (int i = 0; i < 5; i++) {
            CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            futures.add(future.thenApply(response -> {
                System.out.println("Got response: " + response.body().trim());
                return response;
            }).exceptionally(ex -> {
                System.err.println("Request failed: " + ex.getMessage());
                return null;
            }));
        }
        
        // Wait for all requests to complete
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            System.out.println("All requests completed");
        } catch (Exception e) {
            System.err.println("Error waiting for requests to complete: " + e.getMessage());
        }
    }
    
    // Part 2: Compare HTTP clients (virtual threads vs platform threads)
    private static void compareHttpClients() throws Exception {
        System.out.println("Comparing HTTP clients with virtual threads vs platform threads");
        // Reducing the number of concurrent requests for better stability in demos
        int concurrentRequests = Math.min(CONCURRENT_REQUESTS, 100);
        System.out.println("for " + concurrentRequests + " concurrent requests:\n");
        
        // Create the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                                       .uri(URI.create("http://localhost:" + SERVER_PORT + "/test"))
                                       .GET()
                                       .build();
        
        // Test with platform threads
        System.out.println("Using platform threads:");
        try (ExecutorService platformExecutor = Executors.newFixedThreadPool(50)) {
            HttpClient platformClient = HttpClient.newBuilder()
                                                .executor(platformExecutor)
                                                .build();
            
            long platformStart = System.currentTimeMillis();
            runConcurrentRequests(platformClient, request, concurrentRequests);
            long platformDuration = System.currentTimeMillis() - platformStart;
            
            System.out.println("Platform threads completed in: " + platformDuration + "ms");
        }
        
        // Test with virtual threads
        System.out.println("\nUsing virtual threads:");
        try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            HttpClient virtualClient = HttpClient.newBuilder()
                                              .executor(virtualExecutor)
                                              .build();
            
            long virtualStart = System.currentTimeMillis();
            runConcurrentRequests(virtualClient, request, concurrentRequests);
            long virtualDuration = System.currentTimeMillis() - virtualStart;
            
            System.out.println("Virtual threads completed in: " + virtualDuration + "ms");
        }
    }
    
    // Helper to run concurrent requests
    private static void runConcurrentRequests(HttpClient client, HttpRequest request, int count) 
            throws Exception {
        AtomicInteger completed = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);
        List<CompletableFuture<HttpResponse<String>>> futures = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, 
                    HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int now = completed.incrementAndGet();
                        if (now % 20 == 0 || now == count) {
                            System.out.println("Completed " + now + " requests");
                        }
                        return response;
                    })
                    .exceptionally(ex -> {
                        failed.incrementAndGet();
                        return null;
                    });
            
            futures.add(future);
        }
        
        // Wait for all to complete
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            if (failed.get() > 0) {
                System.out.println("Warning: " + failed.get() + " requests failed");
            }
        } catch (Exception e) {
            System.err.println("Error in concurrent requests: " + e.getMessage());
            throw e;
        }
    }
    
    // Part 3: Explain how to use virtual threads with HTTP servers
    private static void explainServerWithVirtualThreads() {
        System.out.println("HTTP Servers with Virtual Threads");
        System.out.println("-------------------------------");
        System.out.println("Our example server is already using virtual threads for request handling.");
        System.out.println("This is implemented with the following code:\n");
        
        System.out.println("// Create a thread factory for virtual threads");
        System.out.println("ThreadFactory factory = Thread.ofVirtual().name(\"server-\", 0).factory();");
        System.out.println("// Set the server's executor to use virtual threads");
        System.out.println("server.setExecutor(Executors.newThreadPerTaskExecutor(factory));");
        
        System.out.println("\nBenefits for HTTP servers:");
        System.out.println("1. Can handle many more concurrent connections");
        System.out.println("2. Reduced memory footprint");
        System.out.println("3. Simplified programming model (blocking IO is fine)");
        System.out.println("4. Improved throughput for IO-bound operations");
        System.out.println("5. Reduced context switching overhead");
        
        System.out.println("\nReal-world server implementations:");
        System.out.println("- Many Java web servers/containers now support virtual threads");
        System.out.println("- Spring Boot 3.2+ supports virtual threads");
        System.out.println("- Tomcat 10+ supports virtual threads");
        System.out.println("- Jetty 12+ supports virtual threads");
        System.out.println("- Helidon 4.0+ supports virtual threads");
        System.out.println("- Quarkus supports virtual threads");
    }
} 