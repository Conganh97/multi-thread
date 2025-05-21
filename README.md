# Java Multi-Threading Demo Project

A comprehensive Java application demonstrating various multi-threading concepts, patterns, and best practices using Java 21.

## Overview

This project serves as both a learning resource and a reference implementation for Java's concurrency features. It includes practical examples of thread creation, synchronization, performance optimization, and the latest virtual thread capabilities introduced in Java 21.

## Features

- **Thread Creation and Coordination**: Different ways to create threads, thread lifecycle, coordination between threads
- **Performance Optimization**: Thread pools, Fork/Join framework, parallel streams
- **Data Sharing**: Memory model, shared variables, synchronization, thread-local storage
- **Concurrency Challenges**: Race conditions, deadlocks, livelocks, starvation
- **Advanced Locking**: ReentrantLock, ReadWriteLock, StampedLock
- **Inter-Thread Communication**: Wait/notify, condition variables, blocking queues
- **Lock-Free Algorithms**: Atomic variables, compare-and-swap operations
- **High-Performance IO**: Thread models for IO-intensive applications
- **Virtual Threads**: Project Loom features for lightweight concurrency

## Requirements

- Java Development Kit (JDK) 21 or later
- Maven 3.6 or later

## Getting Started

1. Clone the repository
2. Build the project:
   ```
   mvn clean package
   ```
3. Run the application:
   ```
   java -jar target/demo-multi-thread-1.0-SNAPSHOT.jar
   ```

## Usage

The application provides an interactive console menu to explore different multi-threading concepts:

1. Select a category from the main menu
2. Choose a specific example to run
3. Follow on-screen instructions and observe the output
4. Return to main menu to explore other examples

## Project Structure

```
src/main/java/com/example/
├── Main.java                   # Main application with interactive menu
├── threadcreation/             # Thread creation and coordination examples
├── performance/                # Performance optimization examples
├── datasharing/                # Data sharing examples
├── challenges/                 # Concurrency challenges examples
├── locking/                    # Advanced locking examples
├── communication/              # Inter-thread communication examples
├── lockfree/                   # Lock-free algorithms examples
├── io/                         # High-performance IO examples
└── virtualthread/              # Virtual thread examples
```

## Learning Resources

This project accompanies a comprehensive documentation on Java Multi-Threading available in the `multi-thread-doc.md` file. The documentation provides detailed explanations of all concepts demonstrated in the code examples.

## License

This project is available for educational purposes.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
