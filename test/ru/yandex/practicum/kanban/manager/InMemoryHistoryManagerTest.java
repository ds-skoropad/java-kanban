package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager; // Исправлено!

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void add() {
        historyManager.add(new Task("Title task", "Description task"));
        List<Task> history = historyManager.getHistoryList();

        assertNotNull(history, "History is empty");
        assertEquals(1, history.size(), "History is empty");
    }

    @Test
    public void addAndRemoveOneItem() {
        historyManager.add(new Task("Title task " + 1, "Description task " + 1, 1, StatusTask.NEW));
        final List<Task> history1 = historyManager.getHistoryList();
        assertEquals(history1.get(0).getId(), 1, "IDs do not match");

        historyManager.remove(1);
        final List<Task> history2 = historyManager.getHistoryList();
        assertEquals(history2.size(), 0, "Size doesn't match");
    }

    @Test
    public void addAndRemoveTwoItems() {
        historyManager.add(new Task("Title task " + 1, "Description task " + 1, 1, StatusTask.NEW));
        historyManager.add(new Task("Title task " + 2, "Description task " + 2, 2, StatusTask.NEW));
        final List<Task> history1 = historyManager.getHistoryList();
        assertEquals(history1.size(), 2, "Size doesn't match");
        assertEquals(history1.get(0).getId(), 1, "IDs do not match");

        historyManager.remove(1);
        final List<Task> history2 = historyManager.getHistoryList();
        assertEquals(history2.size(), 1, "Size doesn't match");
        assertEquals(history2.get(0).getId(), 2, "IDs do not match");
    }


    @Test
    public void addAndUpdate() {
        int historyAddSize = 10;

        for (int i = 0; i < historyAddSize; i++) {
            historyManager.add(new Task("Title task " + i, "Description task " + i, i, StatusTask.NEW));
        }
        final List<Task> historyOne = historyManager.getHistoryList();
        assertEquals(historyOne.get(1).getId(), 1, "IDs do not match");

        historyManager.add(new Task("Title task " + 1, "Description task " + 1, 1, StatusTask.NEW));
        final List<Task> historyTwo = historyManager.getHistoryList();
        assertNotEquals(historyTwo.get(1).getId(), 1, "The object is in its position");
        assertEquals(historyTwo.get(9).getId(), 1, "The object has moved to the end of the list");
    }

    @Test
    void remove() {
        int historyAddSize = 10;

        for (int i = 0; i < historyAddSize; i++) {
            historyManager.add(new Task("Title task " + i, "Description task " + i, i, StatusTask.NEW));
        }
        historyManager.remove(1);
        final List<Task> history = historyManager.getHistoryList();
        assertEquals(9, history.size(), "The size has not decreased");
        assertNotEquals(history.get(1).getId(), 1, "The object is in its position");
    }
}