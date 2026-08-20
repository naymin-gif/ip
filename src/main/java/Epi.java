import java.util.ArrayList;
import java.util.Scanner;

public class Epi {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();
        String banner = "  ______       _ \n"
                + " |  ____|     (_)\n"
                + " | |__   _ __  _ \n"
                + " |  __| | '_ \\| |\n"
                + " | |____| |_) | |\n"
                + " |______| .__/|_|\n"
                + "        | |      \n"
                + "        |_|      \n";
        System.out.println(banner);
        System.out.println("Meowdy! I'm Epi");
        System.out.println("Are you ready to tackle some purr-fectly good tasks today?");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();

            try {
                String[] commandParts = input.trim().split("\\s+", 2);
                String command = commandParts[0].toLowerCase();
                String argument = commandParts.length > 1 ? commandParts[1] : "";
                if (command.equals("bye")) {
                    System.out.println("Meow for now. See you later!");
                    break;
                } else if (command.equals("list")) {
                    if (tasks.size() == 0) {
                        throw new EpiException("Purr! There is no task in your list");
                    }
                    System.out.println("     Here is your pile of tasks:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println("   " + (i + 1) + ". " + tasks.get(i).toString());
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
                    tasks.remove(taskIdx);
                    System.out.println("Noted, I'll remove that from the task pile");
                    int totalTaskNow = tasks.size();
                    System.out.println("Now you have " + totalTaskNow + " tasks in the list");

                } else if (command.equals("mark") || command.equals("unmark")) {
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

                    if (command.equals("mark")) {
                        tasks.get(taskIdx).markAsDone();
                        System.out.println("     About time you finished something. I've marked it as done:");
                    } else {
                        tasks.get(taskIdx).markAsUndone();
                        System.out.println("     Slacking off, are we? I've marked this as not done:");
                    }
                    System.out.println("       " + tasks.get(taskIdx).toString());
                } else if (command.equals("todo")) {
                    if (argument.isEmpty()) {
                        throw new EpiException("The description of a todo cannot be empty");
                    }
                    String description = argument.trim();
                    tasks.add(new Todo(description));
                    System.out.println("     More work? Fine. I have added this task:");
                    System.out.println("       " + tasks.get(tasks.size() - 1).toString());
                    System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                } else if (command.equals("deadline")) {
                    if (argument.isEmpty()) {
                        throw new EpiException("The description of a deadline cannot be empty!");
                    }
                    String[] deadlineParts = argument.split(" /by ", 2);
                    if (deadlineParts.length < 2 || deadlineParts[0].trim().isEmpty() || deadlineParts[1].trim().isEmpty()) {
                        throw new EpiException("Invalid format! Use: deadline <task> /by <time>");
                    }
                    String description = deadlineParts[0].trim();
                    String by = deadlineParts[1].trim();
                    tasks.add(new Deadline(description, by));
                    System.out.println("     A deadline? Better not miss it. I have added this task:");
                    System.out.println("       " + tasks.get(tasks.size() - 1).toString());
                    System.out.println("     Now you have " + tasks.size() + " tasks in the list.");

                } else if (command.equals("event")) {
                    if (argument.isEmpty()) {
                        throw new EpiException("The description of a event cannot be empty!");
                    }
                    String[] eventParts = argument.split(" /from ", 2);
                    if (eventParts.length < 2 || eventParts[0].trim().isEmpty()) {
                        throw new EpiException("Invalid format! Missing description or '/from'. Use: event <task> /from <start> /to <end>");
                    }
                    String description = eventParts[0].trim();
                    String[] timeparts = eventParts[1].split(" /to ", 2);

                    if (timeparts.length < 2 || timeparts[0].trim().isEmpty() || timeparts[1].trim().isEmpty()) {
                        throw new EpiException("Invalid format! Missing times. Use: event <task> /from <start> /to <end>");
                    }

                    String from = timeparts[0].trim();
                    String to = timeparts[1].trim();
                    tasks.add(new Event(description, from, to));
                    System.out.println("     An event? I hope there will be treats. I have added this task:");
                    System.out.println("       " + tasks.get(tasks.size() - 1).toString());
                    System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                } else {
                    throw new EpiException("I do not understand what that means, Human.");
                }
            } catch (EpiException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid number");
            }
        }
        scanner.close();
    }
}
