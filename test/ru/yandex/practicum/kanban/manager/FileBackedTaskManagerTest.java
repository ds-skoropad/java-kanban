package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    Path tempFile;

    @BeforeEach
    void beforeEach() throws IOException {
        tempFile = Files.createTempFile(null, ".tmp");
    }

    @AfterEach
    void afterEach() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldCorrectLoadFromEmptyFile() {
        final FileBackedTaskManager fb = new FileBackedTaskManager(tempFile);

        assertEquals(1, fb.nextId);
        assertTrue(fb.getTaskGroup().isEmpty());
        assertTrue(fb.getSubTaskGroup().isEmpty());
        assertTrue(fb.getEpicTaskGroup().isEmpty());
    }

    @Test
    void shouldCorrectSaveAndLoadFromFile() {
        final FileBackedTaskManager fb1 = new FileBackedTaskManager(tempFile);

        fb1.addTask(new Task("Task 1", "Description"));
        fb1.addTask(new EpicTask("Epic 1", "Description"));
        fb1.addTask(new SubTask("Sub 1", "Description", 3, StatusTask.NEW, 2));

        FileBackedTaskManager fb2 = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(fb1.getTaskGroup(), fb2.getTaskGroup());
        assertEquals(fb1.getSubTaskGroup(), fb2.getSubTaskGroup());
        assertEquals(fb1.getEpicTaskGroup(), fb2.getEpicTaskGroup());

        assertEquals(fb1.getEpicTaskGroup().getFirst().getSubTaskIds(),
                fb2.getEpicTaskGroup().getFirst().getSubTaskIds());
    }
}