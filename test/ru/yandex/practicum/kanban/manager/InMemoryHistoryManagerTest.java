package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private final int NUM_OF_TASKS = 5; // Тестовое количество задач в списке

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        for (int i = 1; i < NUM_OF_TASKS + 1; i++) {
            historyManager.add(new Task("Title task " + i, "Description task " + i, i, StatusTask.NEW));
        }
    }

    // Тест метода добаления
    @Test
    public void add() {
        final List<Task> history = historyManager.getHistoryList();
        assertNotNull(history, "Список истории пуст");
        assertEquals(NUM_OF_TASKS, history.size(), "Несоответствие размера списка истории");
    }

    // Тест метода удаления
    @Test
    public void remove() {
        historyManager.remove(1);

        final List<Task> history = historyManager.getHistoryList();
        assertEquals(NUM_OF_TASKS - 1, history.size(), "Несоответствие размера списка истории");
    }

    // Тест на удаление первого элемента
    @Test
    public void removeFirst() {
        historyManager.remove(1);

        final List<Task> history = historyManager.getHistoryList();
        assertEquals(NUM_OF_TASKS - 1, history.size(), "Несоответствие размера списка истории");
        // Проверяем порядок всех элементов
        int countId = 2;
        for (int i = 0; i < history.size(); i++) {
            assertEquals(countId, history.get(i).getId(), "Несоответствие ID под индексом " + i);
            countId++;
        }
    }

    // Тест на удаление среднего элемента
    @Test
    public void removeMiddle() {
        int middleId = NUM_OF_TASKS / 2 + 1;
        historyManager.remove(middleId);

        final List<Task> history = historyManager.getHistoryList();
        assertEquals(NUM_OF_TASKS - 1, history.size(), "Несоответствие размера списка истории");
        // Проверяем порядок всех элементов
        int countId = 1;
        for (int i = 0; i < history.size(); i++) {
            if (countId == middleId) countId++;
            assertEquals(countId, history.get(i).getId(), "Несоответствие ID под индексом " + i);
            countId++;
        }
    }

    // Тест на удаление последнего элемента
    @Test
    public void removeLast() {
        historyManager.remove(NUM_OF_TASKS);

        final List<Task> history = historyManager.getHistoryList();
        assertEquals(NUM_OF_TASKS - 1, history.size(), "Несоответствие размера списка истории");
        // Проверяем порядок всех элементов
        int countId = 1;
        for (int i = 0; i < history.size(); i++) {
            assertEquals(countId, history.get(i).getId(), "Несоответствие ID под индексом " + i);
            countId++;
        }
    }

    // Тест на обновление первого элемента
    @Test
    public void updateFirst() {
        int updateId = 1;
        historyManager.add(new Task("Title task " + updateId, "Description task "
                + updateId, updateId, StatusTask.NEW));

        final List<Task> history = historyManager.getHistoryList();
        assertEquals(NUM_OF_TASKS, history.size(), "Колличество элементов не должно поменятся");
        // Проверяем порядок всех элементов
        int countId = 2;
        for (int i = 0; i < history.size(); i++) {
            if (i == history.size() - 1) countId = updateId;
            assertEquals(countId, history.get(i).getId(), "Несоответствие ID под индексом " + i);
            countId++;
        }
    }

    // Тест на обновление среднего элемента
    @Test
    public void updateMiddle() {
        int updateId = NUM_OF_TASKS / 2 + 1;
        historyManager.add(new Task("Title task " + updateId, "Description task "
                + updateId, updateId, StatusTask.NEW));

        final List<Task> history = historyManager.getHistoryList();
        assertEquals(NUM_OF_TASKS, history.size(), "Колличество элементов не должно поменятся");
        // Проверяем порядок всех элементов
        int countId = 1;
        for (int i = 0; i < history.size(); i++) {
            if (countId == updateId) countId++;
            if (i == history.size() - 1) countId = updateId;
            assertEquals(countId, history.get(i).getId(), "Несоответствие ID под индексом " + i);
            countId++;
        }
    }

    // Тест на обновление последнего элемента
    @Test
    public void updateLast() {
        int updateId = NUM_OF_TASKS;
        historyManager.add(new Task("Title task " + updateId, "Description task "
                + updateId, updateId, StatusTask.NEW));

        final List<Task> history = historyManager.getHistoryList();
        assertEquals(NUM_OF_TASKS, history.size(), "Колличество элементов не должно поменятся");
        // Проверяем порядок всех элементов
        int countId = 1;
        for (int i = 0; i < history.size(); i++) {
            assertEquals(countId, history.get(i).getId(), "Несоответствие ID под индексом " + i);
            countId++;
        }
    }
}