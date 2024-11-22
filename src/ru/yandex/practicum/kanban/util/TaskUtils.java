package ru.yandex.practicum.kanban.util;

import ru.yandex.practicum.kanban.task.*;

import java.util.ArrayList;

public final class TaskUtils {
    private static final String CSV_SEPARATOR = ",";

    private enum CsvHead {
        id,
        type,
        name,
        status,
        description,
        epic
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
        for (CsvHead element : CsvHead.values()) {
            sb.append(",").append(switch (element) {
                case id -> Integer.toString(task.getId());
                case type -> task.getType().toString();
                case name -> task.getTitle();
                case status -> task.getStatus().toString();
                case description -> task.getDescription();
                case epic -> task.getType().equals(TypeTask.SUB_TASK) ?
                        Integer.toString(((SubTask) task).getEpicTaskId()) : "";
            });
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }

    public static Task fromCsvLine(String value) {
        if (value == null || value.isEmpty()) return null;

        String[] parts = value.split(CSV_SEPARATOR, -1);
        if (parts.length < CsvHead.values().length) return null; // нарушена структура строки

        TypeTask type;
        if (stringInArrEnum(TypeTask.values(), parts[CsvHead.type.ordinal()])) {
            type = TypeTask.valueOf(parts[CsvHead.type.ordinal()]);
        } else {
            return null;
        }

        int id;
        if (isNumber(parts[CsvHead.id.ordinal()])) {
            id = Integer.parseInt(parts[CsvHead.id.ordinal()]);
        } else {
            return null;
        }

        StatusTask status;
        if (stringInArrEnum(StatusTask.values(), parts[CsvHead.status.ordinal()])) {
            status = StatusTask.valueOf(parts[CsvHead.status.ordinal()]);
        } else {
            return null;
        }

        String name = parts[CsvHead.name.ordinal()];
        String description = parts[CsvHead.description.ordinal()];
        Task task;
        task = switch (type) {
            case TypeTask.TASK -> new Task(name, description, id, status);
            case TypeTask.EPIC_TASK -> new EpicTask(name, description, id, status, new ArrayList<>());
            case TypeTask.SUB_TASK -> isNumber(parts[CsvHead.epic.ordinal()]) ?
                    new SubTask(name, description, id, status, Integer.parseInt(parts[CsvHead.epic.ordinal()])) : null;
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
