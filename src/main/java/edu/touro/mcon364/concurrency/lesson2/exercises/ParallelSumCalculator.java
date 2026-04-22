package edu.touro.mcon364.concurrency.lesson2.exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Exercise 5 — Returning values from concurrent tasks with {@link Callable} and
 * {@link Future}.
 *
 * Scenario: split a list of integers across several workers, have each worker
 * compute the sum of its slice, then combine the partial results into a total.
 *
 * Your tasks:
 *
 * (A) Implement {@link #parallelSum(List, int)}.
 *     - Create a fixed thread pool with {@code workers} threads.
 *     - Divide {@code numbers} into {@code workers} roughly-equal slices.
 *     - Submit each slice as a {@code Callable<Long>} that returns the sum of
 *       that slice.
 *     - Collect all {@link Future<Long>} objects BEFORE calling {@code get()} on
 *       any of them (to keep the work concurrent).
 *     - After all futures are collected, call {@code get()} on each and add
 *       partial results to a running total.
 *     - Shut down the pool and return the total.
 *
 * Note: calling {@code get()} immediately after each {@code submit()} turns
 * concurrent work back into sequential work — avoid that pattern here.
 */
public class ParallelSumCalculator {

    /**
     * Computes the sum of {@code numbers} by splitting the work across
     * {@code workers} {@link Callable} tasks submitted to a thread pool.
     *
     * @param numbers list of values to sum
     * @param workers number of pool threads / partitions
     * @return the total sum
     */
    // Callable<Long> is ike Runnable, but it has a return statement.
    // Future<Long> is a receipt for a result that hasn't been finished yet.

    public long parallelSum(List<Integer> numbers, int workers)
            throws InterruptedException, ExecutionException {
        // we need this throws here bc othersie the f.get() below will throw an error bc nothing wld be handling the possible errors

        // TODO: create a thread pool with the right number of workers
        // First we create the pool- and initilaize it to have "workers" amt of threads
        ExecutorService executor = Executors.newFixedThreadPool(workers);
        // Every time you give a worker a task, they give you a Future (a ticket) that says:
        // I don't have the answer yet, but check this ticket later and I'll have it for you.
        List<Future<Long>> futures = new ArrayList<>();

        // TODO: divide numbers into roughly equal slices — one slice per worker
        //       Think: how do you calculate the slice size without losing
        //       the last few elements when the list doesn't divide evenly?

        int size = numbers.size();
        int chunkSize = (int) Math.ceil((double) size / workers);

        // TODO: submit each slice as a task that returns its partial sum.
        //       Collect the handles to the results — but do NOT ask for the
        //       answers yet, so that all slices run at the same time.

        for (int i = 0; i < workers; i++) {
            // start tells the worker where to begin in the list, and end tells them where to stop
            int start = i * chunkSize;
            // Math.min(..., size) makes sure the last slice doesn't go out of bounds- bc we did ceiling.
            // if the list ends at index 100, but the math accidentally tries to ask for index 105, the program crashes. This line forces the end to be the actual end of the list.
            int end = Math.min(start + chunkSize, size);

            if (start < end) {
                // we create a subList which is a view that we hand to each worker.
                // the sublist only shows each thead their specific section of the big list.
                List<Integer> slice = numbers.subList(start, end);

                // the lambda is a Callable and returns a value (sum). Unlike runnable which only does work.
                Future<Long> future = executor.submit(() -> {
                    long sum = 0;
                    // for each number in the slice, we add to the sum. so sum= sum of all numbers in the slice
                    for (int num : slice)
                        sum += num;
                    return sum; // This value is wrapped in the Future
                });

                // we take the threads future ticket and add it to a big list of all the futures
                futures.add(future);
            }
        }

        // TODO: now that all slices are running, collect each partial sum
        //       and add it to the total

        long total = 0;
        for (Future<Long> f : futures) {
            // .get() pauses until that specific worker is done
            // we loop thru all the futures- if the worker/thread for that future is done,
            // we take their sum. If they are still working, we sit here and wait until they finish
            // since we called submit on all workers first, they are all working at the same time while the main thread waits here.- by f.get()
            total += f.get();
        }

        // TODO: release pool resources before returning
        // we close the program- so the threads don't stay alive forever
        executor.shutdown();

        return total;
    }
}
