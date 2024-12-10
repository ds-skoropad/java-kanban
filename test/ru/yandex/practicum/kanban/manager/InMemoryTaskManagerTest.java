package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        super.beforeEach(new InMemoryTaskManager(new InMemoryHistoryManager()));
    }

    @Test
    @Override
    void addTask() {
        super.addTask();
    }

    @Test
    @Override
    void updateTask() {
        super.updateTask();
    }

    @Test
    @Override
    void getTask() {
        super.getTask();
    }

    @Test
    @Override
    void getEpic() {
        super.getEpic();
    }

    @Test
    @Override
    void getSub() {
        super.getSub();
    }

    @Test
    @Override
    void getTaskGroup() {
        super.getTaskGroup();
    }

    @Test
    @Override
    void getEpicGroup() {
        super.getEpicGroup();
    }

    @Test
    @Override
    void getSubGroup() {
        super.getSubGroup();
    }

    @Test
    @Override
    void getEpicSubTasks() {
        super.getEpicSubTasks();
    }

    @Test
    @Override
    void getPrioritizedTasks() {
        super.getPrioritizedTasks();
    }

    @Test
    @Override
    void clearTaskGroup() {
        super.clearTaskGroup();
    }

    @Test
    @Override
    void clearSubGroup() {
        super.clearSubGroup();
    }

    @Test
    @Override
    void clearEpicGroup() {
        super.clearEpicGroup();
    }

    @Test
    @Override
    void removeTask() {
        super.removeTask();
    }

    @Test
    @Override
    void removeSub() {
        super.removeSub();
    }

    @Test
    @Override
    void removeEpic() {
        super.removeEpic();
    }

    @Test
    @Override
    void nextIdIsCorrect() {
        super.nextIdIsCorrect();
    }

    @Test
    @Override
    void notAddSubIfMissingEpic() {
        super.notAddSubIfMissingEpic();
    }

    @Test
    @Override
    void linkBetweenSubAndEpicIsCorrect() {
        super.linkBetweenSubAndEpicIsCorrect();
    }

    @Test
    @Override
    void statusShouldBeNewForNewTask() {
        super.statusShouldBeNewForNewTask();
    }

    @Test
    @Override
    void statusShouldBeNewForEpic() {
        super.statusShouldBeNewForEpic();
    }

    @Test
    @Override
    void statusShouldBeDoneForEpic() {
        super.statusShouldBeDoneForEpic();
    }

    @Test
    @Override
    void statusShouldBeInProgressForEpic() {
        super.statusShouldBeInProgressForEpic();
    }

    @Test
    @Override
    void shouldBeCorrectUpdateTask() {
        super.shouldBeCorrectUpdateTask();
    }

    @Test
    @Override
    void shouldBeCorrectUpdateEpic() {
        super.shouldBeCorrectUpdateEpic();
    }

    @Test
    @Override
    void shouldBeCorrectUpdateSub() {
        super.shouldBeCorrectUpdateSub();
    }

    @Test
    @Override
    void shouldBeCorrectEpicTimesAfterAddSubs() {
        super.shouldBeCorrectEpicTimesAfterAddSubs();
    }

    @Test
    @Override
    void shouldBeCorrectEpicTimeAfterUpdateSub() {
        super.shouldBeCorrectEpicTimeAfterUpdateSub();
    }

    @Test
    @Override
    void shouldBeCorrectEpicTimesAfterRemoveSub() {
        super.shouldBeCorrectEpicTimesAfterRemoveSub();
    }

    @Test
    @Override
    void shouldBeCorrectEpicTimesAfterRemoveAllSubs() {
        super.shouldBeCorrectEpicTimesAfterRemoveAllSubs();
    }

    @Test
    @Override
    void shouldBeCorrectPrioritizedTasksAfterRemoveTask() {
        super.shouldBeCorrectPrioritizedTasksAfterRemoveTask();
    }

    @Test
    @Override
    void shouldBeCorrectPrioritizedTasksAfterRemoveSub() {
        super.shouldBeCorrectPrioritizedTasksAfterRemoveSub();
    }

    @Test
    @Override
    void shouldBeCorrectPrioritizedTasksAfterRemoveEpic() {
        super.shouldBeCorrectPrioritizedTasksAfterRemoveEpic();
    }

    @Test
    @Override
    void shouldBeExceptionValidateTimeForAddOverlayTask() {
        super.shouldBeExceptionValidateTimeForAddOverlayTask();
    }

    @Test
    @Override
    void shouldBeExceptionValidateTimeForAddOverlaySub() {
        super.shouldBeExceptionValidateTimeForAddOverlaySub();
    }

    @Test
    @Override
    void notShouldBeExceptionValidateTimeForAddOverlayEpic() {
        super.notShouldBeExceptionValidateTimeForAddOverlayEpic();
    }

    @Test
    @Override
    void shouldBeExceptionValidateTimeForUpdateOverlayTask() {
        super.shouldBeExceptionValidateTimeForUpdateOverlayTask();
    }

    @Test
    @Override
    void shouldBeExceptionValidateTimeForUpdateOverlaySub() {
        super.shouldBeExceptionValidateTimeForUpdateOverlaySub();
    }

    @Test
    @Override
    void notShouldBeExceptionValidateTimeForUpdateOverlayEpic() {
        super.notShouldBeExceptionValidateTimeForUpdateOverlayEpic();
    }

    @Test
    @Override
    void shouldBeExceptionOutOfRangeLeftIntervals() {
        super.shouldBeExceptionOutOfRangeLeftIntervals();
    }
}