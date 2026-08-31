package epi.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import epi.task.Task;
import epi.task.TaskList;
import epi.task.Todo;
import epi.task.Deadline;
import epi.task.Event;

/** Reads and writes tasks using the application's persistent file format. */
public class Storage {
    private final String filePath;

    /** Creates storage backed by the specified file path. */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /** Saves all tasks to the backing file, replacing its previous contents. */
    public void save(TaskList tasks) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Task task : tasks) {
                writer.write(task.toFileFormat() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("I couldn't save your tasks to the hard drive: " + e.getMessage());
        }
    }

    /** Loads tasks from the backing file into a new task list. */
    public TaskList load() {
        TaskList tasks = new TaskList();
        File file = new File(filePath);

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String[] parts = fileScanner.nextLine().split(" \\| ");
                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];
                Task task = null;

                if (type.equals("T")) {
                    task = new Todo(description);
                } else if (type.equals("D")) {
                    task = new Deadline(description, parts[3]);
                } else if (type.equals("E")) {
                    task = new Event(description, parts[3], parts[4]);
                }

                if (task != null) {
                    if (isDone) {
                        task.markAsDone();
                    }
                    tasks.add(task);
                }
            }
        } catch (java.io.FileNotFoundException e) {
            System.out.println("The memory file disappeared while I was trying to read it!");
        }
        return tasks;
    }

    /** Creates the parent directory and backing file when they do not exist. */
    public void prepareFile() {
        File file = new File(filePath);
        File directory = file.getParentFile();
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Something went wrong creating my memory file!");
            }
        }
    }
}
