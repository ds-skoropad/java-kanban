package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    @Test
    void shouldCorrectSaveAndLoadFromFile() throws IOException {
        final Path tempFile = Files.createTempFile(null, ".tmp");
        final FileBackedTaskManager fb1 = new FileBackedTaskManager(tempFile);

        fb1.addTask(new Task("Task 1", "Description"));
        fb1.addTask(new EpicTask("Epic 1", "Description"));
        fb1.addTask(new SubTask("Sub 1", "Description", 3, StatusTask.NEW, 2));

        FileBackedTaskManager fb2 = FileBackedTaskManager.loadFromFile(tempFile);

        assertArrayEquals(fb1.getTaskGroup().toArray(), fb2.getTaskGroup().toArray());
        assertArrayEquals(fb1.getSubTaskGroup().toArray(), fb2.getSubTaskGroup().toArray());

        final List<EpicTask> epic1 = fb1.getEpicTaskGroup();
        final List<EpicTask> epic2 = fb2.getEpicTaskGroup();

        assertEquals(epic1.size(), epic2.size());
        for (int i = 0; i < epic1.size(); i++) {
            assertEquals(epic1.get(i), epic2.get(i));
            assertArrayEquals(epic1.get(i).getSubTaskIds().toArray(), epic2.get(i).getSubTaskIds().toArray());
        }

        Files.deleteIfExists(tempFile);
    }
}