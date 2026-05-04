package edu.touro.mcon364.concurrency.lesson2.homework;

import edu.touro.mcon364.concurrency.common.model.Priority;
import edu.touro.mcon364.concurrency.common.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorTaskManagerTest {

    private final ExecutorTaskManager manager = new ExecutorTaskManager();

    @AfterEach
    void tearDown() throws InterruptedException {
        manager.shutdown();
    }

    // ── ID generation ────────────────────────────────────────────────────────

    @Test
    void nextIdStartsAtOne() {
        assertEquals(1, manager.nextId());
    }

    @Test
    void nextIdIsMonotonicallyIncreasing() {
        int first  = manager.nextId();
        int second = manager.nextId();
        int third  = manager.nextId();
        assertTrue(first < second && second < third,
                "IDs must increase with each call");
    }

    @Test
    void nextIdIsUniqueUnderConcurrency() throws InterruptedException {
        java.util.Set<Integer> ids = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < 100; j++) ids.add(manager.nextId());
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        assertEquals(2000, ids.size(), "Every generated ID must be unique");
    }

    // ── submit / awaitAll ────────────────────────────────────────────────────

    @Test
    void submitReturnsNonNullFuture() {
        Future<Task> f = manager.submit("write report", Priority.HIGH);
        assertNotNull(f);
    }

    @Test
    void submittedTaskHasCorrectDescriptionAndPriority() throws Exception {
        Future<Task> f = manager.submit("send email", Priority.LOW);
        Task task = f.get();
        assertEquals("send email", task.description());
        assertEquals(Priority.LOW, task.priority());
    }

    @Test
    void submittedTaskIdIsPositive() throws Exception {
        Task task = manager.submit("backup db", Priority.MEDIUM).get();
        assertTrue(task.id() > 0);
    }

    @Test
    void awaitAllReturnsAllResults() throws Exception {
        List<Future<Task>> futures = new ArrayList<>();
        futures.add(manager.submit("task-1", Priority.HIGH));
        futures.add(manager.submit("task-2", Priority.MEDIUM));
        futures.add(manager.submit("task-3", Priority.LOW));

        List<Task> results = manager.awaitAll(futures);
        assertEquals(3, results.size());
    }

    @Test
    void allTasksRecordedAsCompleted() throws Exception {
        List<Future<Task>> futures = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            futures.add(manager.submit("task-" + i, Priority.MEDIUM));
        }
        manager.awaitAll(futures);

        // Give the pool a moment to record completions
        Thread.sleep(100);
        assertEquals(8, manager.getCompletedTasks().size(),
                "All 8 tasks must be recorded in completedTasks");
    }

    @Test
    void lastIssuedIdMatchesSubmitCount() {
        manager.submit("a", Priority.HIGH);
        manager.submit("b", Priority.HIGH);
        manager.submit("c", Priority.HIGH);
        assertEquals(3, manager.getLastIssuedId());
    }

    // ── concurrent submissions ───────────────────────────────────────────────

    @Test
    void concurrentSubmissionsAllComplete() throws Exception {
        int count = 20;
        // Use a thread-safe list: multiple threads add futures concurrently
        java.util.concurrent.CopyOnWriteArrayList<Future<Task>> futures =
                new java.util.concurrent.CopyOnWriteArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int id = i;
            threads.add(new Thread(() ->
                    futures.add(manager.submit("concurrent-" + id, Priority.MEDIUM))));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        List<Task> results = manager.awaitAll(futures);
        assertEquals(count, results.size(),
                "All concurrently submitted tasks must complete");
    }
}

