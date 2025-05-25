package ru.yandex.practicum.kanban.http;

import com.google.gson.Gson;
import ru.yandex.practicum.kanban.manager.InMemoryTaskManager;
import ru.yandex.practicum.kanban.manager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class HttpTaskServerTest {

    static final String URI_FIRST = "http://localhost:8080";
    final TaskManager manager = new InMemoryTaskManager();
    final HttpTaskServer taskServer = new HttpTaskServer(manager);
    final Gson gson = HttpTaskServer.getGson();
    String endPoint;

    protected HttpTaskServerTest() throws IOException {
    }

    public void setUp() throws IOException, InterruptedException {
        manager.clearTaskGroup();
        manager.clearSubGroup();
        manager.clearEpicGroup();

        taskServer.start();
    }

    void tearDown() {
        taskServer.stop();
    }

    // Вспомогательные методы

    HttpResponse<String> sendGet(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_FIRST + path))
                .GET()
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendDelete(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_FIRST + path))
                .DELETE()
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendPost(String path, String text) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_FIRST + path))
                .POST(HttpRequest.BodyPublishers.ofString(text))
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}