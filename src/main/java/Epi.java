import java.time.DateTimeException;

public class Epi {
    public static void main(String[] args) {
        ConsoleUi ui = new ConsoleUi();
        TaskList tasks = new TaskList();
        Storage storage = new Storage("./data/epi.txt");
        Parser parser = new Parser();
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

        storage.prepareFile();
        tasks = storage.load();

        while (ui.hasNextLine()) {
            String input = ui.readLine();

            try {
                String[] commandParts = parser.parseInput(input);
                String command = commandParts[0].toLowerCase();
                String argument = commandParts.length > 1 ? commandParts[1] : "";

                if (command.equals("bye")) {
                    ui.showLine("Meow for now. See you later!");
                    break;
                } else if (command.equals("list")) {
                    if (tasks.size() == 0) {
                        throw new EpiException("Purr! There is no task in your list");
                    }
                        ui.showLine("     Here is your pile of tasks:");
                    for (int i = 0; i < tasks.size(); i++) {
                        ui.showLine("   " + (i + 1) + ". " + tasks.get(i).toString());
                    }
                } else if (command.equals("delete")) {
                    if (argument.isEmpty()) {
                        throw new EpiException("You need to give me a task number to delete, human.");
                    }
                    int taskIdx = Integer.parseInt(argument) - 1;
                    if (tasks.size() == 0) {
                        throw new EpiException("Cannot! You don't even have a single task");
                    }
                    if (taskIdx < 0 || taskIdx >= tasks.size()) {
                        throw new EpiException("That task number doesn't exist in my memory!");
                    }
                    tasks.delete(taskIdx);
                    ui.showLine("Noted, I'll remove that from the task pile");
                    int totalTaskNow = tasks.size();
                    ui.showLine("Now you have " + totalTaskNow + " tasks in the list");

                } else if (command.equals("mark") || command.equals("unmark")) {
                    int taskIdx = parser.parseTaskIndex(argument, tasks);

                    if (command.equals("mark")) {
                        tasks.get(taskIdx).markAsDone();
                        ui.showLine("     About time you finished something. I've marked it as done:");
                    } else {
                        tasks.get(taskIdx).markAsUndone();
                        ui.showLine("     Slacking off, are we? I've marked this as not done:");
                    }
                    ui.showLine("       " + tasks.get(taskIdx).toString());
                } else if (command.equals("todo")) {
                    tasks.add(parser.parseTodo(argument));
                    ui.showLine("     More work? Fine. I have added this task:");
                    ui.showLine("       " + tasks.get(tasks.size() - 1).toString());
                    ui.showLine("     Now you have " + tasks.size() + " tasks in the list.");
                } else if (command.equals("deadline")) {
                    tasks.add(parser.parseDeadline(argument));
                    ui.showLine("     A deadline? Better not miss it. I have added this task:");
                    ui.showLine("       " + tasks.get(tasks.size() - 1).toString());
                    ui.showLine("     Now you have " + tasks.size() + " tasks in the list.");

                } else if (command.equals("event")) {
                    tasks.add(parser.parseEvent(argument));
                    ui.showLine("     An event? I hope there will be treats. I have added this task:");
                    ui.showLine("       " + tasks.get(tasks.size() - 1).toString());
                    ui.showLine("     Now you have " + tasks.size() + " tasks in the list.");
                } else {
                    throw new EpiException("I do not understand what that means, Human.");
                }

                if (!command.equals("list")) {
                    storage.save(tasks);
                }

            } catch (EpiException e) {
                ui.showLine(e.getMessage());
            } catch (NumberFormatException e) {
                ui.showLine("That is not a valid number");
            } catch (DateTimeException e) {
                ui.showLine("Invalid date format! Please use: yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)");
            }
        }
        ui.close();
    }
}
