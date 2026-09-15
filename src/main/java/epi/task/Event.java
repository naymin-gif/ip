
package epi.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

/** Represents a task with a start and end date/time. */
public class Event extends Task {
    private static final DateTimeFormatter INPUT_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HHmm").parseDefaulting(ChronoField.ERA, 1).toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
    protected LocalDateTime from;
    protected LocalDateTime to;

    /** Creates an event by parsing its start and end date/time values. */
    public Event(String description, String fromString, String toString) throws DateTimeParseException {
        super(description);
        this.from = LocalDateTime.parse(fromString, INPUT_FORMAT);
        this.to = LocalDateTime.parse(toString, INPUT_FORMAT);
        if (!this.to.isAfter(this.from)) {
            throw new IllegalArgumentException("Meow! An event must end after it starts.");
        }
    }

    private Event(Event original) {
        super(original);
        this.from = original.from;
        this.to = original.to;
    }

    /** Returns an independent event snapshot; its date/time values are immutable. */
    @Override
    public Event copy() {
        return new Event(this);
    }

    /**
     * Returns the event's start date/time for chronological sorting.
     *
     * @return the start date/time, not the end date/time
     */
    @Override
    public LocalDateTime getSortDate() {
        return from;
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
