package epi;

import java.util.List;

/**
 * Carries complete response text and an explicit error flag shared by user interfaces.
 *
 * @param lines Response lines in display order.
 * @param error Whether the command failed without keeping task changes.
 */
public record CommandResult(List<String> lines, boolean error) {
    /** Keeps the response immutable after it is returned to a user interface. */
    public CommandResult {
        lines = List.copyOf(lines);
    }
}
