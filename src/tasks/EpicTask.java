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

    private void setEpicTaskStatus() {
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

    private boolean checkWhetherEpicTaskContainsSubTask(SubTask subTask) {
        return mapOfSubTasksForEpicTask.containsKey(subTask.getId());
    }

    //NOTE: This method RENEWS subtask in epic task
    public void renewSubTaskInEpicTask(SubTask subTask) {
        if(checkWhetherEpicTaskContainsSubTask(subTask)) {
            mapOfSubTasksForEpicTask.put(subTask.getId(), subTask);
            setEpicTaskStatus();
        }
    }

    //NOTE: This method REMOVES subtask from epic task
    public void removeSubTaskInEpicTask(SubTask subTask) {
        if(checkWhetherEpicTaskContainsSubTask(subTask)) {
            mapOfSubTasksForEpicTask.remove(subTask.getId());
            setEpicTaskStatus();
        }
    }

    public void clearAllSubTasks() {
        mapOfSubTasksForEpicTask.clear();
    }
}
