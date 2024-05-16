package ru.yandex.practicum.kanban.manager;

/*
 * Выполняя задание, я упустил момент про утилитный класс.
 * Благодарю за замечание. Исправлено.
 */

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
