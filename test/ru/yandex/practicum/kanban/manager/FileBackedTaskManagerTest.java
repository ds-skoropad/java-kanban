package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {
    Path tempFile;

    @BeforeEach
    void beforeEach() throws IOException {
        tempFile = Files.createTempFile(null, ".tmp");
        super.beforeEach(new FileBackedTaskManager(tempFile));
    }

    @AfterEach
    void afterEach() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    // Дополнительные тесты менеджера файловой поддержки

    // Должен корректно загружать с пустого файла
    @Test
    void shouldBeCorrectLoadFromEmptyFile() {
        final FileBackedTaskManager fb = new FileBackedTaskManager(tempFile);

        assertEquals(1, fb.nextId);
        assertTrue(fb.getTaskGroup().isEmpty());
        assertTrue(fb.getSubGroup().isEmpty());
        assertTrue(fb.getEpicGroup().isEmpty());
    }

    // Должен корректно сохранять в файл и загружать с файла
    @Test
    void shouldBeCorrectSaveAndLoadFromFile() {
        final FileBackedTaskManager fb1 = new FileBackedTaskManager(tempFile);
        final LocalDateTime nowDateTime = LocalDateTime.now();

        fb1.addTask(new Task("Task 1", "Description"));
        fb1.addTask(new Task("Task 2", "Description", 2, StatusTask.NEW,
                Duration.ofMinutes(10), nowDateTime.plusMinutes(30)));
        fb1.addTask(new EpicTask("Epic 1", "Description"));
        fb1.addTask(new SubTask(3, "Sub 1", "Description", 4, StatusTask.NEW,
                Duration.ofMinutes(10), nowDateTime));

        final FileBackedTaskManager fb2 = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(fb1.getTaskGroup(), fb2.getTaskGroup());
        assertEquals(fb1.getSubGroup(), fb2.getSubGroup());
        assertEquals(fb1.getEpicGroup(), fb2.getEpicGroup());

        assertEquals(fb1.getEpicGroup().getFirst().getSubIds(),
                fb2.getEpicGroup().getFirst().getSubIds());

        assertEquals(fb1.getPrioritizedTasks(), fb2.getPrioritizedTasks());
    }

    // Должно быть исключение при загрузке с несуществующего файла
    @Test
    void shouldBeExceptionForLoadFromGhostFile() {
        final Path ghostFile = Path.of("/tmp/no/such/place");

        Assertions.assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(ghostFile));
    }

    // Должно быть исключение при сохранении в несуществующий файл
    @Test
    void shouldBeExceptionForSaveToGhostFile() {
        final Path ghostFile = Path.of("/tmp/no/such/place");
        final FileBackedTaskManager fb = new FileBackedTaskManager(ghostFile);

        Assertions.assertThrows(ManagerSaveException.class, () -> fb.addTask(new Task()));
    }
}