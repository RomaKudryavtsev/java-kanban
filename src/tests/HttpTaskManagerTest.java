package tests;

import api.KVServer;
import managers.HttpTaskManager;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    String urlToServer;
    KVServer server;

    @BeforeEach
    public void beforeEachHttp() throws IOException {
        urlToServer = "http://localhost:8078";
        server = new KVServer();
        server.start();

        kanban = Managers.getDefault(urlToServer);
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

    private boolean compareExpectedAndActualNumberOfTasks(int expected, int result) {
        return expected == result;
    }

    @AfterEach
    public void afterEachHttp() {
        kanban.clearAll();
        server.stop();
    }

    @Test
    void serverWithTasksAndHistory() {
        kanban.getTask(1);
        kanban.getEpicTask(3);
        kanban.getSubTask(5);
        HttpTaskManager resultManager = HttpTaskManager.loadFromServer(urlToServer);
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(2, resultManager.getNumberOfTasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(3, resultManager.getNumberOfSubtasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(2, resultManager.getNumberOfEpicTasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(3,
                resultManager.getHistoryManager().getHistory().size()));
    }

    @Test
    void serverNoTasksCreated() {
        kanban.clearAll();
        HttpTaskManager resultManager = HttpTaskManager.loadFromServer(urlToServer);
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(0, resultManager.getNumberOfTasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(0, resultManager.getNumberOfSubtasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(0, resultManager.getNumberOfEpicTasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(0,
                resultManager.getHistoryManager().getHistory().size()));
    }

    @Test
    void serverEmptyHistory() {
        HttpTaskManager resultManager = HttpTaskManager.loadFromServer(urlToServer);
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(2, resultManager.getNumberOfTasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(3, resultManager.getNumberOfSubtasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(2, resultManager.getNumberOfEpicTasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(0,
                resultManager.getHistoryManager().getHistory().size()));
    }

    @Test
    void serverEpicTaskWithoutSubtasks() {
        kanban.clearAll();
        kanban.createEpicTask("E1", "EE1", TaskStatus.NEW);
        HttpTaskManager resultManager = HttpTaskManager.loadFromServer(urlToServer);
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(0, resultManager.getNumberOfTasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(0, resultManager.getNumberOfSubtasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(1, resultManager.getNumberOfEpicTasks()));
        Assertions.assertTrue(compareExpectedAndActualNumberOfTasks(0,
                resultManager.getHistoryManager().getHistory().size()));
    }
}
