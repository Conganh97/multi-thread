package com.example;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE_BOLD = "\u001B[1;37m";
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            // Display main menu
            displayMainMenu();
            
            System.out.print("\n" + YELLOW + "Enter your choice (0-9): " + RESET);
            int choice;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
                scanner.nextLine(); // Clear the scanner buffer
                continue;
            }

            switch (choice) {
                case 0:
                    System.out.println(YELLOW + "Exiting..." + RESET);
                    scanner.close();
                    return;
                case 1:
                    showThreadCreationMenu(scanner);
                    break;
                case 2:
                    showPerformanceOptimizationMenu(scanner);
                    break;
                case 3:
                    showDataSharingMenu(scanner);
                    break;
                case 4:
                    showConcurrencyChallengesMenu(scanner);
                    break;
                case 5:
                    showAdvancedLockingMenu(scanner);
                    break;
                case 6:
                    showInterThreadCommunicationMenu(scanner);
                    break;
                case 7:
                    showLockFreeAlgorithmsMenu(scanner);
                    break;
                case 8:
                    showHighPerformanceIOMenu(scanner);
                    break;
                case 9:
                    showVirtualThreadMenu(scanner);
                    break;
                default:
                    System.out.println(RED + "Invalid choice. Please try again." + RESET);
            }
        }
    }
    
    /**
     * Prints a formatted menu title
     */
    private static void printMenuTitle(String title) {
        System.out.println(WHITE_BOLD + "\n" + title + RESET);
        String separator = "=".repeat(title.length());
        System.out.println(BLUE + separator + RESET);
    }
    
    /**
     * Prints a formatted menu option
     */
    private static void printMenuItem(int number, String description) {
        System.out.println(GREEN + number + ". " + RESET + CYAN + description + RESET);
    }
    
    /**
     * Displays the main menu with color formatting
     */
    private static void displayMainMenu() {
        printMenuTitle("Java Multi-Threading Examples");
        printMenuItem(1, "Thread Creation and Coordination");
        printMenuItem(2, "Performance Optimization");
        printMenuItem(3, "Data Sharing");
        printMenuItem(4, "Concurrency Challenges and Solutions");
        printMenuItem(5, "Advanced Locking");
        printMenuItem(6, "Inter-Thread Communication");
        printMenuItem(7, "Lock-Free Algorithms");
        printMenuItem(8, "Thread Model for High-Performance IO");
        printMenuItem(9, "Virtual Thread for High-Performance IO");
        printMenuItem(0, "Exit");
    }

    private static void showThreadCreationMenu(Scanner scanner) throws InterruptedException {
        printMenuTitle("Thread Creation and Coordination Examples");
        printMenuItem(1, "Thread Creation - Extending Thread");
        printMenuItem(2, "Thread Creation - Implementing Runnable");
        printMenuItem(3, "Thread Joining");
        printMenuItem(4, "Thread Sleep and Interruption");
        printMenuItem(5, "Thread State Monitoring");
        printMenuItem(6, "Thread Priority");
        printMenuItem(7, "Daemon Threads");
        printMenuItem(0, "Back to Main Menu");

        System.out.print("\n" + YELLOW + "Enter your choice: " + RESET);
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(RED + "Invalid input. Returning to main menu." + RESET);
            scanner.nextLine(); // Clear the scanner buffer
            return;
        }

        switch (choice) {
            case 0:
                return;
            case 1:
                com.example.threadcreation.ExtendingThreadExample.main(null);
                break;
            case 2:
                com.example.threadcreation.RunnableInterfaceExample.main(null);
                break;
            case 3:
                com.example.threadcreation.ThreadJoiningExample.main(null);
                break;
            case 4:
                com.example.threadcreation.ThreadSleepExample.main(null);
                break;
            case 5:
                com.example.threadcreation.ThreadStateExample.main(null);
                break;
            case 6:
                com.example.threadcreation.ThreadPriorityExample.main(null);
                break;
            case 7:
                com.example.threadcreation.DaemonThreadExample.main(null);
                break;
            default:
                System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showPerformanceOptimizationMenu(Scanner scanner) throws InterruptedException {
        printMenuTitle("Performance Optimization Examples");
        printMenuItem(1, "Thread Creation Overhead");
        printMenuItem(2, "Fixed Thread Pool");
        printMenuItem(3, "Cached Thread Pool");
        printMenuItem(4, "Single Thread Executor");
        printMenuItem(5, "Scheduled Thread Pool");
        printMenuItem(6, "Fork/Join Framework");
        printMenuItem(7, "Parallel Streams");
        printMenuItem(8, "Thread Pool Sizing");
        printMenuItem(0, "Back to Main Menu");

        System.out.print("\n" + YELLOW + "Enter your choice: " + RESET);
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(RED + "Invalid input. Returning to main menu." + RESET);
            scanner.nextLine(); // Clear the scanner buffer
            return;
        }

        switch (choice) {
            case 0:
                return;
            case 1:
                com.example.performance.ThreadCreationOverheadExample.main(null);
                break;
            case 2:
                com.example.performance.ThreadPoolExample.main(null);
                break;
            case 3:
                com.example.performance.CachedThreadPoolExample.main(null);
                break;
            case 4:
                com.example.performance.SingleThreadExecutorExample.main(null);
                break;
            case 5:
                com.example.performance.ScheduledThreadPoolExample.main(null);
                break;
            case 6:
                com.example.performance.ForkJoinExample.main(null);
                break;
            case 7:
                com.example.performance.ParallelStreamsExample.main(null);
                break;
            case 8:
                com.example.performance.ThreadPoolSizingExample.main(null);
                break;
            default:
                System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showDataSharingMenu(Scanner scanner) throws InterruptedException {
        printMenuTitle("Data Sharing Examples");
        printMenuItem(1, "Memory Visibility Problem");
        printMenuItem(2, "Shared Memory");
        printMenuItem(3, "Synchronized Methods");
        printMenuItem(4, "Volatile Variables");
        printMenuItem(5, "Thread-Local Storage");
        printMenuItem(6, "Immutable Objects");
        printMenuItem(7, "Thread Confinement");
        printMenuItem(0, "Back to Main Menu");

        System.out.print("\n" + YELLOW + "Enter your choice: " + RESET);
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(RED + "Invalid input. Returning to main menu." + RESET);
            scanner.nextLine(); // Clear the scanner buffer
            return;
        }

        switch (choice) {
            case 0:
                return;
            case 1:
                com.example.datasharing.VisibilityProblemExample.main(null);
                break;
            case 2:
                com.example.datasharing.SharedMemoryExample.main(null);
                break;
            case 3:
                com.example.datasharing.SynchronizedExample.main(null);
                break;
            case 4:
                com.example.datasharing.VolatileExample.main(null);
                break;
            case 5:
                com.example.datasharing.ThreadLocalExample.main(null);
                break;
            case 6:
                com.example.datasharing.ImmutablePersonExample.main(null);
                break;
            case 7:
                com.example.datasharing.ThreadConfinementExample.main(null);
                break;
            default:
                System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showConcurrencyChallengesMenu(Scanner scanner) throws InterruptedException {
        printMenuTitle("Concurrency Challenges Examples");
        printMenuItem(1, "Race Condition");
        printMenuItem(2, "Deadlock");
        printMenuItem(3, "Deadlock Prevention");
        printMenuItem(4, "Livelock");
        printMenuItem(5, "Starvation");
        printMenuItem(6, "Starvation Prevention");
        printMenuItem(7, "Memory Consistency Error");
        printMenuItem(8, "Thread-Safe Design Patterns");
        printMenuItem(0, "Back to Main Menu");

        System.out.print("\n" + YELLOW + "Enter your choice: " + RESET);
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(RED + "Invalid input. Returning to main menu." + RESET);
            scanner.nextLine(); // Clear the scanner buffer
            return;
        }

        switch (choice) {
            case 0:
                return;
            case 1:
                com.example.challenges.RaceConditionExample.main(null);
                break;
            case 2:
                com.example.challenges.DeadlockExample.main(null);
                break;
            case 3:
                com.example.challenges.DeadlockPreventionExample.main(null);
                break;
            case 4:
                com.example.challenges.LivelockExample.main(null);
                break;
            case 5:
                com.example.challenges.StarvationExample.main(null);
                break;
            case 6:
                com.example.challenges.StarvationPreventionExample.main(null);
                break;
            case 7:
                com.example.challenges.MemoryConsistencyExample.main(null);
                break;
            case 8:
                com.example.challenges.ThreadSafePatternExample.main(null);
                break;
            default:
                System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showAdvancedLockingMenu(Scanner scanner) throws InterruptedException {
        printMenuTitle("Advanced Locking Examples");
        printMenuItem(1, "ReentrantLock Basic");
        printMenuItem(2, "ReentrantLock with Timeout");
        printMenuItem(3, "ReadWriteLock");
        printMenuItem(4, "StampedLock");
        printMenuItem(0, "Back to Main Menu");

        System.out.print("\n" + YELLOW + "Enter your choice: " + RESET);
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(RED + "Invalid input. Returning to main menu." + RESET);
            scanner.nextLine(); // Clear the scanner buffer
            return;
        }

        switch (choice) {
            case 0:
                return;
            case 1:
                com.example.locking.ReentrantLockExample.main(null);
                break;
            case 2:
                com.example.locking.ReentrantLockTimeoutExample.main(null);
                break;
            case 3:
                com.example.locking.ReadWriteLockExample.main(null);
                break;
            case 4:
                com.example.locking.StampedLockExample.main(null);
                break;
            default:
                System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showInterThreadCommunicationMenu(Scanner scanner) throws InterruptedException {
        printMenuTitle("Inter-Thread Communication Examples");
        printMenuItem(1, "Wait and Notify Mechanism");
        printMenuItem(2, "Condition Variables");
        printMenuItem(3, "Blocking Queues");
        printMenuItem(4, "CountDownLatch");
        printMenuItem(5, "CyclicBarrier");
        printMenuItem(0, "Back to Main Menu");

        System.out.print("\n" + YELLOW + "Enter your choice: " + RESET);
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(RED + "Invalid input. Returning to main menu." + RESET);
            scanner.nextLine(); // Clear the scanner buffer
            return;
        }

        switch (choice) {
            case 0:
                return;
            case 1:
                com.example.communication.WaitNotifyExample.main(null);
                break;
            case 2:
                com.example.communication.ConditionExample.main(null);
                break;
            case 3:
                com.example.communication.BlockingQueueExample.main(null);
                break;
            case 4:
                com.example.communication.CountDownLatchExample.main(null);
                break;
            case 5:
                com.example.communication.CyclicBarrierExample.main(null);
                break;
            default:
                System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showLockFreeAlgorithmsMenu(Scanner scanner) throws InterruptedException {
        printMenuTitle("Lock-Free Algorithms Examples");
        printMenuItem(1, "Atomic Variables");
        printMenuItem(2, "Compare-And-Swap (CAS) Operations");
        printMenuItem(3, "ABA Problem and Solutions");
        printMenuItem(4, "Lock-Free Data Structures");
        printMenuItem(0, "Back to Main Menu");

        System.out.print("\n" + YELLOW + "Enter your choice: " + RESET);
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(RED + "Invalid input. Returning to main menu." + RESET);
            scanner.nextLine(); // Clear the scanner buffer
            return;
        }

        switch (choice) {
            case 0:
                return;
            case 1:
                com.example.lockfree.AtomicVariablesExample.main(null);
                break;
            case 2:
                com.example.lockfree.CASExample.main(null);
                break;
            case 3:
                com.example.lockfree.ABAProblemExample.main(null);
                break;
            case 4:
                com.example.lockfree.LockFreeDataStructuresExample.main(null);
                break;
            default:
                System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showHighPerformanceIOMenu(Scanner scanner) throws IOException, InterruptedException {
        printMenuTitle("High-Performance IO Examples");
        printMenuItem(1, "Blocking vs Non-Blocking IO");
        printMenuItem(2, "Thread-Per-Connection Model");
        printMenuItem(3, "Thread Pool Model");
        printMenuItem(4, "Reactor Pattern");
        printMenuItem(5, "CompletableFuture for Async IO");
        printMenuItem(0, "Back to Main Menu");

        System.out.print("\n" + YELLOW + "Enter your choice: " + RESET);
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(RED + "Invalid input. Returning to main menu." + RESET);
            scanner.nextLine(); // Clear the scanner buffer
            return;
        }

        switch (choice) {
            case 0:
                return;
            case 1:
                com.example.io.BlockingVsNonBlockingExample.main(null);
                break;
            case 2:
                com.example.io.ThreadPerConnectionExample.main(null);
                break;
            case 3:
                com.example.io.ThreadPoolServerExample.main(null);
                break;
            case 4:
                com.example.io.ReactorPatternExample.main(null);
                break;
            case 5:
                com.example.io.CompletableFutureIOExample.main(null);
                break;
            default:
                System.out.println(RED + "Invalid choice." + RESET);
        }
    }

    private static void showVirtualThreadMenu(Scanner scanner) throws Exception {
        printMenuTitle("Virtual Thread Examples");
        printMenuItem(1, "Virtual Thread Basics");
        printMenuItem(2, "Structured Concurrency");
        printMenuItem(3, "Virtual Thread Best Practices");
        printMenuItem(4, "Virtual Thread Performance");
        printMenuItem(5, "Virtual Thread HTTP Server");
        printMenuItem(0, "Back to Main Menu");

        System.out.print("\n" + YELLOW + "Enter your choice: " + RESET);
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(RED + "Invalid input. Returning to main menu." + RESET);
            scanner.nextLine(); // Clear the scanner buffer
            return;
        }

        switch (choice) {
            case 0:
                return;
            case 1:
                com.example.virtualthread.VirtualThreadExample.main(null);
                break;
            case 2:
                com.example.virtualthread.StructuredConcurrencyExample.main(null);
                break;
            case 3:
                com.example.virtualthread.VirtualThreadBestPracticesExample.main(null);
                break;
            case 4:
                com.example.virtualthread.VirtualThreadPerformanceExample.main(null);
                break;
            case 5:
                com.example.virtualthread.VirtualThreadHttpExample.main(null);
                break;
            default:
                System.out.println(RED + "Invalid choice." + RESET);
        }
    }
}