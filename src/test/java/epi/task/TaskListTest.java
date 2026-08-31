package epi.task;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TaskListTest {

    @Test
    void constructor_newTaskList_hasZeroTasks() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    @Test
    void add_singleTask_increasesSizeAndMakesTaskRetrievable() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    void add_multipleTasks_preservesInsertionOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");

        tasks.add(firstTask);
        tasks.add(secondTask);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    void iterator_tasksAreReturnedInInsertionOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Iterator<Task> iterator = tasks.iterator();

        assertSame(firstTask, iterator.next());
        assertSame(secondTask, iterator.next());
    }

    @Test
    void delete_singleTask_taskListBecomesEmpty() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.delete(0);

        assertEquals(0, tasks.size());
    }

    @Test
    void delete_firstTask_removesFirstTaskAndPreservesOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy book"));

        tasks.delete(0);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] return book", tasks.get(0).toString());
        assertEquals("[T][ ] buy book", tasks.get(1).toString());
    }

    @Test
    void delete_middleTask_removesMiddleTaskAndPreservesOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy book"));

        tasks.delete(1);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] buy book", tasks.get(1).toString());
    }

    @Test
    void delete_lastTask_removesLastTaskAndPreservesEarlierTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy book"));

        tasks.delete(2);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] return book", tasks.get(1).toString());
    }
}
