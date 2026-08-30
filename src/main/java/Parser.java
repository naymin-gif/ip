public class Parser {
    public String[] parseInput(String input) {
        return input.trim().split("\\s+", 2);
    }

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

    public Todo parseTodo(String argument) throws EpiException {
        if (argument.isEmpty()) {
            throw new EpiException("The description of a todo cannot be empty");
        }
        return new Todo(argument.trim());
    }

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

    public Event parseEvent(String argument) throws EpiException {
        if (argument.isEmpty()) {
            throw new EpiException("The description of a event cannot be empty!");
        }
        String[] eventParts = argument.split(" /from ", 2);
        if (eventParts.length < 2 || eventParts[0].trim().isEmpty()) {
            throw new EpiException("Invalid format! Missing description or '/from'. Use: event <task> /from <start> /to <end>");
        }
        String[] timeParts = eventParts[1].split(" /to ", 2);
        if (timeParts.length < 2 || timeParts[0].trim().isEmpty() || timeParts[1].trim().isEmpty()) {
            throw new EpiException("Invalid format! Missing times. Use: event <task> /from <start> /to <end>");
        }
        return new Event(eventParts[0].trim(), timeParts[0].trim(), timeParts[1].trim());
    }
}
