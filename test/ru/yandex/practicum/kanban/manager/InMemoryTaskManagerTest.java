/*
 * Тестирование далось сложновато.
 * Проверял тесты путем изменения методов класса который они тестируют.
 * Вывод старался сделать на анг.
 */

package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {
    static final int NUMBER_TASKS = 2;
    static final int NUMBER_EPIC_TASKS = 2;
    static final int NUMBER_SUB_TASKS = 3;
    static final int NUMBER_EPIC1_SUB_TASKS = 1;
    static final int NUMBER_EPIC2_SUB_TASKS = 2;
    static HistoryManager historyManager;
    static TaskManager taskManager;
    static Task task1;
    static Task task2;
    static EpicTask epic1;
    static EpicTask epic2;
    static SubTask sub1InEpic1;
    static SubTask sub1InEpic2;
    static SubTask sub2InEpic2;

    // Возвращает новую ПодЗадачу на основе переданной с измененным статусом
    static SubTask getCopySubTaskWithNewStatus(SubTask subTask, StatusTask statusTask) {
        if (subTask == null) return null;
        return new SubTask(subTask.getTitle(), subTask.getDescription(), subTask.getId(), statusTask,
                subTask.getEpicTaskId());
    }

    @BeforeEach
    void beforeEach() {
        /*
         * Перед каждым тестом создаем общую структуру задач:
         * Две задачи, один Эпик с одной ПодЗадачей и второй Эпик с двумя ПодЗадачами
         */

        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);

        task1 = new Task("Task title", "Task description");
        task2 = new Task("Task title", "Task description");
        taskManager.addTask(task1); // id = 1
        taskManager.addTask(task2); // id = 2

        epic1 = new EpicTask("EpicTask title", "EpicTask description");
        sub1InEpic1 = new SubTask("SubTask title", "SubTask description");
        taskManager.addEpicTask(epic1); // id = 3
        sub1InEpic1.setEpicTaskId(epic1.getId());
        taskManager.addSubTask(sub1InEpic1); // id = 4

        epic2 = new EpicTask("EpicTask title", "EpicTask description");
        sub1InEpic2 = new SubTask("SubTask title", "SubTask description");
        sub2InEpic2 = new SubTask("SubTask title", "SubTask description");
        taskManager.addEpicTask(epic2); // id = 5
        sub1InEpic2.setEpicTaskId(epic2.getId());
        sub2InEpic2.setEpicTaskId(epic2.getId());
        taskManager.addSubTask(sub1InEpic2); // id = 6
        taskManager.addSubTask(sub2InEpic2); // id = 7
    }

    @Test
    void nextId() {
        assertNotEquals(taskManager.getTask(task1.getId()), taskManager.getTask(task2.getId()),
                "NextId not work in addTask");
        assertNotEquals(taskManager.getTask(task2.getId()), taskManager.getEpicTask(epic1.getId()),
                "NextId not work in addEpicTask");
        assertNotEquals(taskManager.getEpicTask(epic1.getId()), taskManager.getSubTask(sub1InEpic1.getId()),
                "NextId not work in addSubTask");
    }
    @Test
    void newTaskIsCorrect() {
        assertNotNull(taskManager.getTask(task1.getId()), "Task not found");
        assertNotNull(taskManager.getTask(task2.getId()), "Task not found");

        assertEquals(taskManager.getTask(task1.getId()), task1, "Tasks do not match");
        assertEquals(taskManager.getTask(task2.getId()), task2, "Tasks do not match");
    }

    @Test
    void newSubTaskIsCorrect() {
        assertNotNull(taskManager.getSubTask(sub1InEpic1.getId()), "SubTask not found");
        assertNotNull(taskManager.getSubTask(sub1InEpic2.getId()), "SubTask not found");
        assertNotNull(taskManager.getSubTask(sub2InEpic2.getId()), "SubTask not found");

        assertEquals(taskManager.getSubTask(sub1InEpic1.getId()), sub1InEpic1, "SubTasks do not match");
        assertEquals(taskManager.getSubTask(sub1InEpic2.getId()), sub1InEpic2, "SubTasks do not match");
        assertEquals(taskManager.getSubTask(sub2InEpic2.getId()), sub2InEpic2, "SubTasks do not match");

        // Связь соответстует
        assertEquals(sub1InEpic1.getEpicTaskId(), epic1.getId(), "Not match");
        assertEquals(sub1InEpic2.getEpicTaskId(), epic2.getId(), "Not match");
        assertEquals(sub2InEpic2.getEpicTaskId(), epic2.getId(), "Not match");

        // Новые подзадачи можно создать только под эпиком
        final SubTask newSubTaskAddInTask = new SubTask("New SubTask", "New SubTask description");
        newSubTaskAddInTask.setEpicTaskId(task1.getId());  // Попытка прикрепить подзадачу к задаче
        final int resultAddInTask = taskManager.addSubTask(newSubTaskAddInTask);

        assertEquals(0, resultAddInTask, "Wrong EpicTaskId for SubTask");

        final SubTask newSubAddTaskInSubTask = new SubTask("New SubTask", "New SubTask description");
        newSubAddTaskInSubTask.setEpicTaskId(sub1InEpic1.getId());  // Попытка прикрепить подзадачу к подзадаче
        final int resultAddInSubTask = taskManager.addSubTask(newSubAddTaskInSubTask);

        assertEquals(0, resultAddInSubTask, "Wrong EpicTaskId for SubTask");

    }

    @Test
    void newEpicTaskIsCorrect() {
        assertNotNull(taskManager.getEpicTask(epic1.getId()), "EpicTasks not found");
        assertNotNull(taskManager.getEpicTask(epic2.getId()), "EpicTasks not found");

        assertEquals(taskManager.getEpicTask(epic1.getId()), epic1, "EpicTasks do not match");
        assertEquals(taskManager.getEpicTask(epic2.getId()), epic2, "EpicTasks do not match");

        // Соответствие связи
        assertTrue(epic1.getSubTaskIds().contains(sub1InEpic1.getId()), "Not match");
        assertTrue(epic2.getSubTaskIds().contains(sub1InEpic2.getId()), "Not match");
        assertTrue(epic2.getSubTaskIds().contains(sub2InEpic2.getId()), "Not match");
    }

    @Test
    void getTaskGroup() {
        final List<Task> tasks = taskManager.getTaskGroup();
        assertNotNull(tasks, "TaskGroup not found");
        assertEquals(NUMBER_TASKS, tasks.size(), "Incorrect number of Tasks");
    }

    @Test
    void getSubTaskGroup() {
        final List<SubTask> subTasks = taskManager.getSubTaskGroup();
        assertNotNull(subTasks, "SubTaskGroup not found");
        assertEquals(NUMBER_SUB_TASKS, subTasks.size(), "Incorrect number of SubTasks");
    }

    @Test
    void getEpicTaskGroup() {
        final List<EpicTask> epicTasks = taskManager.getEpicTaskGroup();
        assertNotNull(epicTasks, "EpicTaskGroup not found");
        assertEquals(NUMBER_EPIC_TASKS, epicTasks.size(), "Incorrect number of EpicTasks");
    }

    @Test
    void getSubTaskGroupFromEpic() {
        final List<SubTask> subTasksEpic1 = taskManager.getSubTaskGroupFromEpic(epic1.getId());
        assertNotNull(subTasksEpic1, "SubTaskGroup not found");
        assertEquals(NUMBER_EPIC1_SUB_TASKS, subTasksEpic1.size(), "Incorrect number of SubTasks");
        assertTrue(subTasksEpic1.contains(sub1InEpic1), "SubTask not fount");

        final List<SubTask> subTasksEpic2 = taskManager.getSubTaskGroupFromEpic(epic2.getId());
        assertNotNull(subTasksEpic2, "SubTaskGroup not found");
        assertEquals(NUMBER_EPIC2_SUB_TASKS, subTasksEpic2.size(), "Incorrect number of SubTasks");
        assertTrue(subTasksEpic2.contains(sub1InEpic2), "SubTask not fount");
        assertTrue(subTasksEpic2.contains(sub2InEpic2), "SubTask not fount");
    }

    @Test
    void updateTask() {
        final int id = task1.getId();
        final String newTitle = task1.getTitle() + "(New title)";
        final String newDescription = task1.getDescription() +  "(New description)";
        final StatusTask newStatus = StatusTask.IN_PROGRESS;

        taskManager.updateTask(new Task(newTitle, newDescription, id, newStatus));
        final Task task = taskManager.getTask(id);

        assertEquals(newTitle, task.getTitle(), "Not the same title");
        assertEquals(newDescription, task.getDescription(), "Not the same description");
        assertEquals(newStatus, task.getStatus(), "Not the same status");
    }

    @Test
    void updateSubTask() {
        final int id = sub1InEpic1.getId();
        final String newTitle = sub1InEpic1.getTitle() + "(New title)";
        final String newDescription = sub1InEpic1.getDescription() +  "(New description)";
        final StatusTask newStatus = StatusTask.IN_PROGRESS;
        final int newEpicTaskId = sub1InEpic1.getEpicTaskId() + 1;  // Не должно меняться

        taskManager.updateSubTask(new SubTask(newTitle, newDescription, id, newStatus, newEpicTaskId));
        final SubTask subTask = taskManager.getSubTask(id);

        assertEquals(newTitle, subTask.getTitle(), "Not the same title");
        assertEquals(newDescription, subTask.getDescription(), "Not the same description");
        assertEquals(newStatus, subTask.getStatus(), "Not the same status");
        assertNotEquals(newEpicTaskId, subTask.getEpicTaskId(), "Do not change EpicTaskId");
    }

    @Test
    void updateEpicTask() {
        final int id = epic1.getId();
        final String newTitle = epic1.getTitle() + "(New title)";
        final String newDescription = epic1.getDescription() +  "(New description)";
        final StatusTask newStatus = StatusTask.IN_PROGRESS;  // Не должен меняться
        final List<Integer> newSubTaskIds = new ArrayList<>(); // Не должен меняться

        taskManager.updateEpicTask(new EpicTask(newTitle, newDescription, id, newStatus, newSubTaskIds));
        final EpicTask epicTask = taskManager.getEpicTask(id);

        assertEquals(newTitle, epicTask.getTitle(), "Not the same title");
        assertEquals(newDescription, epicTask.getDescription(), "Not the same description");
        assertNotEquals(newStatus, epicTask.getStatus(), "Do not change status");
        assertNotEquals(newSubTaskIds, epicTask.getSubTaskIds(), "Do not change SubTaskIds");
    }

    @Test
    void clearTaskGroup() {
        final int[] taskIds = {task1.getId(), task2.getId()};

        taskManager.clearTaskGroup();

        for (int taskId : taskIds) { // На случай если getTaskGroup() работает не корректно
            assertNull(taskManager.getTask(taskId), "getTask should be null");
        }

        final List<Task> taskGroup = taskManager.getTaskGroup();

        assertNotNull(taskGroup, "TaskGroup should be not null");
        assertTrue(taskGroup.isEmpty(), "TaskGroup should be empty");
    }

    @Test
    void clearSubTaskGroup() {
        final int[] subTaskIds = {sub1InEpic1.getId(), sub1InEpic2.getId(), sub2InEpic2.getId()};

        taskManager.clearSubTaskGroup();

        for (int subTaskId : subTaskIds) {
            assertNull(taskManager.getSubTask(subTaskId), "GetSubTask should be null");
        }

        assertNotNull(taskManager.getSubTaskGroup(), "SubTaskGroup should be not null");
        assertTrue(taskManager.getSubTaskGroup().isEmpty(), "SubTaskGroup should be empty");

        assertNotNull(epic1.getSubTaskIds(), "SubTaskIds should be not null");
        assertNotNull(epic2.getSubTaskIds(), "SubTaskIds should be not null");
        assertTrue(epic1.getSubTaskIds().isEmpty(), "SubTaskIds should be empty");
        assertTrue(epic2.getSubTaskIds().isEmpty(), "SubTaskIds should be empty");

    }

    @Test
    void clearEpicTaskGroup() {
        final int[] subTaskIds = {sub1InEpic1.getId(), sub1InEpic2.getId(), sub2InEpic2.getId()};
        final int[] epicTaskIds = {epic1.getId(), epic2.getId()};

        taskManager.clearEpicTaskGroup();

        for (int subTaskId : subTaskIds) {
            assertNull(taskManager.getSubTask(subTaskId), "getSubTask should be null");
        }
        for (int epicTaskId : epicTaskIds) {
            assertNull(taskManager.getEpicTask(epicTaskId), "getEpicTask should be null");
        }

        assertNotNull(taskManager.getEpicTaskGroup(), "EpicTaskGroup should be not null");
        assertTrue(taskManager.getEpicTaskGroup().isEmpty(), "EpicTaskGroup should be empty");

        assertNotNull(taskManager.getSubTaskGroup(), "SubTaskGroup should be not null");
        assertTrue(taskManager.getSubTaskGroup().isEmpty(), "SubTaskGroup should be empty");
    }

    @Test
    void removeTask() {
        final int idTask = task1.getId();

        taskManager.removeTask(idTask);
        assertNull(taskManager.getTask(idTask), "GetTask should be null");

        for (Task task : taskManager.getTaskGroup()) {
            assertNotEquals(idTask, task.getId(), "Id should not exist");
        }
    }

    @Test
    void removeSubTask() {
        final int idSubTask = sub1InEpic1.getId();

        taskManager.removeSubTask(idSubTask);
        assertNull(taskManager.getSubTask(idSubTask), "GetSubTask should be null");

        for (SubTask subTask : taskManager.getSubTaskGroup()) {
            assertNotEquals(idSubTask, subTask.getId(), "Id should not exist");
        }

        for (EpicTask epicTask : taskManager.getEpicTaskGroup()) { // Не должно быть связи с эпиком
            assertFalse(epicTask.getSubTaskIds().contains(idSubTask), "Epic must not contain SubTaskId");
        }
    }

    @Test
    void removeEpicTask() {
        final int idEpicTask = epic1.getId();
        final int isSubTask = sub1InEpic1.getId();

        taskManager.removeEpicTask(idEpicTask);
        assertNull(taskManager.getEpicTask(idEpicTask), "GetEpicTask should be null");

        for (EpicTask epicTask : taskManager.getEpicTaskGroup()) {
            assertNotEquals(idEpicTask, epicTask.getId(), "Id should not exist");
        }

        // Подзадачи так же не должно быть
        assertNull(taskManager.getSubTask(isSubTask), "GetSubTask should be null");

        for (SubTask subTask : taskManager.getSubTaskGroup()) {
            assertNotEquals(isSubTask, subTask.getId(), "Id should not exist");
        }
    }

    @Test
    public void statusAllNewTasksToBeNew() {
        // Статус всех новых задач должен быть NEW
        assertEquals(StatusTask.NEW,
                taskManager.getTask(task1.getId()).getStatus(),StatusTask.NEW + " status expected");
        assertEquals(StatusTask.NEW,
                taskManager.getTask(task2.getId()).getStatus(),StatusTask.NEW + " status expected");

        assertEquals(StatusTask.NEW,
                taskManager.getEpicTask(epic1.getId()).getStatus(),StatusTask.NEW + " status expected");
        assertEquals(StatusTask.NEW,
                taskManager.getSubTask(sub1InEpic1.getId()).getStatus(), StatusTask.NEW + " status expected");

        assertEquals(StatusTask.NEW,
                taskManager.getEpicTask(epic2.getId()).getStatus(),StatusTask.NEW + " status expected");
        assertEquals(StatusTask.NEW,
                taskManager.getSubTask(sub1InEpic2.getId()).getStatus(), StatusTask.NEW + " status expected");
        assertEquals(StatusTask.NEW,
                taskManager.getSubTask(sub2InEpic2.getId()).getStatus(),StatusTask.NEW + " status expected");
    }

    @Test
    public void statusEpicTaskToBeInProgress() {
        // Эпик1
        sub1InEpic1 = getCopySubTaskWithNewStatus(sub1InEpic1, StatusTask.IN_PROGRESS);
        taskManager.updateSubTask(sub1InEpic1);
        assertEquals(StatusTask.IN_PROGRESS, epic1.getStatus(), StatusTask.IN_PROGRESS + " status expected");

        // Эпик2
        sub1InEpic2 = getCopySubTaskWithNewStatus(sub1InEpic2, StatusTask.DONE);
        taskManager.updateSubTask(sub1InEpic2);
        assertEquals(StatusTask.IN_PROGRESS, epic2.getStatus(), StatusTask.IN_PROGRESS + " status expected");

        sub2InEpic2 = getCopySubTaskWithNewStatus(sub2InEpic2, StatusTask.IN_PROGRESS);
        taskManager.updateSubTask(sub2InEpic2);
        assertEquals(StatusTask.IN_PROGRESS, epic2.getStatus(), StatusTask.IN_PROGRESS + " status expected");

        sub1InEpic2 = getCopySubTaskWithNewStatus(sub1InEpic2, StatusTask.NEW);
        taskManager.updateSubTask(sub1InEpic2);
        assertEquals(StatusTask.IN_PROGRESS, epic2.getStatus(), StatusTask.IN_PROGRESS + " status expected");

        sub2InEpic2 = getCopySubTaskWithNewStatus(sub2InEpic2, StatusTask.DONE);
        taskManager.updateSubTask(sub2InEpic2);
        assertEquals(StatusTask.IN_PROGRESS, epic2.getStatus(), StatusTask.IN_PROGRESS + " status expected");

    }

    @Test
    public void statusEpicTaskToBeDone() {
        // Eсли все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
        // Эпик1
        sub1InEpic1 = getCopySubTaskWithNewStatus(sub1InEpic1, StatusTask.DONE);
        taskManager.updateSubTask(sub1InEpic1);
        assertEquals(StatusTask.DONE, epic1.getStatus(), StatusTask.DONE + " status expected");

        // Эпик2
        sub1InEpic2 = getCopySubTaskWithNewStatus(sub1InEpic2, StatusTask.DONE);
        taskManager.updateSubTask(sub1InEpic2);
        sub2InEpic2 = getCopySubTaskWithNewStatus(sub2InEpic2, StatusTask.DONE);
        taskManager.updateSubTask(sub2InEpic2);
        assertEquals(StatusTask.DONE, epic2.getStatus(), StatusTask.DONE + " status expected");

    }
    @Test
    public void statusEpicTaskToBeNew() {
         // Eсли у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
        // Эпик1
        sub1InEpic1 = getCopySubTaskWithNewStatus(sub1InEpic1, StatusTask.DONE);
        taskManager.updateSubTask(sub1InEpic1);
        assertNotEquals(StatusTask.NEW, epic1.getStatus(), StatusTask.NEW + " status not expected");

        sub1InEpic1 = getCopySubTaskWithNewStatus(sub1InEpic1, StatusTask.NEW);
        taskManager.updateSubTask(sub1InEpic1);
        assertEquals(StatusTask.NEW, epic1.getStatus(), StatusTask.NEW + " status expected");

        sub1InEpic1 = getCopySubTaskWithNewStatus(sub1InEpic1, StatusTask.DONE);
        taskManager.updateSubTask(sub1InEpic1);
        taskManager.removeSubTask(sub1InEpic1.getId());
        assertEquals(StatusTask.NEW, epic1.getStatus(), StatusTask.NEW + " status expected");


        // Эпик2
        sub1InEpic2 = getCopySubTaskWithNewStatus(sub1InEpic2, StatusTask.DONE);
        taskManager.updateSubTask(sub1InEpic2);
        sub2InEpic2 = getCopySubTaskWithNewStatus(sub2InEpic2, StatusTask.DONE);
        taskManager.updateSubTask(sub2InEpic2);
        assertNotEquals(StatusTask.NEW, epic2.getStatus(), StatusTask.NEW + " status not expected");

        sub1InEpic2 = getCopySubTaskWithNewStatus(sub1InEpic2, StatusTask.NEW);
        taskManager.updateSubTask(sub1InEpic2);
        assertNotEquals(StatusTask.NEW, epic2.getStatus(), StatusTask.NEW + " status not expected");

        sub2InEpic2 = getCopySubTaskWithNewStatus(sub2InEpic2, StatusTask.NEW);
        taskManager.updateSubTask(sub2InEpic2);
        assertEquals(StatusTask.NEW, epic2.getStatus(), StatusTask.NEW + " status expected");

        sub2InEpic2 = getCopySubTaskWithNewStatus(sub2InEpic2, StatusTask.DONE);
        taskManager.updateSubTask(sub2InEpic2);
        taskManager.removeSubTask(sub2InEpic2.getId());
        assertEquals(StatusTask.NEW, epic2.getStatus(), StatusTask.NEW + " status expected");
    }

    @Test
    public void addHistory() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "History should be not null");
        assertEquals(0, history.size(), "New history should be empty");

        // Просматриваем корректные id
        taskManager.getTask(task1.getId());
        taskManager.getEpicTask(epic1.getId());
        taskManager.getSubTask(sub1InEpic1.getId());

        // Не корректные(не существующие) id не должны влиять на историю
        taskManager.getTask(100);
        taskManager.getEpicTask(101);
        taskManager.getSubTask(102);

        assertEquals(3, history.size(), "Unexpected history size");
    }
}