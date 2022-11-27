package managers;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
        void createTask(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration);

        void createEpicTask(String name, String description, TaskStatus status);

        void createSubTask(String name, String description, TaskStatus status, int epicTaskId, LocalDateTime startTime,
                           Duration duration);

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

        List<Task> getPrioritizedTasks();

        void clearAll();

        int getNumberOfTasks();

        int getNumberOfSubtasks();

        int getNumberOfEpicTasks();
}
