package ru.yandex.practicum.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.manager.ManagerOverlayTaskException;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.util.TaskUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubtaskHttpHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Route route = new Route()
                .add("/subtasks", RequestMethod.GET, this::getSubtasks)
                .add("/subtasks/{id}", RequestMethod.GET, this::getSubtaskById)
                .add("/subtasks/{id}", RequestMethod.DELETE, this::removeSubtask)
                .add("/subtasks", RequestMethod.POST, this::addSubtask)
                .build();
        runRoute(h, route);
    }

    public HandlerExchange getSubtasks(RouteExchange routeExchange) {
        List<SubTask> sub = manager.getSubGroup();
        String answer = gson.toJson(sub);
        return sendSuccess(answer);
    }

    public HandlerExchange getSubtaskById(RouteExchange routeExchange) {
        String textId = routeExchange.getKeys().get("id");
        if (textId == null || !TaskUtils.isNumber(textId)) {
            return sendNotFound();
        }
        int id = Integer.parseInt(textId);
        Optional<SubTask> sub = manager.getSub(id);
        if (sub.isPresent()) {
            String answer = gson.toJson(sub.get());
            return sendSuccess(answer);
        } else {
            return sendNotFound();
        }
    }

    public HandlerExchange removeSubtask(RouteExchange routeExchange) {
        String textId = routeExchange.getKeys().get("id");
        if (textId == null || !TaskUtils.isNumber(textId)) {
            return sendNotFound();
        }
        int id = Integer.parseInt(textId);
        return (manager.removeSub(id)) ? sendSuccess("Deleted") : sendNotFound();
    }

    public HandlerExchange addSubtask(RouteExchange routeExchange) {
        String text = routeExchange.getRequestBody();
        SubTask sub = gson.fromJson(text, SubTask.class);
        if (sub == null) {
            return sendNotFound();
        }
        HandlerExchange result;
        try {
            if (sub.getId() == 0) {
                manager.addTask(sub);
                result = sendSuccessModify("Created");
            } else {
                result = manager.updateTask(sub) ? sendSuccessModify("Updated") : sendNotFound();
            }
        } catch (ManagerOverlayTaskException e) {
            result = sendNotAcceptable();
        }
        return result;
    }
}