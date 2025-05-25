package ru.yandex.practicum.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.kanban.manager.ManagerOverlayTaskException;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.Task;
import ru.yandex.practicum.kanban.util.TaskUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TaskHttpHandler extends BaseHttpHandler {

    public TaskHttpHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Route route = new Route()
                .add("/tasks", RequestMethod.GET, this::getTasks)
                .add("/tasks/{id}", RequestMethod.GET, this::getTaskById)
                .add("/tasks/{id}", RequestMethod.DELETE, this::removeTask)
                .add("/tasks", RequestMethod.POST, this::addTask)
                .build();
        runRoute(h, route);
    }

    private HandlerExchange getTasks(RouteExchange routeExchange) {
        List<Task> tasks = manager.getTaskGroup();
        String answer = gson.toJson(tasks);
        return sendSuccess(answer);
    }

    private HandlerExchange getTaskById(RouteExchange routeExchange) {
        String textId = routeExchange.getKeys().get("id");
        if (textId == null || !TaskUtils.isNumber(textId)) {
            return sendNotFound();
        }
        int id = Integer.parseInt(textId);
        Optional<Task> task = manager.getTask(id);
        if (task.isPresent()) {
            String answer = gson.toJson(task.get());
            return sendSuccess(answer);
        } else {
            return sendNotFound();
        }
    }

    private HandlerExchange removeTask(RouteExchange routeExchange) {
        String textId = routeExchange.getKeys().get("id");
        if (textId == null || !TaskUtils.isNumber(textId)) {
            return sendNotFound();
        }
        int id = Integer.parseInt(textId);
        return (manager.removeTask(id)) ? sendSuccess("Deleted") : sendNotFound();
    }

    private HandlerExchange addTask(RouteExchange routeExchange) {
        String text = routeExchange.getRequestBody();
        Task task = gson.fromJson(text, Task.class);
        if (task == null) {
            return sendNotFound();
        }
        HandlerExchange result;
        try {
            if (task.getId() == 0) {
                manager.addTask(task);
                result = sendSuccessModify("Created");
            } else {
                result = manager.updateTask(task) ? sendSuccessModify("Updated") : sendNotFound();
            }
        } catch (ManagerOverlayTaskException e) {
            result = sendNotAcceptable();
        }
        return result;
    }
}