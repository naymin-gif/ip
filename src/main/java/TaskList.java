import java.util.ArrayList;

public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public void delete(int index) {
        tasks.remove(index);
    }

    public int size() {
        return tasks.size();
    }

    @Override
    public java.util.Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
