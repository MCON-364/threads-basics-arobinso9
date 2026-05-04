package edu.touro.mcon364.concurrency.lesson1.exercises;

import edu.touro.mcon364.concurrency.common.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exercise 4: Fixing a shared ArrayList race condition
 *
 * Multiple threads add Tasks to the shared list concurrently.
 * The plain ArrayList below is NOT thread-safe: concurrent structural
 * modifications can corrupt its internal state and lose elements.
 *
 * Your task:
 *   Replace the bare ArrayList with a thread-safe alternative so that
 *   all tasks added by all threads are reliably present when every thread
 *   has finished.
 *
 * Constraint: use only what was taught in this lesson —
 *   Collections.synchronizedList(new ArrayList<>())
 *
 * Do NOT change the add() or size() method signatures.
 *
 * Hint: look at how SafeTaskListDemo.java in the demo package initializes
 *       its list.
 */
public class SharedTaskList {

    // TODO: replace this unsafe list with a thread-safe one
    // og : private final List<Task> tasks = new ArrayList<>();
    private final List<Task> tasks = Collections.synchronizedList(new ArrayList<>());

    /** Adds a task to the shared list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the current number of tasks in the list. */
    public int size() {
        return tasks.size();
    }
}

