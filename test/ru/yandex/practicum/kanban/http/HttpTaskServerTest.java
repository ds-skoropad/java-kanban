package ru.yandex.practicum.kanban.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.FileBackedTaskManager;
import ru.yandex.practicum.kanban.manager.InMemoryTaskManager;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {

    static final String URI_FIRST = "http://localhost:8080";
    final TaskManager manager = new InMemoryTaskManager();
    final HttpTaskServer taskServer = new HttpTaskServer(manager);
    final Gson gson = HttpTaskServer.getGson();


    HttpTaskServerTest() throws IOException {
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {
    }

    @BeforeEach
    void setUp() {
        manager.clearTaskGroup();
        manager.clearSubGroup();
        manager.clearEpicGroup();
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    // Общие тесты

    // Должен быть 500: Internal Server Error
    @Test
    void shouldBeInternalServerError() throws IOException, InterruptedException {
        taskServer.stop();
        final Path ghostFile = Path.of("/tmp/no/such/place");
        final FileBackedTaskManager fb = new FileBackedTaskManager(ghostFile);
        HttpTaskServer server = new HttpTaskServer(fb);
        server.start();

        Task task = new Task("Task 1", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpResponse<String> response = sendPost("/tasks", taskJson);
        server.stop();

        assertEquals(500, response.statusCode());
    }

    // Должен быть 405: Method Not Allowed метод не поддерживается
    @Test
    void shouldBeMethodNotAllowed() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_FIRST + "/tasks"))
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
    }

    // Должно быть 404: Not Found для несуществующего пути
    @Test
    void shouldBeNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGet("/notfound");

        assertEquals(404, response.statusCode());
    }

    // Тесты списков

    // Должен быть 200: OK при запросе приоритетного списка задач
    @Test
    void shouldBeSuccessForGetPrioritized() throws IOException, InterruptedException {
        final Task task = new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(task);
        HttpResponse<String> response = sendGet("/prioritized");

        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(task, tasks.getFirst());
    }

    // Должен быть 200: OK при запросе истории
    @Test
    void shouldBeSuccessForGetHistory() throws IOException, InterruptedException {
        final Task task = new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(task);
        manager.getTask(1);
        HttpResponse<String> response = sendGet("/history");

        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(task, tasks.getFirst());
    }

    // Вспомогательные методы

    HttpResponse<String> sendGet(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_FIRST + path))
                .GET()
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