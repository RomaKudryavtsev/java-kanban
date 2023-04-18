package tests;

import managers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import tasks.TaskType;


import java.time.Duration;
import java.time.LocalDateTime;

abstract class TaskManagerTest<T extends TaskManager> {
    protected TaskManager kanban;

    @Test
    void twoTasksCreated() {
        int expected = 2;
        int result = kanban.getListOfTasks().size();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void twoEpicTasksCreated() {
        int expected = 2;
        int result = kanban.getListOfEpicTasks().size();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void threeSubtasksCreated() {
        int expectedNumberOfSubtasks = 3;
        int resultNumberOfSubtasks = kanban.getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubtasks, resultNumberOfSubtasks);
        int expectedIdOfEpicTask = 3;
        int resultIdOfEpicTask = kanban.getSubTask(5).getEpicTaskId();
        Assertions.assertEquals(expectedIdOfEpicTask, resultIdOfEpicTask);
    }

    @Test
    void getTaskWithId2() {
        int expected = 2;
        int result = kanban.getTask(2).getId();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void getTaskWithNonexistentIdThrowsException() {
        final NonexistentIdException exception = Assertions.assertThrows(
                NonexistentIdException.class,
                () -> kanban.getTask(4));
        Assertions.assertEquals("No task with such id was created", exception.getMessage());
    }

    @Test
    void getTaskForEmptyListThrowsException() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.getTask(4));
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void getSubTaskWithId5() {
        int expectedSubtaskId = 5;
        int resultSubtaskId = kanban.getSubTask(5).getId();
        Assertions.assertEquals(expectedSubtaskId, resultSubtaskId);
        int expectedEpicTaskId = 3;
        int resultEpicTaskId = kanban.getSubTask(5).getEpicTaskId();
        Assertions.assertEquals(expectedEpicTaskId, resultEpicTaskId);
    }

    @Test
    void getSubtaskWithNonexistentIdThrowsException() {
        final NonexistentIdException exception = Assertions.assertThrows(
                NonexistentIdException.class,
                () -> kanban.getSubTask(4));
        Assertions.assertEquals("No task with such id was created", exception.getMessage());
    }

    @Test
    void getSubtaskForEmptyListThrowsException() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.getSubTask(4));
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void getEpicTaskWithId3() {
        int expected = 3;
        int result = kanban.getEpicTask(3).getId();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void getEpicTaskWithNonexistentIdThrowsException() {
        final NonexistentIdException exception = Assertions.assertThrows(
                NonexistentIdException.class,
                () -> kanban.getEpicTask(10));
        Assertions.assertEquals("No task with such id was created", exception.getMessage());
    }

    @Test
    void getEpicTaskForEmptyListThrowsException() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.getTask(10));
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void renewTaskChangeOfStatusToInProgress() {
        Task renewedTask = new Task("T1", "TT1", TaskStatus.IN_PROGRESS, TaskType.TASK,
                LocalDateTime.of(2022, 11, 19, 9, 0), Duration.ofMinutes(120), 1);
        kanban.renewTask(renewedTask);
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = kanban.getTask(1).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void renewTaskWithEmptyList() {
        kanban.clearAll();
        Task renewedTask = new Task("T1", "TT1", TaskStatus.IN_PROGRESS, TaskType.TASK,
                LocalDateTime.of(2022, 11, 19, 9, 0), Duration.ofMinutes(120), 1);
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.renewTask(renewedTask));
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void renewTaskWithNonexistentId() {
        Task renewedTask = new Task("T1", "TT1", TaskStatus.IN_PROGRESS, TaskType.TASK,
                LocalDateTime.of(2022, 11, 19, 9, 0), Duration.ofMinutes(120), 4);
        final NonexistentIdException exception = Assertions.assertThrows(
                NonexistentIdException.class,
                () -> kanban.renewTask(renewedTask));
        Assertions.assertEquals("No task with such id was created", exception.getMessage());
    }

    @Test
    void renewSubTaskChangeOfEpicTaskStatusToInProgress() {
        SubTask renewedSubTask1 = new SubTask("S5", "SS5", TaskStatus.IN_PROGRESS, 3,
                TaskType.SUBTASK, LocalDateTime.of(2022, 11, 20, 13, 0),
                Duration.ofMinutes(120), 5);
        SubTask renewedSubTask2 = new SubTask("S6", "SS6", TaskStatus.DONE, 3,
                TaskType.SUBTASK, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120), 6);
        kanban.renewSubTask(renewedSubTask1);
        kanban.renewSubTask(renewedSubTask2);
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = kanban.getEpicTask(3).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void renewSubTaskChangeOfEpicTaskStatusToDone() {
        SubTask renewedSubTask1 = new SubTask("S5", "SS5", TaskStatus.DONE, 3,
                TaskType.SUBTASK, LocalDateTime.of(2022, 11, 20, 13, 0),
                Duration.ofMinutes(120), 5);
        SubTask renewedSubTask2 = new SubTask("S6", "SS6", TaskStatus.DONE, 3,
                TaskType.SUBTASK, LocalDateTime.of(2022, 11, 20, 15, 0),
                Duration.ofMinutes(120), 6);
        kanban.renewSubTask(renewedSubTask1);
        kanban.renewSubTask(renewedSubTask2);
        TaskStatus expected = TaskStatus.DONE;
        TaskStatus result = kanban.getEpicTask(3).getStatus();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void renewSubtaskWithEmptyList() {
        kanban.clearAll();
        SubTask renewedSubTask = new SubTask("S5", "SS5", TaskStatus.DONE, 3,
                TaskType.SUBTASK, LocalDateTime.of(2022, 11, 20, 13, 0),
                Duration.ofMinutes(120), 5);
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.renewSubTask(renewedSubTask));
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void renewSubtaskWithNonexistentId() {
        SubTask renewedSubTask = new SubTask("S5", "SS5", TaskStatus.DONE, 3,
                TaskType.SUBTASK, LocalDateTime.of(2022, 11, 20, 13, 0),
                Duration.ofMinutes(120), 8);
        final NonexistentIdException exception = Assertions.assertThrows(
                NonexistentIdException.class,
                () -> kanban.renewSubTask(renewedSubTask));
        Assertions.assertEquals("No task with such id was created", exception.getMessage());
    }

    @Test
    void removeTaskWithId1() {
        kanban.removeTask(1);
        int expected = 1;
        int result = kanban.getListOfTasks().size();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void removeTaskWithEmptyList() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.removeTask(1));
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void removeTaskWithNonexistentId() {
        final NonexistentIdException exception = Assertions.assertThrows(
                NonexistentIdException.class,
                () -> kanban.removeTask(4));
        Assertions.assertEquals("No task with such id was created", exception.getMessage());
    }

    @Test
    void removeSubTaskWithId5FromEpicTaskWithId3() {
        kanban.removeSubTask(5);
        int expectedNumberOfSubtasksInEpicTask = 1;
        int resultNumberOfSubtasksInEpicTask = kanban.getEpicTask(3).getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubtasksInEpicTask, resultNumberOfSubtasksInEpicTask);
        int expectedNumberOfSubtasks = 2;
        int resultNumberOfSubtasks = kanban.getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubtasks, resultNumberOfSubtasks);
    }

    @Test
    void removeSubtaskWithEmptyList() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.removeSubTask(1));
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void removeSubtaskWithNonexistentId() {
        final NonexistentIdException exception = Assertions.assertThrows(
                NonexistentIdException.class,
                () -> kanban.removeSubTask(4));
        Assertions.assertEquals("No task with such id was created", exception.getMessage());
    }

    @Test
    void removeEpicTaskWithId3() {
        kanban.removeEpicTask(3);
        int expectedNumberOfEpicTasks = 1;
        int resultNumberOfEpicTasks = kanban.getListOfEpicTasks().size();
        Assertions.assertEquals(expectedNumberOfEpicTasks, resultNumberOfEpicTasks);
        int expectedNumberOfSubtasks = 1;
        int resultNumberOfSubtasks = kanban.getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubtasks, resultNumberOfSubtasks);
    }

    @Test
    void removeEpicTaskWithEmptyList() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.removeEpicTask(1));
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void removeEpicTaskWithNonexistentId() {
        final NonexistentIdException exception = Assertions.assertThrows(
                NonexistentIdException.class,
                () -> kanban.removeEpicTask(1));
        Assertions.assertEquals("No task with such id was created", exception.getMessage());
    }

    @Test
    void clearListOfTasksNoTasksRemained() {
        kanban.clearListOfTasks();
        int expected = 0;
        int result = kanban.getListOfTasks().size();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void clearListOfTasksWithEmptyList() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.clearListOfTasks());
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void getListOfSubTasksForEpicTaskWithId4() {
        int expected = 1;
        int result = kanban.getListOfSubTasksForEpicTask(4).size();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void getListOfSubtasksForEpicTaskWithEmptyList() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.getEpicTask(4).getListOfSubTasks());
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void getListOfSubtasksForEpicTaskWithNonexistentId() {
        final NonexistentIdException exception = Assertions.assertThrows(
                NonexistentIdException.class,
                () -> kanban.getEpicTask(8).getListOfSubTasks());
        Assertions.assertEquals("No task with such id was created", exception.getMessage());
    }

    @Test
    void clearListOfSubtasksNoSubtasksRemained() {
        kanban.clearListOfSubtasks();
        int expectedNumberOfSubtasks = 0;
        int resultNumberOfSubtasks = kanban.getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubtasks, resultNumberOfSubtasks);
        int expectedNumberOfSubtasksInEpicTask = 0;
        int resultNumberOfSubtasksInEpicTask = kanban.getEpicTask(4).getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubtasksInEpicTask, resultNumberOfSubtasksInEpicTask);
    }

    @Test
    void clearListOfSubtasksWithEmptyList() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.clearListOfSubtasks());
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    @Test
    void clearListOfEpicTasksNoSubtasksAndEpicTasksRemained() {
        kanban.clearListOfEpicTasks();
        int expectedNumberOfEpicTasks = 0;
        int resultNumberOfEpicTasks = kanban.getListOfEpicTasks().size();
        Assertions.assertEquals(expectedNumberOfEpicTasks, resultNumberOfEpicTasks);
        int expectedNumberOfSubtasks = 0;
        int resultNumberOfSubtasks = kanban.getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubtasks, resultNumberOfSubtasks);
    }

    @Test
    void clearListOfEpicTasksWithEmptyList() {
        kanban.clearAll();
        final NoTasksCreatedException exception = Assertions.assertThrows(
                NoTasksCreatedException.class,
                () -> kanban.clearListOfEpicTasks());
        Assertions.assertEquals("No tasks were created or list of tasks is empty", exception.getMessage());
    }

    //NOTE: Below are test for Sprint 7
    @Test
    void sizeOfPrioritizedTasksListIs5() {
        int expected = 5;
        int result = kanban.getPrioritizedTasks().size();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void sizeOfPrioritizedTasksForEmptyTaskManagerIs0() {
        kanban.clearAll();
        int expected = 0;
        int result = kanban.getPrioritizedTasks().size();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void firstTaskInPrioritizedTasksListHasId2() {
        int expected = 2;
        int result = kanban.getPrioritizedTasks().get(0).getId();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void validationWhileDurationOfTaskMayBeDividedBy15AndDurationFromYearStartMayBeDividedBy15() {
        kanban.clearAll();
        kanban.createTask("A", "A", TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 0, 45), Duration.ofMinutes(45));
        final NonemptySlotsException exception = Assertions.assertThrows(
                NonemptySlotsException.class,
                () -> kanban.createTask("B", "B", TaskStatus.NEW,
                        LocalDateTime.of(2022, 1, 1, 1, 0),
                        Duration.ofMinutes(30)));
        Assertions.assertEquals("Slots are not empty", exception.getMessage());
    }

    @Test
    void validationWhileDurationOfTaskMayNotBeDividedBy15AndDurationFromYearStartMayBeDividedBy15() {
        kanban.clearAll();
        kanban.createTask("A", "A", TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 0, 45), Duration.ofMinutes(17));
        final NonemptySlotsException exception = Assertions.assertThrows(
                NonemptySlotsException.class,
                () -> kanban.createTask("B", "B", TaskStatus.NEW,
                        LocalDateTime.of(2022, 1, 1, 1, 0),
                        Duration.ofMinutes(30)));
        Assertions.assertEquals("Slots are not empty", exception.getMessage());
    }

    @Test
    void validationWhileDurationOfTaskMayBeDividedBy15AndDurationFromYearStartMayNotBeDividedBy15() {
        kanban.clearAll();
        kanban.createTask("A", "A", TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 0, 40), Duration.ofMinutes(15));
        final NonemptySlotsException exception = Assertions.assertThrows(
                NonemptySlotsException.class,
                () -> kanban.createTask("B", "B", TaskStatus.NEW,
                        LocalDateTime.of(2022, 1, 1, 0, 59),
                        Duration.ofMinutes(30)));
        Assertions.assertEquals("Slots are not empty", exception.getMessage());
    }

    @Test
    void validationWhileDurationOfTaskMayNotBeDividedBy15AndDurationFromYearStartMayNotBeDividedBy15() {
        kanban.clearAll();
        kanban.createTask("A", "A", TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 0, 40), Duration.ofMinutes(25));
        final NonemptySlotsException exception = Assertions.assertThrows(
                NonemptySlotsException.class,
                () -> kanban.createTask("B", "B", TaskStatus.NEW,
                        LocalDateTime.of(2022, 1, 1, 1, 0),
                        Duration.ofMinutes(30)));
        Assertions.assertEquals("Slots are not empty", exception.getMessage());
    }

    @Test
    void successfulCreationOfTaskWhileDurationOfTaskMayNotBeDividedBy15AndDurationFromYearStartMayNotBeDividedBy15() {
        kanban.clearAll();
        kanban.createTask("A", "A", TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 0, 40), Duration.ofMinutes(25));
        kanban.createTask("B", "B", TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 1, 16), Duration.ofMinutes(15));
        int expected = 2;
        int result = kanban.getListOfTasks().size();
        Assertions.assertEquals(expected, result);
    }
}