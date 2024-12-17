package ru.yandex.practicum.kanban.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest extends TaskTest {

    @BeforeEach
    @Override
    void beforeEach() {
        setTasks(new EpicTask(), new EpicTask());
    }

    @Test
    @Override
    void taskSameIfIdSame() {
        super.taskSameIfIdSame();
    }

    @Test
    @Override
    void taskDifferentIfIdDifferent() {
        super.taskDifferentIfIdDifferent();
    }

    @Test
    void uniqueIdInListSubTask() {
        final EpicTask epic = new EpicTask();
        final int expectedSize = 1;
        final int subId = 2;

        epic.addSubId(subId);
        epic.addSubId(subId);
        assertEquals(expectedSize, epic.getSubIds().size());
    }
}