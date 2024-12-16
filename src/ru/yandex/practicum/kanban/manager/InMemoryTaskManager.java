package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskGroup;
    protected final Map<Integer, SubTask> subGroup;
    protected final Map<Integer, EpicTask> epicGroup;
    private final HistoryManager historyManager;
    private final Map<Integer, Boolean> timeLine; // 35040 интервалов по 15 мин в году
    protected final Set<Task> prioritizedTasks;
    private final LocalDateTime timeLineStart; // старт планирования
    private static final Comparator<Task> compareStartTime = Comparator.comparing(Task::getStartTime,
            Comparator.comparing(startTime -> startTime.orElse(LocalDateTime.MIN)));
    private static final int TIME_LINE_DAYS = 365;
    private static final int MINUTE_INTERVAL = 15;
    private static final int MAX_INTERVALS = TIME_LINE_DAYS * 24 * 60 / MINUTE_INTERVAL;
    protected int nextId;

    public InMemoryTaskManager() {
        this.taskGroup = new HashMap<>();
        this.subGroup = new HashMap<>();
        this.epicGroup = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        this.timeLineStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
        this.prioritizedTasks = new TreeSet<>(compareStartTime);
        this.nextId = 1;
        this.timeLine = IntStream.rangeClosed(1, MAX_INTERVALS)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> false));
    }

    @Override
    public Optional<Task> getTask(int id) {
        Optional<Task> task = Optional.ofNullable(taskGroup.get(id));

        task.ifPresent(historyManager::add);
        return task;
    }

    @Override
    public Optional<SubTask> getSub(int id) {
        Optional<SubTask> sub = Optional.ofNullable(subGroup.get(id));

        sub.ifPresent(historyManager::add);
        return sub;
    }

    @Override
    public Optional<EpicTask> getEpic(int id) {
        Optional<EpicTask> epic = Optional.ofNullable(epicGroup.get(id));

        epic.ifPresent(historyManager::add);
        return epic;
    }

    @Override
    public List<Task> getTaskGroup() {
        return new ArrayList<>(taskGroup.values());
    }

    @Override
    public List<SubTask> getSubGroup() {
        return new ArrayList<>(subGroup.values());
    }

    @Override
    public List<EpicTask> getEpicGroup() {
        return new ArrayList<>(epicGroup.values());
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        EpicTask epic = epicGroup.get(epicId);
        return (epic == null) ? new ArrayList<>() : epic.getSubIds().stream()
                .filter(subGroup::containsKey)
                .map(subGroup::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public int addTask(Task task) {
        if (task == null) return 0;

        task.setId(nextId);
        switch (task.getType()) {
            case SUB_TASK -> {
                SubTask sub = (SubTask) task;
                EpicTask epic = epicGroup.get(sub.getEpicId());

                if (epic == null) {
                    return 0;
                }

                prioritizedTasksAdd(task);
                subGroup.put(nextId, sub);
                epic.addSubId(nextId);
                updateStatusEpicTask(epic.getId());

                // Вычисление времени эпика. Экономия вызова updateTimeEpicTask()
                if (sub.getStartTime().isPresent() && sub.getEndTime().isPresent()) {
                    if (epic.getStartTime().isEmpty() ||
                            epic.getStartTime().get().isAfter(sub.getStartTime().get())) {
                        epic.setStartTime(sub.getStartTime().get());
                    }
                    if (epic.getEndTime().isEmpty() || epic.getEndTime().get().isBefore(sub.getEndTime().get())) {
                        epic.setEndTime(sub.getEndTime().get());
                    }
                }
                // Продолжительность эпика это продолжительность всех подзадач, даже если нет времени начала у подзадачи
                epic.setDuration(epic.getDuration().plus(sub.getDuration()));
            }
            case EPIC_TASK -> epicGroup.put(nextId, new EpicTask(task.getTitle(), task.getDescription(), nextId));
            case TASK -> {
                prioritizedTasksAdd(task);
                taskGroup.put(nextId, task);
            }
        }
        return ++nextId;
    }

    @Override
    public boolean updateTask(Task task) {
        if (task == null) return false;

        int id = task.getId();
        switch (task.getType()) {
            case SUB_TASK -> {
                if (!subGroup.containsKey(id)) return false;

                prioritizedTasksUpdate(task);
                SubTask subNew = (SubTask) task;
                SubTask subCurrent = subGroup.get(id);
                EpicTask epic = epicGroup.get(subCurrent.getEpicId());

                subCurrent.setTitle(subNew.getTitle());
                subCurrent.setDescription(subNew.getDescription());

                if (subCurrent.getStatus() != subNew.getStatus()) { // Экономия вызова updateStatusEpicTask
                    subCurrent.setStatus(subNew.getStatus());
                    updateStatusEpicTask(subCurrent.getEpicId());
                }

                subCurrent.setStartTime(subNew.getStartTime().orElse(null));
                subCurrent.setDuration(subNew.getDuration());
                updateTimeEpicTask(epic.getId()); // Пересчет эпика time & duration
            }
            case EPIC_TASK -> {
                if (!epicGroup.containsKey(id)) return false;

                EpicTask epicNew = (EpicTask) task;
                EpicTask epicCurrent = epicGroup.get(id);
                epicCurrent.setTitle(epicNew.getTitle());
                epicCurrent.setDescription(epicNew.getDescription());
            }
            case TASK -> {
                if (!taskGroup.containsKey(id)) return false;

                prioritizedTasksUpdate(task);
                taskGroup.put(task.getId(), task);
            }
        }
        return true;
    }

    @Override
    public void clearTaskGroup() {
        taskGroup.values().forEach(this::prioritizedTasksRemove);
        taskGroup.keySet().forEach(historyManager::remove);
        taskGroup.clear();
    }

    @Override
    public void clearSubGroup() {
        subGroup.values().forEach(this::prioritizedTasksRemove);
        subGroup.keySet().forEach(historyManager::remove);
        subGroup.clear();

        for (EpicTask epic : epicGroup.values()) {
            epic.setStatus(StatusTask.NEW);
            epic.getSubIds().clear();
        }
    }

    @Override
    public void clearEpicGroup() {
        subGroup.values().forEach(this::prioritizedTasksRemove);
        subGroup.keySet().forEach(historyManager::remove);
        subGroup.clear();

        epicGroup.keySet().forEach(historyManager::remove);
        epicGroup.clear();
    }

    @Override
    public boolean removeTask(int id) {
        if (!taskGroup.containsKey(id)) {
            return false;
        }

        prioritizedTasksRemove(taskGroup.get(id));
        taskGroup.remove(id);
        historyManager.remove(id);
        return true;
    }

    @Override
    public boolean removeSub(int id) {
        if (!subGroup.containsKey(id)) {
            return false;
        }

        historyManager.remove(id);
        prioritizedTasksRemove(subGroup.get(id));

        int epicId = subGroup.get(id).getEpicId();
        epicGroup.get(epicId).removeSubId(id);
        subGroup.remove(id);
        updateStatusEpicTask(epicId);
        updateTimeEpicTask(epicId);
        return true;
    }

    @Override
    public boolean removeEpic(int id) {
        if (!epicGroup.containsKey(id)) {
            return false;
        }

        for (int subId : epicGroup.get(id).getSubIds()) {
            historyManager.remove(subId);
            prioritizedTasksRemove(subGroup.get(subId));
            subGroup.remove(subId);
        }
        historyManager.remove(id);
        epicGroup.remove(id);
        return true;
    }

    private void updateStatusEpicTask(int id) {
        EpicTask epic = epicGroup.get(id);

        if (epic == null) return;

        if (epic.getSubIds().isEmpty()) {
            epic.setStatus(StatusTask.NEW);
        } else {
            List<SubTask> subs = getEpicSubTasks(id);
            StatusTask firstStatus = subs.getFirst().getStatus();
            epic.setStatus(subs.stream().anyMatch(sub -> sub.getStatus() != firstStatus) ?
                    StatusTask.IN_PROGRESS : firstStatus);
        }
    }

    private void updateTimeEpicTask(int id) {
        EpicTask epic = epicGroup.get(id);
        Duration duration = Duration.ZERO;

        epic.setStartTime(null);
        epic.setEndTime(null);
        for (SubTask sub : getEpicSubTasks(id)) {
            // startTime
            if ((epic.getStartTime().isEmpty() && sub.getStartTime().isPresent()) ||
                    (epic.getStartTime().isPresent() && sub.getStartTime().isPresent() &&
                            epic.getStartTime().get().isAfter(sub.getStartTime().get()))) {
                epic.setStartTime(sub.getStartTime().get());
            }
            // endTime
            if ((epic.getEndTime().isEmpty() && sub.getEndTime().isPresent()) ||
                    (epic.getEndTime().isPresent() && sub.getEndTime().isPresent() &&
                            epic.getEndTime().get().isBefore(sub.getEndTime().get()))) {
                epic.setEndTime(sub.getEndTime().get());
            }
            duration = duration.plus(sub.getDuration());
        }
        epic.setDuration(duration);
    }

    // Метод пересечения двух задач (решение до интервального планирования)
    private boolean overlayTasks(Task taskA, Task taskB) {
        if (taskA == null || taskB == null) return false;

        LocalDateTime startA = taskA.getStartTime().orElse(null);
        LocalDateTime endA = taskA.getEndTime().orElse(null);
        LocalDateTime startB = taskB.getStartTime().orElse(null);
        LocalDateTime endB = taskB.getEndTime().orElse(null);

        if (startA == null || endA == null) return false;

        return startA.isBefore(endB) && endA.isAfter(startB);
    }

    // Проверка пересечения времени со всеми задачами (решение до интервального планирования)
    private boolean overlayPrioritizedTasks(Task task) {
        return getPrioritizedTasks().stream()
                // Без учета ее текущего времени в списке (если вызов с updateTask значит она есть)
                .filter(t -> !t.equals(task))
                .anyMatch(t -> overlayTasks(t, task));
    }

    // Методы дополнительного задания по интервальному планированию

    // Возвращает список номеров интервалов, на которых расположена задача
    private List<Integer> getTaskIntervals(Task task) {
        if (task.getStartTime().isEmpty()) {
            return new ArrayList<>();
        }

        LocalDateTime startTime = task.getStartTime().get();
        LocalDateTime endTime = startTime.plus(task.getDuration());

        // Проверка на выход за границы линии интервалов (в 1 год)
        if (startTime.isBefore(timeLineStart) || endTime.isAfter(timeLineStart.plusYears(1))) {
            throw new ManagerSaveException("Out of range intervals");
        }

        int startInterval = (int) Duration.between(timeLineStart, startTime).toMinutes() / MINUTE_INTERVAL + 1;
        int endInterval = (int) Duration.between(timeLineStart, endTime).toMinutes() / MINUTE_INTERVAL;

        return IntStream.rangeClosed(startInterval, endInterval)
                .boxed()
                .toList();
    }

    // Проверяет интервалы задачи по мапе timeLine
    private boolean overlayPrioritizedTasksInterval(Task task) {
        if (task == null) return false;
        if (task.getStartTime().isEmpty()) return false;

        List<Integer> taskIntervals = getTaskIntervals(task);

        // При обновлении задачи ее старые интервалы не учитываются, т.к. после обновления они будут заменены на новые
        Task currentTask = (task.getType() == TypeTask.TASK) ? taskGroup.get(task.getId()) : subGroup.get(task.getId());
        if (currentTask != null) {
            // Обновление задачи. Старые интервалы обновляемой задачи в приоритетном списке в учет не берутся.
            List<Integer> currentInterval = getTaskIntervals(currentTask);
            taskIntervals = taskIntervals.stream()
                    .filter(i -> !currentInterval.contains(i))
                    .toList();
        }
        return taskIntervals.stream().anyMatch(timeLine::get);
    }

    // Методы для приоритетного списка

    private void prioritizedTasksAdd(Task task) {
        if (task.getStartTime().isEmpty()) return;

        if (overlayPrioritizedTasksInterval(task)) {
            throw new ManagerSaveException("startTime overlay");
        }

        getTaskIntervals(task).forEach(i -> timeLine.put(i, true));
        prioritizedTasks.add(task);
    }

    private void prioritizedTasksUpdate(Task task) {
        if (task.getStartTime().isEmpty()) return;

        if (overlayPrioritizedTasksInterval(task)) {
            throw new ManagerSaveException("startTime overlay");
        }
        // Сначала обработать старые интервалы обновляемой задачи
        Task currentTask = (task.getType() == TypeTask.TASK) ? taskGroup.get(task.getId()) : subGroup.get(task.getId());
        if (currentTask != null) {
            getTaskIntervals(currentTask).forEach(i -> timeLine.put(i, false));
        }
        // Обновляем интервалы и добавляем новую задачу в приоритетный список
        getTaskIntervals(task).forEach(i -> timeLine.put(i, true));
        prioritizedTasks.add(task);
    }

    private void prioritizedTasksRemove(Task task) {
        getTaskIntervals(task).forEach(i -> timeLine.put(i, false));
        prioritizedTasks.remove(task);
    }
}