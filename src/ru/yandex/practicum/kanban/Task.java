package ru.yandex.practicum.kanban;

import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected StatusTask status;

    public Task(String title, String description) { // Добавлено!
        this.title = title;
        this.description = description;
        this.id = 0;
        this.status = StatusTask.NEW;
    }

    public Task(String title, String description, int id, StatusTask status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Исправлено! Да, я использую автогенерацию.
        // Я предположил, что можно вернуть просто ID т.к. он уникален и не требуется лишняя нагрузка.
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
