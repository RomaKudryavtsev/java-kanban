package tests;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasks.SubTask;
import tasks.TaskStatus;
import tasks.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTaskTest {
    private TaskManager kanban;

    @BeforeEach
    public void beforeEach() {
        kanban = Managers.getInMemory();
        kanban.createEpicTask("E1", "EE1", TaskStatus.NEW);
    }

    private void createSubtasks() {
        kanban.createSubTask("S2", "SS2", TaskStatus.NEW, 1,
                LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
        kanban.createSubTask("S3", "SS3", TaskStatus.NEW,
                1, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120));
    }

    @Test
    void statusOfEmptyEpicTaskIsNew() {
        TaskStatus expected = TaskStatus.NEW;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void calculationOfStartTimeForEpicTask() {
        createSubtasks();
        LocalDateTime expected = LocalDateTime.of(2022, 11, 20, 13, 0);
        LocalDateTime result = kanban.getEpicTask(1).getStartTime();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void calculationOfDurationForEpicTask() {
        createSubtasks();
        Duration expected = Duration.ofMinutes(240);
        Duration result = kanban.getEpicTask(1).getDuration();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void calculationOfEndTimeForEpicTask() {
        createSubtasks();
        LocalDateTime expected = LocalDateTime.of(2022, 11, 20, 15, 0)
                .plus(Duration.ofMinutes(120));
        LocalDateTime result = kanban.getEpicTask(1).getEndTime();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void statusOfEpicTaskWithAllNewSubtasksIsNew() {
        createSubtasks();
        TaskStatus expected = TaskStatus.NEW;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }


    @Test
    void statusOfEpicTaskWithAllDoneSubtasksIsDone() {
        createSubtasks();
        kanban.renewSubTask(new SubTask("S2", "SS2", TaskStatus.DONE, 1, TaskType.SUBTASK,
                LocalDateTime.of(2022, 11, 20, 13, 0),
                Duration.ofMinutes(120), 2));
        kanban.renewSubTask(new SubTask("S3", "SS3", TaskStatus.DONE, 1, TaskType.SUBTASK,
                LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120), 3));
        TaskStatus expected = TaskStatus.DONE;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void statusOfEpicTaskWithNewAndDoneSubtasksIsInProgress() {
        createSubtasks();
        kanban.renewSubTask(new SubTask("S2", "SS2", TaskStatus.DONE, 1, TaskType.SUBTASK,
                LocalDateTime.of(2022, 11, 20, 13, 0),
                Duration.ofMinutes(120), 2));
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void statusOfEpicTaskWithNewAndInProgressSubtasksIsInProgress() {
        createSubtasks();
        kanban.renewSubTask(new SubTask("S3", "SS3", TaskStatus.IN_PROGRESS, 1, TaskType.SUBTASK,
                LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120), 3));
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void statusOfEpicTaskWithAllInProgressSubtasksIsInProgress() {
        createSubtasks();
        kanban.renewSubTask(new SubTask("S2", "SS2", TaskStatus.IN_PROGRESS, 1,
                TaskType.SUBTASK, LocalDateTime.of(2022, 11, 20, 13, 0),
                Duration.ofMinutes(120), 2));
        kanban.renewSubTask(new SubTask("S3", "SS3", TaskStatus.IN_PROGRESS, 1,
                TaskType.SUBTASK, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120), 3));
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }
}