package ru.yandex.practicum.kanban;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> taskGroup;
    private final HashMap<Integer, SubTask> subTaskGroup;
    private final HashMap<Integer, EpicTask> epicTaskGroup;
    private int nextId;

    public TaskManager() {
        this.taskGroup = new HashMap<>();
        this.subTaskGroup = new HashMap<>();
        this.epicTaskGroup = new HashMap<>();
        this.nextId = 1;
    }

    // ТЗ 2-с: Получение по идентификатору.
    public Task getTask(int id) {
        return taskGroup.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTaskGroup.get(id);
    }

    public EpicTask getEpicTask(int id) {
        return epicTaskGroup.get(id);
    }

    // ТЗ 2-a: Получение списка всех задач.
    public ArrayList<Task> getTaskGroup() {
        return new ArrayList<>(taskGroup.values()); // Исправлено! И действительно, нужно вернуть списки, а не мапы.
    }

    public ArrayList<SubTask> getSubTaskGroup() {
        return new ArrayList<>(subTaskGroup.values());
    }

    public ArrayList<EpicTask> getEpicTaskGroup() {
        return new ArrayList<>(epicTaskGroup.values());
    }

    // ТЗ 3-а: Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getSubTaskGroup(int epicTaskId) {
        ArrayList<SubTask> resultGroup = new ArrayList<>();

        if (!subTaskGroup.isEmpty() || epicTaskGroup.containsKey(epicTaskId)) {
            for (SubTask subTask : subTaskGroup.values()) {
                if (subTask.getEpicTaskId() == epicTaskId) {
                    resultGroup.add(subTask);
                }
            }
        }
        return resultGroup; // При неверных вводных пустая коллекция. При воврате null(отказался) возможны исключения.
    }

    // ТЗ 2-d: Создание. Сам объект должен передаваться в качестве параметра.
    public int addTask(Task task) { // Исправлено! Принцип использования перегрузки методов принял во внимание.
        int id = nextId++;

        task.setId(id);
        taskGroup.put(id, task);
        return id;
    }

    public int addSubTask(SubTask subTask) {
        int epicId = subTask.getEpicTaskId();
        if (getEpicTask(epicId) == null) {
            return 0;
        }

        int id = nextId++;

        subTask.setId(id);
        subTaskGroup.put(id, subTask);
        epicTaskGroup.get(epicId).addSubTaskId(id);
        updateStatusEpicTask(epicId);
        return id;
    }

    public int addEpicTask(EpicTask epicTask) {
        int id = nextId++;

        epicTask.setId(id);
        epicTaskGroup.put(id, epicTask);
        return id;
    }

    // ТЗ 2-е: Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public boolean updateTask(Task task) {
        if (!taskGroup.containsKey(task.getId())) {
            return false;
        }

        taskGroup.put(task.getId(), task);
        return true;
    }

    public boolean updateSubTask(SubTask subTask) {
        int id = subTask.getId();

        if (!subTaskGroup.containsKey(id)) {
            return false;
        }
        // Оставил неизменным согласно ответу в "Пачке".
        boolean isModifyStatus = subTask.getStatus() != getSubTask(id).getStatus();

        subTaskGroup.put(id, subTask);
        if (isModifyStatus) {
            updateStatusEpicTask(subTask.getEpicTaskId());
        }
        return true;
    }

    public boolean updateEpicTask(EpicTask epicTask) {
        if (!epicTaskGroup.containsKey(epicTask.getId())) {
            return false;
        }

        EpicTask epicTaskOriginal = getEpicTask(epicTask.getId()); // Исправлено! Стало безопаснее.
        epicTaskOriginal.setTitle(epicTask.getTitle());
        epicTaskOriginal.setDescription(epicTask.getDescription());
        return true;
    }

    // ТЗ 2-b: Удаление всех задач.
    public void clearTaskGroup() {
        taskGroup.clear();
    }

    public void clearSubTaskGroup() {
        subTaskGroup.clear();

        // Исправлено! Действительно проверка на пустоту лишняя
        for (EpicTask epicTask : epicTaskGroup.values()) {
            epicTask.getSubTaskIds().clear();
            epicTask.setStatus(StatusTask.NEW);
        }
    }

    public void clearEpicTaskGroup() {
        subTaskGroup.clear();
        epicTaskGroup.clear();
    }

    // ТЗ 2-f: Удаление по идентификатору.
    public boolean removeTask(int id) {
        return taskGroup.remove(id) != null;
    }

    public boolean removeSubTask(int id) {
        if (!subTaskGroup.containsKey(id)) {
            return false;
        }

        int epicId = getSubTask(id).getEpicTaskId();
        getEpicTask(epicId).removeSubTaskId(id);
        subTaskGroup.remove(id);
        updateStatusEpicTask(epicId);
        return true;
    }

    public boolean removeEpicTask(int id) {
        if (!epicTaskGroup.containsKey(id)) {
            return false;
        }

        for (Integer subTaskId : getEpicTask(id).getSubTaskIds()) {
            subTaskGroup.remove(subTaskId);
        }
        epicTaskGroup.remove(id);
        return true;
    }

    private void updateStatusEpicTask(int id) {
        EpicTask epicTask = getEpicTask(id);

        if (epicTask.getSubTaskIds().isEmpty()) {
            epicTask.setStatus(StatusTask.NEW);
            return;
        }

        boolean isAllNew = true;
        boolean isAllDone = true;

        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            switch (getSubTask(subTaskId).getStatus()) {
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
