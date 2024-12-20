package ru.yandex.practicum.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHttpHandler extends BaseHttpHandler {

    public HistoryHttpHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Route route = new Route()
                .add("/history", RequestMethod.GET, this::getHistoryTasks)
                .build();
        runRoute(h, route);
    }

    private HandlerExchange getHistoryTasks(RouteExchange routeExchange) {
        List<Task> tasks = manager.getHistory();
        String text = gson.toJson(tasks);
        return sendSuccess(text);
    }
}