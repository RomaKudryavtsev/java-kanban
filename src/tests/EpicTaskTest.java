package tests;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTaskTest {
    private TaskManager kanban;

    @BeforeEach
    public void beforeEach() {
        kanban = Managers.getDefault();
        kanban.createEpicTask("Сдать финальное задание Практикума", "Проект канбана",
                TaskStatus.NEW);
    }

    @Test
    void statusOfEmptyEpicTaskIsNew() {
        TaskStatus expected = TaskStatus.NEW;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected,result);
    }

    @Test
    void calculationOfStartTimeForEpicTask() {
        kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.NEW, 1,
                LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
        kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.NEW,
                1, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120));
        LocalDateTime expected = LocalDateTime.of(2022, 11, 20, 13, 0);
        LocalDateTime result = kanban.getEpicTask(1).getStartTime();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void calculationOfDurationForEpicTask() {
        kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.NEW, 1,
                LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
        kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.NEW,
                1, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120));
        Duration expected = Duration.ofMinutes(240);
        Duration result = kanban.getEpicTask(1).getDuration();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void calculationOfEndTimeForEpicTask() {
        kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.NEW, 1,
                LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
        kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.NEW,
                1, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120));
        LocalDateTime expected = LocalDateTime.of(2022, 11, 20, 15, 0)
                .plus(Duration.ofMinutes(120));
        LocalDateTime result = kanban.getEpicTask(1).getEndTime();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void statusOfEpicTaskWithAllNewSubtasksIsNew() {
        kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.NEW, 1,
                LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
        kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.NEW,
                1, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120));
        TaskStatus expected = TaskStatus.NEW;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }


    @Test
    void statusOfEpicTaskWithAllDoneSubtasksIsDone() {
        kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.DONE, 1,
                LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
        kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.DONE,
                1, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120));
        TaskStatus expected = TaskStatus.DONE;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void statusOfEpicTaskWithNewAndDoneSubtasksIsInProgress() {
        kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.DONE, 1,
                LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
        kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.NEW,
                1, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120));
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void statusOfEpicTaskWithNewAndInProgressSubtasksIsInProgress() {
        kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.NEW, 1,
                LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
        kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.IN_PROGRESS,
                1, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120));
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void statusOfEpicTaskWithAllInProgressSubtasksIsInProgress() {
        kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
        kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.IN_PROGRESS,
                1, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120));
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = kanban.getEpicTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }
}