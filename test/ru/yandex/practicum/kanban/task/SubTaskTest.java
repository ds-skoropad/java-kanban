package ru.yandex.practicum.kanban.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubTaskTest extends TaskTest {

    @BeforeEach
    @Override
    void beforeEach() {
        setTasks(new SubTask(), new SubTask());
    }

    @Test
    @Override
    void taskSameIfIdSame() {
        ((SubTask) task1).setEpicId(1);
        super.taskSameIfIdSame();
    }

    @Test
    @Override
    void taskDifferentIfIdDifferent() {
        super.taskDifferentIfIdDifferent();
    }
}