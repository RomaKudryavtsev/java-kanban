package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class EpicTask extends Task {
    private final HashMap<Integer, SubTask> mapOfSubTasksForEpicTask = new HashMap<>();

    public EpicTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }
    public EpicTask(String name, String description, TaskStatus status, TaskType type) {
        super(name, description, status, type);
    }

    public ArrayList<SubTask> getListOfSubTasks() {
        ArrayList<SubTask> listOfSubTasks = new ArrayList<>();
        listOfSubTasks.addAll(mapOfSubTasksForEpicTask.values());
        return listOfSubTasks;
    }

    public void addSubTask(SubTask subTask) {
        mapOfSubTasksForEpicTask.put(subTask.getId(), subTask);
    }

    private boolean checkWhetherEpicTaskIsDone() {
        boolean allSubTasksAreDone = true;

        if (mapOfSubTasksForEpicTask.isEmpty()) {
            allSubTasksAreDone = false;
        } else {
            for(SubTask subTask : mapOfSubTasksForEpicTask.values()) {
                if (subTask.getStatus() == TaskStatus.IN_PROGRESS || subTask.getStatus() == TaskStatus.NEW) {
                    allSubTasksAreDone = false;
                    break;
                }
            }
        }
        return allSubTasksAreDone;
    }

    public void renewSubTaskInEpicTask(SubTask subTask) {
        if(mapOfSubTasksForEpicTask.containsKey(subTask.getId())) {
            int subTaskId = subTask.getId();
            mapOfSubTasksForEpicTask.put(subTaskId, subTask);
            if (this.checkWhetherEpicTaskIsDone()) {
                this.setStatus(TaskStatus.DONE);
            } else {
                this.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    public void removeSubTaskInEpicTask(SubTask subTask) {
        if(mapOfSubTasksForEpicTask.containsKey(subTask.getId())) {
            int subTaskId = subTask.getId();
            mapOfSubTasksForEpicTask.remove(subTaskId);
            if (mapOfSubTasksForEpicTask.isEmpty()) {
                this.setStatus(TaskStatus.NEW);
            } else {
                if (this.checkWhetherEpicTaskIsDone()) {
                    this.setStatus(TaskStatus.DONE);
                } else {
                    this.setStatus(TaskStatus.IN_PROGRESS);
                }
            }
        }
    }

    public void clearAllSubTasks() {
        mapOfSubTasksForEpicTask.clear();
    }
}
