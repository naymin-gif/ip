package epi.task;

/** Represents a task without an associated date or time. */
public class Todo extends Task {
    /** Creates an incomplete todo task. */
    public Todo(String description) {
        super(description);
    }

    /** Returns the serialized todo representation. */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }

    /** Returns the user-facing todo representation. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
