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
            if (input.equalsIgnoreCase("bye")) {
                System.out.println("Meow for now. See you later!");
                break;
            } else if (input.equalsIgnoreCase("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println("     Behold, your endless mountain of chores:");
                    System.out.println("   " + (i + 1) + ". " + tasks[i].toString());
                }
            } else if (input.startsWith("mark ")) {
                int taskIdx = Integer.parseInt(input.substring(5)) - 1;
                tasks[taskIdx].markAsDone();
                System.out.println("     About time you finished something. I've marked it as done:");
                System.out.println("       " + tasks[taskIdx].toString());
            } else if (input.startsWith("unmark ")) {
                int taskIdx = Integer.parseInt(input.substring(7)) - 1;
                tasks[taskIdx].markAsUndone();
                System.out.println("     Slacking off, are we? I've marked this as not done:");
                System.out.println("       " + tasks[taskIdx].toString());
            }
            else {
                tasks[taskCount] = new Task(input);

                System.out.println("Ugh, fine. Added: input");
                //System.out.println("       " + tasks[taskCount].toString());
                taskCount++;
            }
        }
        scanner.close();
    }
}
