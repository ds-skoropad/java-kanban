package ru.yandex.practicum.kanban.task;

public class SubTask extends Task{
    protected int epicTaskId;

    public SubTask(String title, String description) { // Исправлено!
        super(title, description);
    }

    public SubTask(String title, String description, int id, StatusTask status, int epicTaskId) {
        super(title, description, id, status);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epicTaskId=" + epicTaskId +
                '}';
    }
}
