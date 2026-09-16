package epi;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import epi.exception.EpiException;
import epi.parser.Parser;
import epi.storage.Storage;
import epi.task.Task;
import epi.task.TaskList;
import epi.ui.ConsoleUi;

/** Coordinates Epi's user interface, command parsing, task list, and storage. */
public class Epi {
    private final ConsoleUi ui;
    private final Storage storage;
    private final Parser parser;
    private TaskList tasks;

    /** Creates an Epi instance using the supplied task-file path. */
    public Epi(String filePath) {
        this(filePath, true);
    }

    /** Creates an Epi instance, optionally displaying the command-line banner. */
    public Epi(String filePath, boolean showBanner) {
        ui = new ConsoleUi();
        storage = new Storage(filePath);
        parser = new Parser();
        tasks = storage.load();
        if (!showBanner) {
            return;
        }
        String banner = "  ______       _ \n"
                + " |  ____|     (_)\n"
                + " | |__   _ __  _ \n"
                + " |  __| | '_ \\| |\n"
                + " | |____| |_) | |\n"
                + " |______| .__/|_|\n"
                + "        | |      \n"
                + "        |_|      \n";
        ui.showLine(banner);
        ui.showLine("Meowdy! I'm Epi");
        ui.showLine("Are you ready to tackle some purr-fectly good tasks today?");
        getLoadingWarnings().forEach(ui::showLine);
    }

    /** Processes one command and returns the lines that should be shown to a user interface. */
    public List<String> processCommand(String input) {
        return processCommandResult(input).lines();
    }

    /** Returns startup warnings without hiding file errors in a GUI-only process's console. */
    public List<String> getLoadingWarnings() {
        return storage.getWarnings();
    }

    /**
     * Processes a command and converts failures into responses shared by the CLI and GUI.
     *
     * @param input The user's command.
     * @return Complete reply lines and an explicit error flag, independent of their wording.
     */
    public CommandResult processCommandResult(String input) {
        assert input != null : "A command must be provided to Epi";
        try {
            return new CommandResult(executeCommand(input), false);
        } catch (EpiException e) {
            return new CommandResult(List.of(e.getMessage()), true);
        } catch (NumberFormatException e) {
            return new CommandResult(List.of("That is not a valid number"), true);
        } catch (DateTimeException e) {
            return new CommandResult(List.of(
                    "Invalid date format! Please use: yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)"), true);
        } catch (IllegalArgumentException e) {
            return new CommandResult(List.of(e.getMessage()), true);
        }
    }

    /** Parses the command word and delegates to its handler without formatting replies here. */
    private List<String> executeCommand(String input) throws EpiException {
        String[] commandParts = parser.parseInput(input);
        assert commandParts.length > 0 : "The parser must return a command part";
        String command = commandParts[0].toLowerCase(Locale.ROOT);
        String argument = commandParts.length > 1 ? commandParts[1] : "";
        return switch (command) {
            case "bye" -> handleBye(argument);
            case "list" -> handleList(argument);
            case "sort" -> handleSort(argument);
            case "find" -> handleFind(argument);
            case "delete" -> handleDelete(argument);
            case "mark" -> handleStatusChange(argument, true);
            case "unmark" -> handleStatusChange(argument, false);
            case "todo" -> handleTodo(argument);
            case "deadline" -> handleDeadline(argument);
            case "event" -> handleEvent(argument);
            default -> throw new EpiException("I do not understand what that means, Human.");
        };
    }

    /** Validates a farewell command; the calling interface decides whether to exit. */
    private List<String> handleBye(String argument) throws EpiException {
        requireNoArgument("bye", argument);
        return List.of("Meow for now. See you later!");
    }

    /** Lists every task in its original order after validating the command. */
    private List<String> handleList(String argument) throws EpiException {
        requireNoArgument("list", argument);
        requireTasks();
        List<String> output = new ArrayList<>();
        output.add("Here is your pile of tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            output.add((i + 1) + ". " + tasks.get(i));
        }
        return output;
    }

    /** Displays tasks chronologically without changing the saved order or task numbers. */
    private List<String> handleSort(String argument) throws EpiException {
        if (!argument.trim().equalsIgnoreCase("date")) {
            throw new EpiException("Meow! Use: sort date");
        }
        requireTasks();
        return formatTaskView("Here is your pile of tasks, sorted by date (original task numbers):",
                tasks.getSortedByDate());
    }

    /** Searches descriptions and reports a normal informational reply when nothing matches. */
    private List<String> handleFind(String argument) throws EpiException {
        String keyword = argument.trim();
        if (keyword.isEmpty()) {
            throw new EpiException("Please provide a keyword to search for.");
        }
        List<Task> matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            return List.of("Meow! I couldn't find any tasks matching \"" + keyword + "\".");
        }
        return formatTaskView("Here are the matching tasks in your list:", matches);
    }

    /** Formats a filtered or sorted view using each task's original full-list number. */
    private List<String> formatTaskView(String heading, List<Task> displayedTasks) {
        List<String> output = new ArrayList<>();
        output.add(heading);
        for (Task task : displayedTasks) {
            output.add((tasks.getIndex(task) + 1) + ". " + task);
        }
        return output;
    }

    /** Removes a task and confirms the new count only after saving succeeds. */
    private List<String> handleDelete(String argument) throws EpiException {
        int taskIdx = parseTaskIndex(argument);
        applyChange(updated -> updated.delete(taskIdx));
        return List.of("Noted, I'll remove that from the task pile",
                "Now you have " + tasks.size() + " tasks in the list");
    }

    /** Applies the requested completion status, avoiding a save when it is already set. */
    private List<String> handleStatusChange(String argument, boolean isDone) throws EpiException {
        int taskIdx = parseTaskIndex(argument);
        if (tasks.get(taskIdx).isDone() == isDone) {
            String message = isDone
                    ? "Purr! Task " + (taskIdx + 1) + " is already marked as done."
                    : "Meow! Task " + (taskIdx + 1) + " is already marked as not done.";
            return List.of(message, tasks.get(taskIdx).toString());
        }
        applyChange(updated -> {
            if (isDone) {
                updated.get(taskIdx).markAsDone();
            } else {
                updated.get(taskIdx).markAsUndone();
            }
        });
        String message = isDone
                ? "About time you finished something. I've marked it as done:"
                : "Slacking off, are we? I've marked this as not done:";
        return List.of(message, tasks.get(taskIdx).toString());
    }

    /** Parses a todo and supplies its task-specific confirmation. */
    private List<String> handleTodo(String argument) throws EpiException {
        return addTask(parser.parseTodo(argument), "More work? Fine. I have added this task:");
    }

    /** Parses a deadline and supplies its task-specific confirmation. */
    private List<String> handleDeadline(String argument) throws EpiException {
        return addTask(parser.parseDeadline(argument), "A deadline? Better not miss it. I have added this task:");
    }

    /** Parses an event and supplies its task-specific confirmation. */
    private List<String> handleEvent(String argument) throws EpiException {
        return addTask(parser.parseEvent(argument), "An event? I hope there will be treats. I have added this task:");
    }

    /** Saves a new task before returning the shared confirmation lines for either interface. */
    private List<String> addTask(Task task, String message) throws EpiException {
        applyChange(updated -> updated.add(task));
        return List.of(message, task.toString(), "Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Checks the parser's index contract before a task is changed or removed. */
    private int parseTaskIndex(String argument) throws EpiException {
        int taskIdx = parser.parseTaskIndex(argument, tasks);
        assert taskIdx >= 0 && taskIdx < tasks.size() : "Parser returned an invalid task index";
        return taskIdx;
    }

    /** Rejects list views that require at least one task. */
    private void requireTasks() throws EpiException {
        if (tasks.size() == 0) {
            throw new EpiException("Purr! There is no task in your list");
        }
    }

    /** Publishes a task change in memory only after the complete new snapshot has been saved. */
    private void applyChange(Consumer<TaskList> change) throws EpiException {
        TaskList updated = tasks.copy();
        change.accept(updated);
        storage.save(updated);
        tasks = updated;
    }

    /** Rejects ignored trailing text so malformed commands do not look successful. */
    private void requireNoArgument(String command, String argument) throws EpiException {
        if (!argument.isBlank()) {
            throw new EpiException("Meow! Use: " + command);
        }
    }

    /** Runs the command loop until the user exits or input ends. */
    public void run() {
        while (ui.hasNextLine()) {
            String input = ui.readLine();
            processCommand(input).forEach(ui::showLine);
            if (isExitCommand(input)) {
                break;
            }
        }
        ui.close();
    }

    /** Returns whether the user input requests termination of the command loop. */
    private boolean isExitCommand(String input) {
        String[] commandParts = parser.parseInput(input);
        return commandParts.length == 1 && commandParts[0].equalsIgnoreCase("bye");
    }

    /** Starts Epi using its default task-file location. */
    public static void main(String[] args) {
        new Epi("./data/epi.txt").run();
    }
}
