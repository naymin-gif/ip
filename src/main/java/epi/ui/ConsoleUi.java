package epi.ui;

import java.util.Scanner;

/** Handles console input and output for the application. */
public class ConsoleUi {
    private final Scanner scanner;

    /** Creates a console UI connected to standard input and output. */
    public ConsoleUi() {
        scanner = new Scanner(System.in);
    }

    /** Returns whether another input line is available. */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /** Reads and returns the next input line. */
    public String readLine() {
        return scanner.nextLine();
    }

    /** Prints one message to the console. */
    public void showLine(String message) {
        System.out.println(message);
    }

    /** Closes the underlying input scanner. */
    public void close() {
        scanner.close();
    }
}
