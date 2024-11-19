package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    FileBackedTaskManager fb;
    Path tempFile;

    final String[] fileContent = {
            "1,TASK,Task 1,NEW,Description,",
            "2,EPIC_TASK,Epic 1,NEW,Description,",
            "3,SUB_TASK,Sub 1,NEW,Description,2"};

    @BeforeEach
    public void beforeEach() throws IOException {
        tempFile = Files.createTempFile(null, ".tmp");
        fb = new FileBackedTaskManager(tempFile, new InMemoryHistoryManager());
    }

    @AfterEach
    public void afterEach() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void saveEmpty() throws IOException {
        fb.save();
        assertEquals(Files.size(tempFile), 0);
    }

    @Test
    void loadEmpty() {
        fb.loadFromFile(tempFile);
        assertTrue(fb.getTaskGroup().isEmpty());
        assertTrue(fb.getEpicTaskGroup().isEmpty());
        assertTrue(fb.getSubTaskGroup().isEmpty());
    }

    @Test
    void save() throws IOException {
        fb.addTask(new Task("Task 1", "Description"));
        fb.addTask(new EpicTask("Epic 1", "Description"));
        fb.addTask(new SubTask("Sub 1", "Description", 3, StatusTask.NEW, 2));

        ArrayList<String> data = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(tempFile.toFile(), StandardCharsets.UTF_8));
        while (reader.ready()) {
            data.add(reader.readLine());
        }
        reader.close();

        assertArrayEquals(data.toArray(), fileContent);
    }

    @Test
    void load() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile(), StandardCharsets.UTF_8));
        for (String s : fileContent) {
            writer.write(s);
            writer.newLine();
        }
        writer.close();

        fb.loadFromFile(tempFile);

        assertNotNull(fb.getTask(1));
        assertNotNull(fb.getEpicTask(2));
        assertNotNull(fb.getSubTask(3));
    }
}