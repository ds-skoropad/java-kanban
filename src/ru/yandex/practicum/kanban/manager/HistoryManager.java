package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.List;

public interface HistoryManager {
    boolean add(Task task);

    List<Task> getHistory();
}
