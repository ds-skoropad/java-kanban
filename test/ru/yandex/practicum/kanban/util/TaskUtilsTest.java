package ru.yandex.practicum.kanban.util;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskUtilsTest {
    @Test
    void shouldBeCorrectCsvHead() {
        final String trueCsvHead = "id,type,name,status,description,epic";

        assertEquals(trueCsvHead, TaskUtils.getCsvHead());
    }

    @Test
    void shouldCorrectGiveCsvLineFromTask() {
        final Task task = new Task("Task1", "Description task1", 1, StatusTask.NEW);
        final String csvLine = "1,TASK,Task1,NEW,Description task1,";

        assertEquals(csvLine, TaskUtils.toCsvLine(task));
    }

    @Test
    void shouldCorrectGiveTaskFromCsvLine() {
        final Task task = new Task("Task1", "Description task1", 1, StatusTask.NEW);
        final String csvLine = "1,TASK,Task1,NEW,Description task1,";

        assertEquals(task, TaskUtils.fromCsvLine(csvLine));
    }
}