package edu.touro.mcon364.concurrency.lesson2.exercises;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BatchTaskExecutorTest {

    @Test
    void allTasksComplete() throws InterruptedException {
        var executor = new BatchTaskExecutor();
        List<String> tasks = List.of("a", "b", "c", "d", "e", "f", "g", "h");
        executor.processBatch(tasks);
        assertEquals(tasks.size(), executor.getCompletedCount(),
                "Every submitted task must complete");
    }

    @Test
    void distinctWorkerCountIsAtMostPoolSize() throws InterruptedException {
        var executor = new BatchTaskExecutor();
        List<String> tasks = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        executor.processBatch(tasks);
        assertTrue(executor.getDistinctWorkerCount() <= BatchTaskExecutor.POOL_SIZE,
                "Only " + BatchTaskExecutor.POOL_SIZE + " distinct threads should be used");
    }

    @Test
    void workerNamesListSizeMatchesTaskCount() throws InterruptedException {
        var executor = new BatchTaskExecutor();
        List<String> tasks = List.of("x", "y", "z");
        executor.processBatch(tasks);
        assertEquals(tasks.size(), executor.getWorkerNames().size());
    }

    @Test
    void emptyBatchCompletesWithZeroCount() throws InterruptedException {
        var executor = new BatchTaskExecutor();
        executor.processBatch(List.of());
        assertEquals(0, executor.getCompletedCount());
    }
}

