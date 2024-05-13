package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskGroup;
    private final Map<Integer, SubTask> subTaskGroup;
    private final Map<Integer, EpicTask> epicTaskGroup;
    private final HistoryManager historyManager;
    private int nextId;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.taskGroup = new HashMap<>();
        this.subTaskGroup = new HashMap<>();
        this.epicTaskGroup = new HashMap<>();
        this.historyManager = historyManager;
        this.nextId = 1;
    }

    // ТЗ 2-с: Получение по идентификатору.
    @Override
    public Task getTask(int id) {
        historyManager.add(taskGroup.get(id));  // Добавления не будет если taskGroup.get(id) == null
        return taskGroup.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        historyManager.add(subTaskGroup.get(id));
        return subTaskGroup.get(id);
    }

    @Override
    public EpicTask getEpicTask(int id) {
        historyManager.add(epicTaskGroup.get(id));
        return epicTaskGroup.get(id);
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
    public int addTask(Task task) {
        if (task instanceof SubTask || task instanceof EpicTask) {
            // Режем возможность добавить наследников
            return 0;
        }
        int id = nextId++;

        task.setId(id);
        taskGroup.put(id, task);
        return id;
    }

    @Override
    public int addSubTask(SubTask subTask) {
        int epicId = subTask.getEpicTaskId();
        if (epicTaskGroup.get(epicId) == null) {
            return 0;
        }

        int id = nextId++;

        subTask.setId(id);
        subTaskGroup.put(id, subTask);
        epicTaskGroup.get(epicId).addSubTaskId(id);
        updateStatusEpicTask(epicId);
        return id;
    }

    @Override
    public int addEpicTask(EpicTask epicTask) {
        int id = nextId++;

        epicTask.setId(id);
        epicTaskGroup.put(id, epicTask);
        return id;
    }

    // ТЗ 2-е: Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public boolean updateTask(Task task) {
        if (!taskGroup.containsKey(task.getId())) {
            return false;
        }

        taskGroup.put(task.getId(), task);
        return true;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        if (!subTaskGroup.containsKey(subTask.getId())) {
            return false;
        }

        SubTask subTaskOriginal = subTaskGroup.get(subTask.getId());  // Закрыть изменение поля epicTaskId
        subTaskOriginal.setTitle(subTask.getTitle());
        subTaskOriginal.setDescription(subTask.getDescription());

        if (subTaskOriginal.getStatus() != subTask.getStatus()) {
            subTaskOriginal.setStatus(subTask.getStatus());
            updateStatusEpicTask(subTaskOriginal.getEpicTaskId());
        }
        return true;
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        if (!epicTaskGroup.containsKey(epicTask.getId())) {
            return false;
        }

        EpicTask epicTaskOriginal = epicTaskGroup.get(epicTask.getId());
        epicTaskOriginal.setTitle(epicTask.getTitle());
        epicTaskOriginal.setDescription(epicTask.getDescription());
        return true;
    }

    // ТЗ 2-b: Удаление всех задач.
    @Override
    public void clearTaskGroup() {
        taskGroup.clear();
    }

    @Override
    public void clearSubTaskGroup() {
        subTaskGroup.clear();

        for (EpicTask epicTask : epicTaskGroup.values()) {
            epicTask.getSubTaskIds().clear();
            epicTask.setStatus(StatusTask.NEW);
        }
    }

    @Override
    public void clearEpicTaskGroup() {
        subTaskGroup.clear();
        epicTaskGroup.clear();
    }

    // ТЗ 2-f: Удаление по идентификатору.
    @Override
    public boolean removeTask(int id) {
        return taskGroup.remove(id) != null;
    }

    @Override
    public boolean removeSubTask(int id) {
        if (!subTaskGroup.containsKey(id)) {
            return false;
        }

        int epicId = subTaskGroup.get(id).getEpicTaskId();
        epicTaskGroup.get(epicId).removeSubTaskId(id);
        subTaskGroup.remove(id);
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
        }
        epicTaskGroup.remove(id);
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

