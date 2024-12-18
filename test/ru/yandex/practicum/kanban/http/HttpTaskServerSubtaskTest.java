package ru.yandex.practicum.kanban.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.InMemoryTaskManager;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.EpicTask;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.SubTask;

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

class HttpTaskServerSubtaskTest {

    static final String URI_FIRST = "http://localhost:8080";
    final TaskManager manager = new InMemoryTaskManager();
    final HttpTaskServer taskServer = new HttpTaskServer(manager);
    final Gson gson = HttpTaskServer.getGson();


    HttpTaskServerSubtaskTest() throws IOException {
    }

    static class SubtaskListTypeToken extends TypeToken<List<SubTask>> {
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

    // Тесты /subtasks

    // Должен быть 200: OK при запросе списка подзадач
    @Test
    void shouldBeSuccessForGetSubtasks() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1"));
        final SubTask subTask = new SubTask(1, "Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(subTask);
        HttpResponse<String> response = sendGet("/subtasks");

        assertEquals(200, response.statusCode());

        List<SubTask> subTasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertEquals(subTask, subTasks.getFirst());
    }

    // Должен быть 200: OK при запросе существующей подзадачи по ID
    @Test
    void shouldBeSuccessForGetSubtaskById() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1")); // id=1
        final SubTask subTask = new SubTask(1, "Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(subTask);

        HttpResponse<String> response = sendGet("/subtasks/2");

        assertEquals(200, response.statusCode());

        SubTask subTaskJson = gson.fromJson(response.body(), SubTask.class);

        assertEquals(subTask, subTaskJson);
    }

    // Должен быть 404: Not Found при запросе НЕ существующей подзадачи по ID
    @Test
    void shouldBeNotFoundForGetSubtaskById() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1")); // id=1
        final SubTask subTask = new SubTask(1, "Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(subTask);
        HttpResponse<String> response = sendGet("/subtasks/10");

        assertEquals(404, response.statusCode());
    }

    // Должен быть 200: OK при удалении существующей подзадачи по ID
    @Test
    void shouldBeSuccessForDeleteSubtaskById() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1")); // id=1
        final SubTask subTask = new SubTask(1, "Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(subTask);
        HttpResponse<String> response = sendDelete("/subtasks/2");

        assertEquals(200, response.statusCode());
    }

    // Должен быть 404: OK при удалении НЕ существующей подзадачи по ID
    @Test
    void shouldBeNotFoundForDeleteSubtaskById() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1")); // id=1
        final SubTask subTask = new SubTask(1, "Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(subTask);
        HttpResponse<String> response = sendDelete("/subtasks/10");

        assertEquals(404, response.statusCode());
    }

    // Должно быть 201: OK при добавлении подзадачи
    @Test
    void shouldBeSuccessForAddSubtask() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1")); // id=1
        final SubTask subTask = new SubTask(1, "Subtask 1", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String subTaskJson = gson.toJson(subTask);
        HttpResponse<String> response = sendPost("/subtasks", subTaskJson);

        assertEquals(201, response.statusCode());

        List<SubTask> subTasks = manager.getSubGroup();

        assertNotNull(subTasks);
        assertEquals(1, subTasks.size());
        assertEquals("Subtask 1", subTasks.getFirst().getTitle());
    }

    // Должно быть 201: OK при обновлении существующей подзадачи (id эпика не обновляется)
    @Test
    void shouldBeSuccessForUpdateSubtask() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1")); // id=1
        final SubTask subTask = new SubTask(1, "Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(subTask);

        SubTask updateSubtask = new SubTask(1, "Updated Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String subTaskJson = gson.toJson(updateSubtask);
        HttpResponse<String> response = sendPost("/subtasks", subTaskJson);

        assertEquals(201, response.statusCode());

        List<SubTask> subTasks = manager.getSubGroup();

        assertNotNull(subTasks);
        assertEquals(1, subTasks.size());
        assertEquals("Updated Subtask 1", subTasks.getFirst().getTitle());
    }

    // Должно быть 404: Not Found при обновлении НЕ существующей подзадачи
    @Test
    void shouldBeNotFoundForUpdateSubtask() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1")); // id=1
        final SubTask subTask = new SubTask(1, "Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(subTask);

        SubTask updateSubtask = new SubTask(1, "Updated Subtask 1", "Description 1", 10, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String subTaskJson = gson.toJson(updateSubtask);
        HttpResponse<String> response = sendPost("/subtasks", subTaskJson);

        assertEquals(404, response.statusCode());

        List<SubTask> subTasks = manager.getSubGroup();

        assertNotNull(subTasks);
        assertEquals(1, subTasks.size());
        assertEquals("Subtask 1", subTasks.getFirst().getTitle());
    }

    // Должно быть 406: Not Acceptable при добавлении подзадачи с пересечением
    @Test
    void shouldBeNotAcceptableForAddSubtask() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1")); // id=1
        manager.addTask(new SubTask(1, "Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now()));

        final SubTask subTask = new SubTask(1, "Subtask 2", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String subTaskJson = gson.toJson(subTask);
        HttpResponse<String> response = sendPost("/subtasks", subTaskJson);

        assertEquals(406, response.statusCode());
    }

    // Должно быть 406: Not Acceptable при обновлении подзадачи с пересечением
    @Test
    void shouldBeNotAcceptableForUpdateSubtask() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1")); // id=1
        manager.addTask(new SubTask(1, "Subtask 1", "Description 1", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now())); // id=2
        manager.addTask(new SubTask(1, "Subtask 1", "Description 1", 3, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(30))); // id=3

        final SubTask subTask = new SubTask(1, "Subtask 2", "Description 1", 3, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now()); // обновляем №3, пересечение с №2
        String subTaskJson = gson.toJson(subTask);
        HttpResponse<String> response = sendPost("/subtasks", subTaskJson);

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