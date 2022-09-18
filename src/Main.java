public class Main {
    public static void main(String[] args) {
        TaskManager kanban = new TaskManager();
        // Test 1 - Creating all tasks
        kanban.createTask("Сделать домашку по ивриту", "См. стр. 33 учебника", TaskStatus.NEW);
        kanban.createTask("Убраться дома", "Помыть пол", TaskStatus.NEW);
        kanban.createEpicTask("Сдать финальное задание Практикума", "Проект канбана", TaskStatus.NEW );
        kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.NEW, 1);
        kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.NEW,
                1);
        kanban.createEpicTask("Подтвердить диплом", "Подтверждение диплома в министерстве образования",
                TaskStatus.NEW);
        kanban.createSubTask("Получить подтверждение в министерстве абсорбции", "Сходить к координатору",
                TaskStatus.NEW, 2);
        // Test 2a - Get task, subtask or epic task
        System.out.println("Разные виды задач, полученные по идентификатору");
        System.out.println(kanban.getTask(1));
        System.out.println(kanban.getEpicTask(2));
        System.out.println(kanban.getSubTask(3));
        // Test 2b - Printing lists of all tasks
        System.out.println("Список задач");
        printListOfTasks(kanban);
        System.out.println("Список подзадач");
        printListOfSubTasks(kanban);
        System.out.println("Список эпиков");
        printListOfEpicTasks(kanban);
        // Test 3 - Renewal of task
        Task renewedTask = new Task("Сделать домашку по ивриту", "См. стр. 33 учебника",
                TaskStatus.IN_PROGRESS, 1);
        kanban.renewTask(renewedTask);
        System.out.println("Обновленный список задач");
        printListOfTasks(kanban);
        // Test 4a - Renewal of subtasks and epic task in progress
        SubTask renewedSubTask1 = new SubTask("Подготовить код", "См. ТЗ 3го спринта",
                TaskStatus.IN_PROGRESS, 1, 1);
        SubTask renewedSubTask2 = new SubTask("Проверить код стайл", "См. правила код стайла",
                TaskStatus.DONE, 1, 2);
        kanban.renewSubTask(renewedSubTask1);
        kanban.renewSubTask(renewedSubTask2);
        System.out.println("Обновленный список подзадач");
        printListOfSubTasks(kanban);
        System.out.println("Обновленный список эпиков");
        printListOfEpicTasks(kanban);
        // Test 4b - Renewal of subtasks and epic task done
        SubTask renewedSubTask3 = new SubTask("Подготовить код", "См. ТЗ 3го спринта",
                TaskStatus.DONE, 1, 1);
        SubTask renewedSubTask4 = new SubTask("Проверить код стайл", "См. правила код стайла",
                TaskStatus.DONE, 1, 2);
        kanban.renewSubTask(renewedSubTask3);
        kanban.renewSubTask(renewedSubTask4);
        System.out.println("Обновленный список подзадач");
        printListOfSubTasks(kanban);
        System.out.println("Обновленный список эпиков");
        printListOfEpicTasks(kanban);
        // Test 5 - Removal of task
        kanban.removeTask(1);
        System.out.println("Список задач после удаления задачи");
        printListOfTasks(kanban);
        // Test 6 - Removal of subtask
        kanban.removeSubTask(1);
        System.out.println("Список подзадач после удаления подзадачи");
        printListOfSubTasks(kanban);
        System.out.println("Список эпиков после удаления подзадачи");
        printListOfEpicTasks(kanban);
        // Test 7 - Removal of epic task
        kanban.removeEpicTask(1);
        System.out.println("Список эпиков после удаления эпика");
        printListOfEpicTasks(kanban);
        System.out.println("Список подзадач после удаления эпика");
        printListOfSubTasks(kanban);
        // Test 8 - Print list of subtasks for an epic task
        System.out.println("Список подзадач для эпика 2");
        for(SubTask subTask : kanban.getListOfSubTasksForEpicTask(2)) {
            System.out.println(subTask);
        }
        // Test 9 - Clear list of tasks
        kanban.clearListOfTasks();
        System.out.println("Список задач после очистки");
        printListOfTasks(kanban);
        // Test 10 - Clear list of subtasks
        kanban.clearListOfSubtasks();
        System.out.println("Список подзадач после очистки подзадач");
        printListOfSubTasks(kanban);
        System.out.println("Список эпиков после очистки подзадач");
        printListOfEpicTasks(kanban);
        // Test 11 - Clear list of epic tasks
        kanban.clearListOfEpicTasks();
        System.out.println("Список эпиков после очистки эпиков");
        printListOfEpicTasks(kanban);
        System.out.println("Список подзадач после очистки эпиков");
        printListOfSubTasks(kanban);
    }

    // Auxiliary methods needed for the above tests (therefore, they are not included in TaskManager class)
    public static void printListOfTasks(TaskManager manager) {
        if (manager.getListOfTasks().isEmpty()) {
            System.out.println("Список пуст");
        } else {
            for (Task task : manager.getListOfTasks()) {
                System.out.println(task);
            }
        }
    }

    public static void printListOfSubTasks(TaskManager manager) {
        if (manager.getListOfSubTasks().isEmpty()) {
            System.out.println("Список пуст");
        } else {
            for (SubTask subTask : manager.getListOfSubTasks()) {
                System.out.println(subTask);
            }
        }
    }

    public static void printListOfEpicTasks(TaskManager manager) {
        if (manager.getListOfEpicTasks().isEmpty()) {
            System.out.println("Список пуст");
        } else {
            for (EpicTask epicTask : manager.getListOfEpicTasks()) {
                System.out.println(epicTask);
            }
        }
    }
}
