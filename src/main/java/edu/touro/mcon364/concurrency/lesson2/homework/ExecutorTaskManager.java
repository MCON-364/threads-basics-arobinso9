package edu.touro.mcon364.concurrency.lesson2.homework;

import edu.touro.mcon364.concurrency.common.model.Priority;
import edu.touro.mcon364.concurrency.common.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Homework — Executor-backed task manager with atomic IDs.
 *
 * Extend the task-manager from Lesson 1 so that tasks are executed through a
 * thread pool, IDs are generated atomically, and results are returned via
 * {@link Future}.
 *
 * Requirements (read each TODO carefully):
 *
 * 1. ID generation
 *    - {@link #nextId()} must use an {@link AtomicInteger} to generate IDs.
 *    - IDs start at 1 and increase monotonically, even under concurrent calls.
 *
 * 2. Submitting work
 *    - {@link #submit(String, Priority)} must:
 *        a. Call {@code nextId()} to obtain a unique ID.
 *        b. Build a {@link Task} record with that ID, the given description, and priority.
 *        c. Submit a {@link Callable} to the pool that "processes" the task
 *           (for now, just sleep 10 ms and return the task).
 *        d. Return the resulting {@link Future<Task>}.
 *
 * 3. Collecting results
 *    - {@link #awaitAll(List)} must call {@code get()} on every future in order
 *      and return the list of completed {@link Task} objects.
 *    - Wrap checked exceptions in {@link RuntimeException}.
 *
 * 4. Shutdown
 *    - {@link #shutdown()} must call {@code pool.shutdown()} followed by
 *      {@code pool.awaitTermination(30, TimeUnit.SECONDS)}.
 *
 * 5. Where a lock is needed
 *    - The {@code completedTasks} list is written by worker threads.
 *      Protect it with a {@link java.util.concurrent.locks.ReentrantLock}
 *      (or a thread-safe alternative) in {@link #recordCompleted(Task)}.
 *      Add a comment explaining WHY a lock is needed there.
 *
 * 6. Synchronizer choice (comment required)
 *    - In the Javadoc comment just below "SYNCHRONIZER CHOICE", explain in
 *      1–3 sentences which synchronizer from the lesson you would use if you
 *      needed to wait for a batch of tasks to finish before starting the next
 *      batch, and why.
 */
public class ExecutorTaskManager {

    /* ── SYNCHRONIZER CHOICE ────────────────────────────────────────────────
     * TODO: In 1–3 sentences, explain which synchronizer you would add to
     *       wait for a complete batch before the next batch starts, and why.
     * I would use a CountDownLatch and initialize it with the batch size-
     * bc every time a task finishes, it calls countDown(),
     * and the main thread calls await(), ensuring no new batch starts until the current one reaches zero.
     * ──────────────────────────────────────────────────────────────────────*/

    private static final int POOL_SIZE = 4;

    // TODO: declare the thread pool — what factory method gives you a fixed-size pool?
    private final ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

    // TODO: declare the ID counter— what type guarantees uniqueness without synchronized?
    // an AtomicInt uses CAS- compare and swap. It looks at a value- ex:5, then calculates the new value- ex:6
    // and then to make the swap it asks the CPU, is the value still 5? If yes, change it to 6.
    // If it changed while I was busy, throw my work away and let me try again.
    // so its an atomic op now:)
    // we set the initial value to 0 so the id counter will start at 1. we are using pre- increment here = incrementAndGet
    // which means we add 1 to the current value first, and then get that result. So we will b starting off with 1.
    private final AtomicInteger idCounter = new AtomicInteger(0);

    // List of tasks that have finished — written by worker threads, so needs protection
    private final List<Task> completedTasks = new ArrayList<>();

    // TODO: declare the lock that will protect completedTasks
    private final Lock listLock = new ReentrantLock();

    // ── ID generation ────────────────────────────────────────────────────────

    /**
     * Returns a unique, auto-incremented task ID.
     * TODO: generate the next ID atomically — no synchronized keyword allowed
     */
    public int nextId() {
        // TODO: implement
        return idCounter.incrementAndGet(); // returns 1 on first call- as we said b4
    }

    // ── task submission ──────────────────────────────────────────────────────

    /**
     * Creates a {@link Task} and submits it to the thread pool for execution.
     *
     * @param description task description (must be non-blank)
     * @param priority    task priority
     * @return a {@link Future<Task>} that will hold the completed task
     */
    public Future<Task> submit(String description, Priority priority) {
        // TODO: obtain a unique ID for this task
        int id = nextId();
        // TODO: build the Task record
        Task task = new Task(id, description, priority);

        // TODO: hand the task to the pool as a Callable that processes it and
        //       returns it when done — return the Future the pool gives you back
        // We return the Future the pool gives us
        // when we call submit, we are handing the code inside the { } to the thread pool.
        // the outer return- returns the future. even tho the task hasn't actually slept or saved yet,
        // the method finishes instantly and gives the caller the Future.
        return pool.submit(() -> {
            Thread.sleep(10); // we simulate processing
            recordCompleted(task); // then we save it to the CompletedTasks list
            return task; // task is inside the future. when we call .get()- we retrieve this task from the future
        });
    }

    // ── recording completion ─────────────────────────────────────────────────

    /**
     * Records a finished task.
     *
     * This method is called from worker threads concurrently.
     * TODO: protect the list so that two threads cannot corrupt it at the same time.
     *       Add a comment explaining exactly why a lock is necessary here.
     * An ArrayList is not thread-safe. If two worker threads try to add() a task at the exact same time,
     * they might try to write to the same spot in the internal array, causing a crash or losing data.
     */

    private void recordCompleted(Task task) {
        // TODO: implement
        listLock.lock();
        try {
            completedTasks.add(task);
        } finally {
            listLock.unlock();
        }
    }

    // ── collecting results ───────────────────────────────────────────────────

    /**
     * Waits for every future in {@code futures} to complete and returns the
     * resulting {@link Task} objects in submission order.
     *
     * TODO: retrieve each result in order and collect them into a list.
     *       What should happen if a task threw an exception or was interrupted?
     */
    public List<Task> awaitAll(List<Future<Task>> futures) {
        // TODO: implement
        // .get() pauses until that specific worker is done
        // we loop thru all the futures- if the worker/thread for that future is done,
        // we add their task to results. If they are still working, we sit here and wait until they finish.
        // since we called submit on all workers first, they are all working at the same time while the main thread waits here.- by f.get()
        /**
         * The .get() method on a Future is risky code. Java knows that two things could go wrong while we r waiting for a worker thread:
         * InterruptedException: Someone tried to kill the thread while it was waiting.
         * ExecutionException: The code inside the worker thread crashed (e.g., a NullPointerException).
         * Because these are Checked Exceptions, Java will not let the code compile unless we either try-catch them or add throws to the method signature
         * like we have in ParallelSumCalculator.java.
         */

        List<Task> results = new ArrayList<>();
        try {
            for (Future<Task> f : futures) {
                results.add(f.get()); // Blocks until task is done
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Task execution failed", e);
        }
        return results;
    }

    // ── lifecycle ────────────────────────────────────────────────────────────

    /**
     * Shuts down the pool and waits up to 30 seconds for all tasks to finish.
     *
     * TODO: signal the pool to stop accepting new work, then block until all
     *       in-flight tasks have completed or the timeout expires
     */
    public void shutdown() throws InterruptedException {
        // TODO: implement
        pool.shutdown();
        // the main thread will wait up to 30 seconds for the worker threads to complete b4 shutting down the program
        pool.awaitTermination(30, TimeUnit.SECONDS);
    }

    // ── observability ────────────────────────────────────────────────────────

    /** Returns a snapshot of the tasks that have completed so far. */
    public List<Task> getCompletedTasks() {
        // TODO: protect the read with the same lock used in recordCompleted,
        //       then return a defensive copy so callers cannot mutate internal state
        listLock.lock();
        try{
            return new ArrayList<>(completedTasks);
        }finally {
            listLock.unlock();
        }
    }

    /** Returns the most recently generated ID (useful for assertions). */
    public int getLastIssuedId() {
        // TODO: read the current value from the ID counter
        return idCounter.get();
    }
}
