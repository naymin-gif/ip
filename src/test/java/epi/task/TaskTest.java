package epi.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void markAsDone_incompleteTask_marksTaskComplete() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("[X] read book", task.toString());
    }

    @Test
    void markAsUndone_completedTask_marksTaskIncomplete() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsUndone();

        assertEquals("[ ] read book", task.toString());
    }
}
