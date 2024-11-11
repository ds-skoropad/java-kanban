package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.Objects;

public class Node {
    private Node previous;
    private Node next;
    private Task task;

    public Node getPrevious() {
        return previous;
    }

    public Node getNext() {
        return next;
    }

    public Task getTask() {
        return task;
    }

    public Node(Task task) {
        this.task = task;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    // По текущей функциональности сравнение нодов не требуется и в будущем картина пока не ясна.
    // Так что выполнил реализацию в сравнении по задачам, из чего вытекает сравнение по id.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(task, node.task);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(task);
    }

    @Override
    public String toString() {
        return "Node{" +
                "previous=" + previous +
                ", next=" + next +
                ", task=" + task +
                '}';
    }
}
