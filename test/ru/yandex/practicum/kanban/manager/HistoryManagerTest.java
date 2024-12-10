package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class HistoryManagerTest<T extends HistoryManager> {
    T manager;
    Task task1;
    Task task2;
    Task task3;
    final int ID_TASK_1 = 1;
    final int ID_TASK_2 = 2;
    final int ID_TASK_3 = 3;

    void beforeEach(T manager) {
        this.manager = manager;
        task1 = new Task("Title task 1", "Description task 1", ID_TASK_1, StatusTask.NEW);
        task2 = new Task("Title task 2", "Description task 2", ID_TASK_2, StatusTask.NEW);
        task3 = new Task("Title task 3", "Description task 3", ID_TASK_3, StatusTask.NEW);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
    }

    // Тесты методов интерфейса HistoryManager

    void add() {
        assertEquals(List.of(task1, task2, task3), manager.getHistoryList());
    }

    void remove() {
        manager.remove(ID_TASK_1);
        assertEquals(List.of(task2, task3), manager.getHistoryList());
    }

    // Дополнительные тесты

    void shouldBeEmptyIfRemoveAll() {
        manager.remove(ID_TASK_1);
        manager.remove(ID_TASK_2);
        manager.remove(ID_TASK_3);
        assertTrue(manager.getHistoryList().isEmpty());
    }

    void shouldBeNoDuplicates() {
        manager.add(new Task("Title task 3", "Description task 3", ID_TASK_3, StatusTask.NEW));
        assertEquals(List.of(task1, task2, task3), manager.getHistoryList());
    }

    void shouldBeCorrectRemoveFirst() {
        manager.remove(ID_TASK_1);
        assertEquals(List.of(task2, task3), manager.getHistoryList());
    }

    void shouldBeCorrectRemoveMiddle() {
        manager.remove(ID_TASK_2);
        assertEquals(List.of(task1, task3), manager.getHistoryList());
    }

    void shouldBeCorrectRemoveLast() {
        manager.remove(ID_TASK_3);
        assertEquals(List.of(task1, task2), manager.getHistoryList());
    }

    void shouldBeCorrectUpdateFirst() {
        manager.add(new Task("Title task 1", "Description task 1", ID_TASK_1, StatusTask.NEW));
        assertEquals(List.of(task2, task3, task1), manager.getHistoryList());
    }

    void shouldBeCorrectUpdateMiddle() {
        manager.add(new Task("Title task 2", "Description task 2", ID_TASK_2, StatusTask.NEW));
        assertEquals(List.of(task1, task3, task2), manager.getHistoryList());
    }

    void shouldBeCorrectUpdateLast() {
        manager.add(new Task("Title task 3", "Description task 3", ID_TASK_3, StatusTask.NEW));
        assertEquals(List.of(task1, task2, task3), manager.getHistoryList());
    }
}