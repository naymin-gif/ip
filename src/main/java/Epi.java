import java.util.Scanner;

public class Epi {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;
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
            String[] commandParts = input.trim().split("\\s+", 2);
            String command = commandParts[0].toLowerCase();
            String argument = commandParts.length > 1? commandParts[1]: "";
            if (command.equals("bye")) {
                System.out.println("Meow for now. See you later!");
                break;
            } else if (command.equals("list")) {
                System.out.println("     Behold, your endless mountain of chores:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println("   " + (i + 1) + ". " + tasks[i].toString());
                }
            } else if (command.equals("mark")) {
                int taskIdx = Integer.parseInt(commandParts[1]) - 1;
                tasks[taskIdx].markAsDone();
                System.out.println("     About time you finished something. I've marked it as done:");
                System.out.println("       " + tasks[taskIdx].toString());
            } else if (command.equals("unmark")) {
                int taskIdx = Integer.parseInt(commandParts[1]) - 1;
                tasks[taskIdx].markAsUndone();
                System.out.println("     Slacking off, are we? I've marked this as not done:");
                System.out.println("       " + tasks[taskIdx].toString());
            } else if (command.equals("todo")) {
                String description = argument.trim();
                tasks[taskCount] = new Todo(description);
                System.out.println("     More work? Fine. I have added this task:");
                System.out.println("       " + tasks[taskCount].toString());
                taskCount++;
                System.out.println("     Now you have " + taskCount + " tasks in the list.");
            } else if (command.equals("deadline")) {
                String[] deadlineParts = argument.split(" /by ", 2);
                String description = deadlineParts[0].trim();
                String by = deadlineParts[1].trim();
                tasks[taskCount] = new Deadline(description, by);
                System.out.println("     A deadline? Better not miss it. I have added this task:");
                System.out.println("       " + tasks[taskCount].toString());
                taskCount++;
                System.out.println("     Now you have " + taskCount + " tasks in the list.");

            } else if (command.equals("event")) {
                String[] eventParts = argument.split(" /from ", 2);
                String description = eventParts[0].trim();
                String[] timeparts = eventParts[1].split(" /to ", 2);
                String from = timeparts[0].trim();
                String to = timeparts[1].trim();
                tasks[taskCount] = new Event(description, from, to);
                System.out.println("     An event? I hope there will be treats. I have added this task:");
                System.out.println("       " + tasks[taskCount].toString());
                taskCount++;
                System.out.println("     Now you have " + taskCount + " tasks in the list.");
            }
            else {
                System.out.println("     I do not understand what this mean, Human.");
            }
        }
        scanner.close();
    }
}
