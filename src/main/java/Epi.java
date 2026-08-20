import java.util.Scanner;

public class Epi {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
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
                    System.out.println("   " + (i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println("Added: " + input);
            }
        }
        scanner.close();
    }
}
