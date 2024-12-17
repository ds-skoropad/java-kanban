package ru.yandex.practicum.kanban.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    protected int epicId;

    public SubTask(int epicId, String title, String description, int id, StatusTask status, Duration duration,
                   LocalDateTime startTime) {
        super(title, description, id, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(int epicId, String title, String description, int id) {
        super(title, description, id);
        this.epicId = epicId;
    }

    public SubTask(int epicId, String title, String description) {
        super(title, description);
        this.epicId = epicId;
    }

    public SubTask(int epicId) {
        super();
        this.epicId = epicId;
    }

    public SubTask() {
        super();
        this.epicId = 0;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUB_TASK;
    }

    @Override
    public String toString() {
        return "Sub{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", epicId=" + epicId +
                '}';
    }
}
