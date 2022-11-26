import managers.FileBackedTasksManager;
import managers.Managers;
import managers.TaskManager;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // TaskManager is instantiated with empty file
        {
            File f = new File("kanban back-up file.csv");
            TaskManager kanbanBacked = new Managers().getFileBacked(f);
            kanbanBacked.createTask("Сделать домашку по ивриту", "См. стр. 33 учебника", TaskStatus.NEW,
                    LocalDateTime.of(2022, 11, 19, 20, 0), Duration.ofMinutes(120));
            kanbanBacked.createTask("Убраться дома", "Помыть пол", TaskStatus.NEW,
                    LocalDateTime.of(2022, 11, 19, 12, 0), Duration.ofMinutes(180));
            kanbanBacked.createEpicTask("Сдать финальное задание Практикума", "Проект канбана",
                    TaskStatus.NEW);
            kanbanBacked.createEpicTask("Подтвердить диплом",
                    "Подтверждение диплома в министерстве образования", TaskStatus.NEW);
            kanbanBacked.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.NEW, 3,
                    LocalDateTime.of(2022, 11, 20, 13, 0), Duration.ofMinutes(120));
            kanbanBacked.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.NEW,
                    3, LocalDateTime.of(2022, 11, 20, 15, 0),
                    Duration.ofMinutes(120));
            kanbanBacked.createSubTask("Получить подтверждение в министерстве абсорбции",
                    "Сходить к координатору", TaskStatus.NEW, 4,
                    LocalDateTime.of(2022, 11, 21, 9, 0), Duration.ofMinutes(90));
            kanbanBacked.getTask(1);
            kanbanBacked.getEpicTask(3);
            kanbanBacked.getSubTask(5);
        }
        //TaskManager is instantiated from file
        {
            File f = new File("kanban back-up file.csv");
            TaskManager kanbanBacked = FileBackedTasksManager.loadFromFile(f);
            ListOfTasksPrinter printer = new ListOfTasksPrinter();
            System.out.println("Список задач:");
            printer.printList(kanbanBacked.getListOfTasks());
            System.out.println("Список эпиков:");
            printer.printList(kanbanBacked.getListOfEpicTasks());
            System.out.println("Список подзадач:");
            printer.printList(kanbanBacked.getListOfSubTasks());
            System.out.println("История просмотров:");
            printer.printList(kanbanBacked.getHistoryManager().getHistory());
            System.out.println("Список задач и подзадач по приоритету:");
            printer.printList(kanbanBacked.getPrioritizedTasks());
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






