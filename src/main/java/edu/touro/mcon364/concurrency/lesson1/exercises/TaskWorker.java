package edu.touro.mcon364.concurrency.lesson1.exercises;

import edu.touro.mcon364.concurrency.common.model.Task;

import java.util.List;

/**
 * Exercise 2: Thread creation, naming, start(), and join()
 *
 * A TaskWorker is given a list of tasks and the name it should give
 * its background thread.  When run() is called it must:
 *
 *   1. Create a Thread that iterates over every task in the list and
 *      records how many tasks it processed.
 *   2. Give the thread the name supplied to the constructor.
 *   3. Start the thread.
 *   4. Wait (join) until the thread has fully finished before returning.
 *
 * After run() returns the caller must be able to read the result via
 * getProcessedCount() and the thread's name via getWorkerName().
 *
 * Hint: use new Thread(Runnable, String) and thread.join().
 */
public class TaskWorker {

    private final List<Task> tasks;
    private final String threadName;
    private int processedCount = 0;
    private String workerName;

    public TaskWorker(List<Task> tasks, String threadName) {
        this.tasks = tasks;
        this.threadName = threadName;
    }

    /**
     * Create, name, start, and join the worker thread.
     * Must block until the thread finishes.
     */
    public void run() throws InterruptedException {
        // TODO: create a thread with the given name that processes every task
        //       in the list (increment processedCount for each one),
        //       then start it and join it.

        Thread t = new Thread(() -> { // first we create the thread
            // get the name actually assigned to the thread
            // The getName() method is a built-in method of the java.lang.Thread class.
            // Thread.currentThread(): This static method returns the Thread object that is currently running that specific line of code.
            //.getName(): This instance method returns the String name assigned to that thread.
            this.workerName = Thread.currentThread().getName();

            for (Task task : this.tasks) {
                processedCount++;
            }
        }, threadName); // we use the string from the constructor

        t.start();
        t.join();

    }

    /** Returns the number of tasks processed by the worker thread. */
    public int getProcessedCount() {
        return processedCount;
    }

    /**
     * Returns the name of the thread that was created.
     * Capture it inside run() via Thread.currentThread().getName().
     */
    public String getWorkerName() {
        return workerName;
    }
}

