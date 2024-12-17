package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        super.beforeEach(new InMemoryTaskManager());
    }
}