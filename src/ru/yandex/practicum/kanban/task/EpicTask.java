package ru.yandex.practicum.kanban.task;

import java.util.ArrayList;

public class EpicTask extends Task {
    protected ArrayList<Integer> subTaskIds;

    public EpicTask(String title, String description) { // Добавлено!
        super(title, description);
        this.subTaskIds = new ArrayList<>();
    }

    public EpicTask(String title, String description, int id, StatusTask status, ArrayList<Integer> subTaskIds) {
        super(title, description, id, status);
        this.subTaskIds = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public boolean addSubTaskId(int id) {
        if (subTaskIds.contains(id)) {
            return false;
        }
        return subTaskIds.add(id);
    }

    public boolean removeSubTaskId(Integer id) {
        return subTaskIds.remove(id); // Исправлено!
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subTaskIds=" + subTaskIds +
                '}';
    }
}
