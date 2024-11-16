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
        if (task == null) return;
        removeNode(history.get(task.getId()));
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
    private void removeNode(Node node) { // Удаление по Node
        if (node == null) return;
        if (first == null) return;

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

    private void linkLast(Task task) { // Добавляет задачу в конец списка
        Node newNode = new Node(task);
        if (first == null) {
            first = newNode; // Если история пуста новая нода первая
        } else {
            last.next = newNode;
            newNode.previous = last;
        }
        last = newNode; // Новая нода последняя в любом случае
        history.put(task.getId(), newNode);
    }

    private List<Task> getTasks() { // Возвращает все задачи
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

        public Node(Task task) {
            this.task = task;
        }
    }
}
