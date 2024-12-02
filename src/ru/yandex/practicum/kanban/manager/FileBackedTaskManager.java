package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.*;
import ru.yandex.practicum.kanban.util.TaskUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final Charset FILE_CHARSET = StandardCharsets.UTF_8;
    private final Path file;

    public FileBackedTaskManager(Path file) {
        this.file = file;
    }

    public FileBackedTaskManager(Path file, HistoryManager historyManager) {
        this.file = file;
        setHistoryManager(historyManager);
    }

    @Override
    public int addTask(Task task) {
        int result = super.addTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public void clearTaskGroup() {
        super.clearTaskGroup();
        save();
    }

    @Override
    public void clearSubTaskGroup() {
        super.clearSubTaskGroup();
        save();
    }

    @Override
    public void clearEpicTaskGroup() {
        super.clearEpicTaskGroup();
        save();
    }

    @Override
    public boolean removeSubTask(int id) {
        boolean result = super.removeSubTask(id);
        save();
        return result;
    }

    @Override
    public boolean removeTask(int id) {
        boolean result = super.removeTask(id);
        save();
        return result;
    }

    @Override
    public boolean removeEpicTask(int id) {
        boolean result = super.removeEpicTask(id);
        save();
        return result;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), FILE_CHARSET))) {
            writer.write(TaskUtils.getCsvHead());
            writer.newLine();
            for (Task task : getTaskGroup()) {
                writer.write(TaskUtils.toCsvLine(task));
                writer.newLine();
            }
            for (Task epic : getEpicTaskGroup()) {
                writer.write(TaskUtils.toCsvLine(epic));
                writer.newLine();
            }
            for (Task sub : getSubTaskGroup()) {
                writer.write(TaskUtils.toCsvLine(sub));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager fb = new FileBackedTaskManager(file);
        String taskData;
        Task task;
        SubTask sub;
        int idMax = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile(), FILE_CHARSET))) {
            taskData = reader.readLine();
            if (taskData.equals(TaskUtils.getCsvHead())) {
                while (reader.ready()) {
                    taskData = reader.readLine();
                    task = TaskUtils.fromCsvLine(taskData);
                    if (task != null && !fb.taskGroup.containsKey(task.getId()) &&
                            !fb.epicTaskGroup.containsKey(task.getId()) &&
                            !fb.subTaskGroup.containsKey(task.getId())) {
                        if (task.getId() > idMax) {
                            idMax = task.getId();
                        }
                        switch (task.getType()) {
                            case TASK -> fb.taskGroup.put(task.getId(), task);
                            case EPIC_TASK -> fb.epicTaskGroup.put(task.getId(), (EpicTask) task);
                            case SUB_TASK -> {
                                sub = (SubTask) task;
                                fb.subTaskGroup.put(sub.getId(), sub);
                                fb.epicTaskGroup.get(sub.getEpicTaskId()).addSubTaskId(sub.getId());
                            }
                        }
                    }
                }
                fb.nextId = ++idMax;
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        return fb;
    }
}

