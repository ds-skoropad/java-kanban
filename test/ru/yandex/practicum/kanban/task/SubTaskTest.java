package ru.yandex.practicum.kanban.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    // Добавил на всякий случай. Не уверен что нужны дублирующие проверки учитывая наследование.

    @Test
    void taskSameIfIdSame() {
        // Все поля разные кроме id
        final SubTask task_1 = new SubTask("Task_1 title", "Task_1 description", 1, StatusTask.NEW, 0);
        final SubTask task_2 = new SubTask("Task_2 title", "Task_2 description", 1, StatusTask.DONE, 0);

        assertEquals(task_1, task_2);
        assertEquals(task_1.hashCode(), task_2.hashCode());
    }
    @Test
    void taskDifferentIfIdDifferent() {
        // Все поля одинаковые кроме id
        final SubTask task_1 = new SubTask("Task title", "Task description", 1, StatusTask.NEW, 0);
        final SubTask task_2 = new SubTask("Task title", "Task description", 2, StatusTask.NEW, 0);

        assertNotEquals(task_1, task_2);
        assertNotEquals(task_1.hashCode(), task_2.hashCode());
    }
    /*
     * Отказался от проверки: может ли добавить ПодЗадача сама себя в поле epicId.
     * Не стал добавлять такой функционал.
     * Все таки обо всех ПодЗадачах знает менеджер он и будет проверять, иначе получится двойная проверка.
     */
}