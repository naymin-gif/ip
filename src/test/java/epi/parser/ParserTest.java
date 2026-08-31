package epi.parser;

import epi.exception.EpiException;
import epi.task.Deadline;
import epi.task.Event;
import epi.task.Task;
import epi.task.TaskList;
import epi.task.Todo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseInput_commandWithArgument_splitsOnlyAtFirstWhitespace() {
        String[] parts = parser.parseInput("todo read a book");

        assertEquals("todo", parts[0]);
        assertEquals("read a book", parts[1]);
    }

    @Test
    void parseTodo_validDescription_createsTodoTask() throws EpiException {
        Task task = parser.parseTodo(" borrow book ");

        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    void parseDeadline_validDateTime_createsFormattedDeadline() throws EpiException {
        Deadline deadline = parser.parseDeadline("return book /by 2019-12-02 1800");

        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)", deadline.toString());
    }

    @Test
    void parseEvent_validDateTimes_createsFormattedEvent() throws EpiException {
        Event event = parser.parseEvent("project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");

        assertEquals("[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)", event.toString());
    }

    @Test
    void parseDeadline_missingByMarker_throwsEpiException() {
        assertThrows(EpiException.class, () -> parser.parseDeadline("return book"));
    }

    @Test
    void parseEvent_missingToMarker_throwsEpiException() {
        assertThrows(EpiException.class,
                () -> parser.parseEvent("project meeting /from 2019-12-02 1400"));
    }

    @Test
    void parseTaskIndex_validOneBasedIndex_returnsZeroBasedIndex() throws EpiException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, parser.parseTaskIndex("1", tasks));
    }
}
