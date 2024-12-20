package ru.yandex.practicum.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.util.TaskUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Route route = new Route()
                .add("/epics", RequestMethod.GET, this::getEpics)
                .add("/epics/{id}", RequestMethod.GET, this::getEpicById)
                .add("/epics/{id}/subtasks", RequestMethod.GET, this::getEpicSubtasks)
                .add("/epics/{id}", RequestMethod.DELETE, this::removeEpic)
                .add("/epics", RequestMethod.POST, this::addEpic)
                .build();
        runRoute(h, route);
    }

    private HandlerExchange getEpics(RouteExchange routeExchange) {
        List<EpicTask> epics = manager.getEpicGroup();
        String text = gson.toJson(epics);
        return sendSuccess(text);
    }

    private HandlerExchange getEpicById(RouteExchange routeExchange) {
        String textId = routeExchange.getKeys().get("id");
        if (textId == null || !TaskUtils.isNumber(textId)) {
            return sendNotFound();
        }
        int id = Integer.parseInt(textId);
        Optional<EpicTask> epic = manager.getEpic(id);
        if (epic.isPresent()) {
            String answer = gson.toJson(epic.get());
            return sendSuccess(answer);
        } else {
            return sendNotFound();
        }
    }

    private HandlerExchange getEpicSubtasks(RouteExchange routeExchange) {
        String textId = routeExchange.getKeys().get("id");
        if (textId == null || !TaskUtils.isNumber(textId)) {
            return sendNotFound();
        }
        int id = Integer.parseInt(textId);
        List<SubTask> subs = manager.getEpicSubTasks(id);
        String text = gson.toJson(subs);
        return sendSuccess(text);
    }

    private HandlerExchange removeEpic(RouteExchange routeExchange) {
        String textId = routeExchange.getKeys().get("id");
        if (textId == null || !TaskUtils.isNumber(textId)) {
            return sendNotFound();
        }
        int id = Integer.parseInt(textId);
        return (manager.removeEpic(id)) ? sendSuccess("Deleted") : sendNotFound();
    }

    private HandlerExchange addEpic(RouteExchange routeExchange) {
        String text = routeExchange.getRequestBody();
        EpicTask epic = gson.fromJson(text, EpicTask.class);
        if (epic == null) {
            return sendNotFound();
        }
        if (epic.getId() == 0) {
            manager.addTask(epic);
            return sendSuccessModify("Created");
        } else {
            return manager.updateTask(epic) ? sendSuccessModify("Updated") : sendNotFound();
        }
    }
}