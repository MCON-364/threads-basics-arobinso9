package edu.touro.mcon364.concurrency.common.model;

import java.util.Objects;

public record Task(int id, String description, Priority priority) implements Comparable<Task> {

    public Task {
        if (id <= 0) throw new IllegalArgumentException("id must be positive");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must be non-blank");
        }
        Objects.requireNonNull(priority, "priority must not be null");
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(other.priority().ordinal(), this.priority.ordinal());
    }
}
