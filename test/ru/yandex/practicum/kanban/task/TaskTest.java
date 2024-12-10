package ru.yandex.practicum.kanban.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    Task task1;
    Task task2;

    void setTasks(Task task1, Task task2) {
        this.task1 = task1;
        this.task2 = task2;
    }

    @BeforeEach
    void beforeEach() {
        setTasks(new Task(), new Task());
    }

    // Все поля разные кроме id
    @Test
    void taskSameIfIdSame() {
        final int id = 1;

        task1.setId(id);
        task1.setTitle("Title1");
        task1.setDescription("Description1");
        task1.setStatus(StatusTask.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(10));

        task2.setId(id);
        task2.setTitle("Title2");
        task2.setDescription("Description2");
        task2.setStatus(StatusTask.DONE);
        task2.setStartTime(LocalDateTime.now().plusMinutes(20));
        task2.setDuration(Duration.ofMinutes(20));

        assertEquals(task1, task2);
    }

    // Все поля одинаковые кроме id
    @Test
    void taskDifferentIfIdDifferent() {
        final String title = "Title";
        final String description = "Description";
        final StatusTask status = StatusTask.NEW;
        final Duration duration = Duration.ofMinutes(10);
        final LocalDateTime now = LocalDateTime.now();

        task1.setId(1);
        task1.setTitle(title);
        task1.setDescription(description);
        task1.setStatus(status);
        task1.setStartTime(now);
        task1.setDuration(duration);

        task2.setId(2);
        task2.setTitle(title);
        task2.setDescription(description);
        task2.setStatus(status);
        task2.setStartTime(now);
        task2.setDuration(duration);

        assertNotEquals(task1, task2);
    }
}