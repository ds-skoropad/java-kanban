package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.List;

public interface TaskManager {
    // ТЗ 2-с: Получение по идентификатору.
    Task getTask(int id);

    SubTask getSubTask(int id);

    EpicTask getEpicTask(int id);

    // ТЗ 2-a: Получение списка всех задач.
    List<Task> getTaskGroup();

    List<SubTask> getSubTaskGroup();

    List<EpicTask> getEpicTaskGroup();

    // ТЗ 3-а: Получение списка всех подзадач определённого эпика.
    List<SubTask> getSubTaskGroupFromEpic(int epicTaskId);

    // ТЗ 2-d: Создание. Сам объект должен передаваться в качестве параметра.
    int addTask(Task task);

    int addSubTask(SubTask subTask);

    int addEpicTask(EpicTask epicTask);

    // ТЗ 2-е: Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    boolean updateTask(Task task);

    boolean updateSubTask(SubTask subTask);

    boolean updateEpicTask(EpicTask epicTask);

    // ТЗ 2-b: Удаление всех задач.
    void clearTaskGroup();

    void clearSubTaskGroup();

    void clearEpicTaskGroup();

    // ТЗ 2-f: Удаление по идентификатору.
    boolean removeTask(int id);

    boolean removeSubTask(int id);

    boolean removeEpicTask(int id);
}
