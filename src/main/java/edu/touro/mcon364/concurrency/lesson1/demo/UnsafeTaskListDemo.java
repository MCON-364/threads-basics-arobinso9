package edu.touro.mcon364.concurrency.lesson1.demo;

import edu.touro.mcon364.concurrency.common.model.Priority;
import edu.touro.mcon364.concurrency.common.model.Task;

import java.util.ArrayList;
import java.util.List;

public class UnsafeTaskListDemo {

    public static void main(String[] args) throws InterruptedException {
        List<Task> tasks = new ArrayList<>(); // intentionally unsafe shared collection
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            final int workerId = i;
            Thread t = new Thread(() -> {
                for (int j = 1; j <= 1_000; j++) {
                    tasks.add(new Task(workerId * 10_000 + j, "Task " + j, Priority.MEDIUM));
                    if (!tasks.isEmpty() && j % 9 == 0) {
                        tasks.remove(tasks.size() - 1); // may race with other threads
                    }
                }
            }, "unsafe-list-" + i);
            threads.add(t);
        }

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
        }

        System.out.println("Final list size (nondeterministic): " + tasks.size());
        System.out.println("This demo may sometimes throw an exception and may sometimes merely produce inconsistent results.");
    }
}
