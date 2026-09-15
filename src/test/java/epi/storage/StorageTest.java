package epi.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import epi.task.Deadline;
import epi.task.Event;
import epi.task.TaskList;
import epi.task.Todo;

/**
 * Protects the existing task-file format using independent temporary directories.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void prepareFile_missingParents_createsEmptyFile() throws IOException {
        Path path = temporaryDirectory.resolve("nested/task data/epi.txt");
        Storage storage = new Storage(path.toString());

        storage.prepareFile();

        assertTrue(Files.isRegularFile(path));
        assertEquals("", Files.readString(path));
        assertEquals(0, storage.load().size());
    }

    @Test
    void prepareFile_existingFile_preservesContents() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        String saved = "T | 1 | read book\n";
        Files.writeString(path, saved);

        new Storage(path.toString()).prepareFile();

        assertEquals(saved, Files.readString(path));
    }

    @Test
    void load_existingRecords_restoresTypesStatusDatesAndOrder() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 1 | read book\n"
                + "D | 0 | return book | 2019-12-02 1800\n"
                + "E | 1 | meeting | 2019-12-02 1400 | 2019-12-02 1600\n");

        TaskList loaded = new Storage(path.toString()).load();

        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)", loaded.get(1).toString());
        assertEquals("[E][X] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)",
                loaded.get(2).toString());
    }

    @Test
    void saveAndLoad_mixedTasks_roundTripsAllFields() throws IOException {
        Path path = temporaryDirectory.resolve("tasks with spaces.txt");
        Storage storage = new Storage(path.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read café notes"));
        tasks.add(new Deadline("return book", "2019-12-02 1800"));
        tasks.add(new Event("meeting", "2019-12-02 1400", "2019-12-02 1600"));
        tasks.get(1).markAsDone();
        storage.prepareFile();

        storage.save(tasks);
        TaskList loaded = new Storage(path.toString()).load();

        assertEquals(List.of("T | 0 | read café notes", "D | 1 | return book | 2019-12-02 1800",
                "E | 0 | meeting | 2019-12-02 1400 | 2019-12-02 1600"), Files.readAllLines(path));
        assertEquals(tasks.size(), loaded.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).getClass(), loaded.get(i).getClass());
            assertEquals(tasks.get(i).toString(), loaded.get(i).toString());
            assertEquals(tasks.get(i).toFileFormat(), loaded.get(i).toFileFormat());
        }
    }

    @Test
    void save_shorterList_replacesOldRecords() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 0 | first\nT | 0 | second\nT | 0 | third\n");
        Storage storage = new Storage(path.toString());
        TaskList tasks = storage.load();
        tasks.delete(1);
        tasks.delete(1);

        storage.save(tasks);

        assertEquals(List.of("T | 0 | first"), Files.readAllLines(path));
        assertEquals(1, new Storage(path.toString()).load().size());
    }

    @Test
    void save_emptyList_removesPreviousRecords() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 0 | read book\n");
        Storage storage = new Storage(path.toString());

        storage.save(new TaskList());

        assertEquals("", Files.readString(path));
        assertEquals(0, storage.load().size());
    }

    @Test
    void save_separateFiles_doesNotAlterOtherData() throws IOException {
        Path first = temporaryDirectory.resolve("first.txt");
        Path second = temporaryDirectory.resolve("second.txt");
        Files.writeString(first, "T | 0 | first\n");
        Files.writeString(second, "T | 1 | second\n");

        new Storage(first.toString()).save(new TaskList());

        assertEquals("T | 1 | second\n", Files.readString(second));
        assertEquals("[T][X] second", new Storage(second.toString()).load().get(0).toString());
    }
}
