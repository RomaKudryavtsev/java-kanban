package managers;

import tasks.*;
import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> mapOfTasks = new HashMap<>();
    protected HashMap<Integer, SubTask> mapOfSubTasks = new HashMap<>();
    protected HashMap<Integer, EpicTask> mapOfEpicTasks = new HashMap<>();
    // NOTE: In this version the single id is used for all types of tasks
    protected Integer taskId = 0;

    HistoryManager defaultHistory = Managers.getDefaultHistory();

    @Override
    public void createTask(String name, String description, TaskStatus status) {
        taskId = taskId + 1;
        Task task = new Task(name, description, status, TaskType.TASK);
        task.setId(taskId);
        mapOfTasks.put(task.getId(), task);
    }

    @Override
    public void createEpicTask(String name, String description, TaskStatus status) {
        taskId = taskId + 1;
        EpicTask epicTask = new EpicTask(name, description, status, TaskType.EPIC);
        epicTask.setId(taskId);
        mapOfEpicTasks.put(epicTask.getId(), epicTask);
    }

    @Override
    public void createSubTask(String name, String description, TaskStatus status, int epicTaskId) {
        taskId = taskId + 1;
        SubTask subTask = new SubTask(name, description, status, epicTaskId, TaskType.SUBTASK);
        subTask.setId(taskId);
        mapOfSubTasks.put(subTask.getId(), subTask);
        mapOfEpicTasks.get(epicTaskId).addSubTask(subTask);
    }

    @Override
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(mapOfTasks.values());
    }

    @Override
    public ArrayList<SubTask> getListOfSubTasks() {
        return new ArrayList<>(mapOfSubTasks.values());
    }

    @Override
    public ArrayList<EpicTask> getListOfEpicTasks() {
        return new ArrayList<>(mapOfEpicTasks.values());
    }

    @Override
    public Task getTask(Integer id) {
        if (mapOfTasks.containsKey(id)) {
            defaultHistory.add(mapOfTasks.get(id));
            return mapOfTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public SubTask getSubTask(Integer id) {
        if (mapOfSubTasks.containsKey(id)) {
            defaultHistory.add(mapOfSubTasks.get(id));
            return mapOfSubTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public EpicTask getEpicTask(Integer id) {
        if (mapOfEpicTasks.containsKey(id)) {
            defaultHistory.add(mapOfEpicTasks.get(id));
            return mapOfEpicTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void renewTask(Task task) {
        if (mapOfTasks.containsKey(task.getId())) {
            mapOfTasks.put(task.getId(), task);
        }
    }

    @Override
    public void renewSubTask(SubTask subTask) {
        if (mapOfSubTasks.containsKey(subTask.getId())) {
            mapOfSubTasks.put(subTask.getId(), subTask);
        }
        mapOfEpicTasks.get(subTask.getEpicTaskId()).renewSubTaskInEpicTask(subTask);
    }

    @Override
    public void clearListOfTasks() {
        if (!mapOfTasks.isEmpty()) {
            mapOfTasks.clear();
        }
    }

    @Override
    public void clearListOfSubtasks() {
        //NOTE: If all subtasks are cleared, epic tasks have to be updated as well
        if (!mapOfSubTasks.isEmpty()) {
            for (SubTask subTaskToBeCleared : mapOfSubTasks.values()) {
                int idOfEpicTask = subTaskToBeCleared.getEpicTaskId();
                mapOfEpicTasks.get(idOfEpicTask).removeSubTaskInEpicTask(subTaskToBeCleared);
            }
            mapOfSubTasks.clear();
        }
    }

    @Override
    public void clearListOfEpicTasks() {
        //NOTE: If all epic tasks are cleared, subtasks have to be cleared as well
        if (!mapOfEpicTasks.isEmpty()) {
            mapOfEpicTasks.clear();
            mapOfSubTasks.clear();
        }
    }

    @Override
    public void removeTask(Integer id) {
        if (mapOfTasks.containsKey(id)) {
            mapOfTasks.remove(id);
            defaultHistory.remove(id);
        }
    }

    @Override
    public void removeSubTask(Integer id) {
        //NOTE: Subtask is also removed from the epic task
        if (mapOfSubTasks.containsKey(id)) {
            SubTask subTaskToBeRemoved = mapOfSubTasks.get(id);
            mapOfEpicTasks.get(subTaskToBeRemoved.getEpicTaskId()).removeSubTaskInEpicTask(subTaskToBeRemoved);
            mapOfSubTasks.remove(id);
            defaultHistory.remove(id);
        }
    }

    @Override
    public void removeEpicTask (Integer id) {
        //NOTE: All subtasks of the epic task are removed
        if (mapOfEpicTasks.containsKey(id)) {
            for (SubTask subtaskToBeRemoved : mapOfEpicTasks.get(id).getListOfSubTasks()) {
                mapOfSubTasks.remove(subtaskToBeRemoved.getId());
                //NOTE: Removal of epic task from history invokes removal of all subtasks of this epic from history
                defaultHistory.remove(subtaskToBeRemoved.getId());
            }
            mapOfEpicTasks.get(id).clearAllSubTasks();
            mapOfEpicTasks.remove(id);
            defaultHistory.remove(id);
        }
    }

    @Override
    public ArrayList<SubTask> getListOfSubTasksForEpicTask(Integer id) {
        ArrayList<SubTask> result = new ArrayList<>();
        if (mapOfEpicTasks.containsKey(id)){
            result = mapOfEpicTasks.get(id).getListOfSubTasks();
        }
        return result;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return defaultHistory;
    }
}
