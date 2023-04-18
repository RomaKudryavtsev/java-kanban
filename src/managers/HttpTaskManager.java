package managers;

import api.DurationAdapter;
import api.KVTaskClient;
import api.LocalDateTimeAdapter;
import com.google.gson.*;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager implements TaskManager {
    static KVTaskClient client;
    private static final String LOCAL_DATE_TIME_FORMATTER = "dd--MM--yyyy HH:mm";

    public HttpTaskManager(String urlToServer) {
        super(urlToServer);
        client = new KVTaskClient(urlToServer);
    }

    protected HttpTaskManager(String urlToServer, InMemoryTaskManager bufferKanban) {
        super(urlToServer, bufferKanban);
    }

    private String listOfTasksToJson(List<Task> list) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.toJson(list);
    }

    private String listOfSubTasksToJson(List<SubTask> list) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.toJson(list);
    }

    private String listOfEpicTasksToJson(List<EpicTask> list) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.toJson(list);
    }

    private String listOfIdsToJson(List<Integer> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list);
    }

    @Override
    protected void save() throws ManagerSaveException {
        List<Task> tasksList = new ArrayList<>(mapOfTasks.values());
        List<SubTask> subTasksList = new ArrayList<>(mapOfSubTasks.values());
        List<EpicTask> epicTasksList = new ArrayList<>(mapOfEpicTasks.values());
        List<Integer> listOfIdsFromHistory = defaultHistory.getHistory().stream().map(Task::getId)
                .collect(Collectors.toList());
        client.put("task", listOfTasksToJson(tasksList));
        client.put("subtask", listOfSubTasksToJson(subTasksList));
        client.put("epic", listOfEpicTasksToJson(epicTasksList));
        client.put("history", listOfIdsToJson(listOfIdsFromHistory));
    }

    private static List<Task> listOfTasksFromJson(String json) {
        List<Task> listOfTasks = new ArrayList<>();
        if (!json.isEmpty()) {
            JsonElement jsonElement = JsonParser.parseString(json);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement e : jsonArray) {
                JsonObject jsonObject = e.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String description = jsonObject.get("description").getAsString();
                TaskType type = TaskType.valueOf(jsonObject.get("type").getAsString());
                TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
                LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(),
                        DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER));
                Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
                Task task = new Task(name, description, status, type, startTime, duration);
                listOfTasks.add(task);
            }
        }
        return listOfTasks;
    }

    private static List<SubTask> listOfSubTasksFromJson(String json) {
        List<SubTask> listOfSubTasks = new ArrayList<>();
        if (!json.isEmpty()) {
            JsonElement jsonElement = JsonParser.parseString(json);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement e : jsonArray) {
                JsonObject jsonObject = e.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String description = jsonObject.get("description").getAsString();
                TaskType type = TaskType.valueOf(jsonObject.get("type").getAsString());
                TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
                int epicTaskId = jsonObject.get("epicTaskId").getAsInt();
                LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(),
                        DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER));
                Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
                SubTask subTask = new SubTask(name, description, status, epicTaskId, type, startTime, duration);
                listOfSubTasks.add(subTask);
            }
        }
        return listOfSubTasks;
    }

    private static List<EpicTask> listOfEpicTasksFromJson(String json) {
        List<EpicTask> listOfEpicTasks = new ArrayList<>();
        if (!json.isEmpty()) {
            JsonElement jsonElement = JsonParser.parseString(json);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement e : jsonArray) {
                JsonObject jsonObject = e.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String description = jsonObject.get("description").getAsString();
                TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
                TaskType type = TaskType.valueOf(jsonObject.get("type").getAsString());
                EpicTask epicTask = new EpicTask(name, description, status, type);
                listOfEpicTasks.add(epicTask);
            }
        }
        return listOfEpicTasks;
    }

    private static List<Integer> listOfIdsFromJson(String json) {
        List<Integer> listOfIdsFromHistory = new ArrayList<>();
        if (!json.isEmpty()) {
            JsonElement jsonElement = JsonParser.parseString(json);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement e : jsonArray) {
                JsonPrimitive jsonPrimitive = e.getAsJsonPrimitive();
                listOfIdsFromHistory.add(jsonPrimitive.getAsInt());
            }
        }
        return listOfIdsFromHistory;
    }

    private static void loadTasksToBuffer(InMemoryTaskManager bufferKanban) {
        String jsonTasksString = client.load("task");
        if (!jsonTasksString.isEmpty()) {
            List<Task> listOfTasks = listOfTasksFromJson(jsonTasksString);
            for (Task task : listOfTasks) {
                bufferKanban.createTask(task.getName(), task.getDescription(), task.getStatus(), task.getStartTime(),
                        task.getDuration());
            }
        }
        String jsonEpicString = client.load("epic");
        if (!jsonEpicString.isEmpty()) {
            List<EpicTask> listOfEpicTasks = listOfEpicTasksFromJson(jsonEpicString);
            for (EpicTask epic : listOfEpicTasks) {
                bufferKanban.createEpicTask(epic.getName(), epic.getDescription(), epic.getStatus());
            }
        }
        String jsonSubtasksString = client.load("subtask");
        if (!jsonSubtasksString.isEmpty()) {
            List<SubTask> listOfSubtasks = listOfSubTasksFromJson(jsonSubtasksString);
            for (SubTask subtask : listOfSubtasks) {
                bufferKanban.createSubTask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                        subtask.getEpicTaskId(), subtask.getStartTime(), subtask.getDuration());
            }
        }
    }

    private static void loadHistoryToBuffer(InMemoryTaskManager bufferKanban) {
        String jsonHistoryString = client.load("history");
        List<Integer> listOfIdsFromHistory = listOfIdsFromJson(jsonHistoryString);
        for (Integer id : listOfIdsFromHistory) {
            if (bufferKanban.mapOfTasks.containsKey(id)) {
                bufferKanban.getTask(id);
            } else if (bufferKanban.mapOfSubTasks.containsKey(id)) {
                bufferKanban.getSubTask(id);
            } else if (bufferKanban.mapOfEpicTasks.containsKey(id)) {
                bufferKanban.getEpicTask(id);
            }
        }
    }

    public static HttpTaskManager loadFromServer(String uri) {
        InMemoryTaskManager bufferKanban = new InMemoryTaskManager();
        loadTasksToBuffer(bufferKanban);
        loadHistoryToBuffer(bufferKanban);
        return new HttpTaskManager(uri, bufferKanban);
    }

    private void clearServer() {
        client.clear();
    }

    @Override
    public void clearAll() {
        this.clearAllData();
        clearServer();
    }
}
