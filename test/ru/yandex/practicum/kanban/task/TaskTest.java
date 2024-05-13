package ru.yandex.practicum.kanban.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void taskSameIfIdSame() {
        // Все поля разные кроме id
        final Task task_1 = new Task("Task_1 title", "Task_1 description", 1, StatusTask.NEW);
        final Task task_2 = new Task("Task_2 title", "Task_2 description", 1, StatusTask.DONE);

        assertEquals(task_1, task_2);
        assertEquals(task_1.hashCode(), task_2.hashCode());
    }
    @Test
    void taskDifferentIfIdDifferent() {
        // Все поля одинаковые кроме id
        final Task task_1 = new Task("Task title", "Task description", 1, StatusTask.NEW);
        final Task task_2 = new Task("Task title", "Task description", 2, StatusTask.NEW);

        assertNotEquals(task_1, task_2);
        assertNotEquals(task_1.hashCode(), task_2.hashCode());
    }
}