package edu.touro.mcon364.concurrency.lesson1.homework;

import edu.touro.mcon364.concurrency.common.model.Priority;
import edu.touro.mcon364.concurrency.common.model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class TaskRegistryTest {

    @Test
    void addAndFindById_workForSingleThreadedUsage() {
        var registry = new TaskRegistry();
        var task = new Task(1, "Prepare lesson", Priority.HIGH);

        registry.add(task);

        assertEquals(1, registry.size());
        assertEquals(task, registry.findById(1).orElseThrow());
    }

    @Test
    void removeReturnsRemovedTask() {
        var registry = new TaskRegistry();
        var task = new Task(2, "Write exercises", Priority.MEDIUM);
        registry.add(task);

        var removed = registry.remove(2);

        assertTrue(removed.isPresent());
        assertEquals(task, removed.get());
        assertTrue(registry.findById(2).isEmpty());
    }

    @Test
    void snapshotIsDefensive() {
        var registry = new TaskRegistry();
        registry.add(new Task(3, "Grade work", Priority.LOW));

        var snapshot = registry.snapshot();

        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.put(4, new Task(4, "Should fail", Priority.CRITICAL)));
    }

    @Test
    void supportsConcurrentAddsWithoutLosingEntries() throws InterruptedException {
        var registry = new TaskRegistry();
        int threadCount = 12;
        int tasksPerThread = 250;
        var ready = new CountDownLatch(threadCount);
        var start = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int worker = i;
            Thread thread = new Thread(() -> {
                ready.countDown();
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    fail("thread interrupted");
                }
                for (int j = 1; j <= tasksPerThread; j++) {
                    int id = worker * 10_000 + j;
                    registry.add(new Task(id, "Task " + id, Priority.MEDIUM));
                }
            });
            threads.add(thread);
        }

        threads.forEach(Thread::start);
        ready.await();
        start.countDown();
        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(threadCount * tasksPerThread, registry.size());
    }
}
