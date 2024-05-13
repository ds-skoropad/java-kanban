package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    static Managers managers;
    @BeforeEach
    void beforeEach() {
        managers = new Managers();
    } 
    @Test
    void getDefault() {
        final TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "It should be TaskManager");
    }

    @Test
    void getDefaultHistory() {
        final HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "It should be HistoryManager");
    }

}