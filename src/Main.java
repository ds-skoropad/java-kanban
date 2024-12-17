import ru.yandex.practicum.kanban.manager.HistoryManager;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static TaskManager taskManager;
    public static HistoryManager historyManager;

    public static void main(String[] args) {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();

        System.out.println("TASK MANAGER | Version 0.3\n");

        IntStream.rangeClosed(1, 3)
                .mapToObj(i -> Stream.of(
                        new Task("Task " + i, "Description"),
                        new EpicTask("Epic " + i, "Description"),
                        new SubTask(i, "Sub " + i, "Description")))
                .flatMap(Function.identity())
                .sorted(Comparator.comparing(Task::getType).reversed())
                .forEach(taskManager::addTask);

        printAllTask();
    }

    public static void printAllTask() {
        Stream.of(Stream.of(taskManager.getEpicGroup(), taskManager.getTaskGroup(), taskManager.getSubGroup()))
                .flatMap(Function.identity())
                .flatMap(List::stream)
                .sorted(Comparator.comparing(Task::getId))
                .forEach(System.out::println);

    }
}
