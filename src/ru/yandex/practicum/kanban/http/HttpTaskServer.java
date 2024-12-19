package ru.yandex.practicum.kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.http.adapter.DurationAdapter;
import ru.yandex.practicum.kanban.http.adapter.LocalDateTimeAdapter;
import ru.yandex.practicum.kanban.http.handler.*;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final int BACK_LOG = 0;
    private static final int STOP_DELAY = 0;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), BACK_LOG);

        Gson gson = getGson();
        server.createContext("/tasks", new TaskHttpHandler(manager, gson));
        server.createContext("/epics", new EpicHttpHandler(manager, gson));
        server.createContext("/subtasks", new SubtaskHttpHandler(manager, gson));
        server.createContext("/history", new HistoryHttpHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHttpHandler(manager, gson));
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(STOP_DELAY);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
