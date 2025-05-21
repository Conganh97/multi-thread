# Java Multi-Threading Documentation

## Detailed Index
1. [Thread Creation and Coordination](#thread-creation-and-coordination)
   - [Creating Threads in Java](#creating-threads-in-java) [20-113]
     - [Extending the Thread class](#1-extending-the-thread-class) [26-64]
     - [Implementing the Runnable interface](#2-implementing-the-runnable-interface-preferred) [66-113]
   - [Thread Lifecycle and States](#thread-lifecycle-and-states) [115-121]
   - [Thread Coordination](#thread-coordination) [123-124]
     - [Thread Joining](#thread-joining) [126-156]
     - [Thread Sleep and Interruption](#thread-sleep-and-interruption) [158-185]
     - [Thread State Monitoring](#thread-state-monitoring) [187-217]
     - [Thread Priority and Scheduling](#thread-priority-and-scheduling) [219-242]
     - [Daemon Threads](#daemon-threads) [244-273]

2. [Performance Optimization](#performance-optimization)
   - [Thread Creation Overhead](#thread-creation-overhead) [280-289]
   - [Thread Pools with ExecutorService](#thread-pools-with-executorservice) [291-331]
   - [Different Types of Thread Pools](#different-types-of-thread-pools) [333-392]
     - [Fixed Thread Pool](#fixed-thread-pool) [335-339]
     - [Cached Thread Pool](#cached-thread-pool) [340-344]
     - [Single-Threaded Executor](#single-threaded-executor) [345-349]
     - [Scheduled Thread Pool](#scheduled-thread-pool) [350-354]
   - [Fork/Join Framework for Recursive Tasks](#forkjoin-framework-for-recursive-tasks) [394-454]
   - [Parallel Streams](#parallel-streams) [456-493]
   - [Thread Pool Sizing](#thread-pool-sizing) [495-532]

3. [Data Sharing](#data-sharing)
   - [Memory Model and Visibility](#memory-model-and-visibility) [537-570]
   - [Shared Memory and Variables](#shared-memory-and-variables) [572-621]
   - [Synchronization Mechanisms](#synchronization-mechanisms) [623-624]
     - [Synchronized Blocks and Methods](#synchronized-blocks-and-methods) [626-680]
     - [Volatile Variables](#volatile-variables) [682-713]
   - [Thread-Local Storage](#thread-local-storage) [715-767]
   - [Immutable Objects for Safe Sharing](#immutable-objects-for-safe-sharing) [769-834]
   - [Thread Confinement](#thread-confinement) [836-877]

4. [Concurrency Challenges and Solutions](#concurrency-challenges-and-solutions)
   - [Race Conditions](#race-conditions) [882-935]
   - [Deadlocks](#deadlocks) [937-997]
   - [Deadlock Prevention](#deadlock-prevention) [999-1109]
   - [Livelock](#livelock) [1111-1154]
   - [Starvation](#starvation) [1156-1199]
   - [Preventing Starvation](#preventing-starvation) [1201-1229]
   - [Thread Interference and Memory Consistency Errors](#thread-interference-and-memory-consistency-errors) [1231-1275]
   - [Thread-Safe Design Patterns](#thread-safe-design-patterns) [1277-1339]

5. [Advanced Locking](#advanced-locking)
   - [Basic vs. Advanced Locking](#basic-vs-advanced-locking) [1343-1357]
   - [ReentrantLock](#reentrantlock) [1359-1561]
   - [ReadWriteLock](#readwritelock) [1563-1679]
   - [StampedLock](#stampedlock) [1681-1723]

6. [Inter-Thread Communication](#inter-thread-communication)
   - [Wait and Notify Mechanism](#wait-and-notify-mechanism) [1728-1781]
   - [Condition Variables](#condition-variables) [1783-1873]
   - [Blocking Queues](#blocking-queues) [1875-1940]
   - [CountDownLatch and CyclicBarrier](#countdownlatch-and-cyclicbarrier) [1942-1947]
     - [CountDownLatch](#countdownlatch) [1949-1996]
     - [CyclicBarrier](#cyclicbarrier) [1998-2047]

7. [Lock-Free Algorithms](#lock-free-algorithms)
   - [Atomic Variables](#atomic-variables) [2052-2099]
   - [Compare-And-Swap (CAS) Operations](#compare-and-swap-cas-operations) [2101-2156]
   - [ABA Problem and Solutions](#aba-problem-and-solutions) [2158-2234]
   - [Lock-Free Data Structures](#lock-free-data-structures) [2236-2283]

8. [Thread Model for High-Performance IO](#thread-model-for-high-performance-io)
   - [Blocking vs Non-Blocking IO](#blocking-vs-non-blocking-io) [2288-2321]
   - [Thread Models for IO-Intensive Applications](#thread-models-for-io-intensive-applications) [2323-2324]
     - [Thread-Per-Connection Model](#1-thread-per-connection-model) [2326-2363]
     - [Thread Pool Model](#2-thread-pool-model) [2365-2403]
     - [Reactor Pattern](#3-reactor-pattern) [2405-2499]
     - [Multi-Reactor Pattern](#4-multi-reactor-pattern) [2501-2597]
   - [Using CompletableFuture for Asynchronous IO](#using-completablefuture-for-asynchronous-io) [2599-2644]
   - [JDBC and Thread Models](#jdbc-and-thread-models) [2646-2686]

9. [Virtual Thread for High-Performance IO](#virtual-thread-for-high-performance-io)
   - [Virtual Threads Characteristics](#virtual-threads-for-high-performance-io) [2688-2752]
   - [Structured Concurrency](#structured-concurrency) [2754-2817]
   - [Best Practices for Virtual Threads](#best-practices-for-virtual-threads) [2819-2881]
   - [Virtual Thread Performance Considerations](#virtual-thread-performance-considerations) [2883-2948]
   - [Virtual Threads for HTTP Services](#virtual-threads-for-http-services) [2950-3020]
   - [Virtual Threads and JDBC](#virtual-threads-and-jdbc) [3022-3102]

## Table of Contents
1. [Thread Creation and Coordination](#thread-creation-and-coordination)
2. [Performance Optimization](#performance-optimization)
3. [Data Sharing](#data-sharing)
4. [Concurrency Challenges and Solutions](#concurrency-challenges-and-solutions)
5. [Advanced Locking](#advanced-locking)
6. [Inter-Thread Communication](#inter-thread-communication)
7. [Lock-Free Algorithms](#lock-free-algorithms)
8. [Thread Model for High-Performance IO](#thread-model-for-high-performance-io)
9. [Virtual Thread for High-Performance IO](#virtual-thread-for-high-performance-io)

## Thread Creation and Coordination

The foundation of Java's concurrency model is the `Thread` class, which represents an independent path of execution within a program. Understanding how to create, manage, and coordinate threads is fundamental to mastering multi-threaded programming.

### Creating Threads in Java

Java provides two primary approaches for creating threads, each with distinct advantages:

#### 1. Extending the Thread class

This approach involves creating a subclass of the `Thread` class and overriding its `run()` method to define the thread's behavior. When you call `start()`, the JVM allocates resources to the thread and then calls the `run()` method.

**Key components:**
- **Thread subclass**: Your custom class that extends `Thread`
- **run() method**: Contains the code that will execute in the new thread
- **start() method**: Allocates system resources and calls run() in a new thread
- **Thread states**: NEW → RUNNABLE → (BLOCKED/WAITING/TIMED_WAITING) → TERMINATED

**Advantages:**
- Direct access to thread methods like `getName()`, `setPriority()`, etc.
- Simpler when you don't need to extend another class

**Disadvantages:**
- Java doesn't support multiple inheritance, so extending Thread limits your class hierarchy
- Tighter coupling between task logic and thread management

```java
public class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread is running: " + Thread.currentThread().getName());
        // Your thread logic here
    }

    public static void main(String[] args) {
        MyThread thread1 = new MyThread();
        thread1.setName("Custom-Thread-1");
        thread1.start();  // Don't call run() directly
    }
}
```

#### 2. Implementing the Runnable interface (preferred)

The Runnable interface defines a single method `run()` that contains the code to be executed in a thread. This approach separates the task logic (what to run) from the thread object (how to run it).

**Key components:**
- **Runnable implementation**: Your class implementing the `Runnable` interface
- **run() method**: Contains the code to be executed in the thread
- **Thread construction**: Pass your Runnable to a Thread constructor
- **Lambda expressions**: Java 8+ allows defining Runnables using lambda expressions

**Advantages:**
- Decouples task logic from thread management
- Your class can still extend another class
- Promotes the design principle of composition over inheritance
- Can submit the same Runnable to multiple threads or executor services

**Disadvantages:**
- Can't directly access thread methods without using Thread.currentThread()

```java
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread is running: " + Thread.currentThread().getName());
        // Your thread logic here
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new MyRunnable(), "Custom-Thread-1");
        thread1.start();
        
        // Using lambda (Java 8+)
        Thread thread2 = new Thread(() -> {
            System.out.println("Lambda thread is running: " + Thread.currentThread().getName());
        }, "Lambda-Thread");
        thread2.start();
    }
}
```

### Thread Lifecycle and States

A Java thread can exist in several states throughout its lifecycle:

1. **NEW**: Thread has been created but not started yet
2. **RUNNABLE**: Thread is executing or ready to execute but waiting for resource allocation
3. **BLOCKED**: Thread is waiting to acquire a monitor lock
4. **WAITING**: Thread is waiting indefinitely for another thread to perform an action
5. **TIMED_WAITING**: Thread is waiting for another thread to perform an action for a specified time
6. **TERMINATED**: Thread has completed execution or was terminated abnormally

Understanding these states is crucial for diagnosing threading issues like deadlocks and livelocks.

### Thread Coordination

Thread coordination involves managing the execution flow between multiple threads to ensure they work together harmoniously.

#### Thread Joining

The `join()` method allows one thread to wait for the completion of another thread. This is useful when a thread requires the results produced by another thread before it can proceed.

**Key aspects of join():**
- Makes the current thread wait until the thread on which join() is called completes
- Can specify a timeout parameter to avoid waiting indefinitely
- Throws InterruptedException if the waiting thread is interrupted

```java
public class JoinExample {
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1 started");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread 1 completed");
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2 started");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread 2 completed");
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();  // Main thread waits for thread1 to complete
            thread2.join();  // Main thread waits for thread2 to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Both threads have completed execution");
    }
}
```

#### Thread Sleep and Interruption

The `sleep()` method temporarily pauses thread execution for a specified period. The `interrupt()` method provides a mechanism to interrupt a sleeping or waiting thread.

**Key aspects of sleep() and interrupt():**
- `sleep()` causes the current thread to suspend execution for a specified time
- `sleep()` doesn't release any locks the thread has acquired
- `interrupt()` sets the interrupted status flag of the target thread
- If the target thread is blocked in a method like sleep() or wait(), it will exit with an InterruptedException

```java
public class SleepAndInterruptExample {
    public static void main(String[] args) {
        Thread sleepingThread = new Thread(() -> {
            try {
                System.out.println("Thread going to sleep for 10 seconds");
                Thread.sleep(10000);
                System.out.println("Thread woke up normally");
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted while sleeping");
            }
        });

        sleepingThread.start();
        
        // Let the thread sleep for a bit before interrupting
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Interrupt the sleeping thread
        sleepingThread.interrupt();
    }
}
```

#### Thread State Monitoring

Java provides methods to check and monitor the state of a thread, which is valuable for debugging and coordination.

**Key methods for monitoring:**
- `getState()`: Returns the current state of the thread
- `isAlive()`: Tests if the thread is alive
- `isInterrupted()`: Tests if the thread has been interrupted
- `Thread.activeCount()`: Estimates the number of active threads in the current thread's thread group

```java
public class ThreadStateExample {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        
        Thread thread = new Thread(() -> {
            try {
                synchronized (lock) {
                    System.out.println("Waiting...");
                    lock.wait();
                    System.out.println("Notified!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        System.out.println("Before starting: " + thread.getState());
        thread.start();
        
        Thread.sleep(100); // Give thread time to start and wait
        System.out.println("After wait() call: " + thread.getState());
        
        synchronized (lock) {
            lock.notify();
            System.out.println("After notify(): " + thread.getState());
        }
        
        thread.join();
        System.out.println("After completion: " + thread.getState());
    }
}
```

#### Thread Priority and Scheduling

Java threads have a priority that influences (but doesn't guarantee) the order in which they are scheduled by the JVM and operating system.

**Key priority concepts:**
- Priority ranges from MIN_PRIORITY (1) to MAX_PRIORITY (10), with NORM_PRIORITY (5) as default
- Higher-priority threads are generally executed in preference to lower-priority threads
- Thread scheduling is platform-dependent; priorities are handled differently across operating systems
- Relying heavily on thread priorities can lead to non-portable code

```java
public class ThreadPriorityExample {
    public static void main(String[] args) {
        Thread highPriorityThread = new Thread(() -> {
            System.out.println("High priority thread running");
        });
        
        Thread lowPriorityThread = new Thread(() -> {
            System.out.println("Low priority thread running");
        });
        
        highPriorityThread.setPriority(Thread.MAX_PRIORITY);  // 10
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);   // 1
        
        lowPriorityThread.start();
        highPriorityThread.start();
    }
}
```

#### Daemon Threads

Daemon threads are background threads that don't prevent the JVM from exiting when the program finishes. They're useful for service tasks like garbage collection.

**Key daemon thread characteristics:**
- The JVM exits when only daemon threads remain
- Must be set as daemon before starting the thread
- Child threads inherit the daemon status of their parent thread
- Daemon threads are abruptly terminated when the JVM exits, which can leave resources unclosed

```java
public class DaemonThreadExample {
    public static void main(String[] args) {
        Thread daemonThread = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Daemon thread running...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        daemonThread.setDaemon(true);  // Must be set before starting
        daemonThread.start();
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Main thread exiting, daemon will be terminated");
    }
}
```

## Performance Optimization

Efficient multi-threading can dramatically improve application performance, especially for CPU-intensive or IO-bound operations. However, poorly implemented threading can introduce overhead that negates these benefits. This section covers techniques to optimize thread performance.

### Thread Creation Overhead

Creating threads in Java is a relatively expensive operation:

- Each thread requires memory for its stack (typically 512KB to 1MB)
- Thread creation involves system calls to the underlying OS
- JVM must initialize thread-specific data structures
- Context switching between threads adds CPU overhead

For applications that need to process many short-lived tasks, repeatedly creating and destroying threads can severely impact performance.

### Thread Pools with ExecutorService

Thread pools solve the thread creation overhead problem by reusing a fixed set of worker threads to execute multiple tasks. The Java ExecutorService framework manages thread creation, reuse, and lifecycle for you.

**Key components of ExecutorService:**
- **Worker threads**: Persistent threads that execute submitted tasks
- **Task queue**: Stores tasks waiting to be executed
- **Thread factory**: Creates new threads when needed
- **Rejection policy**: Determines how to handle tasks when the pool is saturated

**Benefits of thread pools:**
- Eliminates the overhead of thread creation and destruction
- Limits the number of concurrent threads, avoiding resource exhaustion
- Provides task queuing when all threads are busy
- Supports various execution policies and lifecycle management

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExample {
    public static void main(String[] args) {
        // Create a fixed thread pool with 5 threads
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // Submit 10 tasks to be executed by the thread pool
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " executed by " + 
                                  Thread.currentThread().getName());
                try {
                    // Simulate work
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "Task " + taskId + " result";
            });
        }
        
        // Proper shutdown
        executor.shutdown();
        try {
            // Wait for all tasks to complete or timeout after 60 seconds
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        
        System.out.println("All tasks completed");
    }
}
```

### Different Types of Thread Pools

Java's Executors class provides factory methods for creating different types of thread pools, each optimized for specific use cases:

#### Fixed Thread Pool
- **Creation**: `Executors.newFixedThreadPool(n)`
- **Characteristics**: Creates a pool with a fixed number of threads
- **Task handling**: If all threads are busy, new tasks wait in a queue
- **Best for**: CPU-bound tasks with a predictable number of concurrent operations

#### Cached Thread Pool
- **Creation**: `Executors.newCachedThreadPool()`
- **Characteristics**: Creates new threads as needed, reuses idle threads
- **Task handling**: Threads are created on demand and recycled when idle for 60 seconds
- **Best for**: Many short-lived tasks, especially in IO-bound applications

#### Single-Threaded Executor
- **Creation**: `Executors.newSingleThreadExecutor()`
- **Characteristics**: Uses a single worker thread to execute tasks sequentially
- **Task handling**: Tasks execute in the order they were submitted
- **Best for**: Tasks that must execute sequentially, guaranteed ordering

#### Scheduled Thread Pool
- **Creation**: `Executors.newScheduledThreadPool(n)`
- **Characteristics**: Can schedule tasks to run after a delay or periodically
- **Task handling**: Supports delayed and periodic task execution
- **Best for**: Tasks that need to run at specific times or intervals

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTypes {
    public static void main(String[] args) {
        // Fixed thread pool - best for CPU-bound tasks with limited parallelism
        ExecutorService fixedPool = Executors.newFixedThreadPool(4);
        
        // Cached thread pool - good for many short-lived tasks
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        
        // Single-threaded executor - ensures sequential execution
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        
        // Scheduled thread pool - for delayed or periodic tasks
        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);
        
        // Schedule a task to run after 2 seconds
        scheduledPool.schedule(() -> System.out.println("Delayed task"), 2, TimeUnit.SECONDS);
        
        // Schedule a task to run every 3 seconds, after an initial delay of 0 seconds
        scheduledPool.scheduleAtFixedRate(() -> System.out.println("Periodic task"), 
                                          0, 3, TimeUnit.SECONDS);
        
        // Don't forget to shutdown your executors in a real application
    }
}
```

### Fork/Join Framework for Recursive Tasks

The Fork/Join framework, introduced in Java 7, is specialized for dividing a task into smaller subtasks, processing them in parallel, and then combining the results—a paradigm known as "divide and conquer."

**Key components of Fork/Join:**
- **ForkJoinPool**: A specialized thread pool for fork/join tasks
- **RecursiveTask**: A task that returns a result
- **RecursiveAction**: A task that doesn't return a result
- **Work-stealing algorithm**: Idle threads "steal" tasks from busy threads' queues

**When to use Fork/Join:**
- For CPU-intensive tasks that can be broken into smaller subtasks
- When the work can be divided recursively
- For tasks that benefit from parallel processing but require the results to be combined

**How Fork/Join differs from regular thread pools:**
- Designed specifically for divide-and-conquer algorithms
- Uses work-stealing to balance thread workloads automatically
- Optimized for tasks that spawn subtasks and wait for their completion

```java
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinExample {
    public static void main(String[] args) {
        int[] array = new int[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }
        
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        int sum = forkJoinPool.invoke(new SumTask(array, 0, array.length));
        System.out.println("Sum: " + sum);
    }
    
    static class SumTask extends RecursiveTask<Integer> {
        private static final int THRESHOLD = 10;
        private final int[] array;
        private final int start;
        private final int end;
        
        SumTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        protected Integer compute() {
            int length = end - start;
            if (length <= THRESHOLD) {
                return computeDirectly();
            }
            
            int mid = start + length / 2;
            
            // Fork subtasks
            SumTask leftTask = new SumTask(array, start, mid);
            leftTask.fork();
            
            SumTask rightTask = new SumTask(array, mid, end);
            
            // Compute right part and join left part
            return rightTask.compute() + leftTask.join();
        }
        
        private Integer computeDirectly() {
            int sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        }
    }
}
```

### Parallel Streams

Java 8 introduced parallel streams, which provide a high-level abstraction for parallel processing of collections. Under the hood, parallel streams use the Fork/Join framework.

**Key benefits of parallel streams:**
- Simplifies parallel processing with a declarative API
- Automatically divides the workload among available processor cores
- Handles the coordination and thread management for you

**When to use parallel streams:**
- For operations on large data sets that can be processed independently
- When the operations are CPU-intensive and benefit from parallelization
- When the data source splits efficiently (like ArrayList, but not LinkedList)

```java
import java.util.Arrays;
import java.util.List;

public class ParallelStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Sequential stream
        long startSeq = System.currentTimeMillis();
        int sumSeq = numbers.stream()
                          .map(n -> computeExpensive(n))
                          .reduce(0, Integer::sum);
        long endSeq = System.currentTimeMillis();
        
        System.out.println("Sequential sum: " + sumSeq);
        System.out.println("Sequential time: " + (endSeq - startSeq) + "ms");
        
        // Parallel stream
        long startPar = System.currentTimeMillis();
        int sumPar = numbers.parallelStream()
                          .map(n -> computeExpensive(n))
                          .reduce(0, Integer::sum);
        long endPar = System.currentTimeMillis();
        
        System.out.println("Parallel sum: " + sumPar);
        System.out.println("Parallel time: " + (endPar - startPar) + "ms");
    }
    
    private static int computeExpensive(int n) {
        // Simulate expensive computation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return n * n;
    }
}
```

### Thread Pool Sizing

Determining the optimal thread pool size is crucial for performance. Too few threads may not fully utilize available resources, while too many can lead to excessive context switching and resource contention.

**Factors affecting optimal thread pool size:**
- **Number of CPU cores**: For CPU-bound tasks, usually optimal around number of cores
- **Task nature**: IO-bound tasks can benefit from more threads than CPU cores
- **Memory constraints**: Each thread consumes memory (stack size)
- **External resource limits**: Database connections, network bandwidth, etc.

**Common thread pool sizing formulas:**
- **CPU-bound tasks**: N = number of CPU cores
- **IO-bound tasks**: N = number of cores * (1 + wait time / service time)

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolSizing {
    public static void main(String[] args) {
        // Get number of available cores
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available cores: " + cores);
        
        // For CPU-bound tasks
        ExecutorService cpuBoundPool = Executors.newFixedThreadPool(cores);
        
        // For IO-bound tasks (assuming 90% wait time)
        int waitTimeRatio = 9;  // 90% wait time means ratio of 9:1
        ExecutorService ioBoundPool = Executors.newFixedThreadPool(cores * (1 + waitTimeRatio));
        
        System.out.println("CPU-bound pool size: " + cores);
        System.out.println("IO-bound pool size: " + cores * (1 + waitTimeRatio));
        
        // Don't forget to shutdown your executors in a real application
        cpuBoundPool.shutdown();
        ioBoundPool.shutdown();
    }
}
```

## Data Sharing

In multi-threaded applications, threads often need to share data with each other. Understanding how data is shared, the visibility of changes, and strategies for safe sharing are fundamental to writing correct concurrent code.

### Memory Model and Visibility

Java's memory model defines how threads interact with memory and with each other through memory. Key concepts include:

**Memory visibility challenges:**
- Each CPU core may have its own cache with a copy of memory values
- JVM and compiler optimizations may reorder operations
- Changes made by one thread may not be immediately visible to other threads

**Happens-before relationship:**
- Formal way to reason about when one memory operation is guaranteed to be visible to another
- Established by synchronization mechanisms (synchronized, volatile, locks)
- Crucial for understanding thread interactions

```java
public class VisibilityProblem {
    private static boolean flag = false;
    private static int value = 0;
    
    public static void main(String[] args) throws InterruptedException {
        Thread writerThread = new Thread(() -> {
            value = 42;  // Step 1
            flag = true;  // Step 2
        });
        
        Thread readerThread = new Thread(() -> {
            // Without proper synchronization, the reader might see
            // flag = true but value = 0, or might not see updated values at all
            while (!flag) {
                // Busy-wait until flag becomes true
                Thread.yield();
            }
            System.out.println("Value: " + value);  // May not print 42!
        });
        
        readerThread.start();
        writerThread.start();
        
        writerThread.join();
        readerThread.join();
    }
}
```

### Shared Memory and Variables

Threads within the same JVM share the heap memory but have their own stack:

**What is shared:**
- Static fields (class variables)
- Instance fields (object variables)
- Array elements
- Objects referenced from above

**What is thread-local:**
- Local variables
- Method parameters
- Exception handler parameters
- Inherently thread-safe: primitive local variables and references (but not necessarily the objects they refer to)

```java
public class SharedMemoryExample {
    // Shared variable (class/static variable)
    private static int counter = 0;
    
    // Instance variable - shared when the object is shared
    private int instanceCounter = 0;
    
    public static void main(String[] args) throws InterruptedException {
        // Local variable - not shared between threads
        final SharedMemoryExample example = new SharedMemoryExample();
        
        Runnable incrementTask = () -> {
            // Local variable - each thread has its own copy
            int localCounter = 0;
            
            for (int i = 0; i < 10000; i++) {
                // Increment shared static variable
                counter++;  // This operation is not atomic
                
                // Increment shared instance variable
                example.instanceCounter++;  // This operation is not atomic
                
                // Increment thread-local variable
                localCounter++;  // This is safe and doesn't need synchronization
            }
            
            System.out.println(Thread.currentThread().getName() + 
                              " local counter: " + localCounter);
        };
        
        Thread thread1 = new Thread(incrementTask, "Thread-1");
        Thread thread2 = new Thread(incrementTask, "Thread-2");
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        // The result is likely less than 20000 due to race conditions
        System.out.println("Final static counter value: " + counter);
        System.out.println("Final instance counter value: " + example.instanceCounter);
    }
}
```

### Synchronization Mechanisms

Java provides several mechanisms to synchronize access to shared data:

#### Synchronized Blocks and Methods

The `synchronized` keyword creates a critical section that can be executed by only one thread at a time, ensuring mutual exclusion.

**Key aspects of synchronized:**
- Uses the intrinsic lock (monitor) of an object
- Establishes happens-before relationships
- Guarantees both mutual exclusion and memory visibility
- Can synchronize on `this`, another object, or a class (for static methods)

```java
public class SynchronizedExample {
    private int counter = 0;
    private static int staticCounter = 0;
    private final Object lock = new Object();
    
    // Synchronized instance method - uses 'this' as the lock
    public synchronized void incrementCounter() {
        counter++;
    }
    
    // Synchronized static method - uses class object as the lock
    public static synchronized void incrementStaticCounter() {
        staticCounter++;
    }
    
    // Synchronized block on a specific object
    public void incrementWithLock() {
        synchronized (lock) {
            counter++;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        SynchronizedExample example = new SynchronizedExample();
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                example.incrementCounter();
                incrementStaticCounter();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                example.incrementWithLock();
                incrementStaticCounter();
            }
        });
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        System.out.println("Counter: " + example.counter);  // Should be 20000
        System.out.println("Static counter: " + staticCounter);  // Should be 20000
    }
}
```

#### Volatile Variables

The `volatile` keyword ensures that reads and writes to a variable are directly from/to main memory, providing visibility guarantees but not atomicity.

**Key aspects of volatile:**
- Guarantees visibility of changes across threads
- Prevents reordering of operations involving the volatile variable
- Does not provide atomicity for compound operations (e.g., i++)
- Lighter weight than synchronization

```java
public class VolatileExample {
    // Without volatile, the thread might never see running = false
    private static volatile boolean running = true;
    
    public static void main(String[] args) throws InterruptedException {
        Thread workerThread = new Thread(() -> {
            int counter = 0;
            while (running) {
                counter++;
            }
            System.out.println("Worker stopped. Counted to " + counter);
        });
        
        workerThread.start();
        
        // Give the worker time to start
        Thread.sleep(1000);
        
        // Signal the worker to stop
        running = false;
        System.out.println("Set running to false");
        
        // Wait for the worker to finish
        workerThread.join();
    }
}
```

### Thread-Local Storage

ThreadLocal variables provide a way to give each thread its own private copy of a variable, preventing thread interference.

**Key aspects of ThreadLocal:**
- Each thread has its own copy of the variable
- Changes made by one thread are not visible to other threads
- Useful for thread-specific context like user ID, transaction ID, etc.
- Should be cleaned up properly to avoid memory leaks in thread pools

```java
public class ThreadLocalExample {
    // Thread-local variable - each thread has its own copy
    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);
    
    // Method to access thread-local variable
    public static int get() {
        return threadLocal.get();
    }
    
    // Method to update thread-local variable
    public static void set(int value) {
        threadLocal.set(value);
    }
    
    // Method to clean up thread-local variable
    public static void remove() {
        threadLocal.remove();
    }
    
    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            
            // Get the current thread's value
            int value = get();
            System.out.println(threadName + " initial value: " + value);
            
            // Modify the thread-local value
            set(value + 100);
            System.out.println(threadName + " after increment: " + get());
            
            // Do some processing
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Value persists throughout thread execution
            System.out.println(threadName + " final value: " + get());
            
            // Clean up to prevent memory leaks in thread pools
            remove();
        };
        
        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        // Main thread has its own separate value
        System.out.println("Main thread value: " + get());
    }
}
```

### Immutable Objects for Safe Sharing

Immutable objects cannot be modified after creation, making them inherently thread-safe and ideal for sharing between threads without synchronization.

**Characteristics of immutable objects:**
- All fields are final and initialized in the constructor
- The object's state cannot be changed after construction
- No setters or other methods that modify state
- Proper encapsulation of mutable objects (defensive copies)
- No leaking of the "this" reference during construction

**Benefits of immutability:**
- Thread-safe without synchronization
- No visibility issues
- No race conditions
- Can be shared freely between threads

```java
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Immutable class example
public final class ImmutablePerson {
    // All fields are final
    private final String name;
    private final int age;
    private final Map<String, String> attributes;
    
    public ImmutablePerson(String name, int age, Map<String, String> attributes) {
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
        Map<String, String> attrs = new HashMap<>();
        attrs.put("eyeColor", "blue");
        
        ImmutablePerson person = new ImmutablePerson("John", 30, attrs);
        
        // Can be safely shared across threads without synchronization
        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName() + 
                              " reading: " + person.getName() + 
                              ", " + person.getAge() +
                              ", " + person.getAttributes());
        };
        
        new Thread(task, "Thread-1").start();
        new Thread(task, "Thread-2").start();
        
        // This won't affect the immutable object's internal state
        attrs.put("hairColor", "brown");
        
        // This will throw UnsupportedOperationException
        try {
            person.getAttributes().put("height", "180cm");
        } catch (UnsupportedOperationException e) {
            System.out.println("Couldn't modify attributes: " + e.getMessage());
        }
    }
}
```

### Thread Confinement

Thread confinement is a technique where access to mutable data is restricted to a single thread, eliminating the need for synchronization.

**Types of thread confinement:**
- **Ad-hoc thread confinement**: Convention-based - keeping data in the thread that created it
- **Stack confinement**: Using local variables, which exist on the thread's stack
- **ThreadLocal confinement**: Using ThreadLocal to give each thread its own copy

```java
public class ThreadConfinementExample {
    // Shared object (would need synchronization if accessed by multiple threads)
    private static StringBuilder sharedBuilder = new StringBuilder();
    
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            // Stack confinement - localBuilder is confined to this thread's stack
            StringBuilder localBuilder = new StringBuilder();
            
            for (int i = 0; i < 100; i++) {
                // This is thread-safe because localBuilder is confined to this thread
                localBuilder.append(i).append(",");
                
                // This is not thread-safe if multiple threads access it
                synchronized(sharedBuilder) {
                    sharedBuilder.append(i).append(",");
                }
            }
            
            System.out.println("Local builder result: " + localBuilder.toString());
        });
        
        Thread thread2 = new Thread(() -> {
            // Another stack-confined object
            StringBuilder localBuilder = new StringBuilder();
            
            for (int i = 100; i < 200; i++) {
                // Thread-safe: stack confinement
                localBuilder.append(i).append(",");
                
                // Needs synchronization for shared object
                synchronized(sharedBuilder) {
                    sharedBuilder.append(i).append(",");
                }
            }
            
            System.out.println("Local builder result: " + localBuilder.toString());
        });
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        System.out.println("Shared builder result: " + sharedBuilder.toString());
    }
}
```

## Concurrency Challenges and Solutions

Concurrent programming introduces unique challenges that don't exist in single-threaded applications. Understanding these challenges and their solutions is crucial to writing correct, high-performance concurrent code.

### Race Conditions

Race conditions occur when multiple threads access and modify shared data simultaneously, leading to unpredictable and inconsistent results.

**Types of race conditions:**
- **Read-modify-write**: Reading a value, modifying it, and writing it back without proper synchronization
- **Check-then-act**: Checking a condition and then acting on it, where the condition might change between checking and acting
- **Interleaved operations**: Multiple thread operations interleaving in an unpredictable order

**Solutions to race conditions:**
- **Synchronization**: Using synchronized blocks or methods to create critical sections
- **Atomic classes**: Using atomic variables from java.util.concurrent.atomic
- **Locks**: Using explicit lock classes from java.util.concurrent.locks
- **Thread confinement**: Restricting access to shared data to a single thread

```java
import java.util.concurrent.atomic.AtomicInteger;

public class RaceConditionExample {
    // Unsafe counter without synchronization
    private static int unsafeCounter = 0;
    
    // Safe counter using AtomicInteger
    private static AtomicInteger safeCounter = new AtomicInteger(0);
    
    // Safe counter using synchronization
    private static int synchronizedCounter = 0;
    private static final Object lock = new Object();
    
    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // 1. Unsafe increment - prone to race conditions
                    unsafeCounter++; // This is a read-modify-write operation
                    
                    // 2. Safe increment using AtomicInteger
                    safeCounter.incrementAndGet(); // Atomic operation
                    
                    // 3. Safe increment using synchronization
                    synchronized (lock) {
                        synchronizedCounter++;
                    }
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Expected count: " + (10 * 1000));
        System.out.println("Unsafe counter: " + unsafeCounter);
        System.out.println("Safe counter (atomic): " + safeCounter.get());
        System.out.println("Safe counter (synchronized): " + synchronizedCounter);
    }
}
```

### Deadlocks

Deadlocks occur when two or more threads wait for each other to release locks, resulting in all involved threads being blocked indefinitely.

**Four conditions for deadlock:**
1. **Mutual exclusion**: Resources cannot be shared simultaneously (locks)
2. **Hold and wait**: A thread holds resources while waiting for others
3. **No preemption**: Resources cannot be forcibly taken from threads
4. **Circular wait**: A circular chain of threads, each waiting for a resource held by the next

**Deadlock detection strategies:**
- Thread dumps to identify blocked threads
- JConsole or other monitoring tools
- Using timeout versions of lock acquisition

```java
public class DeadlockExample {
    private static final Object RESOURCE_A = new Object();
    private static final Object RESOURCE_B = new Object();
    
    public static void main(String[] args) {
        // Thread 1 tries to lock resources in order: A -> B
        Thread thread1 = new Thread(() -> {
            synchronized (RESOURCE_A) {
                System.out.println("Thread 1: Locked resource A");
                
                try {
                    Thread.sleep(100); // Delay to increase deadlock probability
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Now try to lock resource B
                synchronized (RESOURCE_B) {
                    System.out.println("Thread 1: Locked resource B");
                }
            }
        }, "Thread-1");
        
        // Thread 2 tries to lock resources in order: B -> A
        Thread thread2 = new Thread(() -> {
            synchronized (RESOURCE_B) {
                System.out.println("Thread 2: Locked resource B");
                
                try {
                    Thread.sleep(100); // Delay to increase deadlock probability
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Now try to lock resource A
                synchronized (RESOURCE_A) {
                    System.out.println("Thread 2: Locked resource A");
                }
            }
        }, "Thread-2");
        
        thread1.start();
        thread2.start();
        
        // This code demonstrates a deadlock, which is a problem to solve
        // In a real application, you would use techniques to prevent or recover from deadlocks
        
        // To detect the deadlock, we can check if threads are still alive after some time
        try {
            Thread.sleep(2000);
            System.out.println("Thread 1 state: " + thread1.getState());
            System.out.println("Thread 2 state: " + thread2.getState());
            if (thread1.getState() == Thread.State.BLOCKED && 
                thread2.getState() == Thread.State.BLOCKED) {
                System.out.println("Potential deadlock detected!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### Deadlock Prevention

To prevent deadlocks, you need to break at least one of the four necessary conditions:

**Strategies for deadlock prevention:**
1. **Lock ordering**: Always acquire locks in the same order in all threads
2. **Lock timeout**: Use timed lock attempts instead of indefinite waiting
3. **Lock hierarchy**: Create a hierarchy of locks that must be acquired in order
4. **Resource allocation graph**: Detect potential cycles in resource allocation
5. **Use higher-level concurrency utilities**: java.util.concurrent classes are designed to avoid deadlocks

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class DeadlockPrevention {
    private static final Lock LOCK_A = new ReentrantLock();
    private static final Lock LOCK_B = new ReentrantLock();
    
    // Method 1: Consistent lock ordering
    public static void method1() {
        // Always acquire locks in the same order across all threads
        synchronized (LOCK_A) {
            System.out.println("Method 1: Acquired lock A");
            synchronized (LOCK_B) {
                System.out.println("Method 1: Acquired lock B");
            }
        }
    }
    
    // Method 2: Lock timeout (using explicit locks)
    public static void method2() {
        boolean lockAcquired = false;
        boolean lockBacquired = false;
        try {
            // Try to acquire both locks with timeout
            lockAcquired = LOCK_A.tryLock(500, TimeUnit.MILLISECONDS);
            if (lockAcquired) {
                System.out.println("Method 2: Acquired lock A");
                
                lockBacquired = LOCK_B.tryLock(500, TimeUnit.MILLISECONDS);
                if (lockBacquired) {
                    try {
                        System.out.println("Method 2: Acquired lock B");
                        // Do work here
                    } finally {
                        LOCK_B.unlock();
                    }
                } else {
                    System.out.println("Method 2: Could not acquire lock B, releasing lock A and retrying");
                    // Couldn't get both locks, release the first and retry later
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lockAcquired) {
                LOCK_A.unlock();
            }
        }
    }
    
    // Method 3: Global lock ordering with helper method
    public static void operateOnResources(Object resource1, Object resource2) {
        // Ensure consistent lock ordering by comparing object identity
        Object firstLock, secondLock;
        if (System.identityHashCode(resource1) < System.identityHashCode(resource2)) {
            firstLock = resource1;
            secondLock = resource2;
        } else {
            firstLock = resource2;
            secondLock = resource1;
        }
        
        synchronized (firstLock) {
            System.out.println("Acquired first lock");
            synchronized (secondLock) {
                System.out.println("Acquired second lock");
                // Operate on both resources safely
            }
        }
    }
    
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                method1();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                method2();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        Thread thread3 = new Thread(() -> {
            // Using the helper method ensures consistent lock ordering
            operateOnResources(LOCK_A, LOCK_B);
        });
        
        Thread thread4 = new Thread(() -> {
            // Even when called with locks in different order, deadlock is prevented
            operateOnResources(LOCK_B, LOCK_A);
        });
        
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
```

### Livelock

Livelocks occur when threads keep responding to each other without making progress. Unlike deadlocks, threads are not blocked—they are actively executing but unable to complete their task due to continuous interference from other threads.

**Characteristics of livelocks:**
- Threads are actively running, not blocked
- Each thread responds to the actions of others
- None of the threads make progress toward completion
- Often results from attempts to recover from a deadlock

```java
public class LivelockExample {
    static class Worker {
        private String name;
        private boolean active;

        public Worker(String name, boolean active) {
            this.name = name;
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }

        public void work(Worker otherWorker, Object commonResource) {
            while (active) {
                // Wait while the other worker is active
                if (otherWorker.isActive()) {
                    System.out.println(name + ": Waiting for " + otherWorker.getName() + " to complete");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                // If we reach here, the other worker is not active
                System.out.println(name + ": Working on " + commonResource);
                active = false;
                System.out.println(name + ": Completed work on " + commonResource);
            }
        }
    }

    public static void main(String[] args) {
        final Worker worker1 = new Worker("Worker 1", true);
        final Worker worker2 = new Worker("Worker 2", true);
        final Object resource = "Shared Resource";

        // Both workers will keep waiting for each other
        new Thread(() -> worker1.work(worker2, resource)).start();
        new Thread(() -> worker2.work(worker1, resource)).start();
        
        // This livelock can be resolved by adding randomization to break symmetry:
        // if (otherWorker.isActive() && Math.random() > 0.5) { ... }
    }
}
```

### Starvation

Starvation occurs when a thread is denied access to resources for extended periods, preventing it from making progress. This can happen when a resource is continuously monopolized by higher-priority threads.

**Common causes of starvation:**
- Thread priority disparities
- Inefficient resource sharing (greedy threads)
- Lock implementations that favor certain threads
- Poor scheduling algorithms

```java
public class StarvationExample {
    private static final Object sharedResource = new Object();

    public static void main(String[] args) {
        // Create a high-priority thread
        Thread highPriorityThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (sharedResource) {
                    System.out.println("High priority thread working");
                    try {
                        // Hold the lock for a long time
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                // Briefly release the lock to give other threads a chance
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "HighPriorityThread");
        
        highPriorityThread.setPriority(Thread.MAX_PRIORITY);
        
        // Create multiple low-priority threads
        for (int i = 0; i < 5; i++) {
            Thread lowPriorityThread = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    long startTime = System.currentTimeMillis();
                    synchronized (sharedResource) {
                        long waitTime = System.currentTimeMillis() - startTime;
                        System.out.println(Thread.currentThread().getName() + 
                                          " acquired the resource after waiting " + 
                                          waitTime + "ms");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "LowPriorityThread-" + i);
            
            lowPriorityThread.setPriority(Thread.MIN_PRIORITY);
            lowPriorityThread.start();
        }
        
        // Start the high-priority thread last to demonstrate starvation
        highPriorityThread.start();
    }
}
```

### Preventing Starvation

Starvation can be addressed with several techniques:

**Strategies to prevent starvation:**
- **Fair locks**: Use fair lock implementations that service threads in FIFO order
- **Bounded waiting**: Ensure no thread waits indefinitely (time-based resource allocation)
- **Priority aging**: Gradually increase the priority of waiting threads
- **Resource partitioning**: Divide resources to ensure all threads get some access

```java
import java.util.concurrent.locks.ReentrantLock;

public class StarvationPrevention {
    // Use a fair lock to prevent starvation
    private static final ReentrantLock fairLock = new ReentrantLock(true);
    
    public static void main(String[] args) {
        // Create multiple threads that all compete for the same resource
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    fairLock.lock();
                    try {
                        System.out.println("Thread " + threadId + " acquired the lock");
                        // Simulate work
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        fairLock.unlock();
                    }
                    
                    // Do some work outside the lock
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Thread-" + i);
            
            thread.start();
        }
    }
}
```

### Thread Interference and Memory Consistency Errors

In addition to the challenges above, multi-threaded applications face other types of concurrency issues:

**Thread interference:**
- Occurs when multiple threads access and modify the same data
- Results from unpredictable thread scheduling
- Manifests as corrupted data or inconsistent states

**Memory consistency errors:**
- Occur when different threads have inconsistent views of shared data
- Result from CPU caching, compiler optimizations, and instruction reordering
- Can be prevented by proper synchronization or memory barriers

```java
public class MemoryConsistencyExample {
    private static boolean ready = false;
    private static int number = 0;
    
    public static void main(String[] args) throws InterruptedException {
        Thread writerThread = new Thread(() -> {
            number = 42;  // Write number
            ready = true;  // Set flag
        });
        
        Thread readerThread = new Thread(() -> {
            while (!ready) {
                Thread.yield();  // Wait until ready
            }
            System.out.println("Number: " + number);
        });
        
        readerThread.start();
        writerThread.start();
        
        writerThread.join();
        readerThread.join();
        
        // Solution using volatile would be:
        // private static volatile boolean ready = false;
        // private static volatile int number = 0;
        
        // Or using synchronized:
        // private static final Object lock = new Object();
        // then access both variables within synchronized(lock) blocks
    }
}
```

### Thread-Safe Design Patterns

Several design patterns can help create thread-safe components and applications:

**1. Immutable Objects Pattern**
- Make all fields final
- Initialize all fields in the constructor
- Don't provide mutator methods
- Ensure proper encapsulation
- Don't leak the 'this' reference during construction

**2. Thread-Local Storage Pattern**
- Use ThreadLocal to give each thread its own copy
- Useful for maintaining thread-specific state
- Avoids explicit synchronization
- Example: transaction contexts, user identities

**3. Monitor Pattern**
- Encapsulate data with synchronized access methods
- One object contains both data and synchronization
- All access goes through these methods
- Implemented via synchronized methods in Java

```java
// Monitor pattern example
public class BankAccount {
    private double balance;
    
    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
    }
    
    // Synchronized accessor methods form a monitor
    public synchronized double getBalance() {
        return balance;
    }
    
    public synchronized void deposit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot deposit negative amount");
        }
        double newBalance = balance + amount;
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        balance = newBalance;
    }
    
    public synchronized void withdraw(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot withdraw negative amount");
        }
        if (balance < amount) {
            throw new IllegalStateException("Insufficient funds");
        }
        double newBalance = balance - amount;
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        balance = newBalance;
    }
    
    public static void main(String[] args) throws InterruptedException {
        BankAccount account = new BankAccount(1000);
        
        Thread depositThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.deposit(100);
                System.out.println("Deposited 100, new balance: " + account.getBalance());
            }
        });
        
        Thread withdrawThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.withdraw(100);
                System.out.println("Withdrew 100, new balance: " + account.getBalance());
            }
        });
        
        depositThread.start();
        withdrawThread.start();
        
        depositThread.join();
        withdrawThread.join();
        
        System.out.println("Final balance: " + account.getBalance());
    }
}
```

## Advanced Locking

While Java's built-in synchronization mechanisms (`synchronized` keyword) provide a foundation for thread coordination, the `java.util.concurrent.locks` package introduces more sophisticated locking capabilities for advanced scenarios.

### Basic vs. Advanced Locking

**Basic Synchronization (synchronized keyword)**
- Simple to use with automatic lock acquisition and release
- Implicit locking through intrinsic object monitors
- Limited flexibility (can't interrupt a waiting thread, no timeout, etc.)
- Always acquires exclusive locks (no read/write distinction)

**Advanced Locks (java.util.concurrent.locks)**
- More flexibility and control over locking behavior
- Explicit lock acquisition and release (try/finally pattern)
- Support for timed waits, interruptibility, and non-blocking attempts
- Various specialized lock implementations for different use cases
- Often better performance under high contention

### ReentrantLock

`ReentrantLock` provides the same mutual exclusion and memory visibility guarantees as `synchronized` blocks but with additional features and explicit control.

**Key features of ReentrantLock:**
- **Reentrance**: The same thread can acquire the lock multiple times (must release it the same number of times)
- **Fairness**: Optional fair ordering policy (FIFO) for waiting threads
- **Timed lock acquisition**: Attempt to acquire a lock with a timeout
- **Interruptible lock acquisition**: Allows threads to be interrupted while waiting
- **Non-blocking lock acquisition**: Try to acquire a lock without blocking (tryLock)
- **Condition variables**: Finer control over thread waiting and signaling

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class ReentrantLockExample {
    private static final Lock lock = new ReentrantLock();
    // For demonstrating fairness comparison
    private static final Lock fairLock = new ReentrantLock(true);
    private static int counter = 0;
    
    public static void main(String[] args) throws InterruptedException {
        // Demonstrate basic locking
        basicLockingExample();
        
        // Demonstrate timed lock acquisition
        timedLockAcquisitionExample();
        
        // Demonstrate interruptible lock acquisition
        interruptibleLockAcquisitionExample();
        
        // Demonstrate lock reentrance
        lockReentranceExample();
        
        // Demonstrate fairness
        fairnessExample();
    }
    
    private static void basicLockingExample() throws InterruptedException {
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                // Always put lock acquisition in a try block and release in a finally block
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " acquired the lock");
                    counter++;
                    
                    // Simulate some work
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    // Always release the lock in a finally block
                    lock.unlock();
                    System.out.println(Thread.currentThread().getName() + " released the lock");
                }
            });
            
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Final counter value: " + counter);
    }
    
    private static void timedLockAcquisitionExample() {
        Thread thread = new Thread(() -> {
            try {
                // Try to acquire the lock with a timeout
                boolean acquired = lock.tryLock(1, TimeUnit.SECONDS);
                if (acquired) {
                    try {
                        System.out.println("Lock acquired with timeout");
                        Thread.sleep(500);
                    } finally {
                        lock.unlock();
                    }
                } else {
                    System.out.println("Failed to acquire lock within timeout");
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for lock");
            }
        });
        
        // Acquire the lock in the main thread first
        lock.lock();
        try {
            System.out.println("Main thread acquired lock, starting timeout thread");
            thread.start();
            // Hold the lock for a while
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("Main thread released lock");
        }
        
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void interruptibleLockAcquisitionExample() {
        Thread thread = new Thread(() -> {
            try {
                System.out.println("Thread attempting to acquire lock (interruptibly)");
                // This call is interruptible
                lock.lockInterruptibly();
                try {
                    System.out.println("Thread acquired lock");
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted while waiting for lock");
            }
        });
        
        // Acquire the lock in the main thread first
        lock.lock();
        try {
            thread.start();
            // Let the thread wait for a bit
            Thread.sleep(100);
            // Interrupt the waiting thread
            thread.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void lockReentranceExample() {
        // Demonstrate that the same thread can acquire the same lock multiple times
        lock.lock();
        try {
            System.out.println("First lock acquisition");
            
            // Reentrant: acquire the same lock again
            lock.lock();
            try {
                System.out.println("Second lock acquisition (reentrance)");
                
                // Even a third time is fine
                lock.lock();
                try {
                    System.out.println("Third lock acquisition (reentrance)");
                } finally {
                    lock.unlock();
                }
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }
    
    private static void fairnessExample() throws InterruptedException {
        // With fair lock, threads generally acquire the lock in the order they requested it
        
        Runnable fairRunnable = () -> {
            for (int i = 0; i < 3; i++) {
                fairLock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " acquired fair lock");
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    fairLock.unlock();
                }
            }
        };
        
        Thread[] fairThreads = new Thread[5];
        for (int i = 0; i < fairThreads.length; i++) {
            fairThreads[i] = new Thread(fairRunnable, "FairThread-" + i);
            fairThreads[i].start();
        }
        
        for (Thread t : fairThreads) {
            t.join();
        }
    }
}
```

### ReadWriteLock

`ReadWriteLock` differentiates between read and write operations, allowing multiple readers to access shared data simultaneously while ensuring exclusive access for writers.

**Key concepts of ReadWriteLock:**
- **Read (shared) lock**: Multiple threads can hold read locks simultaneously
- **Write (exclusive) lock**: Only one thread can hold a write lock, and no read locks can be held concurrently
- **Lock downgrading**: A thread holding a write lock can acquire a read lock and then release the write lock
- **Lock upgrading**: Not directly supported (read → write requires releasing read lock first)
- **Suitable for**: Read-heavy workloads with occasional writes

```java
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    private static final Map<String, String> cache = new HashMap<>();
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    // Statistics counters
    private static int reads = 0;
    private static int writes = 0;
    private static final Object statsLock = new Object();
    
    public static String getData(String key) {
        // First try to read with read lock
        rwLock.readLock().lock();
        try {
            String value = cache.get(key);
            if (value != null) {
                incrementReads();
                System.out.println(Thread.currentThread().getName() + 
                                  " read from cache: " + key + " = " + value);
                return value;
            }
        } finally {
            rwLock.readLock().unlock();
        }
        
        // Value not in cache, get write lock to update
        rwLock.writeLock().lock();
        try {
            // Double-check in case another thread wrote while we were waiting
            String value = cache.get(key);
            if (value == null) {
                // Simulate fetching data from a slow source
                value = fetchDataFromSlowSource(key);
                cache.put(key, value);
                incrementWrites();
                System.out.println(Thread.currentThread().getName() + 
                                  " added to cache: " + key + " = " + value);
            } else {
                // Someone else added it while we were waiting
                incrementReads();
                System.out.println(Thread.currentThread().getName() + 
                                  " read from cache (after write lock): " + key + " = " + value);
            }
            return value;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    private static String fetchDataFromSlowSource(String key) {
        // Simulate a slow operation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Value for " + key;
    }
    
    private static void incrementReads() {
        synchronized (statsLock) {
            reads++;
        }
    }
    
    private static void incrementWrites() {
        synchronized (statsLock) {
            writes++;
        }
    }
    
    // Demonstrate lock downgrading (write → read)
    public static void updateAndRead(String key, String newValue) {
        rwLock.writeLock().lock(); // Get write lock
        try {
            // Update the value
            cache.put(key, newValue);
            incrementWrites();
            System.out.println(Thread.currentThread().getName() + 
                              " updated cache: " + key + " = " + newValue);
            
            // Downgrade by acquiring read lock before releasing write lock
            rwLock.readLock().lock();
            try {
                // Now we can release the write lock but still have read access
                rwLock.writeLock().unlock();
                System.out.println(Thread.currentThread().getName() + 
                                  " downgraded to read lock");
                
                // Read the value we just wrote (with only a read lock)
                String value = cache.get(key);
                incrementReads();
                System.out.println(Thread.currentThread().getName() + 
                                  " read with downgraded lock: " + key + " = " + value);
            } finally {
                rwLock.readLock().unlock();
            }
        } finally {
            // In case we didn't downgrade
            if (rwLock.writeLock().isHeldByCurrentThread()) {
                rwLock.writeLock().unlock();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // Simulate multiple reader threads
        Thread[] readerThreads = new Thread[5];
        for (int i = 0; i < readerThreads.length; i++) {
            final int id = i;
            readerThreads[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    getData("Key-" + (j % 3));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Reader-" + id);
        }
        
        // Start all reader threads
        for (Thread thread : readerThreads) {
            thread.start();
        }
        
        // Demonstrate lock downgrading
        Thread downgradingThread = new Thread(() -> {
            try {
                // Wait a bit to let readers start
                Thread.sleep(200);
                updateAndRead("Key-1", "Updated value");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "DowngradingThread");
        
        downgradingThread.start();
        
        // Wait for all threads to complete
        for (Thread thread : readerThreads) {
            thread.join();
        }
        downgradingThread.join();
        
        // Print statistics
        System.out.println("\nStatistics:");
        System.out.println("Total reads: " + reads);
        System.out.println("Total writes: " + writes);
        System.out.println("Final cache size: " + cache.size());
        System.out.println("Cache contents: " + cache);
    }
}
```

### StampedLock

Introduced in Java 8, `StampedLock` is a capability-based lock with three modes: writing, reading, and optimistic reading. It offers better performance than ReadWriteLock in many scenarios.

**Key features of StampedLock:**
- **Write lock**: Exclusive access, similar to writeLock in ReadWriteLock
- **Read lock**: Shared access, similar to readLock in ReadWriteLock
- **Optimistic read**: Allows reading without acquiring a lock, later validating if the read was consistent
- **Non-reentrant**: Unlike ReentrantLock and ReentrantReadWriteLock
- **No support for conditions**: Cannot create condition variables
- **Lock conversion**: Can convert between lock modes (e.g., optimistic → read)

```java
import java.util.concurrent.locks.StampedLock;

public class StampedLockExample {
    private double x, y;  // Shared mutable state
    private final StampedLock lock = new StampedLock();
    
    // Write method - exclusive access
    public void move(double deltaX, double deltaY) {
        // Acquire write lock
        long stamp = lock.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            lock.unlockWrite(stamp);
        }
    }
    
    // Read method - shared access
    public double distance() {
        long stamp = lock.tryOptimisticRead();
        double currentX = x;
        double currentY = y;
        
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
    
    // Optimistic read method
    public void moveIfAtOrigin(double newX, double newY) {
        long stamp = lock.tryOptimisticRead();
        double currentX = x;
        double currentY = y;
        
        if (currentX != 0 || currentY != 0) {
            stamp = lock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        
        if (currentX != newX || currentY != newY) {
            stamp = lock.writeLock();
            try {
                x = newX;
                y = newY;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }
}
```

## Inter-Thread Communication

Communication between threads is essential for coordinated execution in concurrent applications. Java provides several mechanisms for threads to exchange information and synchronize their activities.

### Wait and Notify Mechanism

The `wait()`, `notify()`, and `notifyAll()` methods, inherited from `Object`, provide a fundamental mechanism for inter-thread communication:

**Key concepts:**
- **wait()**: Causes the current thread to wait until another thread invokes `notify()` or `notifyAll()`
- **notify()**: Wakes up a single thread waiting on the object
- **notifyAll()**: Wakes up all threads waiting on the object
- **Must be called from synchronized context**: These methods must be called from within a synchronized block or method

```java
public class WaitNotifyExample {
    private static final Object lock = new Object();
    private static boolean dataReady = false;
    private static String data = null;
    
    public static void main(String[] args) {
        // Consumer thread - waits for data
        Thread consumerThread = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Consumer waiting for data...");
                while (!dataReady) {
                    try {
                        lock.wait(); // Release lock and wait
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                // Process data after being notified
                System.out.println("Consumer received data: " + data);
            }
        });
        
        // Producer thread - prepares data and notifies
        Thread producerThread = new Thread(() -> {
            // Simulate some work
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            
            synchronized (lock) {
                // Prepare data
                data = "Important information";
                dataReady = true;
                
                // Notify waiting thread
                System.out.println("Producer is notifying consumer...");
                lock.notify();
            }
        });
        
        consumerThread.start();
        producerThread.start();
    }
}
```

**Common pitfalls with wait/notify:**
- **Lost wakeup**: Notify occurs before wait, causing the waiting thread to miss the notification
- **Spurious wakeup**: Thread may wake up without notification; always check condition in a loop
- **Deadlock**: If a thread holding the lock calls wait() but is never notified
- **Missed signals**: If notify() is called but no thread is waiting

### Condition Variables

`Condition` objects, part of the `java.util.concurrent.locks` package, provide an alternative to `wait()/notify()` with more flexibility:

**Advantages over wait/notify:**
- **Multiple wait-sets**: One lock can have multiple conditions
- **Better integration with explicit locks**: Works with ReentrantLock, ReadWriteLock, etc.
- **Additional features**: Timed waits, ability to interrupt waiting threads
- **FIFO queuing**: When using fair locks, offers first-in-first-out queuing policy

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionExample {
    private final Lock lock = new ReentrantLock();
    private final Condition dataAvailable = lock.newCondition();
    private final Condition spaceAvailable = lock.newCondition();
    private final String[] buffer = new String[10];
    private int count = 0, putIndex = 0, takeIndex = 0;
    
    public void put(String item) throws InterruptedException {
        lock.lock();
        try {
            // Wait until space is available
            while (count == buffer.length) {
                spaceAvailable.await();
            }
            
            // Add item to buffer
            buffer[putIndex] = item;
            putIndex = (putIndex + 1) % buffer.length;
            count++;
            
            // Signal to consumer that data is available
            dataAvailable.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public String take() throws InterruptedException {
        lock.lock();
        try {
            // Wait until data is available
            while (count == 0) {
                dataAvailable.await();
            }
            
            // Remove item from buffer
            String item = buffer[takeIndex];
            takeIndex = (takeIndex + 1) % buffer.length;
            count--;
            
            // Signal to producer that space is available
            spaceAvailable.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) {
        ConditionExample buffer = new ConditionExample();
        
        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    String item = "Item-" + i;
                    buffer.put(item);
                    System.out.println("Produced: " + item);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    String item = buffer.take();
                    System.out.println("Consumed: " + item);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
    }
}
```

### Blocking Queues

Blocking queues provide a higher-level abstraction for producer-consumer patterns, handling all the synchronization internally:

**Key implementations:**
- **ArrayBlockingQueue**: Bounded queue backed by an array
- **LinkedBlockingQueue**: Optionally bounded queue backed by linked nodes
- **PriorityBlockingQueue**: Unbounded priority queue
- **DelayQueue**: Queue where elements can only be taken when their delay has expired
- **SynchronousQueue**: Queue with no internal capacity where each put must wait for a take

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueExample {
    public static void main(String[] args) {
        // Create a bounded blocking queue with capacity of 5
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
        
        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    String item = "Item-" + i;
                    
                    // Put will block if queue is full
                    queue.put(item);
                    System.out.println("Produced: " + item + ", Queue size: " + queue.size());
                    
                    // Simulate varying production rates
                    Thread.sleep((long) (Math.random() * 100));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    // Wait for up to 2 seconds for an item
                    String item = queue.poll(2, TimeUnit.SECONDS);
                    
                    if (item == null) {
                        // No more items available within timeout
                        System.out.println("Consumer timed out, assuming production complete");
                        break;
                    }
                    
                    System.out.println("Consumed: " + item + ", Queue size: " + queue.size());
                    
                    // Simulate varying consumption rates
                    Thread.sleep((long) (Math.random() * 200));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
    }
}
```

### CountDownLatch and CyclicBarrier

These synchronization aids help coordinate the activities of multiple threads:

#### CountDownLatch

A synchronization aid that allows one or more threads to wait until a set of operations in other threads completes.

**Key characteristics:**
- **One-time use**: Cannot be reset after count reaches zero
- **Decrementing counter**: Threads count down by calling `countDown()`
- **Waiting threads**: Threads can wait for counter to reach zero with `await()`
- **No ownership**: Any thread can count down, any thread can wait

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        // Create a CountDownLatch with count of 5
        CountDownLatch latch = new CountDownLatch(5);
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        for (int i = 0; i < 5; i++) {
            final int workerId = i;
            executor.submit(() -> {
                try {
                    // Simulate task initialization
                    Thread.sleep(1000 + (int)(Math.random() * 1000));
                    System.out.println("Worker " + workerId + " completed initialization");
                    
                    // Signal that this worker is ready
                    latch.countDown();
                    System.out.println("Worker " + workerId + " counted down, remaining: " + latch.getCount());
                    
                    // Continue with additional work after initialization
                    Thread.sleep(1000);
                    System.out.println("Worker " + workerId + " continuing with remaining tasks");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Main thread waits for all workers to complete initialization
        System.out.println("Main thread waiting for workers to initialize...");
        latch.await();
        System.out.println("All workers have initialized, main thread continues");
        
        // Cleanup
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
```

#### CyclicBarrier

A synchronization aid that allows a set of threads to wait for each other to reach a common barrier point.

**Key characteristics:**
- **Reusable**: Automatically resets after all threads arrive
- **Barrier action**: Can execute a runnable when the barrier is tripped
- **Thread coordination**: All threads must reach the barrier to proceed
- **Useful for**: Algorithms that proceed in synchronous phases

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample {
    public static void main(String[] args) {
        final int THREAD_COUNT = 3;
        final int ITERATIONS = 3;
        
        // Create a CyclicBarrier with an action that executes when all threads arrive
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT, () -> {
            System.out.println("All threads reached the barrier, executing barrier action!");
        });
        
        // Create and start threads
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int iteration = 0; iteration < ITERATIONS; iteration++) {
                        System.out.println("Thread " + threadId + " is doing work in iteration " + iteration);
                        
                        // Simulate some work
                        Thread.sleep(1000 + (int)(Math.random() * 1000));
                        
                        System.out.println("Thread " + threadId + " reached the barrier in iteration " + iteration);
                        
                        // Wait for all threads to reach this point
                        int arrivalIndex = barrier.await();
                        
                        System.out.println("Thread " + threadId + 
                                          " continuing after the barrier (arrival index: " + arrivalIndex + 
                                          ") in iteration " + iteration);
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    System.err.println("Thread " + threadId + " interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
```

## Lock-Free Algorithms

Lock-free algorithms allow thread-safe access to shared data without using locks, avoiding problems like deadlocks and priority inversion. These algorithms use atomic operations and are designed to ensure that at least one thread makes progress at any time.

### Atomic Variables

Java's `java.util.concurrent.atomic` package provides classes that support lock-free, thread-safe operations on single variables:

**Core atomic classes:**
- **AtomicBoolean**: Boolean value that can be updated atomically
- **AtomicInteger/AtomicLong**: Integer values with atomic operations
- **AtomicReference**: Reference to an object with atomic operations
- **AtomicIntegerArray/AtomicLongArray/AtomicReferenceArray**: Arrays with atomic element access

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicVariablesExample {
    // Non-atomic counter (would require synchronization)
    private static int unsafeCounter = 0;
    
    // Atomic counter
    private static AtomicInteger atomicCounter = new AtomicInteger(0);
    
    // Atomic reference
    private static AtomicReference<String> atomicString = new AtomicReference<>("initial");
    
    public static void main(String[] args) throws InterruptedException {
        // Create multiple threads to increment the counters
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // Unsafe increment (prone to lost updates)
                    unsafeCounter++;
                    
                    // Atomic increment (thread-safe)
                    atomicCounter.incrementAndGet();
                    
                    // Compare-and-set pattern for atomic reference
                    String current;
                    do {
                        current = atomicString.get();
                    } while (!atomicString.compareAndSet(current, current + "*"));
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Expected count: " + (10 * 1000));
        System.out.println("Unsafe counter: " + unsafeCounter); // Likely less than expected
        System.out.println("Atomic counter: " + atomicCounter.get()); // Should be exactly 10000
        System.out.println("Final atomic string: " + atomicString.get());
    }
}
```

### Compare-And-Swap (CAS) Operations

CAS is the foundation of lock-free algorithms. It's an atomic operation that compares a memory location to an expected value and, if they match, updates it to a new value.

**Key characteristics of CAS:**
- **Atomic**: The entire operation happens as a single, uninterruptible unit
- **Non-blocking**: Threads don't block when contention occurs
- **Optimistic**: Assumes updates will succeed without conflicts (but handles them when they occur)
- **Hardware support**: Implemented using processor-specific instructions like CMPXCHG

```java
import java.util.concurrent.atomic.AtomicReference;

public class CASExample {
    // A simple class to demonstrate atomic reference updates
    static class Counter {
        private final int value;
        
        public Counter(int value) {
            this.value = value;
        }
        
        public Counter increment() {
            return new Counter(value + 1);
        }
        
        public int getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return "Counter{value=" + value + '}';
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // Create an atomic reference to a Counter
        AtomicReference<Counter> atomicCounter = new AtomicReference<>(new Counter(0));
        
        // Implement increment with CAS
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // CAS loop pattern
                    while (true) {
                        Counter current = atomicCounter.get();
                        Counter next = current.increment();
                        if (atomicCounter.compareAndSet(current, next)) {
                            // Success - exit the loop
                            break;
                        }
                        // Failure - another thread updated the reference, so retry
                        System.out.println("CAS failed, retrying...");
                    }
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Final counter value: " + atomicCounter.get().getValue());
    }
}
```

### ABA Problem and Solutions

The ABA problem is a common issue in CAS-based algorithms where a value changes from A to B and back to A between a thread's read and CAS operations, potentially leading to incorrect behavior.

**The ABA problem scenario:**
1. Thread 1 reads value A
2. Thread 1 gets suspended
3. Thread 2 changes value from A to B
4. Thread 2 changes value back to A
5. Thread 1 resumes and performs CAS, which succeeds because value is still A
6. However, the state is not what Thread 1 expected (the structure may have changed)

**Solutions to the ABA problem:**
- **Versioning/stamping**: Associate a version number or timestamp with the value
- **Immutable data structures**: Use immutable values that can't be modified
- **Hazard pointers**: Mark objects that are in use by threads
- **AtomicStampedReference/AtomicMarkableReference**: Java classes specifically designed to prevent ABA

```java
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABAProblemExample {
    public static void main(String[] args) throws InterruptedException {
        // Regular AtomicReference (vulnerable to ABA)
        java.util.concurrent.atomic.AtomicReference<Integer> atomicRef = 
            new java.util.concurrent.atomic.AtomicReference<>(1);
        
        // AtomicStampedReference (protects against ABA)
        AtomicStampedReference<Integer> stampedRef = 
            new AtomicStampedReference<>(1, 0);
        
        // Prepare for demonstration
        int initialStamp = stampedRef.getStamp();
        
        // Thread 1 - will be delayed to demonstrate the ABA problem
        Thread thread1 = new Thread(() -> {
            try {
                // Remember initial values
                Integer initialValue = atomicRef.get();
                System.out.println("Thread 1: Initial value = " + initialValue);
                
                // Get the current value and stamp of stampedRef
                int[] stampHolder = new int[1];
                Integer initialValueStamped = stampedRef.get(stampHolder);
                int initialStampLocal = stampHolder[0];
                System.out.println("Thread 1: Initial stamped value = " + initialValueStamped + 
                                  ", stamp = " + initialStampLocal);
                
                // Sleep to allow Thread 2 to make changes
                Thread.sleep(1000);
                
                // Try to update with CAS - this will succeed despite the ABA problem
                boolean casSuccess = atomicRef.compareAndSet(initialValue, 3);
                System.out.println("Thread 1: CAS result = " + casSuccess + 
                                  ", new value = " + atomicRef.get());
                
                // Try to update with stamped CAS - this will fail due to stamp change
                boolean stampedCasSuccess = stampedRef.compareAndSet(
                    initialValueStamped, 3, initialStampLocal, initialStampLocal + 1);
                System.out.println("Thread 1: Stamped CAS result = " + stampedCasSuccess + 
                                  ", new value = " + stampedRef.get(new int[1]));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Thread 2 - will perform the A -> B -> A change
        Thread thread2 = new Thread(() -> {
            // Change value from A to B
            atomicRef.set(2);
            System.out.println("Thread 2: Changed atomic value to 2");
            
            // Change stamped value from A to B, incrementing stamp
            stampedRef.set(2, initialStamp + 1);
            System.out.println("Thread 2: Changed stamped value to 2, stamp to " + 
                              (initialStamp + 1));
            
            // Change value back to A (causing ABA in atomicRef)
            atomicRef.set(1);
            System.out.println("Thread 2: Changed atomic value back to 1");
            
            // Change stamped value back to A, but with different stamp
            stampedRef.set(1, initialStamp + 2);
            System.out.println("Thread 2: Changed stamped value back to 1, stamp to " + 
                              (initialStamp + 2));
        });
        
        // Start the threads
        thread1.start();
        Thread.sleep(200); // Ensure Thread 1 gets initial values
        thread2.start();
        
        // Wait for completion
        thread1.join();
        thread2.join();
    }
}
```

### Lock-Free Data Structures

Lock-free data structures provide thread-safe access without using locks, typically using CAS operations:

**Common lock-free data structures in Java:**
- **ConcurrentLinkedQueue**: Lock-free queue implementation
- **ConcurrentLinkedDeque**: Lock-free double-ended queue
- **ConcurrentSkipListMap/Set**: Lock-free sorted map/set implementations
- **CopyOnWriteArrayList/Set**: Thread-safe collections that create a new copy for each modification

```java
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LockFreeDataStructuresExample {
    public static void main(String[] args) throws InterruptedException {
        // Create a lock-free queue
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        
        // Create producer and consumer threads
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Producer tasks
        for (int i = 0; i < 2; i++) {
            final int producerId = i;
            executor.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    String item = "P" + producerId + "-" + j;
                    queue.add(item);
                    System.out.println("Producer " + producerId + " added: " + item);
                    try {
                        Thread.sleep((long) (Math.random() * 10));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
        }
        
        // Consumer tasks
        for (int i = 0; i < 2; i++) {
            final int consumerId = i;
            executor.submit(() -> {
                while (true) {
                    String item = queue.poll();
                    if (item != null) {
                        System.out.println("Consumer " + consumerId + " processed: " + item);
                    } else {
                        // Queue might be temporarily empty, do something else
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            });
        }
        
        // Let the simulation run for a while
        Thread.sleep(1000);
        
        // Clean up
        executor.shutdownNow();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        
        System.out.println("Final queue size: " + queue.size());
    }
}
```

## Thread Model for High-Performance IO

High-performance IO operations require specialized thread models that efficiently handle numerous concurrent connections without exhausting system resources. Traditional thread-per-request models often face scalability limitations when handling thousands of connections.

### Blocking vs Non-Blocking IO

**Blocking IO:**
- Thread waits (blocks) until IO operation completes
- Simple programming model but inefficient resource utilization
- One thread per active connection limits scalability

**Non-Blocking IO:**
- Thread can continue processing while IO operations are pending
- More complex programming model but better resource utilization
- Can handle thousands of connections with fewer threads

```java
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BlockingVsNonBlockingExample {
    // Blocking IO example
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
    
    // Non-blocking IO example
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
```

### Thread Models for IO-Intensive Applications

#### 1. Thread-Per-Connection Model

The simplest approach where each client connection gets its own thread.

**Characteristics:**
- Simple to implement and understand
- Direct mapping between connections and threads
- Limited scalability (typically <1000 connections)
- High memory overhead for many connections

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadPerConnectionServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            final Socket clientSocket = serverSocket.accept();
            // Spawn a new thread for each connection
            new Thread(() -> {
                try {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    
    private static void handleClient(Socket clientSocket) throws IOException {
        // Handle client connection
        // Read/write from/to socket until client disconnects
        // ...
        clientSocket.close();
    }
}
```

#### 2. Thread Pool Model

Uses a fixed number of worker threads to handle multiple connections.

**Characteristics:**
- Better resource management than thread-per-connection
- Controlled concurrency with configurable pool size
- Still uses blocking IO, limiting scalability
- Good for moderate concurrency (<few thousand connections)

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolServer {
    public static void main(String[] args) throws IOException {
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        ServerSocket serverSocket = new ServerSocket(8080);
        
        while (true) {
            final Socket clientSocket = serverSocket.accept();
            executor.submit(() -> {
                try {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    
    private static void handleClient(Socket clientSocket) throws IOException {
        // Handle client connection
        // ...
        clientSocket.close();
    }
}
```

#### 3. Reactor Pattern

Event-driven architecture using non-blocking IO with a small number of threads.

**Characteristics:**
- Highly scalable, can handle thousands of connections
- Uses Java NIO's Selector for efficient IO multiplexing
- Complex programming model
- Excellent for high-concurrency scenarios

```java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ReactorPatternServer {
    public static void main(String[] args) throws IOException {
        // Create selector
        Selector selector = Selector.open();
        
        // Create and configure server socket channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8080));
        
        // Register server channel with selector for accept events
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        
        while (true) {
            // Block until there are events
            selector.select();
            
            // Get set of ready keys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                
                if (key.isAcceptable()) {
                    // Accept new connection
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    
                    // Register for reading
                    client.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // Read data from client
                    SocketChannel client = (SocketChannel) key.channel();
                    buffer.clear();
                    int bytesRead = client.read(buffer);
                    
                    if (bytesRead > 0) {
                        buffer.flip();
                        // Process data
                        // ...
                        
                        // Register for writing if needed
                        key.interestOps(SelectionKey.OP_WRITE);
                    } else if (bytesRead < 0) {
                        // Client closed connection
                        client.close();
                        key.cancel();
                    }
                } else if (key.isWritable()) {
                    // Write data to client
                    SocketChannel client = (SocketChannel) key.channel();
                    // Write response
                    // ...
                    
                    // Switch back to reading mode
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }
}
```

#### 4. Multi-Reactor Pattern

An extension of the Reactor pattern using multiple reactors (selectors) on multiple threads.

**Characteristics:**
- Distributes work across multiple CPU cores
- Main reactor accepts connections, worker reactors handle IO
- Scales well on multi-core systems
- Used in high-performance servers like Netty

```java
// Simplified multi-reactor pattern (pseudocode)
public class MultiReactorServer {
    // Main reactor accepts connections and distributes to worker reactors
    class MainReactor implements Runnable {
        private final Selector selector;
        private final ServerSocketChannel serverChannel;
        private final WorkerReactor[] workers;
        private int nextWorker = 0;
        
        MainReactor(int port, int numWorkers) throws IOException {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            // Create worker reactors
            workers = new WorkerReactor[numWorkers];
            for (int i = 0; i < numWorkers; i++) {
                workers[i] = new WorkerReactor();
                new Thread(workers[i]).start();
            }
        }
        
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    for (SelectionKey key : keys) {
                        if (key.isAcceptable()) {
                            // Accept connection and hand off to worker
                            SocketChannel channel = serverChannel.accept();
                            channel.configureBlocking(false);
                            workers[nextWorker].register(channel);
                            nextWorker = (nextWorker + 1) % workers.length;
                        }
                    }
                    keys.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Worker reactors handle IO for assigned connections
    class WorkerReactor implements Runnable {
        private final Selector selector;
        private final Queue<SocketChannel> channelQueue = new ConcurrentLinkedQueue<>();
        
        WorkerReactor() throws IOException {
            selector = Selector.open();
        }
        
        void register(SocketChannel channel) {
            channelQueue.add(channel);
            selector.wakeup(); // Wake up select() to process new channel
        }
        
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    // Register any new channels
                    SocketChannel channel;
                    while ((channel = channelQueue.poll()) != null) {
                        channel.register(selector, SelectionKey.OP_READ);
                    }
                    
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    for (SelectionKey key : keys) {
                        if (key.isReadable()) {
                            // Handle read events
                            // ...
                        } else if (key.isWritable()) {
                            // Handle write events
                            // ...
                        }
                    }
                    keys.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### Using CompletableFuture for Asynchronous IO

Java's CompletableFuture provides a higher-level abstraction for asynchronous programming:

**Key benefits:**
- Declarative style for composing asynchronous operations
- Handles exceptions across asynchronous boundaries
- Supports various completion stages and transformations
- Can be combined with NIO for powerful asynchronous IO

```java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureIOExample {
    // Asynchronously read file and process its contents
    public static CompletableFuture<String> readFileAsync(Path path) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        try {
            AsynchronousFileChannel channel = 
                AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (result > 0) {
                        attachment.flip();
                        byte[] data = new byte[attachment.remaining()];
                        attachment.get(data);
                        future.complete(new String(data));
                    } else {
                        future.complete("");
                    }
                    try {
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
    
    public static void main(String[] args) {
        // Example usage
        Path path = Paths.get("example.txt");
        
        readFileAsync(path)
            .thenApply(content -> content.toUpperCase())
            .thenAccept(System.out::println)
            .exceptionally(ex -> {
                System.err.println("Error: " + ex.getMessage());
                return null;
            });
        
        // Do other work while IO is in progress
        System.out.println("Reading file asynchronously...");
    }
}
```

### JDBC and Thread Models

JDBC operations are typically blocking, requiring appropriate thread models for database interactions:

**Key approaches:**
- **Connection pooling**: Reuse database connections across threads
- **Asynchronous wrappers**: Wrap JDBC operations in CompletableFuture
- **Reactive drivers**: Use reactive database drivers when available
- **Read/write separation**: Separate thread pools for reads and writes

```java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncJdbcExample {
    private final ExecutorService dbExecutor = Executors.newFixedThreadPool(10);
    private final ConnectionPool connectionPool; // Assume this exists
    
    public AsyncJdbcExample(ConnectionPool pool) {
        this.connectionPool = pool;
    }
    
    public CompletableFuture<User> getUserAsync(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE id = ?")) {
                
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email")
                        );
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        }, dbExecutor);
    }
    
    // Example usage
    public void processUser(int userId) {
        getUserAsync(userId)
            .thenAccept(user -> {
                if (user != null) {
                    System.out.println("Found user: " + user.getName());
                    // Do something with user
                } else {
                    System.out.println("User not found");
                }
            })
            .exceptionally(ex -> {
                System.err.println("Database error: " + ex.getMessage());
                return null;
            });
    }
}
```

### Virtual Threads for High-Performance IO

Virtual threads are designed for high-performance IO and can be used to handle many concurrent IO operations efficiently.

**Key characteristics of virtual threads:**
- **Low memory footprint**: Each virtual thread consumes much less memory than a platform thread
- **Automatic yield**: Virtual threads automatically yield when blocked, allowing other threads to run
- **Structured concurrency**: Tasks started in a particular scope complete before leaving that scope

**When to use virtual threads:**
- For IO-bound tasks with many concurrent operations
- When the task is short-lived and can be started and stopped frequently
- When the task is CPU-bound but can be offloaded to virtual threads

```java
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VirtualThreadHighPerformanceIO {
    public static void main(String[] args) throws InterruptedException {
        int taskCount = 10_000;
        
        // Platform threads
        long platformStart = System.currentTimeMillis();
        
        try (ExecutorService platformExecutor = Executors.newFixedThreadPool(taskCount)) {
            for (int i = 0; i < taskCount; i++) {
                final int id = i;
                platformExecutor.submit(() -> {
                    // Simulate IO-bound task
                    try {
                        System.out.println("IO task " + id + " starting on " + Thread.currentThread());
                        Thread.sleep(100);
                        System.out.println("IO task " + id + " completed");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        
        long platformDuration = System.currentTimeMillis() - platformStart;
        System.out.println("Platform threads completed in " + platformDuration + "ms");
        
        // Virtual threads
        long virtualStart = System.currentTimeMillis();
        
        try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                final int id = i;
                virtualExecutor.submit(() -> {
                    // Simulate IO-bound task
                    try {
                        System.out.println("IO task " + id + " starting on " + Thread.currentThread());
                        Thread.sleep(100);
                        System.out.println("IO task " + id + " completed");
                        return id;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return -1;
                    }
                });
            }
            
            virtualExecutor.shutdown();
            virtualExecutor.awaitTermination(1, TimeUnit.MINUTES);
        }
        
        long virtualDuration = System.currentTimeMillis() - virtualStart;
        System.out.println("Virtual threads completed in " + virtualDuration + "ms");
        System.out.println("Speedup: " + (double)platformDuration / virtualDuration + "x");
    }
}
```

### Structured Concurrency

Structured Concurrency, introduced with virtual threads, provides a more disciplined approach to managing concurrent tasks. It ensures that tasks started in a particular scope complete before leaving that scope.

**Key benefits:**
- **Improved reliability**: Ensures tasks don't leak
- **Better error propagation**: Propagates exceptions from subtasks
- **Cleaner lifecycle management**: Ties subtask lifetime to a parent scope
- **Simplified cancellation**: Cancels all subtasks when the scope is cancelled

```java
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeUnit;

public class StructuredConcurrencyExample {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Traditional approach
        traditionalApproach();
        
        // Structured approach
        structuredApproach();
    }
    
    private static void traditionalApproach() throws InterruptedException {
        System.out.println("Traditional approach:");
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> userFuture = executor.submit(() -> fetchUser());
            Future<String> orderFuture = executor.submit(() -> fetchOrder());
            
            try {
                String user = userFuture.get();
                String order = orderFuture.get();
                System.out.println("Combined result: " + user + " - " + order);
            } catch (ExecutionException e) {
                System.out.println("Error: " + e.getCause().getMessage());
                // Note: if one fails, we should cancel the other, but often forgotten
                userFuture.cancel(true);
                orderFuture.cancel(true);
            }
        }
    }
    
    private static void structuredApproach() throws InterruptedException, ExecutionException {
        System.out.println("Structured approach:");
        
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            StructuredTaskScope.Subtask<String> userTask = scope.fork(() -> fetchUser());
            StructuredTaskScope.Subtask<String> orderTask = scope.fork(() -> fetchOrder());
            
            // Wait for both tasks to complete or one to fail
            scope.join();
            // Propagate exceptions
            scope.throwIfFailed();
            
            // Extract results
            String user = userTask.get();
            String order = orderTask.get();
            System.out.println("Combined result: " + user + " - " + order);
        }
        // When scope closes, all subtasks are guaranteed to be complete
    }
    
    private static String fetchUser() throws InterruptedException {
        Thread.sleep(Duration.ofMillis(100));
        System.out.println("User fetched by " + Thread.currentThread());
        return "User123";
    }
    
    private static String fetchOrder() throws InterruptedException {
        Thread.sleep(Duration.ofMillis(150));
        System.out.println("Order fetched by " + Thread.currentThread());
        return "Order456";
    }
}
```

### Best Practices for Virtual Threads

While virtual threads use the same Thread API as platform threads, some practices are particularly important for effective use:

**Do:**
- Use virtual threads for IO-bound tasks
- Keep synchronization blocks short
- Use structured concurrency where possible
- Use per-task thread creation (one virtual thread per task)
- Use standard blocking IO operations

**Don't:**
- Use thread pools for virtual threads (defeats the purpose)
- Use ThreadLocal extensively with virtual threads
- Hold locks during blocking operations
- Use virtual threads for CPU-intensive tasks
- Pin virtual threads (keep them mounted on carriers)

```java
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadBestPractices {
    public static void main(String[] args) {
        // DO: Use virtual threads for IO-bound tasks
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; i++) {
                final int id = i;
                executor.submit(() -> {
                    // Simulate IO-bound task
                    try {
                        // This is IO-bound and will benefit from virtual threads
                        System.out.println("IO task " + id + " starting on " + Thread.currentThread());
                        Thread.sleep(100);
                        System.out.println("IO task " + id + " completed");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        
        // DON'T: Create pools of virtual threads
        // WRONG:
        // ExecutorService wrongPool = Executors.newFixedThreadPool(100, Thread.ofVirtual().factory());
        
        // DO: Use atomic variables instead of synchronized for counters
        AtomicInteger counter = new AtomicInteger(0);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; i++) {
                executor.submit(() -> {
                    // Good: Using atomic operations instead of synchronized blocks
                    int value = counter.incrementAndGet();
                    System.out.println("Counter: " + value);
                });
            }
        }
        
        // DON'T: Rely heavily on ThreadLocal with virtual threads
        // Use alternatives like explicit parameters or context objects
        
        // Example of a better approach passing context explicitly
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10; i++) {
                final String taskContext = "Context-" + i;
                executor.submit(() -> processWithContext(taskContext));
            }
        }
    }
    
    private static void processWithContext(String context) {
        // Better: Context passed explicitly rather than using ThreadLocal
        System.out.println("Processing with context: " + context);
    }
}
```

### Virtual Thread Performance Considerations

Virtual threads offer significant performance improvements for IO-bound applications, but there are important considerations for maximizing their benefit:

**Pinning**:
- Virtual threads are temporarily "pinned" to carrier threads during synchronized blocks
- Pinned virtual threads cannot yield during blocking operations
- Long synchronized blocks can reduce the efficiency of virtual threads

**IO Operations**:
- Standard blocking IO operations automatically yield the carrier thread
- Virtual threads shine with many concurrent IO operations
- Network calls, database queries, and file operations all benefit

**Memory Consumption**:
- Each virtual thread consumes much less memory than a platform thread
- Still monitor memory usage when creating millions of threads
- Tune stack size if necessary (usually not needed)

```java
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadPerformanceExample {
    // Bad: Long-held lock can cause pinning
    private static final Object badLock = new Object();
    
    // Running counter
    private static final AtomicInteger counter = new AtomicInteger();
    
    public static void main(String[] args) throws InterruptedException {
        int taskCount = 10_000;
        
        // Demonstrate pinning problem
        long pinnedStart = System.currentTimeMillis();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                executor.submit(() -> {
                    synchronized (badLock) {
                        try {
                            // This thread is pinned during the entire sleep
                            // Virtual threads can't yield during this time
                            Thread.sleep(10);
                            counter.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            }
        }
        
        long pinnedDuration = System.currentTimeMillis() - pinnedStart;
        System.out.println("Pinned approach completed in: " + pinnedDuration + "ms");
        
        counter.set(0);
        
        // Better approach: Minimize synchronized blocks
        long unpinnedStart = System.currentTimeMillis();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                executor.submit(() -> {
                    try {
                        // Do the blocking operation outside the synchronized block
                        Thread.sleep(10);
                        
                        // Only synchronize the small critical section
                        synchronized (badLock) {
                            counter.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        
        long unpinnedDuration = System.currentTimeMillis() - unpinnedStart;
        System.out.println("Unpinned approach completed in: " + unpinnedDuration + "ms");
        System.out.println("Improvement: " + (double)pinnedDuration / unpinnedDuration + "x");
    }
}
```

### Virtual Threads for HTTP Services

One of the most common use cases for virtual threads is implementing HTTP services that handle many concurrent connections.

**Benefits for HTTP services:**
- **Simpler programming model**: One thread per request is viable again
- **Higher throughput**: Handle many more concurrent connections
- **Resource efficiency**: Less memory consumed per connection
- **Potential latency improvements**: More responsive under high load

```java
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class VirtualThreadHttpServer {
    public static void main(String[] args) throws IOException {
        // Create HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Add context for handling requests
        server.createContext("/api", new ApiHandler());
        
        // Use virtual threads for request handling
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        
        // Start the server
        server.start();
        
        System.out.println("Server started on port 8080");
    }
    
    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Get the request method and path
                String requestMethod = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                
                System.out.println(Thread.currentThread() + " handling " + 
                                  requestMethod + " " + path);
                
                // Simulate processing delay (e.g., database query, external API call)
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Create response
                String response = """
                    {
                        "message": "Hello from virtual thread!",
                        "thread": "%s",
                        "path": "%s",
                        "method": "%s"
                    }
                    """.formatted(Thread.currentThread(), path, requestMethod);
                
                // Send headers
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                
                // Send response body
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } finally {
                exchange.close();
            }
        }
    }
}
```

### Virtual Threads and JDBC

Database operations are typically IO-bound and can benefit significantly from virtual threads. JDBC 4.3 and newer drivers are designed to work well with virtual threads.

**Key considerations:**
- **Connection pooling**: Still needed, but can use smaller pools
- **Transaction boundaries**: Keep transactions short
- **Statement execution**: Blocking operations that benefit from virtual threads
- **Driver compatibility**: Some drivers are better optimized for virtual threads

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VirtualThreadJdbcExample {
    // Note: This uses H2 in-memory database for demonstration
    // In a real application, you would use your actual database URL
    private static final String DB_URL = "jdbc:h2:mem:testdb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    public static void main(String[] args) throws Exception {
        // Initialize database
        initDatabase();
        
        // Run queries with virtual threads
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 100; i++) {
                final int userId = i % 10 + 1; // Users 1-10
                executor.submit(() -> queryUserData(userId));
            }
            
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
    
    private static void initDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.createStatement().execute(
                "CREATE TABLE users (" +
                "id INT PRIMARY KEY, " +
                "name VARCHAR(100), " +
                "email VARCHAR(100))");
            
            // Insert test data
            for (int i = 1; i <= 10; i++) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
                    ps.setInt(1, i);
                    ps.setString(2, "User " + i);
                    ps.setString(3, "user" + i + "@example.com");
                    ps.executeUpdate();
                }
            }
        }
    }
    
    private static void queryUserData(int userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Simulate some delay as if connecting to a real database
            Thread.sleep(100);
            
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE id = ?")) {
                ps.setInt(1, userId);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        
                        System.out.println(Thread.currentThread() + " - User: " + 
                                          name + ", Email: " + email);
                    }
                }
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

In conclusion, virtual threads represent a significant advancement in Java's concurrency model, enabling highly concurrent applications without the complexity of asynchronous programming. They excel particularly in IO-bound scenarios like web servers, API clients, and database applications. By understanding their characteristics and best practices, developers can create simpler, more efficient concurrent applications that scale to handle thousands or even millions of concurrent operations.
