package ru.yandex.practicum.kanban.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.ManagerSaveException;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskUtilsTest {
    @Test
    void shouldBeCorrectCsvHead() {
        final String trueCsvHead = "id,type,name,status,description,epic,duration,start";

        assertEquals(trueCsvHead, TaskUtils.getCsvHead());
    }

    @Test
    void shouldBeCorrectGiveCsvLineFromTask() {
        final LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(10000), ZoneOffset.UTC);
        final Task task = new Task("Task1", "Description task1", 1, StatusTask.NEW, Duration.ZERO, now);
        final String csvLine = "1,TASK,Task1,NEW,Description task1,,0,10000";

        assertEquals(csvLine, TaskUtils.toCsvLine(task));
    }

    @Test
    void shouldBeCorrectGiveTaskFromCsvLine() {
        final LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(10000), ZoneOffset.UTC);
        final Task task = new Task("Task1", "Description task1", 1, StatusTask.NEW, Duration.ZERO, now);
        final String csvLine = "1,TASK,Task1,NEW,Description task1,,0,10000";

        assertEquals(task, TaskUtils.fromCsvLine(csvLine));
    }

    // CSV head: id,type,name,status,description,epic,duration,start
    @Test
    void notShouldBeExceptionForValidLine() {
        final String validLine = "1,TASK,name,NEW,description,1,1,1";

        Assertions.assertDoesNotThrow(() -> TaskUtils.fromCsvLine(validLine));
    }

    @Test
    void shouldBeExceptionForNullOrEmptyLine() {
        Assertions.assertThrows(ManagerSaveException.class, () -> TaskUtils.fromCsvLine(null));
        Assertions.assertThrows(ManagerSaveException.class, () -> TaskUtils.fromCsvLine(""));
    }

    @Test
    void shouldBeExceptionForInvalidNumberFields() {
        final String invalidLine = "name,status,description";

        Assertions.assertThrows(ManagerSaveException.class, () -> TaskUtils.fromCsvLine(invalidLine));
    }

    @Test
    void shouldBeExceptionForInvalidId() {
        final String invalidLine = "[NOT_NUMBER],TASK,name,status,description,1,1,1";

        Assertions.assertThrows(ManagerSaveException.class, () -> TaskUtils.fromCsvLine(invalidLine));
    }

    @Test
    void shouldBeExceptionForInvalidType() {
        final String invalidLine = "1,[INVALID_TYPE],name,status,description,1,1,1";

        Assertions.assertThrows(ManagerSaveException.class, () -> TaskUtils.fromCsvLine(invalidLine));
    }

    @Test
    void shouldBeExceptionForInvalidEpicId() {
        final String invalidLine = "1,TASK,name,status,description,[NOT_NUMBER],1,1";

        Assertions.assertThrows(ManagerSaveException.class, () -> TaskUtils.fromCsvLine(invalidLine));
    }

    @Test
    void shouldBeExceptionForInvalidDuration() {
        final String invalidLine = "1,TASK,name,status,description,1,[NOT_NUMBER],1";

        Assertions.assertThrows(ManagerSaveException.class, () -> TaskUtils.fromCsvLine(invalidLine));
    }

    @Test
    void shouldBeExceptionForInvalidStartTime() {
        final String invalidLine = "1,TASK,name,status,description,1,1,[NOT_NUMBER]";

        Assertions.assertThrows(ManagerSaveException.class, () -> TaskUtils.fromCsvLine(invalidLine));
    }
}