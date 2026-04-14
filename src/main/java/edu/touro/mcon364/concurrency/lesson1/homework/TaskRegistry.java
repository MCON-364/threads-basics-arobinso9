package edu.touro.mcon364.concurrency.lesson1.homework;

import edu.touro.mcon364.concurrency.common.model.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Homework:
 * Implement a thread-safe registry of tasks keyed by id.
 * Basically just add "synchronize" keywords in each method and make sure the return type is not removed!
 *
 * Requirements:
 * - add(task): store or replace a task by id
 * - findById(id): return Optional
 * - remove(id): remove and return Optional of removed task
 * - size(): return current number of tasks
 * - snapshot(): return a defensive copy that callers cannot use to mutate internal state
 */
public class TaskRegistry {

    private final Map<Integer, Task> tasks = new HashMap<>();

    public synchronized void add(Task task) {
        // TODO: make thread-safe
        tasks.put(task.id(), task);
    }

    public synchronized Optional <Task> findById(int id) {
        // TODO: make thread-safe
        return Optional.ofNullable(tasks.get(id));
    }

    public synchronized Optional<Task> remove(int id) {
        // TODO: make thread-safe
        return Optional.ofNullable(tasks.remove(id));
    }

    public synchronized int size() {
        // TODO: make thread-safe
        return tasks.size();
    }

    public synchronized Map<Integer, Task> snapshot() {
        // TODO: return a defensive copy safely
        return Map.copyOf(tasks);
    }
}
