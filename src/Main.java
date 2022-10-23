import managers.*;
import tasks.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Tests for Sprint 3
        {
            TaskManager kanban = Managers.getDefault();
            // Test 1 - Creating all tasks & getting lists of tasks
            System.out.println("Тест 1 - Создание задач и получение списков задач");
            kanban.createTask("Сделать домашку по ивриту", "См. стр. 33 учебника", TaskStatus.NEW);
            kanban.createTask("Убраться дома", "Помыть пол", TaskStatus.NEW);
            kanban.createEpicTask("Сдать финальное задание Практикума", "Проект канбана", TaskStatus.NEW);
            kanban.createEpicTask("Подтвердить диплом", "Подтверждение диплома в министерстве образования",
                    TaskStatus.NEW);
            kanban.createSubTask("Подготовить код", "См. ТЗ 3го спринта", TaskStatus.NEW, 3);
            kanban.createSubTask("Проверить код стайл", "См. правила код стайла", TaskStatus.NEW,
                    3);
            kanban.createSubTask("Получить подтверждение в министерстве абсорбции", "Сходить к координатору",
                    TaskStatus.NEW, 4);
            assert kanban.getListOfTasks().size() == 2;
            assert kanban.getListOfEpicTasks().size() == 2;
            assert kanban.getListOfSubTasks().size() == 3;
            System.out.println("Успех");
            // Test 2 - Get a particular task
            System.out.println("Тест 2 - Просмотр отдельной задачи");
            assert kanban.getTask(2).getName().equals("Убраться дома");
            assert kanban.getEpicTask(4).getName().equals("Подтвердить диплом");
            assert kanban.getSubTask(5).getName().equals("Подготовить код");
            System.out.println("Успех");
            // Test 3 - Renewal of task
            System.out.println("Тест 3 - Обновление задачи");
            Task renewedTask = new Task("Сделать домашку по ивриту", "См. стр. 33 учебника",
                    TaskStatus.IN_PROGRESS, 1);
            kanban.renewTask(renewedTask);
            assert kanban.getTask(1).getStatus().equals(TaskStatus.IN_PROGRESS);
            System.out.println("Успех");
            // Test 4 - Renewal of subtasks and epic task in progress
            System.out.println("Тест 4 - Обновление подзадачи, статус эпика - in progress");
            SubTask renewedSubTask1 = new SubTask("Подготовить код", "См. ТЗ 3го спринта",
                    TaskStatus.IN_PROGRESS, 3, 5);
            SubTask renewedSubTask2 = new SubTask("Проверить код стайл", "См. правила код стайла",
                    TaskStatus.DONE, 3, 6);
            kanban.renewSubTask(renewedSubTask1);
            kanban.renewSubTask(renewedSubTask2);
            assert kanban.getSubTask(5).getStatus().equals(TaskStatus.IN_PROGRESS);
            assert kanban.getSubTask(6).getStatus().equals(TaskStatus.DONE);
            assert kanban.getEpicTask(3).getStatus().equals(TaskStatus.IN_PROGRESS);
            System.out.println("Успех");
            // Test 5 - Renewal of subtasks and epic task done
            System.out.println("Тест 5 - Обновление подзадачи, статус эпика - done");
            SubTask renewedSubTask3 = new SubTask("Подготовить код", "См. ТЗ 3го спринта",
                    TaskStatus.DONE, 3, 5);
            SubTask renewedSubTask4 = new SubTask("Проверить код стайл", "См. правила код стайла",
                    TaskStatus.DONE, 3, 6);
            kanban.renewSubTask(renewedSubTask3);
            kanban.renewSubTask(renewedSubTask4);
            assert kanban.getSubTask(5).getStatus().equals(TaskStatus.DONE);
            assert kanban.getSubTask(6).getStatus().equals(TaskStatus.DONE);
            assert kanban.getEpicTask(3).getStatus().equals(TaskStatus.DONE);
            System.out.println("Успех");
            // Test 6 - Removal of task
            System.out.println("Тест 6 - Удаление задачи");
            kanban.removeTask(1);
            assert kanban.getListOfTasks().size() == 1;
            assert kanban.getTask(2).getName().equals("Убраться дома");
            System.out.println("Успех");
            // Test 7 - Removal of subtask & get list of subtasks for an epic task
            System.out.println("Тест 7 - Удаление подзадачи и получение списка подзадач эпика");
            kanban.removeSubTask(5);
            assert kanban.getListOfSubTasksForEpicTask(3).size() == 1;
            assert kanban.getSubTask(6).getName().equals("Проверить код стайл");
            System.out.println("Успех");
            // Test 8 - Removal of epic task
            System.out.println("Тест 8 - Удаление эпика");
            kanban.removeEpicTask(3);
            assert kanban.getListOfEpicTasks().size() == 1;
            assert kanban.getListOfSubTasks().size() == 1;
            assert kanban.getSubTask(7).getName().equals("Получить подтверждение в министерстве абсорбции");
            System.out.println("Успех");
            // Test 9 - Clear list of tasks
            System.out.println("Тест 9 - Очистка списка задач");
            kanban.clearListOfTasks();
            assert kanban.getListOfTasks().size() == 0;
            System.out.println("Успех");
            // Test 10 - Clear list of subtasks
            System.out.println("Тест 10 - Очистка списка подзадач");
            kanban.clearListOfSubtasks();
            assert kanban.getListOfSubTasks().size() == 0;
            assert kanban.getListOfSubTasksForEpicTask(4).size() == 0;
            System.out.println("Успех");
            // Test 11 - Clear list of epic tasks
            System.out.println("Тест 11 - Очистка списка эпиков");
            kanban.clearListOfEpicTasks();
            assert kanban.getListOfEpicTasks().size() == 0;
            System.out.println("Успех");
        }
        // Tests for Sprint 4
        {
            System.out.println("Тест 12 - Проверка истории просмотров");
            TaskManager kanban = Managers.getDefault();
            // Creation of 5 tasks
            for(int i = 0; i < 5; i += 1) {
                kanban.createTask("Имя задачи", "Описание задачи", TaskStatus.NEW);
            }
            // Creation of 5 epic tasks
            for(int i = 0; i < 5; i += 1) {
                kanban.createEpicTask("Имя эпика", "Описание эпика", TaskStatus.NEW);
            }
            // Creation of 5 subtasks (each relates to a different epic task)
            int epicId = 6;
            for(int i = 0; i < 5; i += 1) {
                kanban.createSubTask("Имя подзадачи", "Описание подзадачи", TaskStatus.NEW, epicId);
                epicId += 1;
            }
            // Get 5 tasks and check history
            for(int idToGet = 1; idToGet <= 5; idToGet += 1) {
                kanban.getTask(idToGet);
            }
            assert kanban.getHistoryManager().getHistory().size() == 5;
            assert kanban.getHistoryManager().getHistory().get(4).getName().equals("Имя задачи");
            // Get 5 epic tasks and check history
            for(int epicIdToGet = 6; epicIdToGet <= 10; epicIdToGet += 1) {
                kanban.getEpicTask(epicIdToGet);
            }
            assert kanban.getHistoryManager().getHistory().size() == 10;
            assert kanban.getHistoryManager().getHistory().get(9).getName().equals("Имя эпика");
            // Get 5 subtasks and check history
            for(int subtaskIdToGet = 11; subtaskIdToGet <= 15; subtaskIdToGet += 1) {
                kanban.getSubTask(subtaskIdToGet);
            }
            assert kanban.getHistoryManager().getHistory().size() == 10;
            assert kanban.getHistoryManager().getHistory().get(0).getName().equals("Имя эпика");
            assert kanban.getHistoryManager().getHistory().get(9).getName().equals("Имя подзадачи");
            System.out.println("Успех");
        }
        // Tests for Sprint 5
        {
            System.out.println("Тест 13 - Проверка истории просмотров (удаление задач и отсутствие дубликатов)");
            TaskManager kanban = Managers.getDefault();
            kanban.createTask("Запись к врачу", "Записаться к врачу", TaskStatus.NEW);
            kanban.createTask("Сделать покупки", "Сходить в магазин", TaskStatus.NEW);
            kanban.createEpicTask("Сдать ТЗ 5", "Завершить финальный проект", TaskStatus.NEW);
            kanban.createEpicTask("Пройти курс Яндекса по математике",
                    "Основы математики для цифровых профессий", TaskStatus.NEW);
            kanban.createSubTask("Написать код", "Реализовать новый функционал", TaskStatus.NEW,
                    3);
            kanban.createSubTask("Написать тесты", "Тесты для нового функционала", TaskStatus.NEW,
                    3);
            kanban.createSubTask("Получить ревью", "Отправить задание на ревью", TaskStatus.NEW,
                    3);
            kanban.createTask("Задача 8", "Задача 8", TaskStatus.NEW);
            kanban.createTask("Задача 9", "Задача 9", TaskStatus.NEW);
            kanban.createTask("Задача 10", "Задача 10", TaskStatus.NEW);
            kanban.createTask("Задача 11", "Задача 11", TaskStatus.NEW);
            kanban.getTask(1);
            assert kanban.getHistoryManager().getHistory().size() == 1;
            kanban.getTask(1);
            assert kanban.getHistoryManager().getHistory().size() == 1;
            kanban.getTask(2);
            assert kanban.getHistoryManager().getHistory().size() == 2;
            kanban.getTask(2);
            assert kanban.getHistoryManager().getHistory().size() == 2;
            kanban.getSubTask(7);
            assert kanban.getHistoryManager().getHistory().size() == 3;
            kanban.getEpicTask(4);
            assert kanban.getHistoryManager().getHistory().size() == 4;
            assert kanban.getHistoryManager().getHistory().get(3).getId() == 4;
            kanban.getEpicTask(3);
            assert kanban.getHistoryManager().getHistory().size() == 5;
            kanban.getSubTask(5);
            assert kanban.getHistoryManager().getHistory().size() == 6;
            kanban.getSubTask(6);
            assert kanban.getHistoryManager().getHistory().size() == 7;
            kanban.getSubTask(7);
            assert kanban.getHistoryManager().getHistory().size() == 7;
            kanban.getTask(11);
            assert kanban.getHistoryManager().getHistory().size() == 8;
            kanban.getTask(10);
            assert kanban.getHistoryManager().getHistory().size() == 9;
            kanban.getTask(9);
            assert kanban.getHistoryManager().getHistory().size() == 10;
            kanban.getTask(8);
            assert kanban.getHistoryManager().getHistory().size() == 10;
            kanban.removeTask(8);
            assert kanban.getHistoryManager().getHistory().size() == 9;
            assert kanban.getHistoryManager().getHistory().get(8).getId() == 9;
            kanban.removeEpicTask(3);
            assert kanban.getHistoryManager().getHistory().size() == 5;
            assert kanban.getHistoryManager().getHistory().get(0).getId() == 2;
            kanban.removeTask(2);
            assert kanban.getHistoryManager().getHistory().size() == 4;
            assert kanban.getHistoryManager().getHistory().get(0).getId() == 4;
            System.out.println("Успех");
            // Print history "as is"
            ListOfTasksPrinter.printList(kanban.getHistoryManager().getHistory());
        }
    }
}

// Auxiliary class for printing lists of tasks / history
class ListOfTasksPrinter {
    static public void printList(List<Task> listOfTasks) {
        if (listOfTasks.isEmpty()) {
            System.out.println("Список пуст");
        } else {
            for (Task task : listOfTasks) {
                System.out.println(task);
            }
        }
    }
}

