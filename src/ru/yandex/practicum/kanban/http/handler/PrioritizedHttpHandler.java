package ru.yandex.practicum.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHttpHandler  extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Route route = new Route()
                .add("/prioritized", RequestMethod.GET, this::getPrioritizedTasks)
                .build();
        runRoute(h, route);
    }

    public HandlerExchange getPrioritizedTasks(RouteExchange routeExchange) {
        List<Task> tasks = manager.getPrioritizedTasks();
        String text = gson.toJson(tasks);
        return sendSuccess(text);
    }
}