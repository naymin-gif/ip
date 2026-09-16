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

    /** Processes a command, distinguishing errors from successful replies without examining their wording. */
    public CommandResult processCommandResult(String input) {
        assert input != null : "A command must be provided to Epi";
        List<String> output = new ArrayList<>();
        try {
            String[] commandParts = parser.parseInput(input);
            assert commandParts.length > 0 : "The parser must return a command part";
            String command = commandParts[0].toLowerCase(Locale.ROOT);
            String argument = commandParts.length > 1 ? commandParts[1] : "";
            if (command.equals("bye")) {
                requireNoArgument(command, argument);
                output.add("Meow for now. See you later!");
            } else if (command.equals("list")) {
                requireNoArgument(command, argument);
                if (tasks.size() == 0) {
                    throw new EpiException("Purr! There is no task in your list");
                }
                output.add("Here is your pile of tasks:");
                for (int i = 0; i < tasks.size(); i++) {
                    output.add((i + 1) + ". " + tasks.get(i));
                }
            } else if (command.equals("sort")) {
                if (!argument.trim().equalsIgnoreCase("date")) {
                    throw new EpiException("Meow! Use: sort date");
                }
                if (tasks.size() == 0) {
                    throw new EpiException("Purr! There is no task in your list");
                }
                output.add("Here is your pile of tasks, sorted by date (original task numbers):");
                for (Task task : tasks.getSortedByDate()) {
                    output.add((tasks.getIndex(task) + 1) + ". " + task);
                }
            } else if (command.equals("find")) {
                String keyword = argument.trim();
                if (keyword.isEmpty()) {
                    throw new EpiException("Please provide a keyword to search for.");
                }
                List<Task> matches = tasks.find(keyword);
                if (matches.isEmpty()) {
                    output.add("Meow! I couldn't find any tasks matching \"" + keyword + "\".");
                } else {
                    output.add("Here are the matching tasks in your list:");
                    for (Task task : matches) {
                        output.add((tasks.getIndex(task) + 1) + ". " + task);
                    }
                }
            } else if (command.equals("delete")) {
                int taskIdx = parser.parseTaskIndex(argument, tasks);
                assert taskIdx >= 0 && taskIdx < tasks.size() : "Parser returned an invalid task index";
                applyChange(updated -> updated.delete(taskIdx));
                output.add("Noted, I'll remove that from the task pile");
                output.add("Now you have " + tasks.size() + " tasks in the list");
            } else if (command.equals("mark") || command.equals("unmark")) {
                int taskIdx = parser.parseTaskIndex(argument, tasks);
                assert taskIdx >= 0 && taskIdx < tasks.size() : "Parser returned an invalid task index";
                boolean requestedDone = command.equals("mark");
                if (tasks.get(taskIdx).isDone() == requestedDone) {
                    String message = requestedDone
                            ? "Purr! Task " + (taskIdx + 1) + " is already marked as done."
                            : "Meow! Task " + (taskIdx + 1) + " is already marked as not done.";
                    return new CommandResult(List.of(message, tasks.get(taskIdx).toString()), false);
                }
                applyChange(updated -> {
                    if (requestedDone) {
                        updated.get(taskIdx).markAsDone();
                    } else {
                        updated.get(taskIdx).markAsUndone();
                    }
                });
                if (requestedDone) {
                    output.add("About time you finished something. I've marked it as done:");
                } else {
                    output.add("Slacking off, are we? I've marked this as not done:");
                }
                output.add(tasks.get(taskIdx).toString());
            } else if (command.equals("todo")) {
                Task task = parser.parseTodo(argument);
                applyChange(updated -> updated.add(task));
                output.add("More work? Fine. I have added this task:");
                output.add(tasks.get(tasks.size() - 1).toString());
                output.add("Now you have " + tasks.size() + " tasks in the list.");
            } else if (command.equals("deadline")) {
                Task task = parser.parseDeadline(argument);
                applyChange(updated -> updated.add(task));
                output.add("A deadline? Better not miss it. I have added this task:");
                output.add(tasks.get(tasks.size() - 1).toString());
                output.add("Now you have " + tasks.size() + " tasks in the list.");
            } else if (command.equals("event")) {
                Task task = parser.parseEvent(argument);
                applyChange(updated -> updated.add(task));
                output.add("An event? I hope there will be treats. I have added this task:");
                output.add(tasks.get(tasks.size() - 1).toString());
                output.add("Now you have " + tasks.size() + " tasks in the list.");
            } else {
                throw new EpiException("I do not understand what that means, Human.");
            }
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
        return new CommandResult(output, false);
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

    /** Displays the common confirmation shown after adding a task. */
    private void showAddedTask(String message) {
        ui.showLine(message);
        ui.showLine("       " + tasks.get(tasks.size() - 1));
        ui.showLine("     Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Starts Epi using its default task-file location. */
    public static void main(String[] args) {
        new Epi("./data/epi.txt").run();
    }
}
