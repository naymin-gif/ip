package epi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
class TaskTest {

    @Test
    void constructor_unsafeDescriptions_rejectsUnserializableData() {
        for (String description : List.of("", " \t ", "read | book", "a|b", "first\nsecond", "first\rsecond")) {
            assertThrows(IllegalArgumentException.class, () -> new Task(description), description);
        }
        assertThrows(IllegalArgumentException.class, () -> new Task((String) null));
    }

    @Test
    void constructor_ordinaryPunctuationAndUnicode_preservesDescription() {
        String description = "read café / notes #fun & revise!";
        assertEquals("[ ] " + description, new Task(description).toString());
    }

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
