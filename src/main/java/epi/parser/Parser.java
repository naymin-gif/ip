package epi.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import epi.exception.EpiException;
import epi.task.Deadline;
import epi.task.Event;
import epi.task.TaskList;
import epi.task.Todo;

/** Converts raw user commands into task data and validated indexes. */
public class Parser {
    private static final Pattern PARAMETER = Pattern.compile("(?i)(?<!\\S)/(by|from|to)(?=\\s|$)");

    /** Splits a command into its command word and remaining argument. */
    public String[] parseInput(String input) {
        return input.trim().split("\\s+", 2);
    }

    /** Converts a one-based user task number into a validated zero-based index. */
    public int parseTaskIndex(String argument, TaskList tasks) throws EpiException {
        argument = argument.trim();
        if (argument.isEmpty()) {
            throw new EpiException("You need to give me a task number, human.");
        }
        int taskNumber = Integer.parseInt(argument);
        if (tasks.size() == 0) {
            throw new EpiException("Cannot! You don't even have a single task");
        }
        if (taskNumber <= 0 || taskNumber > tasks.size()) {
            throw new EpiException("That task number doesn't exist in my memory!");
        }
        return taskNumber - 1;
    }

    /** Creates a todo from its user-provided description. */
    public Todo parseTodo(String argument) throws EpiException {
        if (argument.isBlank()) {
            throw new EpiException("The description of a todo cannot be empty");
        }
        return new Todo(argument.trim());
    }

    /** Creates a deadline from its description and `/by` date/time text. */
    public Deadline parseDeadline(String argument) throws EpiException {
        if (argument.isBlank()) {
            throw new EpiException("The description of a deadline cannot be empty!");
        }
        List<String> parts = splitTaskFields(argument, List.of("by"),
                "Invalid format! Use: deadline <task> /by <time>");
        return new Deadline(parts.get(0), normalizeDate(parts.get(1)));
    }

    /** Creates an event from its description, start, and end date/time text. */
    public Event parseEvent(String argument) throws EpiException {
        if (argument.isBlank()) {
            throw new EpiException("The description of a event cannot be empty!");
        }
        List<String> parts = splitTaskFields(argument, List.of("from", "to"),
                "Invalid format! Use: event <task> /from <start> /to <end>");
        return new Event(parts.get(0), normalizeDate(parts.get(1)), normalizeDate(parts.get(2)));
    }

    /** Rejects missing, repeated, misplaced, and inappropriate reserved parameters. */
    private List<String> splitTaskFields(String argument, List<String> expected, String usage) throws EpiException {
        List<String> fields = new ArrayList<>();
        Matcher matcher = PARAMETER.matcher(argument);
        int previousEnd = 0;
        int parameterIndex = 0;
        while (matcher.find()) {
            if (parameterIndex >= expected.size()
                    || !matcher.group(1).equalsIgnoreCase(expected.get(parameterIndex))) {
                throw new EpiException(usage);
            }
            fields.add(argument.substring(previousEnd, matcher.start()).trim());
            previousEnd = matcher.end();
            parameterIndex++;
        }
        fields.add(argument.substring(previousEnd).trim());
        if (parameterIndex != expected.size() || fields.stream().anyMatch(String::isBlank)) {
            throw new EpiException(usage);
        }
        return fields;
    }

    /** Normalizes spacing only within a date/time field, preserving task descriptions. */
    private String normalizeDate(String value) {
        return value.replaceAll("\\s+", " ");
    }
}
