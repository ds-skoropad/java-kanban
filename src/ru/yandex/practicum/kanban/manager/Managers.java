package ru.yandex.practicum.kanban.manager;

/*
 * Пока вижу так :)
 * Предполагаю, что далее будет реализация через файлы и БД.
 * Возможно как вариант:
 * enum STORAGE_TYPE {IN_MEMORY, IN_FILE, IN_DB}
 * new Managers(StorageType.IN_MEMORY)
 */

public class Managers {
    public static HistoryManager historyManager;
    public static TaskManager taskManager;

    public Managers() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
