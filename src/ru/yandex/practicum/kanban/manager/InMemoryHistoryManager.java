package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_MAX_SIZE = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public boolean add(Task task) {
        if (task == null) return false;
        if (history.size() >= HISTORY_MAX_SIZE) {
            history.removeFirst();
        }
        return history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history); // Исправлено!
    }
}
