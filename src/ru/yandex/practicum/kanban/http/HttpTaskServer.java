package ru.yandex.practicum.kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.http.handler.*;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.StatusTask;
import ru.yandex.practicum.kanban.util.TaskUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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
                .registerTypeAdapter(StatusTask.class, new StatusTaskAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    private static class StatusTaskAdapter extends TypeAdapter<StatusTask> {

        @Override
        public void write(final JsonWriter jsonWriter, final StatusTask statusTask) throws IOException {
            jsonWriter.value(statusTask.name());
        }

        @Override
        public StatusTask read(final JsonReader jsonReader) throws IOException {
            String statusTask = jsonReader.nextString();
            if (TaskUtils.stringInArrEnum(StatusTask.values(), statusTask)) {
                return StatusTask.valueOf(statusTask);
            } else {
                throw new HttpServerGsonException("Json field parsing error: status=" + statusTask);
            }
        }
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.value("null");
            } else {
                jsonWriter.value(localDateTime.format(ISO_LOCAL_DATE_TIME));
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            try {
                String localDateTime = jsonReader.nextString();
                return (localDateTime.equals("null") || localDateTime.isEmpty())
                        ? null : LocalDateTime.parse(localDateTime, ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                throw new HttpServerGsonException(e.getMessage());
            }
        }
    }

    private static class DurationAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            jsonWriter.value(duration.getSeconds());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            String duration = jsonReader.nextString();
            if (TaskUtils.isNumber(duration)) {
                return Duration.ofSeconds(Long.parseLong(duration));
            } else {
                throw new HttpServerGsonException("Json field parsing error: duration=" + duration);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
