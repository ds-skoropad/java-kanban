package ru.yandex.practicum.kanban.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EpicTask extends Task {
    protected List<Integer> subIds;
    protected LocalDateTime endTime;

    public EpicTask() {
        super();
        this.subIds = new ArrayList<>();
    }

    public EpicTask(String title, String description) {
        super(title, description);
        this.subIds = new ArrayList<>();
    }

    public EpicTask(String title, String description, int id, StatusTask status, List<Integer> subIds) {
        super(title, description, id, status);
        this.subIds = subIds;
    }

    public EpicTask(String title, String description, int id, StatusTask status, List<Integer> subIds,
                    Duration duration, LocalDateTime startTime) {
        super(title, description, id, status, duration, startTime);
        this.subIds = subIds;
    }

    public List<Integer> getSubIds() {
        return subIds;
    }

    public void setSubIds(List<Integer> subIds) {
        this.subIds = subIds;
    }

    public boolean addSubId(int id) {
        if (subIds.contains(id)) {
            return false;
        }
        return subIds.add(id);
    }

    public boolean removeSubId(Integer id) {
        return subIds.remove(id); //
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC_TASK;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", subIds=" + subIds +
                '}';
    }
}
