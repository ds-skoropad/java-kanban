package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Title task 1", "Description task 1", 1, StatusTask.NEW);
        task2 = new Task("Title task 2", "Description task 2", 2, StatusTask.NEW);
        task3 = new Task("Title task 3", "Description task 3", 3, StatusTask.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

    }

    @Test
    public void add() { // Тест метода добаления
        assertEquals(List.of(task1, task2, task3), historyManager.getHistoryList());
    }

    @Test
    public void remove() { // Тест метода удаления
        historyManager.remove(1);
        assertEquals(List.of(task2, task3), historyManager.getHistoryList());

        historyManager.remove(2);
        assertEquals(List.of(task3), historyManager.getHistoryList());

        historyManager.remove(3);
        assertTrue(historyManager.getHistoryList().isEmpty());
    }

    @Test
    public void removeFirst() { // Тест на удаление первого элемента
        historyManager.remove(1);
        assertEquals(List.of(task2, task3), historyManager.getHistoryList());
    }

    @Test
    public void removeMiddle() { // Тест на удаление среднего элемента
        historyManager.remove(2);
        assertEquals(List.of(task1, task3), historyManager.getHistoryList());
    }

    @Test
    public void removeLast() { // Тест на удаление последнего элемента
        historyManager.remove(3);
        assertEquals(List.of(task1, task2), historyManager.getHistoryList());
    }

    @Test
    public void updateFirst() { // Тест на обновление первого элемента
        historyManager.add(new Task("Title task 1", "Description task 1", 1, StatusTask.NEW));
        assertEquals(List.of(task2, task3, task1), historyManager.getHistoryList());
    }

    @Test
    public void updateMiddle() { // Тест на обновление среднего элемента
        historyManager.add(new Task("Title task 2", "Description task 2", 2, StatusTask.NEW));
        assertEquals(List.of(task1, task3, task2), historyManager.getHistoryList());
    }

    @Test
    public void updateLast() { // Тест на обновление последнего элемента
        historyManager.add(new Task("Title task 3", "Description task 3", 3, StatusTask.NEW));
        assertEquals(List.of(task1, task2, task3), historyManager.getHistoryList());
    }
}