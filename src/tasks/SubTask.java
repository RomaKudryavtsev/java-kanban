package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicTaskId;

    public SubTask(String name, String description, TaskStatus status, int epicTaskId) {
        super(name, description, status);
        this.epicTaskId = epicTaskId;
    }

    public SubTask (String name, String description, TaskStatus status, int epicTaskId, int id) {
        super(name, description, status, id);
        this.epicTaskId = epicTaskId;
    }

    public SubTask (String name, String description, TaskStatus status, int epicTaskId, TaskType type) {
        super(name, description, status, type);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicTaskId, TaskType type,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, status, type, startTime, duration);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicTaskId, TaskType type,
                   LocalDateTime startTime, Duration duration, int id) {
        super(name, description, status, type, startTime, duration, id);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }
}
