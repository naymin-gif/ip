package epi.task;

import java.util.ArrayList;

/** Maintains the ordered collection of tasks used by the application. */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the task at the specified zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes the task at the specified zero-based index. */
    public void delete(int index) {
        tasks.remove(index);
    }

    /** Returns the number of tasks currently stored. */
    public int size() {
        return tasks.size();
    }

    /** Returns an iterator over tasks in insertion order. */
    @Override
    public java.util.Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
