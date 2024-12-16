package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;
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
    public void clearSubGroup() {
        super.clearSubGroup();
        save();
    }

    @Override
    public void clearEpicGroup() {
        super.clearEpicGroup();
        save();
    }

    @Override
    public boolean removeSub(int id) {
        boolean result = super.removeSub(id);
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
    public boolean removeEpic(int id) {
        boolean result = super.removeEpic(id);
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
            for (Task epic : getEpicGroup()) {
                writer.write(TaskUtils.toCsvLine(epic));
                writer.newLine();
            }
            for (Task sub : getSubGroup()) {
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
                    if (!fb.taskGroup.containsKey(task.getId()) && !fb.epicGroup.containsKey(task.getId()) &&
                            !fb.subGroup.containsKey(task.getId())) {
                        if (task.getId() > idMax) {
                            idMax = task.getId();
                        }
                        switch (task.getType()) {
                            case TASK -> {
                                fb.taskGroup.put(task.getId(), task);
                                if (task.getStartTime().isPresent()) {
                                    fb.prioritizedTasks.add(task);
                                }
                            }
                            case EPIC_TASK -> fb.epicGroup.put(task.getId(), (EpicTask) task);
                            case SUB_TASK -> {
                                sub = (SubTask) task;
                                fb.subGroup.put(sub.getId(), sub);
                                fb.epicGroup.get(sub.getEpicId()).addSubId(sub.getId());
                                if (sub.getStartTime().isPresent()) {
                                    fb.prioritizedTasks.add(sub);
                                }
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