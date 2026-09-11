package epi;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        storage.prepareFile();
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
    }

    /** Processes one command and returns the lines that should be shown to a user interface. */
    public List<String> processCommand(String input) {
        assert input != null : "A command must be provided to Epi";
        List<String> output = new ArrayList<>();
        try {
            String[] commandParts = parser.parseInput(input);
            assert commandParts.length > 0 : "The parser must return a command part";
            String command = commandParts[0].toLowerCase(Locale.ROOT);
            String argument = commandParts.length > 1 ? commandParts[1] : "";
            if (command.equals("bye")) {
                output.add("Meow for now. See you later!");
            } else if (command.equals("list")) {
                if (tasks.size() == 0) {
                    throw new EpiException("Purr! There is no task in your list");
                }
                output.add("Here is your pile of tasks:");
                for (int i = 0; i < tasks.size(); i++) {
                    output.add((i + 1) + ". " + tasks.get(i));
                }
            } else if (command.equals("find")) {
                if (argument.trim().isEmpty()) {
                    throw new EpiException("Please provide a keyword to search for.");
                }
                output.add("Here are the matching tasks in your list:");
                for (Task task : tasks.find(argument.trim())) {
                    output.add((tasks.getIndex(task) + 1) + ". " + task);
                }
            } else if (command.equals("delete")) {
                int taskIdx = parser.parseTaskIndex(argument, tasks);
                assert taskIdx >= 0 && taskIdx < tasks.size() : "Parser returned an invalid task index";
                tasks.delete(taskIdx);
                output.add("Noted, I'll remove that from the task pile");
                output.add("Now you have " + tasks.size() + " tasks in the list");
                storage.save(tasks);
            } else if (command.equals("mark") || command.equals("unmark")) {
                int taskIdx = parser.parseTaskIndex(argument, tasks);
                assert taskIdx >= 0 && taskIdx < tasks.size() : "Parser returned an invalid task index";
                if (command.equals("mark")) {
                    tasks.get(taskIdx).markAsDone();
                    output.add("About time you finished something. I've marked it as done:");
                } else {
                    tasks.get(taskIdx).markAsUndone();
                    output.add("Slacking off, are we? I've marked this as not done:");
                }
                output.add(tasks.get(taskIdx).toString());
                storage.save(tasks);
            } else if (command.equals("todo")) {
                tasks.add(parser.parseTodo(argument));
                output.add("More work? Fine. I have added this task:");
                output.add(tasks.get(tasks.size() - 1).toString());
                output.add("Now you have " + tasks.size() + " tasks in the list.");
                storage.save(tasks);
            } else if (command.equals("deadline")) {
                tasks.add(parser.parseDeadline(argument));
                output.add("A deadline? Better not miss it. I have added this task:");
                output.add(tasks.get(tasks.size() - 1).toString());
                output.add("Now you have " + tasks.size() + " tasks in the list.");
                storage.save(tasks);
            } else if (command.equals("event")) {
                tasks.add(parser.parseEvent(argument));
                output.add("An event? I hope there will be treats. I have added this task:");
                output.add(tasks.get(tasks.size() - 1).toString());
                output.add("Now you have " + tasks.size() + " tasks in the list.");
                storage.save(tasks);
            } else {
                throw new EpiException("I do not understand what that means, Human.");
            }
        } catch (EpiException e) {
            output.add(e.getMessage());
        } catch (NumberFormatException e) {
            output.add("That is not a valid number");
        } catch (DateTimeException e) {
            output.add("Invalid date format! Please use: yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)");
        }
        return output;
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
        return commandParts[0].equalsIgnoreCase("bye");
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
