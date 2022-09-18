import java.util.HashMap;
import java.util.ArrayList;
public class TaskManager {
    private HashMap<Integer, Task> mapOfTasks = new HashMap<>();
    private HashMap<Integer, SubTask> mapOfSubTasks = new HashMap<>();
    private HashMap<Integer, EpicTask> mapOfEpicTasks = new HashMap<>();
    private Integer taskId = 0;
    private Integer epicTaskId = 0;
    private Integer subTaskId = 0;

    public void createTask(String name, String description, TaskStatus status) {
        taskId = taskId + 1;
        Task task = new Task(name, description, status);
        task.setId(taskId);
        mapOfTasks.put(task.getId(), task);
    }

    public void createEpicTask(String name, String description, TaskStatus status) {
        epicTaskId = epicTaskId + 1;
        EpicTask epicTask = new EpicTask(name, description, status);
        epicTask.setId(epicTaskId);
        mapOfEpicTasks.put(epicTask.getId(), epicTask);
    }

    public void createSubTask(String name, String description, TaskStatus status, int epicTaskId) {
        subTaskId = subTaskId + 1;
        SubTask subTask = new SubTask(name, description, status, epicTaskId);
        subTask.setId(subTaskId);
        mapOfSubTasks.put(subTask.getId(), subTask);
        mapOfEpicTasks.get(epicTaskId).addSubTask(subTask);
    }

    public ArrayList<Task> getListOfTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>();
        for (Task task : mapOfTasks.values()) {
            listOfTasks.add(task);
        }
        return listOfTasks;
    }

    public ArrayList<SubTask> getListOfSubTasks() {
        ArrayList<SubTask> listOfSubTasks = new ArrayList<>();
        for (SubTask subTask : mapOfSubTasks.values()) {
            listOfSubTasks.add(subTask);
        }
        return listOfSubTasks;
    }

    public ArrayList<EpicTask> getListOfEpicTasks() {
        ArrayList<EpicTask> listOfEpicTasks = new ArrayList<>();
        for (EpicTask epicTask : mapOfEpicTasks.values()) {
            listOfEpicTasks.add(epicTask);
        }
        return listOfEpicTasks;
    }

    public Task getTask(Integer id) {
        if (mapOfTasks.containsKey(id)) {
            return mapOfTasks.get(id);
        } else {
            return null;
        }
    }

    public SubTask getSubTask(Integer id) {
        if (mapOfSubTasks.containsKey(id)) {
            return mapOfSubTasks.get(id);
        } else {
            return null;
        }
    }

    public EpicTask getEpicTask(Integer id) {
        if (mapOfEpicTasks.containsKey(id)) {
            return mapOfEpicTasks.get(id);
        } else {
            return null;
        }
    }

    public void renewTask(Task task) {
        if (mapOfTasks.containsKey(task.getId())) {
            mapOfTasks.put(task.getId(), task);
        }
    }

    public void renewSubTask(SubTask subTask) {
        if (mapOfSubTasks.containsKey(subTask.getId())) {
            mapOfSubTasks.put(subTask.getId(), subTask);
        }
        mapOfEpicTasks.get(subTask.getEpicTaskId()).renewSubTaskInEpicTask(subTask);
    }

    public void clearListOfTasks() {
        if (!mapOfTasks.isEmpty()) {
            mapOfTasks.clear();
        }
    }

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

    public void clearListOfEpicTasks() {
        //NOTE: If all epic tasks are cleared, subtasks have to be cleared as well
        if (!mapOfEpicTasks.isEmpty()) {
            mapOfEpicTasks.clear();
            mapOfSubTasks.clear();
        }
    }

    public void removeTask(Integer id) {
        if (mapOfTasks.containsKey(id)) {
            mapOfTasks.remove(id);
        }
    }

    public void removeSubTask(Integer id) {
        //NOTE: Subtask is also removed from the epic task
        if (mapOfSubTasks.containsKey(id)) {
            SubTask subTaskToBeRemoved = mapOfSubTasks.get(id);
            mapOfEpicTasks.get(subTaskToBeRemoved.getEpicTaskId()).removeSubTaskInEpicTask(subTaskToBeRemoved);
            mapOfSubTasks.remove(id);
        }
    }

    public void removeEpicTask (Integer id) {
        //NOTE: All subtasks of the epic task are removed
        if (mapOfEpicTasks.containsKey(id)) {
            for (SubTask subtaskToBeRemoved : mapOfEpicTasks.get(id).getListOfSubTasks()) {
                mapOfSubTasks.remove(subtaskToBeRemoved.getId());
            }
            mapOfEpicTasks.get(id).clearAllSubTasks();
            mapOfEpicTasks.remove(id);
        }
    }

    public ArrayList<SubTask> getListOfSubTasksForEpicTask(Integer id) {
        ArrayList<SubTask> result = new ArrayList<>();
        if (mapOfEpicTasks.containsKey(id)){
            result = mapOfEpicTasks.get(id).getListOfSubTasks();
        }
        return result;
    }
}
