package epi.ui;

import java.util.Scanner;

public class ConsoleUi {
    private final Scanner scanner;

    public ConsoleUi() {
        scanner = new Scanner(System.in);
    }

    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public void showLine(String message) {
        System.out.println(message);
    }

    public void close() {
        scanner.close();
    }
}
