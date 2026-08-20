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
            String[] commandParts = input.trim().split("\\s+");
            if (input.equalsIgnoreCase("bye")) {
                System.out.println("Meow for now. See you later!");
                break;
            } else if (input.equalsIgnoreCase("list")) {
                System.out.println("     Behold, your endless mountain of chores:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println("   " + (i + 1) + ". " + tasks[i].toString());
                }
            } else if (commandParts[0].equalsIgnoreCase("mark")) {
                int taskIdx = Integer.parseInt(commandParts[1]) - 1;
                tasks[taskIdx].markAsDone();
                System.out.println("     About time you finished something. I've marked it as done:");
                System.out.println("       " + tasks[taskIdx].toString());
            } else if (commandParts[0].equalsIgnoreCase("unmark")) {
                int taskIdx = Integer.parseInt(commandParts[1]) - 1;
                tasks[taskIdx].markAsUndone();
                System.out.println("     Slacking off, are we? I've marked this as not done:");
                System.out.println("       " + tasks[taskIdx].toString());
            }
            else {
                tasks[taskCount] = new Task(input);

                System.out.println("Ugh, fine. Added: " + input);
                taskCount++;
            }
        }
        scanner.close();
    }
}
