package ru.yandex.practicum.kanban;

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

    public boolean removeSubTaskId(int id) {
        return subTaskIds.remove((Integer) id); // Исправлено! Перемудрил :)
        // А ведь помню, что идея предлагала remove по объекту, и зачем я прицепился к примитиву...
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
