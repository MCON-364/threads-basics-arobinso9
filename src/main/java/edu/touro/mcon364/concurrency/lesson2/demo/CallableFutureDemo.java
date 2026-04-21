package edu.touro.mcon364.concurrency.lesson2.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Demo: {@link Callable} and {@link Future}.
 *
 * Key ideas shown:
 *  - {@code Runnable} has {@code void run()} — no return value, no checked exceptions.
 *  - {@code Callable<T>} has {@code T call()} — returns a value and may throw.
 *  - {@code Future<T>} is a handle to a result that may not be ready yet.
 *  - {@code future.get()} blocks until the result is available.
 *  - Collecting all futures AFTER submission keeps work truly concurrent.
 *    Calling {@code get()} immediately after each {@code submit()} accidentally
 *    turns concurrent work back into sequential work.
 */
public class CallableFutureDemo {

    // ── 1. simple Callable + Future ──────────────────────────────────────────

    public static void simpleCallableDemo() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        // A Callable that returns a computed value
        Callable<Integer> sumTask = () -> {
            System.out.printf("[%s] computing sum…%n", Thread.currentThread().getName());
            Thread.sleep(300);   // simulate work
            return 21 + 21;
        };

        Future<Integer> future = pool.submit(sumTask);

        System.out.println("[main] task submitted — doing other work while it runs…");
        Thread.sleep(100);
        System.out.println("[main] calling future.get() — will block until result is ready");

        Integer answer = future.get();   // blocks here until the Callable finishes
        System.out.println("[main] result = " + answer);

        pool.shutdown();
    }

    // ── 2. collecting multiple futures AFTER submission ───────────────────────

    /**
     * Submits several tasks, collects all futures, THEN retrieves results.
     * This pattern keeps the tasks truly concurrent.
     */
    public static void concurrentFuturesDemo() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        List<Future<String>> futures = new ArrayList<>();

        // Submit ALL tasks first
        for (int i = 1; i <= 6; i++) {
            int id = i;
            Future<String> f = pool.submit(() -> {
                Thread.sleep(id * 50L);
                return "result-from-" + Thread.currentThread().getName() + "-task-" + id;
            });
            futures.add(f);
        }

        System.out.println("[main] all tasks submitted, now collecting results…");

        // Retrieve results AFTER all have been submitted
        for (Future<String> f : futures) {
            System.out.println("  got: " + f.get());
        }

        pool.shutdown();
    }

    // ── 3. isDone / cancel ────────────────────────────────────────────────────

    public static void futurePollDemo() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newSingleThreadExecutor();

        Future<String> slow = pool.submit(() -> {
            Thread.sleep(500);
            return "slow result";
        });

        System.out.println("[main] isDone immediately after submit: " + slow.isDone());
        Thread.sleep(600);
        System.out.println("[main] isDone after waiting 600ms: " + slow.isDone());
        System.out.println("[main] result: " + slow.get());

        pool.shutdown();
    }

    // ── 4. parallel sum — split work across Callable tasks ───────────────────

    /**
     * Splits a list of numbers into equal slices, computes each slice's sum
     * in a separate {@link Callable}, collects all {@link Future} handles first,
     * then retrieves and combines the partial results.
     *
     * This is the same pattern used in {@code ParallelSumCalculator}:
     *  1. Divide the data.
     *  2. Submit all slices — DO NOT call get() yet.
     *  3. Only after everything is submitted, collect results with get().
     */
    public static void parallelSumDemo() throws ExecutionException, InterruptedException {
        List<Integer> numbers = IntStream.rangeClosed(1, 12).boxed().toList();
        int workers = 4;
        int chunkSize = (numbers.size() + workers - 1) / workers;   // ceiling division

        ExecutorService pool = Executors.newFixedThreadPool(workers);
        List<Future<Long>> futures = new ArrayList<>();

        // ── Step 1: submit all slices — collect Future handles, don't call get() yet
        IntStream.iterate(0, start -> start < numbers.size(), start -> start + chunkSize)
                .forEach(start -> {
                    List<Integer> slice = numbers.subList(start, Math.min(start + chunkSize, numbers.size()));
                    System.out.printf("[main]   submitting slice %s to pool%n", slice);
                    futures.add(pool.submit(
                            () -> slice.stream().mapToLong(Integer::longValue).sum()
                    ));
                });

        System.out.println("[main] all slices submitted — now collecting partial sums…");

        // ── Step 2: retrieve each partial result and combine
        long total = 0;
        for (Future<Long> f : futures) {
            long partial = f.get();   // blocks only until that one slice is done
            System.out.printf("[main]   partial sum: %d%n", partial);
            total += partial;
        }

        long expected = numbers.stream().mapToLong(Integer::longValue).sum();
        System.out.printf("[main] total = %d  (expected %d, match = %b)%n",
                total, expected, total == expected);

        pool.shutdown();
    }

    // ── 5. parallel sum — same result using a parallel stream ─────────────────

    /**
     * Does exactly the same work as {@link #parallelSumDemo()} but lets the
     * JVM's common fork/join pool handle the splitting and merging automatically.
     *
     * Key differences from the Callable/Future approach:
     *  - No pool to create or shut down — {@code parallelStream()} uses the
     *    shared {@code ForkJoinPool.commonPool()} behind the scenes.
     *  - No manual slicing — the stream framework decides how to partition.
     *  - Much less code, but you give up control over the number of threads
     *    and cannot retrieve individual partial results.
     *
     * Rule of thumb: use a parallel stream when the work is a simple aggregate
     * over a collection; use Callable/Future when you need fine-grained control,
     * different pool sizes, or want to inspect individual results.
     */
    public static void parallelStreamSumDemo() {
        List<Integer> numbers = IntStream.rangeClosed(1, 12).boxed().toList();

        System.out.println("[main] summing with a sequential stream…");
        long sequential = numbers.stream()
                .mapToLong(Integer::longValue)
                .sum();
        System.out.printf("[main]   sequential result: %d%n", sequential);

        System.out.println("[main] summing with a parallel stream…");
        long parallel = numbers.parallelStream()
                .peek(n -> System.out.printf("[%s]   processing %d%n",
                        Thread.currentThread().getName(), n))
                .mapToLong(Integer::longValue)
                .sum();
        System.out.printf("[main]   parallel result:    %d  (match = %b)%n",
                parallel, parallel == sequential);
    }

    // ── main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        System.out.println("=== Simple Callable + Future ===");
        simpleCallableDemo();

        System.out.println("\n=== Concurrent futures (collect after submit) ===");
        concurrentFuturesDemo();

        System.out.println("\n=== Future poll (isDone) ===");
        futurePollDemo();

        System.out.println("\n=== Parallel sum (split → submit all → collect) ===");
        parallelSumDemo();

        System.out.println("\n=== Parallel sum using parallelStream() ===");
        parallelStreamSumDemo();
    }
}

