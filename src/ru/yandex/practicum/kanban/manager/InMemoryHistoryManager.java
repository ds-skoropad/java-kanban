package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> history;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    // Добавить задачу в список просмотра
    @Override
    public void add(Task task) {
        if (task == null) return;
        removeNode(history.get(task.getId()));
        linkLast(task);
    }

    // Удаление задачи из просмотра (также для применения при удалении задачи)
    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }

    // Возвращает список истории
    @Override
    public List<Task> getHistoryList() {
        return getTasks();
    }

    // Методы двух-связного списка

    private void removeNode(Node node) {
        if (node == null || first == null) return;

        if (node.previous == null) {
            first = node.next;
        } else {
            node.previous.next = node.next;
        }

        if (node.next == null) {
            last = node.previous;
        } else {
            node.next.previous = node.previous;
        }

        history.remove(node.task.getId());
    }

    // Добавляет задачу в конец списка
    private void linkLast(Task task) {
        Node newNode = new Node(task, last, null);
        if (first == null) {
            first = newNode; // Если история пуста новая нода первая
        } else {
            last.next = newNode;
        }
        last = newNode; // Новая нода последняя в любом случае
        history.put(task.getId(), newNode);
    }

    // Возвращает все задачи
    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node countNode = first;
        while (countNode != null) {
            tasks.add(countNode.task);
            countNode = countNode.next;
        }
        return tasks;
    }

    private static class Node {
        private Node previous;
        private Node next;
        private Task task;

        public Node(Task task, Node previous, Node next) {
            this.task = task;
            this.previous = previous;
            this.next = next;
        }
    }
}
