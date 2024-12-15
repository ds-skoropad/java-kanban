package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        super.beforeEach(new InMemoryTaskManager());
    }

    @Test
    void addTask() {
        super.addTask();
    }

    @Test
    void updateTask() {
        super.updateTask();
    }

    @Test
    void getTask() {
        super.getTask();
    }

    @Test
    void getEpic() {
        super.getEpic();
    }

    @Test
    void getSub() {
        super.getSub();
    }

    @Test
    void getTaskGroup() {
        super.getTaskGroup();
    }

    @Test
    void getEpicGroup() {
        super.getEpicGroup();
    }

    @Test
    void getSubGroup() {
        super.getSubGroup();
    }

    @Test
    void getEpicSubTasks() {
        super.getEpicSubTasks();
    }

    @Test
    void getPrioritizedTasks() {
        super.getPrioritizedTasks();
    }

    @Test
    void clearTaskGroup() {
        super.clearTaskGroup();
    }

    @Test
    void clearSubGroup() {
        super.clearSubGroup();
    }

    @Test
    void clearEpicGroup() {
        super.clearEpicGroup();
    }

    @Test
    void removeTask() {
        super.removeTask();
    }

    @Test
    void removeSub() {
        super.removeSub();
    }

    @Test
    void removeEpic() {
        super.removeEpic();
    }

    @Test
    void nextIdIsCorrect() {
        super.nextIdIsCorrect();
    }

    @Test
    void notAddSubIfMissingEpic() {
        super.notAddSubIfMissingEpic();
    }

    @Test
    void linkBetweenSubAndEpicIsCorrect() {
        super.linkBetweenSubAndEpicIsCorrect();
    }

    @Test
    void statusShouldBeNewForNewTask() {
        super.statusShouldBeNewForNewTask();
    }

    @Test
    void statusShouldBeNewForEpic() {
        super.statusShouldBeNewForEpic();
    }

    @Test
    void statusShouldBeDoneForEpic() {
        super.statusShouldBeDoneForEpic();
    }

    @Test
    void statusShouldBeInProgressForEpic() {
        super.statusShouldBeInProgressForEpic();
    }

    @Test
    void shouldBeCorrectUpdateTask() {
        super.shouldBeCorrectUpdateTask();
    }

    @Test
    void shouldBeCorrectUpdateEpic() {
        super.shouldBeCorrectUpdateEpic();
    }

    @Test
    void shouldBeCorrectUpdateSub() {
        super.shouldBeCorrectUpdateSub();
    }

    @Test
    void shouldBeCorrectEpicTimesAfterAddSubs() {
        super.shouldBeCorrectEpicTimesAfterAddSubs();
    }

    @Test
    void shouldBeCorrectEpicTimeAfterUpdateSub() {
        super.shouldBeCorrectEpicTimeAfterUpdateSub();
    }

    @Test
    void shouldBeCorrectEpicTimesAfterRemoveSub() {
        super.shouldBeCorrectEpicTimesAfterRemoveSub();
    }

    @Test
    void shouldBeCorrectEpicTimesAfterRemoveAllSubs() {
        super.shouldBeCorrectEpicTimesAfterRemoveAllSubs();
    }

    @Test
    void shouldBeCorrectPrioritizedTasksAfterRemoveTask() {
        super.shouldBeCorrectPrioritizedTasksAfterRemoveTask();
    }

    @Test
    void shouldBeCorrectPrioritizedTasksAfterRemoveSub() {
        super.shouldBeCorrectPrioritizedTasksAfterRemoveSub();
    }

    @Test
    void shouldBeCorrectPrioritizedTasksAfterRemoveEpic() {
        super.shouldBeCorrectPrioritizedTasksAfterRemoveEpic();
    }

    @Test
    void shouldBeExceptionValidateTimeForAddOverlayTask() {
        super.shouldBeExceptionValidateTimeForAddOverlayTask();
    }

    @Test
    void shouldBeExceptionValidateTimeForAddOverlaySub() {
        super.shouldBeExceptionValidateTimeForAddOverlaySub();
    }

    @Test
    void notShouldBeExceptionValidateTimeForAddOverlayEpic() {
        super.notShouldBeExceptionValidateTimeForAddOverlayEpic();
    }

    @Test
    void shouldBeExceptionValidateTimeForUpdateOverlayTask() {
        super.shouldBeExceptionValidateTimeForUpdateOverlayTask();
    }

    @Test
    void shouldBeExceptionValidateTimeForUpdateOverlaySub() {
        super.shouldBeExceptionValidateTimeForUpdateOverlaySub();
    }

    @Test
    void notShouldBeExceptionValidateTimeForUpdateOverlayEpic() {
        super.notShouldBeExceptionValidateTimeForUpdateOverlayEpic();
    }

    @Test
    void shouldBeExceptionOutOfRangeLeftIntervals() {
        super.shouldBeExceptionOutOfRangeLeftIntervals();
    }
}