package ru.yandex.practicum.kanban.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.InMemoryTaskManager;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTaskTest {

    static final String URI_FIRST = "http://localhost:8080";
    final TaskManager manager = new InMemoryTaskManager();
    final HttpTaskServer taskServer = new HttpTaskServer(manager);
    final Gson gson = HttpTaskServer.getGson();


    HttpTaskServerTaskTest() throws IOException {
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

    // Тесты /tasks

    // Должен быть 200: OK при запросе списка задач
    @Test
    void shouldBeSuccessForGetTasks() throws IOException, InterruptedException {
        final Task task = new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(task);
        HttpResponse<String> response = sendGet("/tasks");

        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(task, tasks.getFirst());
    }

    // Должен быть 200: OK при запросе существующей задачи по ID
    @Test
    void shouldBeSuccessForGetTaskById() throws IOException, InterruptedException {
        final Task task = new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(task);

        HttpResponse<String> response = sendGet("/tasks/1");

        assertEquals(200, response.statusCode());

        Task taskJson = gson.fromJson(response.body(), Task.class);

        assertEquals(task, taskJson);
    }

    // Должен быть 404: Not Found при запросе НЕ существующей задачи по ID
    @Test
    void shouldBeNotFoundForGetTaskById() throws IOException, InterruptedException {
        manager.addTask(new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now()));
        HttpResponse<String> response = sendGet("/tasks/10");

        assertEquals(404, response.statusCode());
    }

    // Должен быть 200: OK при удалении существующей задачи по ID
    @Test
    void shouldBeSuccessForDeleteTaskById() throws IOException, InterruptedException {
        manager.addTask(new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now()));
        HttpResponse<String> response = sendDelete("/tasks/1");

        assertEquals(200, response.statusCode());
    }

    // Должен быть 404: OK при удалении НЕ существующей задачи по ID
    @Test
    void shouldBeNotFoundForDeleteTaskById() throws IOException, InterruptedException {
        manager.addTask(new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now()));
        HttpResponse<String> response = sendDelete("/tasks/10");

        assertEquals(404, response.statusCode());
    }

    // Должно быть 200: OK при добавлении задачи
    @Test
    void shouldBeSuccessForAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpResponse<String> response = sendPost("/tasks", taskJson);

        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getTaskGroup();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.getFirst().getTitle());
    }

    // Должно быть 200: OK при обновлении существующей задачи
    @Test
    void shouldBeSuccessForUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(task);

        Task updateTask = new Task("Updated Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(updateTask);
        HttpResponse<String> response = sendPost("/tasks", taskJson);

        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getTaskGroup();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Updated Task 1", tasks.getFirst().getTitle());
    }

    // Должно быть 404: Not Found при обновлении НЕ существующей задачи
    @Test
    void shouldBeNotFoundForUpdateTask() throws IOException, InterruptedException {
        manager.addTask(new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now()));

        Task updateTask = new Task("Updated Task 1", "Description 1", 10, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(updateTask);
        HttpResponse<String> response = sendPost("/tasks", taskJson);

        assertEquals(404, response.statusCode());

        List<Task> tasks = manager.getTaskGroup();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.getFirst().getTitle());
    }

    // Должно быть 406: Not Acceptable при добавлении задачи с пересечением
    @Test
    void shouldBeNotAcceptableForAddTask() throws IOException, InterruptedException {
        manager.addTask(new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now()));
        Task task2 = new Task("Task 2", "Description 2", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(task2);
        HttpResponse<String> response = sendPost("/tasks", taskJson);

        assertEquals(406, response.statusCode());
    }

    // Должно быть 406: Not Acceptable при обновлении задачи с пересечением
    @Test
    void shouldBeNotAcceptableForUpdateTask() throws IOException, InterruptedException {
        manager.addTask(new Task("Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now()));
        manager.addTask(new Task("Task 2", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(30)));

        Task task = new Task("Update Task 2", "Description 2", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpResponse<String> response = sendPost("/tasks", taskJson);

        assertEquals(406, response.statusCode());
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