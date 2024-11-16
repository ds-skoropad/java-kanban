package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskGroup;
    private final Map<Integer, SubTask> subTaskGroup;
    private final Map<Integer, EpicTask> epicTaskGroup;
    private HistoryManager historyManager;
    private int nextId;

    public InMemoryTaskManager() {
        this.taskGroup = new HashMap<>();
        this.subTaskGroup = new HashMap<>();
        this.epicTaskGroup = new HashMap<>();
        this.nextId = 1;
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.taskGroup = new HashMap<>();
        this.subTaskGroup = new HashMap<>();
        this.epicTaskGroup = new HashMap<>();
        this.historyManager = historyManager;
        this.nextId = 1;
    }

    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // ТЗ 2-с: Получение по идентификатору.
    @Override
    public Task getTask(int id) {
        Task task = taskGroup.get(id); // Исправлено! Согласен, создал лишнюю нагрузку на ЦП.

        if (historyManager != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTaskGroup.get(id);

        if (historyManager != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask epicTask = epicTaskGroup.get(id);

        if (historyManager != null) {
            historyManager.add(epicTask);
        }
        return epicTask;
    }

    // ТЗ 2-a: Получение списка всех задач.
    @Override
    public List<Task> getTaskGroup() {
        return new ArrayList<>(taskGroup.values());
    }

    @Override
    public List<SubTask> getSubTaskGroup() {
        return new ArrayList<>(subTaskGroup.values());
    }

    @Override
    public List<EpicTask> getEpicTaskGroup() {
        return new ArrayList<>(epicTaskGroup.values());
    }

    // ТЗ 3-а: Получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubTaskGroupFromEpic(int epicTaskId) {
        List<SubTask> resultGroup = new ArrayList<>();
        EpicTask epicTask = epicTaskGroup.get(epicTaskId);

        if (epicTask != null) {
            for (int subTaskId : epicTask.getSubTaskIds()) {
                if (subTaskGroup.containsKey(subTaskId)) {
                    resultGroup.add(subTaskGroup.get(subTaskId));
                }
            }
        }
        return resultGroup;
    }

    // ТЗ 2-d: Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public int addTask(Task task) {  // Исправлено!
        if (task == null) return 0;
        int id;

        switch (task.getType()) {
            case SUB_TASK -> {
                SubTask subTask = (SubTask) task;
                int epicId = subTask.getEpicTaskId();

                if (epicTaskGroup.get(epicId) == null) {
                    return 0;
                }

                id = nextId++;
                subTask.setId(id);
                subTaskGroup.put(id, subTask);
                epicTaskGroup.get(epicId).addSubTaskId(id);
                updateStatusEpicTask(epicId);
            }
            case EPIC_TASK -> {
                id = nextId++;
                EpicTask epicTask = (EpicTask) task;
                epicTask.setId(id);
                epicTaskGroup.put(id, epicTask);
            }
            default -> { // TASK
                id = nextId++;
                task.setId(id);
                taskGroup.put(id, task);
            }
        }
        return id;
    }

    // ТЗ 2-е: Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public boolean updateTask(Task task) {
        if (task == null) return false;
        int id = task.getId();

        switch (task.getType()) {
            case SUB_TASK -> {
                if (!subTaskGroup.containsKey(id)) return false;

                SubTask subTask = (SubTask) task;
                SubTask subTaskOriginal = subTaskGroup.get(id);
                subTaskOriginal.setTitle(subTask.getTitle());
                subTaskOriginal.setDescription(subTask.getDescription());

                if (subTaskOriginal.getStatus() != subTask.getStatus()) { // Если статус изменился
                    subTaskOriginal.setStatus(subTask.getStatus());
                    updateStatusEpicTask(subTaskOriginal.getEpicTaskId());
                }
            }
            case EPIC_TASK -> {
                if (!epicTaskGroup.containsKey(id)) return false;

                EpicTask epicTask = (EpicTask) task;
                EpicTask epicTaskOriginal = epicTaskGroup.get(id);
                epicTaskOriginal.setTitle(epicTask.getTitle());
                epicTaskOriginal.setDescription(epicTask.getDescription());
            }
            default -> { // TASK
                if (!taskGroup.containsKey(id)) return false;
                taskGroup.put(task.getId(), task);
            }
        }
        return true;
    }

    // ТЗ 2-b: Удаление всех задач.
    @Override
    public void clearTaskGroup() {
        for (int id : taskGroup.keySet()) {
            historyManager.remove(id);
        }
        taskGroup.clear();
    }

    @Override
    public void clearSubTaskGroup() {
        for (int id : subTaskGroup.keySet()) {
            historyManager.remove(id);
        }
        subTaskGroup.clear();

        for (EpicTask epicTask : epicTaskGroup.values()) {
            epicTask.getSubTaskIds().clear();
            epicTask.setStatus(StatusTask.NEW);
        }
    }

    @Override
    public void clearEpicTaskGroup() {
        for (int id : subTaskGroup.keySet()) {
            historyManager.remove(id);
        }
        subTaskGroup.clear();

        for (int id : epicTaskGroup.keySet()) {
            historyManager.remove(id);
        }
        epicTaskGroup.clear();
    }

    // ТЗ 2-f: Удаление по идентификатору.
    @Override
    public boolean removeTask(int id) {
        if (!taskGroup.containsKey(id)) {
            return false;
        }

        taskGroup.remove(id);
        historyManager.remove(id);
        return true;
    }

    @Override
    public boolean removeSubTask(int id) {
        if (!subTaskGroup.containsKey(id)) {
            return false;
        }

        int epicId = subTaskGroup.get(id).getEpicTaskId();
        epicTaskGroup.get(epicId).removeSubTaskId(id);
        subTaskGroup.remove(id);
        historyManager.remove(id);
        updateStatusEpicTask(epicId);
        return true;
    }

    @Override
    public boolean removeEpicTask(int id) {
        if (!epicTaskGroup.containsKey(id)) {
            return false;
        }

        for (Integer subTaskId : epicTaskGroup.get(id).getSubTaskIds()) {
            subTaskGroup.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        epicTaskGroup.remove(id);
        historyManager.remove(id);
        return true;
    }

    private void updateStatusEpicTask(int id) {
        EpicTask epicTask = epicTaskGroup.get(id);

        if (epicTask == null) {
            return;
        }
        if (epicTask.getSubTaskIds().isEmpty()) {
            epicTask.setStatus(StatusTask.NEW);
            return;
        }

        boolean isAllNew = true;
        boolean isAllDone = true;

        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            switch (subTaskGroup.get(subTaskId).getStatus()) {
                case NEW -> isAllDone = false;
                case DONE -> isAllNew = false;
                default -> {
                    isAllDone = false;
                    isAllNew = false;
                }
            }
            if (!isAllDone && !isAllNew) {
                break;
            }
        }
        if (isAllNew) {
            epicTask.setStatus(StatusTask.NEW);
        } else if (isAllDone) {
            epicTask.setStatus(StatusTask.DONE);
        } else {
            epicTask.setStatus(StatusTask.IN_PROGRESS);
        }
    }
}

