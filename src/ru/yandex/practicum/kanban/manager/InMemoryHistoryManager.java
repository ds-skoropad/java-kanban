package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> history;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    @Override
    public void add(Task task) { // Добавить задачу в список просмотра
        linkLast(task);
    }

    @Override
    public void remove(int id) { // Удаление задачи из просмотра (также для приминения при удалении задачи)
        removeNode(history.get(id));
    }

    @Override
    public List<Task> getHistoryList() { // Возвращает список истории
        return getTasks();
    }

    // Методы двухсвязного списка
    public void removeNode(Node node) { // Удаление по Node
        if (node == null) return;
        if (history.isEmpty()) return;
        if (history.size() == 1) {
            first = null;
            last = null;
            history.clear();
        } else {
            Node prev = node.getPrevious();
            Node next = node.getNext();
            if (prev == null) { // Если первый то первым становится следующий
                first = next;
                first.setPrevious(null);
            } else {
                prev.setNext(next); // null если последний
            }
            if (next == null) { // Если последний
                last = prev;
                last.setNext(null);
            } else {
                next.setPrevious(prev); // null если первый
            }
            history.remove(node.getTask().getId());
        }
    }

    public void linkLast(Task task) { // Добавляет задачу в конец списка
        if (task == null) return;
        removeNode(history.get(task.getId()));
        Node newNode = new Node(task);
        if (history.isEmpty()) {
            first = newNode;
            last = newNode;
        } else {
            last.setNext(newNode);
            newNode.setPrevious(last);
            last = newNode;
        }
        history.put(task.getId(), newNode);
    }

    public List<Task> getTasks() { // Возвращает все задачи
        List<Task> tasks = new ArrayList<>();
        Node countNode = first;
        while (countNode != null) {
            tasks.add(countNode.getTask());
            countNode = countNode.getNext();
        }
        return tasks;
    }
}
