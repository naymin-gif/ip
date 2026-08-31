package epi.parser;

import epi.exception.EpiException;
import epi.task.Deadline;
import epi.task.Event;
import epi.task.TaskList;
import epi.task.Todo;

/** Converts raw user commands into task data and validated indexes. */
public class Parser {
    /** Splits a command into its command word and remaining argument. */
    public String[] parseInput(String input) {
        return input.trim().split("\\s+", 2);
    }

    /** Converts a one-based user task number into a validated zero-based index. */
    public int parseTaskIndex(String argument, TaskList tasks) throws EpiException {
        if (argument.isEmpty()) {
            throw new EpiException("You need to give me a task number, human.");
        }
        int taskIdx = Integer.parseInt(argument) - 1;
        if (tasks.size() == 0) {
            throw new EpiException("Cannot! You don't even have a single task");
        }
        if (taskIdx < 0 || taskIdx >= tasks.size()) {
            throw new EpiException("That task number doesn't exist in my memory!");
        }
        return taskIdx;
    }

    /** Creates a todo from its user-provided description. */
    public Todo parseTodo(String argument) throws EpiException {
        if (argument.isEmpty()) {
            throw new EpiException("The description of a todo cannot be empty");
        }
        return new Todo(argument.trim());
    }

    /** Creates a deadline from its description and `/by` date/time text. */
    public Deadline parseDeadline(String argument) throws EpiException {
        if (argument.isEmpty()) {
            throw new EpiException("The description of a deadline cannot be empty!");
        }
        String[] parts = argument.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new EpiException("Invalid format! Use: deadline <task> /by <time>");
        }
        return new Deadline(parts[0].trim(), parts[1].trim());
    }

    /** Creates an event from its description, start, and end date/time text. */
    public Event parseEvent(String argument) throws EpiException {
        if (argument.isEmpty()) {
            throw new EpiException("The description of a event cannot be empty!");
        }
        String[] eventParts = argument.split(" /from ", 2);
        if (eventParts.length < 2 || eventParts[0].trim().isEmpty()) {
            throw new EpiException("Invalid format! Missing description or '/from'. "
                    + "Use: event <task> /from <start> /to <end>");
        }
        String[] timeParts = eventParts[1].split(" /to ", 2);
        if (timeParts.length < 2 || timeParts[0].trim().isEmpty() || timeParts[1].trim().isEmpty()) {
            throw new EpiException("Invalid format! Missing times. "
                    + "Use: event <task> /from <start> /to <end>");
        }
        return new Event(eventParts[0].trim(), timeParts[0].trim(), timeParts[1].trim());
    }
}
