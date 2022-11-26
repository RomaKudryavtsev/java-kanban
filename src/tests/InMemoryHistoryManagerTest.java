package tests;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryHistoryManagerTest {
    TaskManager kanban;

    @BeforeEach
    public void beforeEachInHistory() {
        kanban = Managers.getDefault();
        kanban.createTask("T1", "TT1", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 19, 12, 0), Duration.ofMinutes(10));
        kanban.createEpicTask("E2", "EE2", TaskStatus.NEW);
        kanban.createSubTask("S3", "SS3", TaskStatus.NEW, 2,
                LocalDateTime.of(2022, 11, 20, 20, 0), Duration.ofMinutes(120));
    }

    @AfterEach
    public void afterEachInHistory() {
        kanban.clearAll();
    }

    @Test
    void addOneTaskInEmptyListSizeOfHistoryIs1() {
        kanban.getTask(1);
        int expectedHistorySize = 1;
        int resultHistorySize = kanban.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }

    @Test
    void addDuplicateSizeOfHistoryIs1() {
        kanban.getEpicTask(2);
        kanban.getEpicTask(2);
        int expectedHistorySize = 1;
        int resultHistorySize = kanban.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }

    @Test
    void addNonDuplicatingTaskSizeOfHistoryIs2() {
        kanban.getEpicTask(2);
        kanban.getTask(1);
        int expectedHistorySize = 2;
        int resultHistorySize = kanban.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }

    @Test
    void addNonDuplicatingTaskToFullyLoadedHistory() {
        kanban.createTask("T4", "TT4", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 21, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T5", "TT5", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 22, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T6", "TT6", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 23, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T7", "TT7", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 24, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T8", "TT8", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 25, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T9", "TT9", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 26, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T10", "TT10", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 27, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T11", "TT11", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 28, 12, 0), Duration.ofMinutes(10));
        kanban.getTask(1);
        kanban.getEpicTask(2);
        kanban.getSubTask(3);
        kanban.getTask(4);
        kanban.getTask(5);
        kanban.getTask(6);
        kanban.getTask(7);
        kanban.getTask(8);
        kanban.getTask(9);
        kanban.getTask(10);
        kanban.getTask(11);
        int expectedIdOfLastTaskInHistory = 11;
        int resultIdOfLastTaskInHistory = kanban.getHistoryManager().getHistory().get(9).getId();
        Assertions.assertEquals(expectedIdOfLastTaskInHistory, resultIdOfLastTaskInHistory);
        int expectedHistorySize = 10;
        int resultHistorySize = kanban.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }

    @Test
    void addDuplicatingTaskToFullyLoadedHistory() {
        kanban.createTask("T4", "TT4", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 21, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T5", "TT5", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 22, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T6", "TT6", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 23, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T7", "TT7", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 24, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T8", "TT8", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 25, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T9", "TT9", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 26, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T10", "TT10", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 27, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T11", "TT11", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 28, 12, 0), Duration.ofMinutes(10));
        kanban.getTask(1);
        kanban.getEpicTask(2);
        kanban.getSubTask(3);
        kanban.getTask(4);
        kanban.getTask(5);
        kanban.getTask(6);
        kanban.getTask(7);
        kanban.getTask(8);
        kanban.getTask(9);
        kanban.getTask(10);
        kanban.getTask(4);
        int expectedIdOfLastTaskInHistory = 4;
        int resultIdOfLastTaskInHistory = kanban.getHistoryManager().getHistory().get(9).getId();
        Assertions.assertEquals(expectedIdOfLastTaskInHistory, resultIdOfLastTaskInHistory);
        int expectedHistorySize = 10;
        int resultHistorySize = kanban.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }

    @Test
    void removeFromEmptyList() {
        kanban.clearAll();
        int expectedHistorySize = 0;
        int resultHistorySize = kanban.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }

    @Test
    void removeFirstIdOfTheFirstTaskInHistoryIs2() {
        kanban.getTask(1);
        kanban.getEpicTask(2);
        kanban.getSubTask(3);
        kanban.removeTask(1);
        int expectedIdOfTheFirstTask = 2;
        int resultIdOfTheFirstTask = kanban.getHistoryManager().getHistory().get(0).getId();
        Assertions.assertEquals(expectedIdOfTheFirstTask, resultIdOfTheFirstTask);
    }

    @Test
    void removeLastIdOfTheLastTaskInHistoryIs2() {
        kanban.getTask(1);
        kanban.getEpicTask(2);
        kanban.getSubTask(3);
        kanban.removeSubTask(3);
        int expectedIdOfTheFirstTask = 2;
        int resultIdOfTheFirstTask = kanban.getHistoryManager().getHistory().get(1).getId();
        Assertions.assertEquals(expectedIdOfTheFirstTask, resultIdOfTheFirstTask);
    }

    @Test
    void removeFromTheMiddleIdOfTheSecondTaskInHistoryIs2() {
        kanban.getTask(1);
        kanban.getSubTask(3);
        kanban.getEpicTask(2);
        kanban.removeSubTask(3);
        int expectedIdOfTheFirstTask = 2;
        int resultIdOfTheFirstTask = kanban.getHistoryManager().getHistory().get(1).getId();
        Assertions.assertEquals(expectedIdOfTheFirstTask, resultIdOfTheFirstTask);
    }

    @Test
    void getHistoryFromEmptyList() {
        int expectedHistorySize = 0;
        int resultHistorySize = kanban.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }

    @Test
    void getHistoryFromNonEmptyList() {
        kanban.getTask(1);
        kanban.getEpicTask(2);
        kanban.getSubTask(3);
        int expectedHistorySize = 3;
        int resultHistorySize = kanban.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }
}