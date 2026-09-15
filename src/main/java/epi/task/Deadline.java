package epi.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

/** Represents a task that must be completed by a date and time. */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HHmm").parseDefaulting(ChronoField.ERA, 1).toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
    protected LocalDateTime by;

    /** Creates a deadline by parsing its date/time in the supported input format. */
    public Deadline(String description, String byString) throws DateTimeParseException {
        super(description);
        this.by = LocalDateTime.parse(byString, INPUT_FORMAT);
    }

    private Deadline(Deadline original) {
        super(original);
        this.by = original.by;
    }

    /** Returns an independent deadline snapshot; LocalDateTime itself is immutable. */
    @Override
    public Deadline copy() {
        return new Deadline(this);
    }

    /**
     * Returns the deadline's due date/time for chronological sorting.
     *
     * @return the due date/time
     */
    @Override
    public LocalDateTime getSortDate() {
        return by;
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
