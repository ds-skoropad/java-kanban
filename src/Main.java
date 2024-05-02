/*
 * Приветствую, Сергей.
 * Как и всегда, буду рад любому комментарию или замечанию.
 */


import ru.yandex.practicum.kanban.*;

import java.util.ArrayList;

public class Main {
    public static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {

        System.out.println("TASK MANAGER [Version 0.1]\n");

        for (int i = 1; i < 3; i++) {
            taskManager.add(new Task("Задача " + i, "Описание задачи " + i, 0, StatusTask.NEW));
        }

        for (int i = 1; i < 3; i++) {
            int epicId = taskManager.add(new EpicTask("Сверхзадача.." + i, "Описание сверхзадачи.." + i,
                    0, StatusTask.NEW, new ArrayList<Integer>()));
            for (int j = 1; j < i + 1; j++) {
                taskManager.add(new SubTask("Подзадача...." + j, "Описание подзадачи...." + j,
                        0, StatusTask.NEW, epicId));
            }
        }
        printAllTask();

        System.out.println("MODIFY: updateTask{id=1}, result=" + taskManager.update(new Task("Задача 1",
                "Описание задачи 1", 1, StatusTask.IN_PROGRESS)));
        System.out.println("MODIFY: updateTask{id=2}, result=" + taskManager.update(new Task("Задача 2",
                "Описание задачи 2", 2, StatusTask.DONE)));
        System.out.println("MODIFY: updateTask{id=9}, result=" + taskManager.update(new Task("Задача 3",
                "Описание задачи 3", 9, StatusTask.DONE)));
        printAllTask();

        System.out.println("MODIFY: removeTask{id=1}, result=" + taskManager.removeTask(1));
        System.out.println("MODIFY: removeTask{id=2}, result=" + taskManager.removeTask(2));
        System.out.println("MODIFY: removeTask{id=9}, result=" + taskManager.removeTask(9));
        printAllTask();

        System.out.println("MODIFY: updateSubTask{id=4}, result=" + taskManager.update(new SubTask("Подзадача....1",
                "Описание подзадачи....1", 4, StatusTask.IN_PROGRESS, 3)));
        System.out.println("MODIFY: updateSubTask{id=6}, result=" + taskManager.update(new SubTask("Подзадача....1",
                "Описание подзадачи....1", 6, StatusTask.DONE, 5)));
        System.out.println("MODIFY: updateSubTask{id=9}, result=" + taskManager.update(new SubTask("Подзадача....3",
                "Описание подзадачи....3", 9, StatusTask.DONE, 5)));
        printAllTask();

        System.out.println("MODIFY: updateSubTask{id=4}, result=" + taskManager.update(new SubTask("Подзадача....1",
                "Описание подзадачи....1", 4, StatusTask.NEW, 3)));
        System.out.println("MODIFY: updateSubTask{id=7}, result=" + taskManager.update(new SubTask("Подзадача....2",
                "Описание подзадачи....2", 7, StatusTask.DONE, 5)));
        printAllTask();

        System.out.println("MODIFY: removeSubTask{id=6}, result=" + taskManager.removeSubTask(6));
        System.out.println("MODIFY: removeSubTask{id=7}, result=" + taskManager.removeSubTask(7));
        System.out.println("MODIFY: removeSubTask{id=9}, result=" + taskManager.removeSubTask(9));
        printAllTask();

        System.out.println("MODIFY: removeEpicTask{id=3}, result=" + taskManager.removeEpicTask(3));
        System.out.println("MODIFY: removeEpicTask{id=9}, result=" + taskManager.removeEpicTask(9));
        printAllTask();

    }

    public static void printAllTask() {
        for (Task task : taskManager.getTaskGroup().values()) {
            System.out.println(task);
        }
        for (EpicTask epicTask : taskManager.getEpicTaskGroup().values()) {
            System.out.println(epicTask);
            for (SubTask subTask : taskManager.getSubTaskGroup(epicTask.getId()).values()) {
                System.out.println(" " + subTask);
            }
        }
        System.out.println();
    }
}
