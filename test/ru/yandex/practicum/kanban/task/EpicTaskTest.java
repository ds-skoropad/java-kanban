package ru.yandex.practicum.kanban.task;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {
    // Добавил на всякий случай. Не уверен что нужны дублирующие проверки учитывая наследование.

    @Test
    void taskSameIfIdSame() {
        // Все поля разные, кроме id
        final EpicTask epicTask_1 = new EpicTask("EpicTask_1 title", "EpicTask_1 description",
                1, StatusTask.NEW, new ArrayList<>());
        final EpicTask epicTask_2 = new EpicTask("EpicTask_2 title", "EpicTask_2 description",
                1, StatusTask.DONE, new ArrayList<>());

        assertEquals(epicTask_1, epicTask_2);
        assertEquals(epicTask_1.hashCode(), epicTask_2.hashCode());
    }
    @Test
    void taskDifferentIfIdDifferent() {
        // Все поля одинаковые, кроме id
        final EpicTask epicTask_1 = new EpicTask("EpicTask title", "EpicTask description",
                1, StatusTask.NEW, new ArrayList<>());
        final EpicTask epicTask_2 = new EpicTask("EpicTask title", "EpicTask description",
                2, StatusTask.NEW, new ArrayList<>());

        assertNotEquals(epicTask_1, epicTask_2);
        assertNotEquals(epicTask_1.hashCode(), epicTask_2.hashCode());
    }

    @Test
    void uniqueIdInListSubTask() {
        final EpicTask epicTask = new EpicTask("EpicTask title", "EpicTask description",
                1, StatusTask.NEW, new ArrayList<>());
        final int expectedSize = 1;

        epicTask.addSubTaskId(2);
        epicTask.addSubTaskId(2);
        assertEquals(expectedSize, epicTask.getSubTaskIds().size());
    }
    /*
     * Отказался от проверки: может ли добавить Эпик сам себя в список ПодЗадач.
     * Не стал добавлять такой функционал.
     * Все таки обо всех Эпиках знает менеджер он и будет проверять, иначе получится двойная проверка.
     */
}