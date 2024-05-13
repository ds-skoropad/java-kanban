package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void add() {
        historyManager.add(new Task("Title task", "Description task"));
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "Empty history");
        assertEquals(1, history.size(), "Empty history");
    }

    @Test
    public void maxSize() {
        int historyMaxSize = 10;
        int historyAddSize = historyMaxSize + 1;

        for (int i = 0; i < historyAddSize; i++) {
            historyManager.add(new Task("Title task", "Description task"));
        }
        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "Wrong size");
    }
}