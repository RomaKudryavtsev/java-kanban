package managers;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final static String DATE_TIME_FORMAT = "[dd.MM.yyyy]/[HH:mm]";
    protected final String fileName;

    public FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    public FileBackedTasksManager(File file) {
        this.fileName = file.getName();
    }

    protected FileBackedTasksManager(String fileName, InMemoryTaskManager inMemoryTaskManager) {
        this.fileName = fileName;
        this.mapOfTasks = inMemoryTaskManager.mapOfTasks;
        this.mapOfSubTasks = inMemoryTaskManager.mapOfSubTasks;
        this.mapOfEpicTasks = inMemoryTaskManager.mapOfEpicTasks;
        this.taskId = inMemoryTaskManager.taskId;
        this.defaultHistory = inMemoryTaskManager.defaultHistory;
        this.priorityComparator = inMemoryTaskManager.priorityComparator;
        this.setOfPrioritizedTasksAndSubtasks = inMemoryTaskManager.setOfPrioritizedTasksAndSubtasks;
        this.slotsValidationMap = inMemoryTaskManager.slotsValidationMap;
    }

    protected void save() throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(fileName, StandardCharsets.UTF_8, false)) {
            fileWriter.write("id,type,name,status,description,startTime,duration,endTime,epic\n");
            Comparator<Integer> taskComparator = Comparator.comparingInt(id -> id);
            Map<Integer, Task> mapOfAllTasks = new TreeMap<>(taskComparator);
            for (Integer id : mapOfTasks.keySet()) {
                mapOfAllTasks.put(id, mapOfTasks.get(id));
            }
            for (Integer id : mapOfSubTasks.keySet()) {
                mapOfAllTasks.put(id, mapOfSubTasks.get(id));
            }
            for (Integer id : mapOfEpicTasks.keySet()) {
                mapOfAllTasks.put(id, mapOfEpicTasks.get(id));
            }
            for (Task task : mapOfAllTasks.values()) {
                String taskString = taskToString(task);
                fileWriter.write(taskString);
            }
            fileWriter.write(" \n");
            if (this.getHistoryManager().getHistory().size() == 0) {
                fileWriter.write(" \n");
            } else {
                fileWriter.write(historyToString(defaultHistory));
            }
        } catch (IOException e) {
            System.out.println("Recording error");
            throw new ManagerSaveException("Error occurred upon try to save TaskManager state into file");
        }
    }

    private String timeDateToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return dateTime.format(formatter);
    }

    private String taskToString(Task task) {
        String taskString;
        if (task.getType().equals(TaskType.SUBTASK)) {
            taskString = String.format("%d,%s,%s,%s,%s,%s,%d,%s,%d%n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), timeDateToString(task.getStartTime()),
                    task.getDuration().toMinutes(), timeDateToString(task.getEndTime()),
                    ((SubTask) task).getEpicTaskId());
        } else {
            taskString = String.format("%d,%s,%s,%s,%s,%s,%d,%s,%n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), timeDateToString(task.getStartTime()),
                    task.getDuration().toMinutes(), timeDateToString(task.getEndTime()));
        }
        return taskString;
    }

    private static String historyToString(HistoryManager manager) {
        String[] historyIdStrings = new String[manager.getHistory().size()];
        for (int i = 0; i < manager.getHistory().size(); ++i) {
            historyIdStrings[i] = Integer.toString(manager.getHistory().get(i).getId());
        }
        return String.join(",", historyIdStrings);
    }

    private static LocalDateTime stringToDateTime(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return LocalDateTime.parse(string, formatter);
    }

    private static Duration stringToDuration(String duration) {
        long minutesDuration = Long.parseLong(duration);
        return Duration.ofMinutes(minutesDuration);
    }

    private static Task stringToTask(String string) {
        String[] values = string.split(",");
        switch (TaskType.valueOf(values[1])) {
            case TASK:
                return new Task(values[2], values[4], TaskStatus.valueOf(values[3]), TaskType.valueOf(values[1]),
                        stringToDateTime(values[5]), stringToDuration(values[6]));
            case SUBTASK:
                return new SubTask(values[2], values[4], TaskStatus.valueOf(values[3]),
                        Integer.parseInt(values[8]), TaskType.valueOf(values[1]), stringToDateTime(values[5]),
                        stringToDuration(values[6]));
            case EPIC:
                return new EpicTask(values[2], values[4], TaskStatus.valueOf(values[3]), TaskType.valueOf(values[1]),
                        stringToDateTime(values[5]), stringToDuration(values[6]));
        }
        return null;
    }


    private static List<Integer> historyFromString(String value) {
        if (!value.isBlank()) {
            String[] taskIdsInHistory = value.split(",");
            List<Integer> listOfTaskIdsInHistory = new ArrayList<>();
            for (String s : taskIdsInHistory) {
                listOfTaskIdsInHistory.add(Integer.parseInt(s));
            }
            return listOfTaskIdsInHistory;
        } else {
            return new ArrayList<>();
        }
    }

    private static void loadHistoryToBuffer(List<Integer> listOfTaskIds, InMemoryTaskManager bufferKanban) {
        if (!listOfTaskIds.isEmpty()) {
            for (Integer id : listOfTaskIds) {
                if (bufferKanban.mapOfTasks.containsKey(id)) {
                    bufferKanban.getTask(id);
                } else if (bufferKanban.mapOfSubTasks.containsKey(id)) {
                    bufferKanban.getSubTask(id);
                } else if (bufferKanban.mapOfEpicTasks.containsKey(id)) {
                    bufferKanban.getEpicTask(id);
                }
            }
        }
    }

    private static String readFileContentsOrNull(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println("Unable to read the file");
            return null;
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        InMemoryTaskManager bufferKanban = new InMemoryTaskManager();
        String content = readFileContentsOrNull(file.getAbsolutePath());
        String[] lines = new String[0];
        if (content != null) {
            lines = content.split("\r?\n");
        }
        if (lines.length != 0) {
            //NOTE: First string is not used
            for (int i = 1; i < lines.length; i = i + 1) {
                //NOTE: Pre-last string is always empty - therefore it has to be skipped
                if (i == lines.length - 2) {
                    continue;
                    //NOTE: Last string is always history - therefore is skipped
                } else if (i == lines.length - 1) {
                    loadHistoryToBuffer(historyFromString(lines[i]), bufferKanban);
                } else {
                    Task task = stringToTask(lines[i]);
                    if (task != null) {
                        switch (task.getType()) {
                            case TASK:
                                bufferKanban.createTask(task.getName(), task.getDescription(), task.getStatus(),
                                        task.getStartTime(), task.getDuration());
                                break;
                            case SUBTASK:
                                bufferKanban.createSubTask(task.getName(), task.getDescription(), task.getStatus(),
                                        ((SubTask) task).getEpicTaskId(), task.getStartTime(), task.getDuration());
                                break;
                            case EPIC:
                                bufferKanban.createEpicTask(task.getName(), task.getDescription(), task.getStatus());
                        }
                    }
                }
            }
        }
        return new FileBackedTasksManager(file.getName(), bufferKanban);
    }

    @Override
    public void createTask(String name, String description, TaskStatus status, LocalDateTime startTime,
                           Duration duration) {
        super.createTask(name, description, status, startTime, duration);
        save();
    }

    @Override
    public void createEpicTask(String name, String description, TaskStatus status) {
        super.createEpicTask(name, description, status);
        save();
    }

    @Override
    public void createSubTask(String name, String description, TaskStatus status, int epicTaskId,
                              LocalDateTime startTime, Duration duration) {
        super.createSubTask(name, description, status, epicTaskId, startTime, duration);
        save();
    }

    @Override
    public Task getTask(Integer id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTask(Integer id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public EpicTask getEpicTask(Integer id) {
        EpicTask epicTask = super.getEpicTask(id);
        save();
        return epicTask;
    }

    @Override
    public void renewTask(Task task) {
        super.renewTask(task);
        save();
    }

    @Override
    public void renewSubTask(SubTask subTask) {
        super.renewSubTask(subTask);
        save();
    }

    @Override
    public void clearListOfTasks() {
        super.clearListOfTasks();
        save();
    }

    @Override
    public void clearListOfSubtasks() {
        super.clearListOfSubtasks();
        save();
    }

    @Override
    public void clearListOfEpicTasks() {
        super.clearListOfEpicTasks();
        save();
    }

    @Override
    public void removeTask(Integer id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(Integer id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpicTask(Integer id) {
        super.removeEpicTask(id);
        save();
    }

    @Override
    public void clearAll() {
        this.clearAllData();
        try {
            Files.newBufferedWriter(Path.of(fileName), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Unable to clear the file");
        }
    }
}
