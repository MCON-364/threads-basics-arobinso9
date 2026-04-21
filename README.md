# Multithreading + Collections Lab

This cumulative Maven repository supports **all three multithreading modules** of lessons on Java multithreading with a strong emphasis on **shared collections, data-structure tradeoffs, and common concurrency patterns**.


### Lesson 1 — Threading Fundamentals + Shared Collections
Goals:
- create threads with `Thread` and `Runnable`
- observe race conditions in shared collections and counters
- fix problems using `synchronized`
- practice reasoning about what operations must be atomic

### Lesson 2 — Executors + Concurrent Collections
Goals:
- replace manual thread creation with `ExecutorService`
- use `ConcurrentHashMap`
- compare `ArrayList`, synchronized wrappers, and copy-on-write options
- discuss why `PriorityQueue` is useful but not inherently thread-safe

### Lesson 3 — Producer-Consumer + Priority Scheduling
Goals:
- implement a producer-consumer design
- use `BlockingQueue` / `PriorityBlockingQueue`
- process tasks according to priority
- design a clean shutdown for worker threads

## Lesson 1 contents in this starter repo

### Runnable demos
- `UnsafeTaskCounterDemo`
- `SafeTaskCounterDemo`
- `UnsafeTaskListDemo`
- `SafeTaskListDemo`

### Exercises
- `ThreadSafeTaskCounter` (exercise)
- `TaskWorker` (exercise)
- `PeriodicLogger` (exercise)
- `SharedTaskList`  (exercise)
- `LambdaRunnableExercise` (exercise)
- `TaskRegistry` (homework)

## Running demos

```bash
mvn -q -DskipTests compile
java -cp target/classes edu.touro.mcon364.concurrency.lesson1.demo.UnsafeTaskCounterDemo
java -cp target/classes edu.touro.mcon364.concurrency.lesson1.demo.SafeTaskCounterDemo
java -cp target/classes edu.touro.mcon364.concurrency.lesson1.demo.UnsafeTaskListDemo
java -cp target/classes edu.touro.mcon364.concurrency.lesson1.demo.SafeTaskListDemo
```

## Notes

- The “unsafe” demos are intentionally broken or fragile so you can observe concurrency bugs.
- The exercise/homework classes contain TODOs for students.

## Lesson 2 contents in this starter repo

### Demos
- `AtomicCounterDemo` — `AtomicInteger` vs plain `int`; `compareAndSet`
- `ReentrantLockDemo` — `lock/finally/unlock`; `tryLock()`; reentrant acquisition
- `SynchronizersDemo` — `CountDownLatch`, `Semaphore`, `CyclicBarrier`, `Phaser` side-by-side
- `ExecutorServiceDemo` — raw-thread style vs fixed thread pool; `shutdown`/`awaitTermination`
- `CallableFutureDemo` — `Callable<T>`, `Future<T>`, `get()`, `isDone()`, submit-all-then-get pattern

### Exercises
- `AtomicTaskCounter` (exercise) — refactor `synchronized` counter to `AtomicInteger`
- `WorkerStartupLatch` (exercise) — startup coordination with `CountDownLatch`
- `PrinterRoom` (exercise) — limit concurrent access with `Semaphore`
- `BatchTaskExecutor` (exercise) — submit tasks to a fixed `ExecutorService` thread pool
- `ParallelSumCalculator` (exercise) — parallel split-sum using `Callable` and `Future`
- `IndependentCounters` (exercise) — fine-grained `ReentrantLock` per counter
- `ExecutorTaskManager` (homework) — atomic IDs, `Callable`/`Future` submission, `ReentrantLock`-protected results list, `awaitAll`, clean `shutdown`

## Running demos

```bash
mvn -q -DskipTests compile
java -cp target/classes edu.touro.mcon364.concurrency.lesson2.demo.AtomicCounterDemo
java -cp target/classes edu.touro.mcon364.concurrency.lesson2.demo.ReentrantLockDemo
java -cp target/classes edu.touro.mcon364.concurrency.lesson2.demo.SynchronizersDemo
java -cp target/classes edu.touro.mcon364.concurrency.lesson2.demo.ExecutorServiceDemo
java -cp target/classes edu.touro.mcon364.concurrency.lesson2.demo.CallableFutureDemo
```

## Notes

- The demos illustrate key concepts of multithreading and concurrency control in Java.
- Exercises are designed to reinforce the concepts learned in the demos and lessons.

