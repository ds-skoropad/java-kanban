package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistoryList();
}
