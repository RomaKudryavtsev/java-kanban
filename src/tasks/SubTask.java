package tasks;

public class SubTask extends Task {
    private int epicTaskId;

    public SubTask(String name, String description, TaskStatus status, int epicTaskId) {
        super(name, description, status);
        this.epicTaskId = epicTaskId;
    }

    public SubTask (String name, String description, TaskStatus status, int epicTaskId, int id) {
        super(name, description, status, id);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }
}
