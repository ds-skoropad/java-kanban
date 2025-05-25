package ru.yandex.practicum.kanban.http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.kanban.http.HttpServerGsonException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

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
