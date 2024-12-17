package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    static final int ID_TASK_1 = 1;
    static final int ID_TASK_2 = 2;
    static final int ID_EPIC_1 = 3;
    static final int ID_EPIC_2 = 4;
    static final int ID_SUB_1_EPIC_1 = 5;
    static final int ID_SUB_1_EPIC_2 = 6;
    static final int ID_SUB_2_EPIC_2 = 7;
    Task task1;
    Task task2;
    EpicTask epic1;
    EpicTask epic2;
    SubTask sub1Epic1;
    SubTask sub1Epic2;
    SubTask sub2Epic2;
    List<Task> tasks;
    List<Task> epics;
    List<Task> subs;
    List<Task> allTasks;
    LocalDateTime startTimeGeneral;

    /*
     * Три задачи без наложений
     * START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
     */

    static final int INTERVAL = 15;
    static final int TASK_1_START = 0;
    static final int SUB_1_EPIC_1_START = INTERVAL * 2;
    static final int SUB_1_EPIC_2_START = INTERVAL * 4;
    static final int SUB_2_EPIC_2_START = INTERVAL * 6;
    static final int TASK1_DURATION = INTERVAL;
    static final int SUB_1_EPIC_1_DURATION = INTERVAL;
    static final int SUB_1_EPIC_2_DURATION = INTERVAL;
    static final int SUB_2_EPIC_2_DURATION = INTERVAL * 2;

    void beforeEach(T manager) {
        this.manager = manager;
        startTimeGeneral = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));

        task1 = new Task("", "", ID_TASK_1);
        task2 = new Task("", "", ID_TASK_2);
        epic1 = new EpicTask("", "", ID_EPIC_1);
        epic2 = new EpicTask("", "", ID_EPIC_2);
        sub1Epic1 = new SubTask(ID_EPIC_1, "", "", ID_SUB_1_EPIC_1);
        sub1Epic2 = new SubTask(ID_EPIC_2, "", "", ID_SUB_1_EPIC_2);
        sub2Epic2 = new SubTask(ID_EPIC_2, "", "", ID_SUB_2_EPIC_2);

        tasks = List.of(task1, task2);
        epics = List.of(epic1, epic2);
        subs = List.of(sub1Epic1, sub1Epic2, sub2Epic2);
        allTasks = List.of(task1, task2, epic1, epic2, sub1Epic1, sub1Epic2, sub2Epic2);

        task1.setStartTime(startTimeGeneral.plusMinutes(TASK_1_START));
        task1.setDuration(Duration.ofMinutes(TASK1_DURATION));
        sub1Epic1.setStartTime(startTimeGeneral.plusMinutes(SUB_1_EPIC_1_START));
        sub1Epic1.setDuration(Duration.ofMinutes(SUB_1_EPIC_1_DURATION));
        sub1Epic2.setStartTime(startTimeGeneral.plusMinutes(SUB_1_EPIC_2_START));
        sub1Epic2.setDuration(Duration.ofMinutes(SUB_1_EPIC_2_DURATION));
        sub2Epic2.setStartTime(startTimeGeneral.plusMinutes(SUB_2_EPIC_2_START));
        sub2Epic2.setDuration(Duration.ofMinutes(SUB_2_EPIC_2_DURATION));

        allTasks.forEach(manager::addTask);
    }

    // Тесты методов интерфейса TaskManager

    @Test
    void addTask() {
        assertEquals(tasks, manager.getTaskGroup());
        assertEquals(subs, manager.getSubGroup());
        assertEquals(epics, manager.getEpicGroup());
    }

    @Test
    void updateTask() {
        final String expectedTitle = "New title for task 1";
        final Task newTask = new Task();

        newTask.setId(ID_TASK_1);
        newTask.setTitle(expectedTitle);

        manager.updateTask(newTask);

        assertTrue(manager.getTask(ID_TASK_1).isPresent());
        final String actualTitle = manager.getTask(ID_TASK_1).get().getTitle();

        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    void getTask() {
        final List<Task> actual = Arrays.asList(manager.getTask(ID_TASK_1).orElse(null),
                manager.getTask(ID_TASK_2).orElse(null));

        assertEquals(tasks, actual);
    }

    @Test
    void getEpic() {
        final List<EpicTask> actual = Arrays.asList(manager.getEpic(ID_EPIC_1).orElse(null),
                manager.getEpic(ID_EPIC_2).orElse(null));

        assertEquals(epics, actual);
    }

    @Test
    void getSub() {
        final List<SubTask> actual = Arrays.asList(manager.getSub(ID_SUB_1_EPIC_1).orElse(null),
                manager.getSub(ID_SUB_1_EPIC_2).orElse(null),
                manager.getSub(ID_SUB_2_EPIC_2).orElse(null));

        assertEquals(subs, actual);
    }

    @Test
    void getTaskGroup() {
        final List<Task> taskGroup = manager.getTaskGroup();

        assertNotNull(taskGroup);
        assertEquals(tasks, taskGroup);
    }

    @Test
    void getEpicGroup() {
        final List<EpicTask> epicGroup = manager.getEpicGroup();

        assertNotNull(epicGroup);
        assertEquals(epics, epicGroup);
    }

    @Test
    void getSubGroup() {
        final List<SubTask> subGroup = manager.getSubGroup();

        assertNotNull(subGroup);
        assertEquals(subs, subGroup);
    }

    @Test
    void getEpicSubTasks() {
        assertEquals(List.of(sub1Epic1), manager.getEpicSubTasks(ID_EPIC_1));
        assertEquals(List.of(sub1Epic2, sub2Epic2), manager.getEpicSubTasks(ID_EPIC_2));
    }

    @Test
    void getPrioritizedTasks() {
        // Должен вернуть список без эпиков, без task2 (т.к. он без установленного времени)
        assertEquals(List.of(task1, sub1Epic1, sub1Epic2, sub2Epic2), manager.getPrioritizedTasks());
    }


    @Test
    void clearTaskGroup() {
        manager.clearTaskGroup();
        assertTrue(manager.getTaskGroup().isEmpty());
    }

    @Test
    void clearSubGroup() {
        manager.clearSubGroup();
        assertTrue(manager.getSubGroup().isEmpty());
    }

    @Test
    void clearEpicGroup() {
        manager.clearEpicGroup();
        assertTrue(manager.getEpicGroup().isEmpty());
    }

    @Test
    void removeTask() {
        manager.removeTask(ID_TASK_1);
        assertEquals(List.of(task2), manager.getTaskGroup());
    }

    @Test
    void removeSub() {
        manager.removeSub(ID_SUB_1_EPIC_1);
        assertEquals(List.of(sub1Epic2, sub2Epic2), manager.getSubGroup());
    }

    @Test
    void removeEpic() {
        manager.removeEpic(ID_EPIC_1);
        assertEquals(List.of(epic2), manager.getEpicGroup());
        assertEquals(List.of(sub1Epic2, sub2Epic2), manager.getSubGroup());
    }

    // Дополнительные тесты

    // Корректная итерация id
    @Test
    void nextIdIsCorrect() {
        assertEquals(ID_TASK_1, manager.getTaskGroup().getFirst().getId());
        assertEquals(ID_TASK_2, manager.getTaskGroup().getLast().getId());
    }

    // Подзадача не добавляется в случае отсутствия связанного эпика
    @Test
    void notAddSubIfMissingEpic() {
        final int nonExistentEpicId = 100;

        manager.addTask(new SubTask(nonExistentEpicId));
        assertEquals(subs, manager.getSubGroup());
    }

    // Корректная связь между эпиком и подзадачами
    @Test
    void linkBetweenSubAndEpicIsCorrect() {
        final List<Integer> expectedListIdSubs = List.of(ID_SUB_1_EPIC_2, ID_SUB_2_EPIC_2);
        final Optional<EpicTask> epic = manager.getEpic(ID_EPIC_2);

        assertTrue(epic.isPresent());
        final List<Integer> actualListIdSubs = epic.get().getSubIds();
        assertEquals(expectedListIdSubs, actualListIdSubs);

        assertTrue(manager.getSub(ID_SUB_1_EPIC_2).isPresent());
        int actualIdEpicInSub = manager.getSub(ID_SUB_1_EPIC_2).get().getEpicId();
        assertEquals(ID_EPIC_2, actualIdEpicInSub);
    }

    // Статус всех новых задач NEW
    @Test
    void statusShouldBeNewForNewTask() {
        assertTrue(manager.getTask(ID_TASK_1).isPresent());
        assertTrue(manager.getTask(ID_TASK_2).isPresent());
        assertTrue(manager.getEpic(ID_EPIC_1).isPresent());
        assertTrue(manager.getEpic(ID_EPIC_2).isPresent());
        assertTrue(manager.getSub(ID_SUB_1_EPIC_1).isPresent());
        assertTrue(manager.getSub(ID_SUB_1_EPIC_2).isPresent());
        assertTrue(manager.getSub(ID_SUB_2_EPIC_2).isPresent());

        assertEquals(StatusTask.NEW, manager.getTask(ID_TASK_1).get().getStatus());
        assertEquals(StatusTask.NEW, manager.getTask(ID_TASK_2).get().getStatus());
        assertEquals(StatusTask.NEW, manager.getEpic(ID_EPIC_1).get().getStatus());
        assertEquals(StatusTask.NEW, manager.getEpic(ID_EPIC_2).get().getStatus());
        assertEquals(StatusTask.NEW, manager.getSub(ID_SUB_1_EPIC_1).get().getStatus());
        assertEquals(StatusTask.NEW, manager.getSub(ID_SUB_1_EPIC_2).get().getStatus());
        assertEquals(StatusTask.NEW, manager.getSub(ID_SUB_2_EPIC_2).get().getStatus());
    }

    // Статус эпика NEW: если нет подзадач или все они имеют статус NEW
    @Test
    void statusShouldBeNewForEpic() {
        manager.updateTask(copyTaskWithNewStatus(sub1Epic1, StatusTask.DONE));
        manager.updateTask(copyTaskWithNewStatus(sub1Epic2, StatusTask.DONE));
        manager.updateTask(copyTaskWithNewStatus(sub2Epic2, StatusTask.DONE));
        manager.updateTask(copyTaskWithNewStatus(sub1Epic1, StatusTask.NEW));
        manager.updateTask(copyTaskWithNewStatus(sub1Epic2, StatusTask.NEW));
        manager.updateTask(copyTaskWithNewStatus(sub2Epic2, StatusTask.NEW));

        assertTrue(manager.getEpic(ID_EPIC_1).isPresent());
        assertTrue(manager.getEpic(ID_EPIC_2).isPresent());

        assertEquals(StatusTask.NEW, manager.getEpic(ID_EPIC_1).get().getStatus());
        assertEquals(StatusTask.NEW, manager.getEpic(ID_EPIC_2).get().getStatus());

        // Изменение статуса эпика при удалении единственной подзадачи
        manager.updateTask(copyTaskWithNewStatus(sub1Epic1, StatusTask.DONE));
        assertEquals(StatusTask.DONE, manager.getEpic(ID_EPIC_1).get().getStatus());
        manager.removeSub(ID_SUB_1_EPIC_1);

        assertEquals(StatusTask.NEW, manager.getEpic(ID_EPIC_1).get().getStatus());
    }

    // Статус эпика DONE: если все подзадачи имеют статус DONE.
    @Test
    void statusShouldBeDoneForEpic() {
        manager.updateTask(copyTaskWithNewStatus(sub1Epic1, StatusTask.DONE));
        manager.updateTask(copyTaskWithNewStatus(sub1Epic2, StatusTask.DONE));
        manager.updateTask(copyTaskWithNewStatus(sub2Epic2, StatusTask.DONE));

        assertTrue(manager.getEpic(ID_EPIC_1).isPresent());
        assertTrue(manager.getEpic(ID_EPIC_2).isPresent());

        assertEquals(StatusTask.DONE, manager.getEpic(ID_EPIC_1).get().getStatus());
        assertEquals(StatusTask.DONE, manager.getEpic(ID_EPIC_2).get().getStatus());
    }

    // Статус эпика IN_PROGRESS: если статусы подзадач различаются
    @Test
    void statusShouldBeInProgressForEpic() {
        assertTrue(manager.getEpic(ID_EPIC_1).isPresent());
        assertTrue(manager.getEpic(ID_EPIC_2).isPresent());

        manager.updateTask(copyTaskWithNewStatus(sub1Epic1, StatusTask.IN_PROGRESS));
        assertEquals(StatusTask.IN_PROGRESS, manager.getEpic(ID_EPIC_1).get().getStatus());

        manager.updateTask(copyTaskWithNewStatus(sub1Epic2, StatusTask.IN_PROGRESS));
        manager.updateTask(copyTaskWithNewStatus(sub2Epic2, StatusTask.DONE));
        assertEquals(StatusTask.IN_PROGRESS, manager.getEpic(ID_EPIC_2).get().getStatus());

        manager.updateTask(copyTaskWithNewStatus(sub1Epic2, StatusTask.DONE));
        manager.updateTask(copyTaskWithNewStatus(sub2Epic2, StatusTask.NEW));
        assertEquals(StatusTask.IN_PROGRESS, manager.getEpic(ID_EPIC_2).get().getStatus());

    }

    // Должно быть корректное обновление задачи
    @Test
    void shouldBeCorrectUpdateTask() {
        final String newTitle = "New title";
        final String newDescription = "New description";
        final StatusTask newStatus = StatusTask.DONE;
        final Duration newDuration = Duration.ofMinutes(INTERVAL * 5);
        final LocalDateTime newStartTime = startTimeGeneral.plusMinutes(INTERVAL * 10);

        // Task - обновляются все поля
        manager.updateTask(new Task(newTitle, newDescription, ID_TASK_1, newStatus, newDuration, newStartTime));
        assertTrue(manager.getTask(ID_TASK_1).isPresent());
        final Task updateTask = manager.getTask(ID_TASK_1).get();

        assertAll("Task - comparing fields",
                () -> assertEquals(newTitle, updateTask.getTitle(), "title"),
                () -> assertEquals(newDescription, updateTask.getDescription(), "description"),
                () -> assertEquals(newStatus, updateTask.getStatus(), "status"),
                () -> assertEquals(newDuration, updateTask.getDuration(), "duration"),
                () -> assertEquals(newStartTime, updateTask.getStartTime().orElse(null), "startTime")
        );
    }

    // Должно быть корректное обновление эпика
    @Test
    void shouldBeCorrectUpdateEpic() {
        final String newTitle = "New title";
        final String newDescription = "New description";
        final StatusTask newStatus = StatusTask.DONE;
        final Duration newDuration = Duration.ofMinutes(INTERVAL * 5);
        final LocalDateTime newStartTime = startTimeGeneral.plusMinutes(INTERVAL * 10);

        // Epic - обновляется только title и description
        final List<Integer> newSubIds = List.of(1000, 2000, 3000);
        manager.updateTask(new EpicTask(newTitle, newDescription, ID_EPIC_1, newStatus, newDuration, newStartTime, newSubIds
        ));
        assertTrue(manager.getEpic(ID_EPIC_1).isPresent());
        final EpicTask updateEpic = manager.getEpic(ID_EPIC_1).get();

        assertAll("EpicTask - comparing fields",
                () -> assertEquals(newTitle, updateEpic.getTitle(), "title"),
                () -> assertEquals(newDescription, updateEpic.getDescription(), "description"),
                () -> assertNotEquals(newStatus, updateEpic.getStatus(), "status"),
                () -> assertNotEquals(newDuration, updateEpic.getDuration(), "duration"),
                () -> assertNotEquals(newStartTime, updateEpic.getStartTime().orElse(null), "startTime"),
                () -> assertNotEquals(newSubIds, updateEpic.getSubIds(), "subsIds")
        );
    }

    // Должно быть корректное обновление подзадачи
    @Test
    void shouldBeCorrectUpdateSub() {
        final String newTitle = "New title";
        final String newDescription = "New description";
        final StatusTask newStatus = StatusTask.DONE;
        final Duration newDuration = Duration.ofMinutes(INTERVAL * 5);
        final LocalDateTime newStartTime = startTimeGeneral.plusMinutes(INTERVAL * 10);

        // SubTask - обновляются все поля кроме EpicId
        final int newEpicId = 1000;
        manager.updateTask(new SubTask(newEpicId, newTitle, newDescription, ID_SUB_2_EPIC_2, newStatus, newDuration,
                newStartTime));
        assertTrue(manager.getSub(ID_SUB_2_EPIC_2).isPresent());
        final SubTask updateSub = manager.getSub(ID_SUB_2_EPIC_2).get();

        assertAll("SubTask - comparing fields",
                () -> assertEquals(newTitle, updateSub.getTitle(), "title"),
                () -> assertEquals(newDescription, updateSub.getDescription(), "description"),
                () -> assertEquals(newStatus, updateSub.getStatus(), "status"),
                () -> assertEquals(newDuration, updateSub.getDuration(), "duration"),
                () -> assertEquals(newStartTime, updateSub.getStartTime().orElse(null), "startTime"),
                () -> assertNotEquals(newEpicId, updateSub.getEpicId(), "epicId")
        );
    }

    // Должно быть корректное время эпиков после добавления подзадач
    @Test
    void shouldBeCorrectEpicTimesAfterAddSubs() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        // Epic 1 (1x Sub)
        assertTrue(manager.getEpic(ID_EPIC_1).isPresent());
        final EpicTask actualEpic1 = manager.getEpic(ID_EPIC_1).get();

        assertTrue(actualEpic1.getStartTime().isPresent());
        final LocalDateTime actualEpic1StartTime = actualEpic1.getStartTime().get();
        assertEquals(startTimeGeneral.plusMinutes(SUB_1_EPIC_1_START), actualEpic1StartTime);

        assertTrue(actualEpic1.getEndTime().isPresent());
        final LocalDateTime actualEpic1EndTime = actualEpic1.getEndTime().get();
        assertEquals(startTimeGeneral.plusMinutes(SUB_1_EPIC_1_START + SUB_1_EPIC_1_DURATION), actualEpic1EndTime);

        assertEquals(Duration.ofMinutes(SUB_1_EPIC_1_DURATION), actualEpic1.getDuration());

        // Epic 2 (2x Subs)
        assertTrue(manager.getEpic(ID_EPIC_2).isPresent());
        final EpicTask actualEpic2 = manager.getEpic(ID_EPIC_2).get();

        assertTrue(actualEpic2.getStartTime().isPresent());
        final LocalDateTime actualEpic2StartTime = actualEpic2.getStartTime().get();
        assertEquals(startTimeGeneral.plusMinutes(SUB_1_EPIC_2_START), actualEpic2StartTime);

        assertTrue(actualEpic2.getEndTime().isPresent());
        final LocalDateTime actualEpic2EndTime = actualEpic2.getEndTime().get();
        assertEquals(startTimeGeneral.plusMinutes(SUB_2_EPIC_2_START + SUB_2_EPIC_2_DURATION), actualEpic2EndTime);

        assertEquals(Duration.ofMinutes(SUB_1_EPIC_2_DURATION + SUB_2_EPIC_2_DURATION), actualEpic2.getDuration());
    }

    // Должно быть корректное время эпика после обновления подзадачи
    @Test
    void shouldBeCorrectEpicTimeAfterUpdateSub() {
        final int plusMin = 5;
        SubTask sub = new SubTask();
        sub.setId(ID_SUB_1_EPIC_1);
        sub.setStartTime(startTimeGeneral.plusMinutes(SUB_1_EPIC_1_START + plusMin));
        sub.setDuration(Duration.ofMinutes(SUB_1_EPIC_1_DURATION + plusMin));
        manager.updateTask(sub);

        assertTrue(manager.getEpic(ID_EPIC_1).isPresent());
        final EpicTask actualEpic1 = manager.getEpic(ID_EPIC_1).get();

        assertTrue(actualEpic1.getStartTime().isPresent());
        final LocalDateTime actualEpic1StartTime = actualEpic1.getStartTime().get();
        assertEquals(startTimeGeneral.plusMinutes(SUB_1_EPIC_1_START + plusMin), actualEpic1StartTime);

        assertTrue(actualEpic1.getEndTime().isPresent());
        final LocalDateTime actualEpic1EndTime = actualEpic1.getEndTime().get();
        assertEquals(startTimeGeneral.plusMinutes(SUB_1_EPIC_1_START + SUB_1_EPIC_1_DURATION + plusMin * 2),
                actualEpic1EndTime);

        assertEquals(Duration.ofMinutes(SUB_1_EPIC_1_DURATION + plusMin), actualEpic1.getDuration());
    }

    // Должно быть корректное время эпиков после удаления подзадачи
    @Test
    void shouldBeCorrectEpicTimesAfterRemoveSub() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        manager.removeSub(ID_SUB_2_EPIC_2);

        assertTrue(manager.getEpic(ID_EPIC_2).isPresent());
        final EpicTask actualEpic2 = manager.getEpic(ID_EPIC_2).get();

        assertTrue(actualEpic2.getStartTime().isPresent());
        final LocalDateTime actualEpic2StartTime = actualEpic2.getStartTime().get();
        assertEquals(startTimeGeneral.plusMinutes(SUB_1_EPIC_2_START), actualEpic2StartTime);

        assertTrue(actualEpic2.getEndTime().isPresent());
        final LocalDateTime actualEpic2EndTime = actualEpic2.getEndTime().get();
        assertEquals(startTimeGeneral.plusMinutes(SUB_1_EPIC_2_START + SUB_1_EPIC_2_DURATION), actualEpic2EndTime);

        assertEquals(Duration.ofMinutes(SUB_1_EPIC_2_DURATION), actualEpic2.getDuration());
    }

    // Должно быть корректное время у эпиков после удаления всех подзадач
    @Test
    void shouldBeCorrectEpicTimesAfterRemoveAllSubs() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        manager.removeSub(ID_SUB_1_EPIC_2);
        manager.removeSub(ID_SUB_2_EPIC_2);

        assertTrue(manager.getEpic(ID_EPIC_2).isPresent());
        final EpicTask actualEpic2 = manager.getEpic(ID_EPIC_2).get();

        assertTrue(actualEpic2.getStartTime().isEmpty());
        assertTrue(actualEpic2.getEndTime().isEmpty());
        assertEquals(Duration.ZERO, actualEpic2.getDuration());
    }

    // Должен быть корректный список приоритетных задач после удаления задачи
    @Test
    void shouldBeCorrectPrioritizedTasksAfterRemoveTask() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        manager.removeTask(ID_TASK_1);
        assertEquals(subs, manager.getPrioritizedTasks());
    }

    // Должен быть корректный список приоритетных задач после удаления подзадачи
    @Test
    void shouldBeCorrectPrioritizedTasksAfterRemoveSub() {
        manager.removeSub(ID_SUB_1_EPIC_1);
        assertEquals(List.of(task1, sub1Epic2, sub2Epic2), manager.getPrioritizedTasks());
    }

    // Должен быть корректный список приоритетных задач после удаления эпика и его подзадач
    @Test
    void shouldBeCorrectPrioritizedTasksAfterRemoveEpic() {
        manager.removeEpic(ID_EPIC_2);
        assertEquals(List.of(task1, sub1Epic1), manager.getPrioritizedTasks());
    }

    // Должно быть исключение валидации времени при добавлении задачи с наложением времени
    @Test
    void shouldBeExceptionValidateTimeForAddOverlayTask() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        final Task newTask = new Task();
        newTask.setStartTime(startTimeGeneral.plusMinutes(SUB_1_EPIC_1_START));
        newTask.setDuration(Duration.ofMinutes(SUB_1_EPIC_1_DURATION));

        Assertions.assertThrows(ManagerSaveException.class, () -> manager.addTask(newTask));
    }

    // Должно быть исключение валидации времени при добавлении подзадачи с наложением времени
    @Test
    void shouldBeExceptionValidateTimeForAddOverlaySub() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        final SubTask newSub = new SubTask(ID_EPIC_1);
        newSub.setStartTime(startTimeGeneral.plusMinutes(TASK_1_START));
        newSub.setDuration(Duration.ofMinutes(TASK1_DURATION));

        Assertions.assertThrows(ManagerSaveException.class, () -> manager.addTask(newSub));
    }

    /*
     * Не должно быть исключения валидации времени при добавлении эпика с наложением времени
     * При добавлении эпика все поля кроме title и description сбрасываются
     */
    @Test
    void notShouldBeExceptionValidateTimeForAddOverlayEpic() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        final EpicTask newEpic = new EpicTask();
        newEpic.setStartTime(startTimeGeneral.plusMinutes(TASK_1_START));
        newEpic.setDuration(Duration.ofMinutes(TASK1_DURATION));

        Assertions.assertDoesNotThrow(() -> manager.addTask(newEpic));
    }

    // Должно быть исключение при обновлении задачи с наложением времени (без учета ее старого времени)
    @Test
    void shouldBeExceptionValidateTimeForUpdateOverlayTask() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        final Task newTask = new Task();
        newTask.setId(ID_TASK_1);
        newTask.setStartTime(startTimeGeneral.plusMinutes(SUB_1_EPIC_1_START));
        newTask.setDuration(Duration.ofMinutes(SUB_1_EPIC_1_DURATION));

        Assertions.assertThrows(ManagerSaveException.class, () -> manager.updateTask(newTask));
    }

    // Должно быть исключение при обновлении подзадачи с наложением времени (без учета ее старого времени)
    @Test
    void shouldBeExceptionValidateTimeForUpdateOverlaySub() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        final SubTask newSub = new SubTask(ID_EPIC_1);
        newSub.setId(ID_SUB_1_EPIC_1);
        newSub.setStartTime(startTimeGeneral.plusMinutes(TASK_1_START));
        newSub.setDuration(Duration.ofMinutes(TASK1_DURATION));

        Assertions.assertThrows(ManagerSaveException.class, () -> manager.updateTask(newSub));
    }

    // Не должно быть исключения при обновлении эпика с наложением времени (обновляется только title и description)
    @Test
    void notShouldBeExceptionValidateTimeForUpdateOverlayEpic() {
        // START + 0m <task1 - 15m> ... 30m <sub1Epic1 - 15m> ... 60m <sub1Epic2 - 15m> ... 90m <sub2Epic2 - 30m> 120m
        final EpicTask newEpic = new EpicTask();
        newEpic.setId(ID_EPIC_1);
        newEpic.setStartTime(startTimeGeneral.plusMinutes(TASK_1_START));
        newEpic.setDuration(Duration.ofMinutes(TASK1_DURATION));

        Assertions.assertDoesNotThrow(() -> manager.updateTask(newEpic));
    }

    // Должно быть исключение при выходе за границы интервалов
    @Test
    void shouldBeExceptionOutOfRangeLeftIntervals() {
        final Task leftRangeTask = new Task();
        leftRangeTask.setStartTime(startTimeGeneral.minusDays(1));
        leftRangeTask.setDuration(Duration.ofMinutes(TASK1_DURATION));

        Assertions.assertThrows(ManagerSaveException.class, () -> manager.addTask(leftRangeTask));

        final Task rightRangeTask = new Task();
        rightRangeTask.setStartTime(startTimeGeneral.plusYears(1));
        rightRangeTask.setDuration(Duration.ofMinutes(TASK1_DURATION));

        Assertions.assertThrows(ManagerSaveException.class, () -> manager.addTask(rightRangeTask));
    }

    // Вспомогательный метод для тестов: возвращает копию задачи с измененным статусом
    private Task copyTaskWithNewStatus(Task task, StatusTask status) {
        if (task == null) return null;

        return switch (task.getType()) {
            case TASK -> new Task(task.getTitle(), task.getDescription(), task.getId(), status,
                    task.getDuration(), task.getStartTime().orElse(null));
            case EPIC_TASK -> {
                List<Integer> subIds = ((EpicTask) task).getSubIds();
                yield new EpicTask(task.getTitle(), task.getDescription(), task.getId(), status,
                        task.getDuration(), task.getStartTime().orElse(null), subIds);
            }
            case SUB_TASK -> {
                int epicId = ((SubTask) task).getEpicId();
                yield new SubTask(epicId, task.getTitle(), task.getDescription(), task.getId(), status,
                        task.getDuration(), task.getStartTime().orElse(null));
            }
        };
    }
}