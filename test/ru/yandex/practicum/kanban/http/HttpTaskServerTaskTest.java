package ru.yandex.practicum.kanban.http;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.task.Task;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTaskTest extends HttpTaskServerTest {

    Task task;

    protected HttpTaskServerTaskTest() throws IOException {
    }

    public static class TaskListTypeToken extends TypeToken<List<Task>> {
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        super.setUp();
        task = new Task("Task 1", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(task);
        endPoint = "/tasks";
    }

    @AfterEach
    public void tearDown() {
        super.tearDown();
    }

    // Должен быть 200: OK при запросе списка задач
    @Test
    void shouldBeSuccessForGetTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGet(endPoint);
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(task, tasks.getFirst());
    }

    // Должен быть 200: OK при запросе существующей задачи по ID
    @Test
    void shouldBeSuccessForGetTaskById() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGet(endPoint + "/1");
        assertEquals(200, response.statusCode());

        Task taskJson = gson.fromJson(response.body(), Task.class);
        assertEquals(task, taskJson);
    }

    // Должен быть 404: Not Found при запросе НЕ существующей задачи по ID
    @Test
    void shouldBeNotFoundForGetTaskById() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGet(endPoint + "/10");
        assertEquals(404, response.statusCode());
    }

    // Должен быть 200: OK при удалении существующей задачи по ID
    @Test
    void shouldBeSuccessForDeleteTaskById() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDelete(endPoint + "/1");
        assertEquals(200, response.statusCode());
    }

    // Должен быть 404: OK при удалении НЕ существующей задачи по ID
    @Test
    void shouldBeNotFoundForDeleteTaskById() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDelete(endPoint + "/10");
        assertEquals(404, response.statusCode());
    }

    // Должно быть 200: OK при добавлении задачи
    @Test
    void shouldBeSuccessForAddTask() throws IOException, InterruptedException {
        manager.clearTaskGroup();
        task = new Task("Task 1", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpResponse<String> response = sendPost(endPoint, taskJson);
        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getTaskGroup();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.getFirst().getTitle());
    }

    // Должно быть 200: OK при обновлении существующей задачи
    @Test
    void shouldBeSuccessForUpdateTask() throws IOException, InterruptedException {
        Task updateTask = new Task("Updated Task 1", "Description 1", 1, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(updateTask);
        HttpResponse<String> response = sendPost(endPoint, taskJson);
        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getTaskGroup();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Updated Task 1", tasks.getFirst().getTitle());
    }

    // Должно быть 404: Not Found при обновлении НЕ существующей задачи
    @Test
    void shouldBeNotFoundForUpdateTask() throws IOException, InterruptedException {
        Task updateTask = new Task("Updated Task 1", "Description 1", 10, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(updateTask);
        HttpResponse<String> response = sendPost(endPoint, taskJson);
        assertEquals(404, response.statusCode());

        List<Task> tasks = manager.getTaskGroup();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.getFirst().getTitle());
    }

    // Должно быть 406: Not Acceptable при добавлении задачи с пересечением
    @Test
    void shouldBeNotAcceptableForAddTask() throws IOException, InterruptedException {
        Task newTask = new Task("Task 1", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(newTask);
        HttpResponse<String> response = sendPost(endPoint, taskJson);

        assertEquals(406, response.statusCode());
    }

    // Должно быть 406: Not Acceptable при обновлении задачи с пересечением
    @Test
    void shouldBeNotAcceptableForUpdateTask() throws IOException, InterruptedException {
        manager.addTask(new Task("Task 2", "Description 1", 0, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(30))); // id=2

        Task task = new Task("Update Task 2", "Description 2", 2, StatusTask.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpResponse<String> response = sendPost(endPoint, taskJson);

        assertEquals(406, response.statusCode());
    }
}
