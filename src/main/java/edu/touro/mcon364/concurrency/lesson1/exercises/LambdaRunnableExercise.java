package edu.touro.mcon364.concurrency.lesson1.exercises;

import edu.touro.mcon364.concurrency.common.model.Priority;
import edu.touro.mcon364.concurrency.common.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 5: Creating threads with a Runnable passed as a lambda
 *
 * The lesson showed four equivalent ways to write a Runnable.
 * This exercise practises the lambda style — the most common modern form:
 *
 *   Runnable r = () -> { ... };
 *   Thread t = new Thread(r, "thread-name");
 *   t.start();
 *
 * Or inline:
 *   Thread t = new Thread(() -> { ... }, "thread-name");
 *
 * Your tasks:
 *
 * (A) launchLoggerThread(List<String> log, String message)
 *     Create a Runnable AS A LAMBDA that appends {@code message} to {@code log},
 *     wrap it in a Thread named "logger", start it, and join it before returning.
 *
 * (B) launchTwoCounterThreads(List<Task> tasks, List<String> threadNames)
 *     Launch exactly two threads using INLINE lambda syntax:
 *       - "counter-a" iterates through tasks and counts those with Priority.HIGH,
 *         storing the result in highCount.
 *       - "counter-b" iterates through tasks and counts those with Priority.LOW,
 *         storing the result in lowCount.
 *     Start both threads, then join both before returning.
 *
 * Do NOT use an anonymous class or a named class — lambdas only.
 */
public class LambdaRunnableExercise {

    // Written by (A)
    private String loggedMessage;

    // Written by (B)
    private int highCount;
    private int lowCount;

    /**
     * (A) Create a lambda Runnable that appends {@code message} to {@code log},
     * wrap it in a Thread named "logger", start it, and join it.
     */
    public void launchLoggerThread(List<String> log, String message) throws InterruptedException {
        // TODO: create a Runnable lambda, pass it to new Thread(..., "logger"),
        //       start the thread, join it, and store the message in loggedMessage.

        // we first define the lambda and store it in a variable
        Runnable r = () -> {
            log.add(message);
            this.loggedMessage = message;
        };

        Thread t = new Thread(r, "logger"); // we create the thread
        t.start();
        t.join();
    }

    /**
     * (B) Launch two threads with inline lambda syntax.
     * "counter-a" counts HIGH-priority tasks → stored in highCount.
     * "counter-b" counts LOW-priority tasks  → stored in lowCount.
     * Start both, then join both before returning.
     */
    public void launchTwoCounterThreads(List<Task> tasks) throws InterruptedException {
        // TODO: create two threads using inline lambda syntax, start both,
        //       join both, and store results in highCount and lowCount.

        // with streams:
        // Thread A: Using Stream to filter and count HIGH priority
        Thread t1 = new Thread(() -> {
            this.highCount = (int) tasks.stream()
                    .filter(task -> task.priority() == Priority.HIGH)
                    .count();
        }, "counter-a");

        // Thread B: Using Stream to filter and count LOW priority
        Thread t2 = new Thread(() -> {
            this.lowCount = (int) tasks.stream()
                    .filter(task -> task.priority() == Priority.LOW)
                    .count();
        }, "counter-b");

        // start both threads
        t1.start();
        t2.start();

        // we join both to ensure counts are finished before the method returns
        t1.join();
        t2.join();
    }
    /*
     with loop:
         // Thread A: Count HIGH priority
        Thread t1 = new Thread(() -> {
            int count = 0;
            for (Task task : tasks) {
                if (task.priority() == Priority.HIGH) {
                    count++;
                }
            }
            this.highCount = count;
        }, "counter-a");

        // Thread B: Count LOW priority
        Thread t2 = new Thread(() -> {
            int count = 0;
            for (Task task : tasks) {
                if (task.priority() == Priority.LOW) {
                    count++;
                }
            }
            this.lowCount = count;
        }, "counter-b");

     */

    public String getLoggedMessage() { return loggedMessage; }
    public int getHighCount()        { return highCount; }
    public int getLowCount()         { return lowCount; }
}

