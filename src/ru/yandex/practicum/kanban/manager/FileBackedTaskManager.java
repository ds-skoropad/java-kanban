package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;

// Решил что логика преобразования в CSV необходима только в данном классе, поэтому не встраивал в классы Task

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String SEPARATOR_CSV = ",";
    private Path file;

    public FileBackedTaskManager(Path file) {
        this.file = file;
    }

    public FileBackedTaskManager(Path file, HistoryManager historyManager) {
        this.file = file;
        setHistoryManager(historyManager);
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
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

    /*
     * В ТЗ хорошо смотрелся бы допник: добавить некий журнал транзакций или кэш-файл,
     * чтобы писать в него операции изменения, а не перезаписывать файл полностью каждый раз.
     */
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), StandardCharsets.UTF_8))) {
            for (Task task : getTaskGroup()) {
                writer.write(taskToStringCsv(task));
                writer.newLine();
            }
            for (Task epic : getEpicTaskGroup()) {
                writer.write(taskToStringCsv(epic));
                writer.newLine();
            }
            for (Task sub : getSubTaskGroup()) {  // Важно подзадачи после эпиков записать
                writer.write(taskToStringCsv(sub));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    // В ТЗ написано создать данный метод статичным ... Зачем, почему не ясно ... Ответа в пачке я пока не получил...
    public void loadFromFile(Path file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile(), StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String taskData = reader.readLine();
                addTask(taskFromStringCsv(taskData));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    // Column: id,type,name,status,description,epic
    private String taskToStringCsv(Task task) {
        if (task == null) return null;

        String epicId = "";
        if (task.getType() == TypeTask.SUB_TASK) {
            epicId = Integer.toString(((SubTask) task).getEpicTaskId());
        }
        return String.join(SEPARATOR_CSV, Integer.toString(task.getId()), task.getType().toString(), task.getTitle(),
                task.getStatus().toString(), task.getDescription(), epicId);
    }

    private Task taskFromStringCsv(String value) {
        if (value == null || value.isEmpty()) return null;

        String[] parts = value.split(SEPARATOR_CSV);
        if (parts.length < 5) return null; // нарушена структура строки

        TypeTask type;
        int id;
        StatusTask status;
        Task task;

        try {
            type = TypeTask.valueOf(parts[1]);
            id = Integer.parseInt(parts[0]);
            status = StatusTask.valueOf(parts[3]);

            task = switch (type) {
                case TypeTask.TASK -> new Task(parts[2], parts[4], id, status);
                case TypeTask.EPIC_TASK -> new EpicTask(parts[2], parts[4], id, status, new ArrayList<>());
                case TypeTask.SUB_TASK -> new SubTask(parts[2], parts[4], id, status, Integer.parseInt(parts[5]));
            };
        } catch (IllegalArgumentException e) {
            task = null;
        }
        return task;
    }

    private static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message) {
            super(message);
        }
    }
}

