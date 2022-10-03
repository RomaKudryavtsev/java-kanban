package managers;

import tasks.*;
import java.util.ArrayList;

public interface TaskManager {
        void createTask(String name, String description, TaskStatus status);

        void createEpicTask(String name, String description, TaskStatus status);

        void createSubTask(String name, String description, TaskStatus status, int epicTaskId);

        ArrayList<Task> getListOfTasks();

        ArrayList<SubTask> getListOfSubTasks();

        ArrayList<EpicTask> getListOfEpicTasks();

        Task getTask(Integer id);

        SubTask getSubTask(Integer id);

        EpicTask getEpicTask(Integer id);

        void renewTask(Task task);

        void renewSubTask(SubTask subTask);

        void clearListOfTasks();

        void clearListOfSubtasks();

        void clearListOfEpicTasks();

        void removeTask(Integer id);

        void removeSubTask(Integer id);

        void removeEpicTask (Integer id);

        ArrayList<SubTask> getListOfSubTasksForEpicTask(Integer id);

        HistoryManager getHistoryManager();
}
