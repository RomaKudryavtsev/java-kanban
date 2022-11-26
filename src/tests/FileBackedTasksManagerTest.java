package tests;

import managers.FileBackedTasksManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    Path path;
    File f;

    @BeforeEach
    public void beforeEachFileBacked() {
        f = new File("kanban back-up file.csv");
        path = Path.of(f.getAbsolutePath());
        kanban = Managers.getFileBacked(f);
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
    public void afterEachFileBacked() {
        kanban.clearAll();
    }

    @Test
    void saveWithTasksAndHistory() {
        kanban.getTask(1);
        kanban.getEpicTask(3);
        kanban.getSubTask(5);
        try {
            String fileString = Files.readString(path);
            String[] lines = fileString.split("\r?\n");
            String expectedLine1 = "id,type,name,status,description,startTime,duration,endTime,epic";
            String resultLine1 = lines[0];
            Assertions.assertEquals(expectedLine1, resultLine1);
            String expectedLine2 = "1,TASK,T1,NEW,TT1,19.11.2022/12:00,10,19.11.2022/12:10,";
            String resultLine2 = lines[1];
            Assertions.assertEquals(expectedLine2, resultLine2);
            String expectedLine3 = "2,TASK,T2,NEW,TT2,19.11.2022/09:00,30,19.11.2022/09:30,";
            String resultLine3 = lines[2];
            Assertions.assertEquals(expectedLine3, resultLine3);
            String expectedLine4 = "3,EPIC,E3,NEW,EE3,20.11.2022/20:00,165,21.11.2022/15:45,";
            String resultLine4 = lines[3];
            Assertions.assertEquals(expectedLine4, resultLine4);
            String expectedLine5 = "4,EPIC,E4,NEW,EE4,21.11.2022/09:00,15,21.11.2022/09:15,";
            String resultLine5 = lines[4];
            Assertions.assertEquals(expectedLine5, resultLine5);
            String expectedLine6 = "5,SUBTASK,S5,NEW,SS5,20.11.2022/20:00,120,20.11.2022/22:00,3";
            String resultLine6 = lines[5];
            Assertions.assertEquals(expectedLine6, resultLine6);
            String expectedLine7 = "6,SUBTASK,S6,NEW,SS6,21.11.2022/15:00,45,21.11.2022/15:45,3";
            String resultLine7 = lines[6];
            Assertions.assertEquals(expectedLine7, resultLine7);
            String expectedLine8 = "7,SUBTASK,S7,NEW,SS7,21.11.2022/09:00,15,21.11.2022/09:15,4";
            String resultLine8 = lines[7];
            Assertions.assertEquals(expectedLine8, resultLine8);
            String expectedLine9 = " ";
            String resultLine9 = lines[8];
            Assertions.assertEquals(expectedLine9, resultLine9);
            String expectedLine10 = "1,3,5";
            String resultLine10 = lines[9];
            Assertions.assertEquals(expectedLine10, resultLine10);
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл.");
        }
    }

    @Test
    void saveNoTasksCreated() {
        kanban.clearAll();
        try {
            String expectedFileString = "";
            String resultFileString = Files.readString(path);
            Assertions.assertEquals(expectedFileString, resultFileString);
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл.");
        }
    }

    @Test
    void saveEmptyHistory() {
        try {
            String fileString = Files.readString(path);
            String[] lines = fileString.split("\r?\n");
            String expectedLine1 = "id,type,name,status,description,startTime,duration,endTime,epic";
            String resultLine1 = lines[0];
            Assertions.assertEquals(expectedLine1, resultLine1);
            String expectedLine2 = "1,TASK,T1,NEW,TT1,19.11.2022/12:00,10,19.11.2022/12:10,";
            String resultLine2 = lines[1];
            Assertions.assertEquals(expectedLine2, resultLine2);
            String expectedLine3 = "2,TASK,T2,NEW,TT2,19.11.2022/09:00,30,19.11.2022/09:30,";
            String resultLine3 = lines[2];
            Assertions.assertEquals(expectedLine3, resultLine3);
            String expectedLine4 = "3,EPIC,E3,NEW,EE3,20.11.2022/20:00,165,21.11.2022/15:45,";
            String resultLine4 = lines[3];
            Assertions.assertEquals(expectedLine4, resultLine4);
            String expectedLine5 = "4,EPIC,E4,NEW,EE4,21.11.2022/09:00,15,21.11.2022/09:15,";
            String resultLine5 = lines[4];
            Assertions.assertEquals(expectedLine5, resultLine5);
            String expectedLine6 = "5,SUBTASK,S5,NEW,SS5,20.11.2022/20:00,120,20.11.2022/22:00,3";
            String resultLine6 = lines[5];
            Assertions.assertEquals(expectedLine6, resultLine6);
            String expectedLine7 = "6,SUBTASK,S6,NEW,SS6,21.11.2022/15:00,45,21.11.2022/15:45,3";
            String resultLine7 = lines[6];
            Assertions.assertEquals(expectedLine7, resultLine7);
            String expectedLine8 = "7,SUBTASK,S7,NEW,SS7,21.11.2022/09:00,15,21.11.2022/09:15,4";
            String resultLine8 = lines[7];
            Assertions.assertEquals(expectedLine8, resultLine8);
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл.");
        }
    }

    @Test
    void saveEpicTaskWithoutSubtasks() {
        kanban.clearAll();
        kanban.createEpicTask("E1", "EE1", TaskStatus.NEW);
        kanban.getEpicTask(1);
        try {
            String fileString = Files.readString(path);
            String[] lines = fileString.split("\r?\n");
            String expectedLine1 = "id,type,name,status,description,startTime,duration,endTime,epic";
            String resultLine1 = lines[0];
            Assertions.assertEquals(expectedLine1, resultLine1);
            String expectedLine2 = "1,EPIC,E1,NEW,EE1,01.01.2000/00:00,0,01.01.2000/00:00,";
            String resultLine2 = lines[1];
            Assertions.assertEquals(expectedLine2, resultLine2);
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл.");
        }
    }

    @Test
    void loadFromFileWithTasksAndHistory() {
        kanban.getTask(1);
        kanban.getEpicTask(3);
        kanban.getSubTask(5);
        TaskManager kanbanInstantiatedFromFile = FileBackedTasksManager.loadFromFile(f);
        int expectedNumberOfTasks = 2;
        int resultNumberOfTasks = kanbanInstantiatedFromFile.getListOfTasks().size();
        Assertions.assertEquals(expectedNumberOfTasks, resultNumberOfTasks);
        int expectedNumberOfSubTasks = 3;
        int resultNumberOfSubtasks = kanbanInstantiatedFromFile.getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubTasks, resultNumberOfSubtasks);
        int expectedNumberOfEpicTasks = 2;
        int resultNumberOfEpicTasks = kanbanInstantiatedFromFile.getListOfEpicTasks().size();
        Assertions.assertEquals(expectedNumberOfEpicTasks, resultNumberOfEpicTasks);
        int expectedHistorySize = 3;
        int resultHistorySize = kanbanInstantiatedFromFile.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }

    @Test
    void loadFromFileNoTasksCreated() {
        kanban.clearAll();
        TaskManager kanbanInstantiatedFromFile = FileBackedTasksManager.loadFromFile(f);
        int expectedNumberOfTasks = 0;
        int resultNumberOfTasks = kanbanInstantiatedFromFile.getListOfTasks().size();
        Assertions.assertEquals(expectedNumberOfTasks, resultNumberOfTasks);
        int expectedNumberOfSubTasks = 0;
        int resultNumberOfSubtasks = kanbanInstantiatedFromFile.getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubTasks, resultNumberOfSubtasks);
        int expectedNumberOfEpicTasks = 0;
        int resultNumberOfEpicTasks = kanbanInstantiatedFromFile.getListOfEpicTasks().size();
        Assertions.assertEquals(expectedNumberOfEpicTasks, resultNumberOfEpicTasks);
        int expectedHistorySize = 0;
        int resultHistorySize = kanbanInstantiatedFromFile.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
    }

    @Test
    void loadFromFileEmptyHistory() {
        TaskManager kanbanInstantiatedFromFile = FileBackedTasksManager.loadFromFile(f);
        int expectedHistorySize = 0;
        int resultHistorySize = kanbanInstantiatedFromFile.getHistoryManager().getHistory().size();
        Assertions.assertEquals(expectedHistorySize, resultHistorySize);
        //если пустая история, нужно при save все равно создавать пустые строчки
    }

    @Test
    void loadFromFileEpicTaskWithoutSubtasks() {
        kanban.clearAll();
        kanban.createEpicTask("E1", "EE1", TaskStatus.NEW);
        kanban.getEpicTask(1);
        TaskManager kanbanInstantiatedFromFile = FileBackedTasksManager.loadFromFile(f);
        int expectedNumberOfEpicTasks = 1;
        int resultNumberOfEpicTasks = kanbanInstantiatedFromFile.getListOfEpicTasks().size();
        Assertions.assertEquals(expectedNumberOfEpicTasks, resultNumberOfEpicTasks);
        int expectedNumberOfSubtasks = 0;
        int resultNumberOfSubtasks = kanbanInstantiatedFromFile.getListOfSubTasks().size();
        Assertions.assertEquals(expectedNumberOfSubtasks, resultNumberOfSubtasks);
    }
}