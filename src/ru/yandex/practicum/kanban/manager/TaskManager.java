package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {

    Optional<Task> getTask(int id);

    Optional<SubTask> getSub(int id);

    Optional<EpicTask> getEpic(int id);

    List<Task> getTaskGroup();

    List<SubTask> getSubGroup();

    List<EpicTask> getEpicGroup();

    List<SubTask> getEpicSubTasks(int epicId);

    List<Task> getPrioritizedTasks();

    int addTask(Task task);

    boolean updateTask(Task task);

    void clearTaskGroup();

    void clearSubGroup();

    void clearEpicGroup();

    boolean removeTask(int id);

    boolean removeSub(int id);

    boolean removeEpic(int id);

    void setHistoryManager(HistoryManager historyManager);
}