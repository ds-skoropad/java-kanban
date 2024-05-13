package ru.yandex.practicum.kanban.task;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    protected List<Integer> subTaskIds;

    public EpicTask(String title, String description) {
        super(title, description);
        this.subTaskIds = new ArrayList<>();
    }

    public EpicTask(String title, String description, int id, StatusTask status, List<Integer> subTaskIds) {
        super(title, description, id, status);
        this.subTaskIds = subTaskIds;
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public boolean addSubTaskId(int id) {
        if (subTaskIds.contains(id)) {
            return false;
        }
        return subTaskIds.add(id);
    }

    public boolean removeSubTaskId(Integer id) {
        return subTaskIds.remove(id); //
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
