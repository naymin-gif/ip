package epi.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Represents a task that must be completed by a date and time. */
public class Deadline extends Task {
    protected LocalDateTime by;
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    /** Creates a deadline by parsing its date/time in the supported input format. */
    public Deadline(String description, String byString) throws DateTimeParseException {
        super(description);
        this.by = LocalDateTime.parse(byString, INPUT_FORMAT);
    }

    /** Returns the serialized deadline representation. */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.format(INPUT_FORMAT);
    }

    /** Returns the user-facing deadline representation. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }
}
