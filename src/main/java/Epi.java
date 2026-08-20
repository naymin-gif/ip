import java.util.Scanner;

public class Epi {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
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

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println("Meow for now. See you later!");
                break;
            } else {
                System.out.println("Meow? You said: " + input);
            }
        }
        scanner.close();
    }
}
