package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.List;

/**
 * Менеджер истории просмотра задач.
 * <ul>
 * <li>В списке нет повторяющейся задачи.
 * <li>Задачи добавляются/обновляются в конец списка
 *  </ul
 */

public interface HistoryManager {

    /**
     * Добавляет/перемещает задачу в конец списка истории просмотра
     * @param task добавляемая задача
     */
    void add(Task task);

    /**
     * Удаляет задачу из списка истории просмотра
     * @param id идентификатор удаляемой задачи
     */
    void remove(int id);

    /**
     * Возвращает список задач из истории просмотра
     * @return список задач
     */
    List<Task> getHistoryList();
}
