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

### Student work
- `ThreadSafeTaskCounter` (exercise)
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
