package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest {
    HistoryManager manager;
    Task task1;
    Task task2;
    Task task3;
    final int ID_TASK_1 = 1;
    final int ID_TASK_2 = 2;
    final int ID_TASK_3 = 3;

    @BeforeEach
    void beforeEach() {
        this.manager = Managers.getDefaultHistory();
        task1 = new Task("", "", ID_TASK_1, StatusTask.NEW, Duration.ZERO, null);
        task2 = new Task("", "", ID_TASK_2, StatusTask.NEW, Duration.ZERO, null);
        task3 = new Task("", "", ID_TASK_3, StatusTask.NEW, Duration.ZERO, null);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
    }

    @Test
    void add() {
        assertEquals(List.of(task1, task2, task3), manager.getHistoryList());
    }

    @Test
    void remove() {
        manager.remove(ID_TASK_1);
        assertEquals(List.of(task2, task3), manager.getHistoryList());
    }

    @Test
    void shouldBeEmptyIfRemoveAll() {
        manager.remove(ID_TASK_1);
        manager.remove(ID_TASK_2);
        manager.remove(ID_TASK_3);
        assertTrue(manager.getHistoryList().isEmpty());
    }

    @Test
    void shouldBeNoDuplicates() {
        manager.add(new Task("", "", ID_TASK_3, StatusTask.NEW, Duration.ZERO, null));
        assertEquals(List.of(task1, task2, task3), manager.getHistoryList());
    }

    @Test
    void shouldBeCorrectRemoveFirst() {
        manager.remove(ID_TASK_1);
        assertEquals(List.of(task2, task3), manager.getHistoryList());
    }

    @Test
    void shouldBeCorrectRemoveMiddle() {
        manager.remove(ID_TASK_2);
        assertEquals(List.of(task1, task3), manager.getHistoryList());
    }

    @Test
    void shouldBeCorrectRemoveLast() {
        manager.remove(ID_TASK_3);
        assertEquals(List.of(task1, task2), manager.getHistoryList());
    }

    @Test
    void shouldBeCorrectUpdateFirst() {
        manager.add(new Task("", "", ID_TASK_1, StatusTask.NEW, Duration.ZERO, null));
        assertEquals(List.of(task2, task3, task1), manager.getHistoryList());
    }

    @Test
    void shouldBeCorrectUpdateMiddle() {
        manager.add(new Task("", "", ID_TASK_2, StatusTask.NEW, Duration.ZERO, null));
        assertEquals(List.of(task1, task3, task2), manager.getHistoryList());
    }

    @Test
    void shouldBeCorrectUpdateLast() {
        manager.add(new Task("", "", ID_TASK_3, StatusTask.NEW, Duration.ZERO, null));
        assertEquals(List.of(task1, task2, task3), manager.getHistoryList());
    }
}