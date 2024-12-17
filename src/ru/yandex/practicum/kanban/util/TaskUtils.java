package ru.yandex.practicum.kanban.util;

import ru.yandex.practicum.kanban.manager.ManagerSaveException;
import ru.yandex.practicum.kanban.task.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public final class TaskUtils {
    private static final String CSV_SEPARATOR = ",";

    private enum CsvHead {
        id,
        type,
        name,
        status,
        description,
        epic,
        duration,
        start
    }

    private TaskUtils() {
    }

    public static String getCsvHead() {
        StringBuilder sb = new StringBuilder();
        for (CsvHead element : CsvHead.values()) {
            sb.append(",").append(element.name());
        }
        sb.deleteCharAt(0); // Удалить первую запятую
        return sb.toString();
    }

    public static String toCsvLine(Task task) {
        if (task == null) return "";

        StringBuilder sb = new StringBuilder();
        String data;
        for (CsvHead element : CsvHead.values()) {
            data = switch (element) {
                case id -> Integer.toString(task.getId());
                case type -> task.getType().toString();
                case name -> task.getTitle();
                case status -> task.getStatus().toString();
                case description -> task.getDescription();
                case epic -> task.getType().equals(TypeTask.SUB_TASK) ?
                        Integer.toString(((SubTask) task).getEpicId()) : "";
                case duration -> Long.toString(task.getDuration().toMinutes());
                case start -> task.getStartTime().isEmpty() ?
                        "" : Long.toString(task.getStartTime().get().toInstant(ZoneOffset.UTC).toEpochMilli());
            };
            sb.append(",").append(data);
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }

    public static Task fromCsvLine(String value) {
        if (value == null || value.isEmpty()) {
            throw new ManagerSaveException("Parse CSV: value = (null or empty)");
        }

        String[] parts = value.split(CSV_SEPARATOR, -1);
        if (parts.length < CsvHead.values().length) {
            throw new ManagerSaveException("Parse CSV: invalid number of fields");
        }

        if (!stringInArrEnum(TypeTask.values(), parts[CsvHead.type.ordinal()])) {
            throw new ManagerSaveException("Parse CSV: invalid type");
        }
        TypeTask type = TypeTask.valueOf(parts[CsvHead.type.ordinal()]);

        if (!isNumber(parts[CsvHead.id.ordinal()])) {
            throw new ManagerSaveException("Parse CSV: id is not a number");
        }
        int id = Integer.parseInt(parts[CsvHead.id.ordinal()]);

        if (!stringInArrEnum(StatusTask.values(), parts[CsvHead.status.ordinal()])) {
            throw new ManagerSaveException("Parse CSV: invalid status");
        }
        StatusTask status = StatusTask.valueOf(parts[CsvHead.status.ordinal()]);

        if (!isNumber(parts[CsvHead.duration.ordinal()])) {
            throw new ManagerSaveException("Parse CSV: duration is not a number");
        }
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[CsvHead.duration.ordinal()]));

        LocalDateTime start;
        if (parts[CsvHead.start.ordinal()].isEmpty()) {
            start = null;
        } else {
            if (!isNumber(parts[CsvHead.start.ordinal()])) {
                throw new ManagerSaveException("Parse CSV: start is not a number");
            }
            start = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(Long.parseLong(parts[CsvHead.start.ordinal()])), ZoneOffset.UTC);
        }

        String name = parts[CsvHead.name.ordinal()];
        String description = parts[CsvHead.description.ordinal()];
        Task task;
        task = switch (type) {
            case TypeTask.TASK -> new Task(name, description, id, status, duration, start);
            case TypeTask.EPIC_TASK -> new EpicTask(name, description, id, status, duration, start, new ArrayList<>());
            case TypeTask.SUB_TASK -> {
                if (!isNumber(parts[CsvHead.epic.ordinal()])) {
                    throw new ManagerSaveException("Parse CSV: epic is not a number");
                } else {
                    yield new SubTask(Integer.parseInt(parts[CsvHead.epic.ordinal()]), name, description, id, status,
                            duration, start);
                }
            }
        };
        return task;
    }

    // Метод проверки содержания строки в массиве значений enum
    private static <E extends Enum<E>> boolean stringInArrEnum(E[] e, String s) {
        if (e == null || s.isEmpty()) return false;

        boolean result = false;
        for (Enum<E> item : e) {
            if (item.name().equals(s)) {
                result = true;
                break;
            }
        }
        return result;
    }

    // Метод проверки строки на состав только из цифр
    private static boolean isNumber(String value) {
        return value.matches("\\d+");
    }
}
