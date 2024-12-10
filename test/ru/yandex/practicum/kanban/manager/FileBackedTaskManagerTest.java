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
        super.beforeEach(new FileBackedTaskManager(tempFile, new InMemoryHistoryManager()));
    }

    @AfterEach
    void afterEach() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    @Override
    void addTask() {
        super.addTask();
    }

    @Test
    @Override
    void updateTask() {
        super.updateTask();
    }

    @Test
    @Override
    void getTask() {
        super.getTask();
    }

    @Test
    @Override
    void getEpic() {
        super.getEpic();
    }

    @Test
    @Override
    void getSub() {
        super.getSub();
    }

    @Test
    @Override
    void getTaskGroup() {
        super.getTaskGroup();
    }

    @Test
    @Override
    void getEpicGroup() {
        super.getEpicGroup();
    }

    @Test
    @Override
    void getSubGroup() {
        super.getSubGroup();
    }

    @Test
    @Override
    void getEpicSubTasks() {
        super.getEpicSubTasks();
    }

    @Test
    @Override
    void getPrioritizedTasks() {
        super.getPrioritizedTasks();
    }

    @Test
    @Override
    void clearTaskGroup() {
        super.clearTaskGroup();
    }

    @Test
    @Override
    void clearSubGroup() {
        super.clearSubGroup();
    }

    @Test
    @Override
    void clearEpicGroup() {
        super.clearEpicGroup();
    }

    @Test
    @Override
    void removeTask() {
        super.removeTask();
    }

    @Test
    @Override
    void removeSub() {
        super.removeSub();
    }

    @Test
    @Override
    void removeEpic() {
        super.removeEpic();
    }

    @Test
    @Override
    void nextIdIsCorrect() {
        super.nextIdIsCorrect();
    }

    @Test
    @Override
    void notAddSubIfMissingEpic() {
        super.notAddSubIfMissingEpic();
    }

    @Test
    @Override
    void linkBetweenSubAndEpicIsCorrect() {
        super.linkBetweenSubAndEpicIsCorrect();
    }

    @Test
    @Override
    void statusShouldBeNewForNewTask() {
        super.statusShouldBeNewForNewTask();
    }

    @Test
    @Override
    void statusShouldBeNewForEpic() {
        super.statusShouldBeNewForEpic();
    }

    @Test
    @Override
    void statusShouldBeDoneForEpic() {
        super.statusShouldBeDoneForEpic();
    }

    @Test
    @Override
    void statusShouldBeInProgressForEpic() {
        super.statusShouldBeInProgressForEpic();
    }

    @Test
    @Override
    void shouldBeCorrectUpdateTask() {
        super.shouldBeCorrectUpdateTask();
    }

    @Test
    @Override
    void shouldBeCorrectUpdateEpic() {
        super.shouldBeCorrectUpdateEpic();
    }

    @Test
    @Override
    void shouldBeCorrectUpdateSub() {
        super.shouldBeCorrectUpdateSub();
    }

    @Test
    @Override
    void shouldBeCorrectEpicTimesAfterAddSubs() {
        super.shouldBeCorrectEpicTimesAfterAddSubs();
    }

    @Test
    @Override
    void shouldBeCorrectEpicTimeAfterUpdateSub() {
        super.shouldBeCorrectEpicTimeAfterUpdateSub();
    }

    @Test
    @Override
    void shouldBeCorrectEpicTimesAfterRemoveSub() {
        super.shouldBeCorrectEpicTimesAfterRemoveSub();
    }

    @Test
    @Override
    void shouldBeCorrectEpicTimesAfterRemoveAllSubs() {
        super.shouldBeCorrectEpicTimesAfterRemoveAllSubs();
    }

    @Test
    @Override
    void shouldBeCorrectPrioritizedTasksAfterRemoveTask() {
        super.shouldBeCorrectPrioritizedTasksAfterRemoveTask();
    }

    @Test
    @Override
    void shouldBeCorrectPrioritizedTasksAfterRemoveSub() {
        super.shouldBeCorrectPrioritizedTasksAfterRemoveSub();
    }

    @Test
    @Override
    void shouldBeCorrectPrioritizedTasksAfterRemoveEpic() {
        super.shouldBeCorrectPrioritizedTasksAfterRemoveEpic();
    }

    @Test
    @Override
    void shouldBeExceptionValidateTimeForAddOverlayTask() {
        super.shouldBeExceptionValidateTimeForAddOverlayTask();
    }

    @Test
    @Override
    void shouldBeExceptionValidateTimeForAddOverlaySub() {
        super.shouldBeExceptionValidateTimeForAddOverlaySub();
    }

    @Test
    @Override
    void notShouldBeExceptionValidateTimeForAddOverlayEpic() {
        super.notShouldBeExceptionValidateTimeForAddOverlayEpic();
    }

    @Test
    @Override
    void shouldBeExceptionValidateTimeForUpdateOverlayTask() {
        super.shouldBeExceptionValidateTimeForUpdateOverlayTask();
    }

    @Test
    @Override
    void shouldBeExceptionValidateTimeForUpdateOverlaySub() {
        super.shouldBeExceptionValidateTimeForUpdateOverlaySub();
    }

    @Test
    @Override
    void notShouldBeExceptionValidateTimeForUpdateOverlayEpic() {
        super.notShouldBeExceptionValidateTimeForUpdateOverlayEpic();
    }

    @Test
    @Override
    void shouldBeExceptionOutOfRangeLeftIntervals() {
        super.shouldBeExceptionOutOfRangeLeftIntervals();
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

        fb1.addTask(new Task("Task 1", "Description"));
        fb1.addTask(new EpicTask("Epic 1", "Description"));
        fb1.addTask(new SubTask("Sub 1", "Description", 3, StatusTask.NEW, 2,
                Duration.ofMinutes(10), LocalDateTime.now()));

        final FileBackedTaskManager fb2 = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(fb1.getTaskGroup(), fb2.getTaskGroup());
        assertEquals(fb1.getSubGroup(), fb2.getSubGroup());
        assertEquals(fb1.getEpicGroup(), fb2.getEpicGroup());

        assertEquals(fb1.getEpicGroup().getFirst().getSubIds(),
                fb2.getEpicGroup().getFirst().getSubIds());
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
        final FileBackedTaskManager fb = new FileBackedTaskManager(ghostFile, new InMemoryHistoryManager());

        Assertions.assertThrows(ManagerSaveException.class, () -> fb.addTask(new Task()));
    }
}