package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest extends HistoryManagerTest<HistoryManager> {

    @BeforeEach
    void beforeEach() {
        super.beforeEach(new InMemoryHistoryManager());
    }

    @Test
    @Override
    void add() {
        super.add();
    }

    @Test
    @Override
    void remove() {
        super.remove();
    }

    @Test
    @Override
    void shouldBeEmptyIfRemoveAll() {
        super.shouldBeEmptyIfRemoveAll();
    }

    @Test
    @Override
    void shouldBeNoDuplicates() {
        super.shouldBeNoDuplicates();
    }

    @Test
    @Override
    void shouldBeCorrectRemoveFirst() {
        super.shouldBeCorrectRemoveFirst();
    }

    @Test
    @Override
    void shouldBeCorrectRemoveMiddle() {
        super.shouldBeCorrectRemoveMiddle();
    }

    @Test
    @Override
    void shouldBeCorrectRemoveLast() {
        super.shouldBeCorrectRemoveLast();
    }

    @Test
    @Override
    void shouldBeCorrectUpdateFirst() {
        super.shouldBeCorrectUpdateFirst();
    }

    @Test
    @Override
    void shouldBeCorrectUpdateMiddle() {
        super.shouldBeCorrectUpdateMiddle();
    }

    @Test
    @Override
    void shouldBeCorrectUpdateLast() {
        super.shouldBeCorrectUpdateLast();
    }
}