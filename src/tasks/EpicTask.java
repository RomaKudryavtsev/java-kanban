package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class EpicTask extends Task {
    private final HashMap<Integer, SubTask> mapOfSubTasksForEpicTask = new HashMap<>();
    private LocalDateTime endTime = DEFAULT_START_TIME;

    public EpicTask(String name, String description, TaskStatus status, TaskType type) {
        super(name, description, status, type);
    }

    public EpicTask(String name, String description, TaskStatus status, TaskType type, LocalDateTime startTime,
                    Duration duration) {
        super(name, description, status, type, startTime, duration);
    }

    private boolean checkWhetherEpicTaskIsDone() {
        boolean allSubTasksAreDone = true;
        if (mapOfSubTasksForEpicTask.isEmpty()) {
            allSubTasksAreDone = false;
        } else {
            for (SubTask subTask : mapOfSubTasksForEpicTask.values()) {
                if (subTask.getStatus() == TaskStatus.IN_PROGRESS || subTask.getStatus() == TaskStatus.NEW) {
                    allSubTasksAreDone = false;
                    break;
                }
            }
        }
        return allSubTasksAreDone;
    }

    private boolean checkWhetherAllSubtasksAreNew() {
        boolean allSubtasksAreNew = true;
        if (mapOfSubTasksForEpicTask.isEmpty()) {
            allSubtasksAreNew = false;
        } else {
            for (SubTask subTask : mapOfSubTasksForEpicTask.values()) {
                if (subTask.getStatus() == TaskStatus.IN_PROGRESS || subTask.getStatus() == TaskStatus.DONE) {
                    allSubtasksAreNew = false;
                    break;
                }
            }
        }
        return allSubtasksAreNew;
    }

    private void setEpicTaskStatus() {
        if (mapOfSubTasksForEpicTask.isEmpty()) {
            this.setStatus(TaskStatus.NEW);
        } else {
            if (this.checkWhetherEpicTaskIsDone()) {
                this.setStatus(TaskStatus.DONE);
            } else {
                if (this.checkWhetherAllSubtasksAreNew()) {
                    this.setStatus(TaskStatus.NEW);
                } else {
                    this.setStatus(TaskStatus.IN_PROGRESS);
                }
            }
        }
    }

    private boolean checkWhetherEpicTaskContainsSubTask(SubTask subTask) {
        return mapOfSubTasksForEpicTask.containsKey(subTask.getId());
    }

    private void calculateDurationOfEpicTask() {
        if (mapOfSubTasksForEpicTask.isEmpty()) {
            this.duration = Duration.ofMinutes(0);
        } else {
            this.duration = mapOfSubTasksForEpicTask.values().stream().map(SubTask::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);
        }
    }

    private void defineStartTimeOfEpicTask() {
        if (mapOfSubTasksForEpicTask.isEmpty()) {
            this.startTime = DEFAULT_START_TIME;
        } else if (mapOfSubTasksForEpicTask.size() == 1) {
            this.startTime = mapOfSubTasksForEpicTask.values().stream().findFirst().get().getStartTime();
        } else {
            this.startTime = mapOfSubTasksForEpicTask.values().stream()
                    .min(Comparator.comparing(Task::getStartTime))
                    .get().getStartTime();
        }
    }

    private void defineEndTimeOfEpicTask() {
        if (mapOfSubTasksForEpicTask.isEmpty()) {
            this.endTime = DEFAULT_START_TIME;
        } else if (mapOfSubTasksForEpicTask.size() == 1) {
            this.endTime = mapOfSubTasksForEpicTask.values().stream().findFirst().get().getEndTime();
        } else {
            SubTask last = mapOfSubTasksForEpicTask.values().stream()
                    .max(Comparator.comparing(Task::getStartTime))
                    .get();
            this.endTime = last.getStartTime().plus(last.getDuration());
        }
    }

    public void addSubTask(SubTask subTask) {
        mapOfSubTasksForEpicTask.put(subTask.getId(), subTask);
        setEpicTaskStatus();
        calculateDurationOfEpicTask();
        defineStartTimeOfEpicTask();
        defineEndTimeOfEpicTask();
    }

    //NOTE: This method RENEWS subtask in epic task
    public void renewSubTaskInEpicTask(SubTask subTask) {
        if (checkWhetherEpicTaskContainsSubTask(subTask)) {
            mapOfSubTasksForEpicTask.put(subTask.getId(), subTask);
            setEpicTaskStatus();
            calculateDurationOfEpicTask();
            defineStartTimeOfEpicTask();
            defineEndTimeOfEpicTask();
        }
    }

    //NOTE: This method REMOVES subtask from epic task
    public void removeSubTaskInEpicTask(SubTask subTask) {
        if (checkWhetherEpicTaskContainsSubTask(subTask)) {
            mapOfSubTasksForEpicTask.remove(subTask.getId());
            setEpicTaskStatus();
            calculateDurationOfEpicTask();
            defineStartTimeOfEpicTask();
            defineEndTimeOfEpicTask();
        }
    }

    public void clearAllSubTasks() {
        mapOfSubTasksForEpicTask.clear();
        setEpicTaskStatus();
        calculateDurationOfEpicTask();
        defineStartTimeOfEpicTask();
        defineEndTimeOfEpicTask();
    }

    public ArrayList<SubTask> getListOfSubTasks() {
        return new ArrayList<>(mapOfSubTasksForEpicTask.values());
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
