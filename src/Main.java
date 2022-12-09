import api.HttpTaskServer;
import api.KVServer;
import managers.HttpTaskManager;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        {
            //Requests to HttpTaskManager via HttpTaskServer using Insomnia
            KVServer repoServer = new KVServer();
            repoServer.start();
            HttpTaskServer taskServer = new HttpTaskServer();
            taskServer.start();
            //NOTE: To test in Insomnia - please comment the following stop-lines.
            repoServer.stop();
            taskServer.stop();
        }

        {
            ListOfTasksPrinter printer = new ListOfTasksPrinter();

            KVServer server = new KVServer();
            server.start();
            //Serialization using the server
            HttpTaskManager kanban = new HttpTaskManager("http://localhost:8078");

            kanban.createTask("T1", "TT1", TaskStatus.NEW,
                    LocalDateTime.of(2022, 1, 1, 0, 0), Duration.ofMinutes(120));
            kanban.createEpicTask("E1", "EE1", TaskStatus.NEW);
            kanban.createSubTask("S1", "SS1", TaskStatus.NEW, 2,
                    LocalDateTime.of(2022, 2, 1, 0, 0), Duration.ofMinutes(120));
            kanban.getTask(1);
            kanban.getSubTask(3);
            kanban.getEpicTask(2);
            //Deserialization using the same server
            HttpTaskManager kanbanFromServer = HttpTaskManager.loadFromServer("http://localhost:8078");
            System.out.println("List of tasks:");
            printer.printList(kanbanFromServer.getListOfTasks());
            System.out.println("List of epics:");
            printer.printList(kanbanFromServer.getListOfEpicTasks());
            System.out.println("List of subtasks:");
            printer.printList(kanbanFromServer.getListOfSubTasks());
            System.out.println("History:");
            printer.printList(kanbanFromServer.getHistoryManager().getHistory());

            server.stop();
        }
    }
}

class ListOfTasksPrinter <T extends Task> {
    public void printList(List<T> listOfTasks) {
        if (listOfTasks.isEmpty()) {
            System.out.println("Список пуст");
        } else {
            for (T task : listOfTasks) {
                System.out.println(task);
            }
        }
    }
}






