package epi.task;

import java.time.LocalDateTime;

/** Represents a generic task with a description and completion state. */
public class Task {
    protected String description;
    protected boolean isDone;
    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        this.isDone = false;
    }

    /** Returns the status marker used when displaying this task. */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns the date/time used to order this task chronologically.
     *
     * @return the relevant date/time, or null for an undated task
     */
    public LocalDateTime getSortDate() {
        return null;
    }

    /** Returns the serialized representation used by persistent storage. */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /** Returns the user-facing representation of this task. */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + description;
    }
}
