package tests;

import managers.InMemoryTaskManager;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEachInMemory() {
        kanban = Managers.getDefault();
        kanban.createTask("T1", "TT1", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 19, 12, 0), Duration.ofMinutes(10));
        kanban.createTask("T2", "TT2", TaskStatus.NEW,
                LocalDateTime.of(2022, 11, 19, 9, 0), Duration.ofMinutes(30));
        kanban.createEpicTask("E3", "EE3", TaskStatus.NEW);
        kanban.createEpicTask("E4", "EE4", TaskStatus.NEW);
        kanban.createSubTask("S5", "SS5", TaskStatus.NEW, 3,
                LocalDateTime.of(2022, 11, 20, 20, 0), Duration.ofMinutes(120));
        kanban.createSubTask("S6", "SS6", TaskStatus.NEW, 3,
                LocalDateTime.of(2022, 11, 21, 15, 0), Duration.ofMinutes(45));
        kanban.createSubTask("S7", "SS7", TaskStatus.NEW, 4,
                LocalDateTime.of(2022, 11, 21, 9, 0), Duration.ofMinutes(15));
    }

    @AfterEach
    public void afterEachInMemory() {
        kanban.clearAll();
    }
}