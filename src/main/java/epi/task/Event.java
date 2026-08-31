
package epi.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Represents a task with a start and end date/time. */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    /** Creates an event by parsing its start and end date/time values. */
    public Event(String description, String fromString, String toString) throws DateTimeParseException {
        super(description);
        this.from = LocalDateTime.parse(fromString, INPUT_FORMAT);
        this.to = LocalDateTime.parse(toString, INPUT_FORMAT);
    }

    /** Returns the user-facing event representation. */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(OUTPUT_FORMAT)
                + " to: " + to.format(OUTPUT_FORMAT) + ")";
    }

    /** Returns the serialized event representation. */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.format(INPUT_FORMAT) + " | " + to.format(INPUT_FORMAT);
    }
}
