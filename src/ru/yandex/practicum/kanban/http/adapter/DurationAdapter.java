package ru.yandex.practicum.kanban.http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.kanban.http.HttpServerGsonException;
import ru.yandex.practicum.kanban.util.TaskUtils;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

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
