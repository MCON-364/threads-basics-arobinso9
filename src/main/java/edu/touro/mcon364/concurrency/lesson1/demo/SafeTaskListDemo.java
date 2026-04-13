package edu.touro.mcon364.concurrency.lesson1.demo;

import edu.touro.mcon364.concurrency.common.model.Priority;
import edu.touro.mcon364.concurrency.common.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SafeTaskListDemo {

    public static void main(String[] args) throws InterruptedException {
        List<Task> tasks = Collections.synchronizedList(new ArrayList<>());
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            final int workerId = i;
            Thread t = new Thread(() -> {
                for (int j = 1; j <= 1_000; j++) {
                    tasks.add(new Task(workerId * 10_000 + j, "Task " + j, Priority.MEDIUM));
                    if (j % 9 == 0) {
                        synchronized (tasks) {
                            if (!tasks.isEmpty()) {
                                tasks.remove(tasks.size() - 1);
                            }
                        }
                    }
                }
            }, "safe-list-" + i);
            threads.add(t);
        }

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Final list size: " + tasks.size());
        System.out.println("No structural race should occur in this version.");
    }
}
