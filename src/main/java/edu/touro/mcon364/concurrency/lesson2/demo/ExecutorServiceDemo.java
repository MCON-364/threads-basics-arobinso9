package edu.touro.mcon364.concurrency.lesson2.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demo: ExecutorService and fixed thread pools.
 *
 * Key ideas shown:
 *  - Raw-thread style (create one Thread per task) vs executor style.
 *  - {@link Executors#newFixedThreadPool(int)}: a stable set of reusable worker threads.
 *  - Submitting both {@code Runnable} tasks (fire-and-forget) and checking completion
 *    via {@code shutdown()} + {@code awaitTermination()}.
 *  - The classroom line: threads are workers, executors are managers, tasks are the jobs.
 */
public class ExecutorServiceDemo {

    // ── 1. raw-thread style (NOT recommended) ────────────────────────────────

    public static void rawThreadStyle(List<String> taskNames) throws InterruptedException {
        System.out.println("[raw] creating one Thread per task — not scalable!");
        List<Thread> threads = new ArrayList<>();
        for (String name : taskNames) {
            Thread t = new Thread(() ->
                    System.out.printf("[raw] %s processed by %s%n",
                            name, Thread.currentThread().getName()));
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) t.join();
        System.out.println("[raw] all tasks done");
    }

    // ── 2. executor style (recommended) ──────────────────────────────────────

    public static void executorStyle(List<String> taskNames) throws InterruptedException {
        System.out.println("[pool] using a fixed thread pool of 3 workers");
        // Create pool ONCE; reuse the same 3 threads for all tasks
        ExecutorService pool = Executors.newFixedThreadPool(3);

        for (String name : taskNames) {
            // submit() hands the Runnable off to the pool's internal queue
            pool.submit(() ->
                    System.out.printf("[pool] %s processed by %s%n",
                            name, Thread.currentThread().getName()));
        }

        // Tell the pool: stop accepting new work, then wait for in-flight tasks
        pool.shutdown();
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("[pool] all tasks done, finished cleanly: " + finished);
    }

    // ── main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws InterruptedException {
        List<String> tasks = List.of("task-A", "task-B", "task-C",
                "task-D", "task-E", "task-F", "task-G");

        System.out.println("=== Raw-thread style ===");
        rawThreadStyle(tasks);

        System.out.println("\n=== Executor / pool style ===");
        executorStyle(tasks);
    }
}

