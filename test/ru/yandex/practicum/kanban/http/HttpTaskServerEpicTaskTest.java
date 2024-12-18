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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerEpicTaskTest {

    static final String URI_FIRST = "http://localhost:8080";
    final TaskManager manager = new InMemoryTaskManager();
    final HttpTaskServer taskServer = new HttpTaskServer(manager);
    final Gson gson = HttpTaskServer.getGson();


    HttpTaskServerEpicTaskTest() throws IOException {
    }

    static class EpicListTypeToken extends TypeToken<List<EpicTask>> {
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

    // Тесты /epics

    // Должен быть 200: OK при запросе списка эпиков
    @Test
    void shouldBeSuccessForGetEpics() throws IOException, InterruptedException {
        final EpicTask epic = new EpicTask("Epic 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>());
        manager.addTask(epic);
        HttpResponse<String> response = sendGet("/epics");

        assertEquals(200, response.statusCode());

        List<EpicTask> epics = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals(epic, epics.getFirst());
    }

    // Должен быть 200: OK при запросе списка подзадач эпика
    @Test
    void shouldBeSuccessForGetEpicSubtasks() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>()));
        manager.addTask(new SubTask(1, "Subtask 1", "Description 1"));
        manager.addTask(new SubTask(1, "Subtask 2", "Description 2"));
        HttpResponse<String> response = sendGet("/epics/1/subtasks");

        assertEquals(200, response.statusCode());

        List<SubTask> subTasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertNotNull(subTasks);
        assertEquals(2, subTasks.size());
        assertEquals("Subtask 1", subTasks.getFirst().getTitle());
    }

    // Должен быть 200: OK при запросе существующего эпика по ID
    @Test
    void shouldBeSuccessForGetEpicById() throws IOException, InterruptedException {
        final EpicTask epic = new EpicTask("Epic 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>());
        manager.addTask(epic);
        HttpResponse<String> response = sendGet("/epics/1");

        assertEquals(200, response.statusCode());

        EpicTask epicJson = gson.fromJson(response.body(), EpicTask.class);

        assertNotNull(epicJson);
        assertEquals(epic, epicJson);
    }

    // Должен быть 404: Not Found при запросе НЕ существующего эпика по ID
    @Test
    void shouldBeNotFoundForGetEpicById() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>()));
        HttpResponse<String> response = sendGet("/epics/10");

        assertEquals(404, response.statusCode());
    }

    // Должен быть 200: OK при удалении существующего эпика по ID
    @Test
    void shouldBeSuccessForDeleteEpicById() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>()));
        HttpResponse<String> response = sendDelete("/epics/1");

        assertEquals(200, response.statusCode());
        assertTrue(manager.getEpicGroup().isEmpty());
    }

    // Должен быть 404: OK при удалении НЕ существующего эпика по ID
    @Test
    void shouldBeNotFoundForDeleteEpicById() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>()));
        HttpResponse<String> response = sendDelete("/epics/10");

        assertEquals(404, response.statusCode());
    }

    // Должно быть 200: OK при добавлении эпика
    @Test
    void shouldBeSuccessForAddEpic() throws IOException, InterruptedException {
        final EpicTask epic = new EpicTask("Epic 1", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>());
        String epicJson = gson.toJson(epic);
        HttpResponse<String> response = sendPost("/epics", epicJson);

        assertEquals(201, response.statusCode());

        List<EpicTask> epics = manager.getEpicGroup();

        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals("Epic 1", epics.getFirst().getTitle());
    }

    // Должно быть 200: OK при обновлении существующего эпика
    @Test
    void shouldBeSuccessForUpdateEpic() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>()));

        EpicTask updateEpic = new EpicTask("Updated Epic 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>());
        String epicJson = gson.toJson(updateEpic);
        HttpResponse<String> response = sendPost("/epics", epicJson);

        assertEquals(201, response.statusCode());

        List<EpicTask> epics = manager.getEpicGroup();

        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals("Updated Epic 1", epics.getFirst().getTitle());
    }

    // Должно быть 404: Not Found при обновлении НЕ существующего эпика
    @Test
    void shouldBeNotFoundForUpdateEpic() throws IOException, InterruptedException {
        manager.addTask(new EpicTask("Epic 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>()));

        EpicTask updateEpic = new EpicTask("Updated Epic 1", "Description 1", 10, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), new ArrayList<>());
        String epicJson = gson.toJson(updateEpic);
        HttpResponse<String> response = sendPost("/epics", epicJson);

        assertEquals(404, response.statusCode());

        List<EpicTask> epics = manager.getEpicGroup();

        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals("Epic 1", epics.getFirst().getTitle());
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