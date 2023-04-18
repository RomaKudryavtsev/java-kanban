package api;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.HttpTaskManager;
import managers.Managers;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServer {
    String url;
    TaskManager httpKanban;
    private static final int PORT = 8080;
    private static final String URL_TO_KV_SERVER = "http://localhost:8078";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        url = URL_TO_KV_SERVER;
        httpKanban = Managers.getDefault(url);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gson = gsonBuilder.create();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(httpKanban));
    }

    public HttpTaskServer(HttpTaskManager httpKanban) throws IOException {
        url = URL_TO_KV_SERVER;
        this.httpKanban = httpKanban;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gson = gsonBuilder.create();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(httpKanban));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-server is started on " + PORT + " port!");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-server is stopped on " + PORT + " port!");
    }

    static class TasksHandler implements HttpHandler {
        TaskManager kanban;

        TasksHandler(TaskManager kanban) {
            this.kanban = kanban;
        }

        private String getCategoryFromPath(String path) {
            String[] pathElements = path.split("/");
            return pathElements[2];
        }

        private int getIdFromQuery(String query) {
            String[] queryElements = query.split("=");
            return Integer.parseInt(queryElements[1]);

        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getRawQuery();
            String path = httpExchange.getRequestURI().getPath();

            if (method.equals("GET") && path.split("/").length == 2) {
                //endpoint: GET /tasks
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    List<Task> listOfAllTasks = kanban.getPrioritizedTasks();
                    os.write(gson.toJson(listOfAllTasks).getBytes());
                }
            } else if (method.equals("DELETE") && path.split("/").length == 2) {
                kanban.clearAll();
            } else {
                String taskCategory = getCategoryFromPath(path);
                switch (method) {
                    case "GET":
                        switch (taskCategory) {
                            case "task":
                                //endpoint: GET /tasks/task/
                                httpExchange.sendResponseHeaders(200, 0);
                                if (query == null) {
                                    try (OutputStream os = httpExchange.getResponseBody()) {
                                        List<Task> listOfTasks = kanban.getListOfTasks();
                                        os.write(gson.toJson(listOfTasks).getBytes());
                                    }
                                } else {
                                    //endpoint: GET /tasks/task/?id=
                                    try (OutputStream os = httpExchange.getResponseBody()) {
                                        int taskId = getIdFromQuery(query);
                                        Task task = kanban.getTask(taskId);
                                        os.write(gson.toJson(task).getBytes());
                                    }
                                }
                                break;
                            case "subtask":
                                //endpoint: GET /tasks/subtask/
                                if (query == null) {
                                    httpExchange.sendResponseHeaders(200, 0);
                                    try (OutputStream os = httpExchange.getResponseBody()) {
                                        List<SubTask> listOfSubtasks = kanban.getListOfSubTasks();
                                        os.write(gson.toJson(listOfSubtasks).getBytes());
                                    }
                                } else {
                                    if (!path.contains("epic")) {
                                        //endpoint: GET /tasks/subtask/?id=
                                        httpExchange.sendResponseHeaders(200, 0);
                                        try (OutputStream os = httpExchange.getResponseBody()) {
                                            int subtaskId = getIdFromQuery(query);
                                            SubTask subTask = kanban.getSubTask(subtaskId);
                                            os.write(gson.toJson(subTask).getBytes());
                                        }
                                    } else {
                                        //endpoint: GET /tasks/subtask/epic/?id=
                                        httpExchange.sendResponseHeaders(200, 0);
                                        try (OutputStream os = httpExchange.getResponseBody()) {
                                            int epicIdForSubtaskList = getIdFromQuery(query);
                                            List<SubTask> listOfSubtasksForEpicTask =
                                                    kanban.getListOfSubTasksForEpicTask(epicIdForSubtaskList);
                                            os.write(gson.toJson(listOfSubtasksForEpicTask).getBytes());
                                        }
                                    }
                                }
                                break;
                            case "epic":
                                //endpoint: GET /tasks/epic/
                                httpExchange.sendResponseHeaders(200, 0);
                                if (query == null) {
                                    try (OutputStream os = httpExchange.getResponseBody()) {
                                        List<EpicTask> listOfEpicTasks = kanban.getListOfEpicTasks();
                                        os.write(gson.toJson(listOfEpicTasks).getBytes());
                                    }
                                } else {
                                    //endpoint: GET /tasks/epic/?id=
                                    try (OutputStream os = httpExchange.getResponseBody()) {
                                        int epicId = getIdFromQuery(query);
                                        EpicTask epicTask = kanban.getEpicTask(epicId);
                                        os.write(gson.toJson(epicTask).getBytes());
                                    }
                                }
                                break;
                            case "history":
                                //endpoint: GET /tasks/history
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    List<Task> history = kanban.getHistoryManager().getHistory();
                                    os.write(gson.toJson(history).getBytes());
                                }
                                break;
                        }
                        break;
                    case "DELETE":
                        switch (taskCategory) {
                            case "task":
                                //endpoint: DELETE /tasks/task/
                                if (query == null) {
                                    kanban.clearListOfTasks();
                                } else {
                                    //endpoint: DELETE /tasks/task/?id=
                                    int taskId = getIdFromQuery(query);
                                    kanban.removeTask(taskId);
                                }
                                httpExchange.sendResponseHeaders(204, -1);
                                httpExchange.close();
                                break;
                            case "subtask":
                                //endpoint: DELETE /tasks/subtask/
                                if (query == null) {
                                    kanban.clearListOfSubtasks();
                                } else {
                                    //endpoint: DELETE /tasks/subtask/?id=
                                    int subtaskId = getIdFromQuery(query);
                                    kanban.removeSubTask(subtaskId);
                                }
                                httpExchange.sendResponseHeaders(204, -1);
                                httpExchange.close();
                                break;
                            case "epic":
                                //endpoint: DELETE /tasks/epic/
                                if (query == null) {
                                    kanban.clearListOfEpicTasks();
                                } else {
                                    //endpoint: DELETE /tasks/epic/?id=
                                    int epicId = getIdFromQuery(query);
                                    kanban.removeEpicTask(epicId);
                                }
                                httpExchange.sendResponseHeaders(204, -1);
                                httpExchange.close();
                                break;
                        }
                        break;
                    case "POST":
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        switch (taskCategory) {
                            case "task":
                                //endpoint: POST /tasks/task/ Body: {task ...} - without id: createTask
                                Task task = gson.fromJson(body, Task.class);
                                if (!body.contains("id")) {
                                    kanban.createTask(task.getName(), task.getDescription(), task.getStatus(),
                                            task.getStartTime(), task.getDuration());
                                } else {
                                    //endpoint: POST /tasks/task/ Body: {task ...} - with id: renewTask
                                    kanban.renewTask(task);
                                }
                                httpExchange.sendResponseHeaders(201, 0);
                                httpExchange.close();
                                break;
                            case "subtask":
                                //endpoint: POST /tasks/subtask/ Body: {task ...} - without id: createSubTask
                                SubTask subTask = gson.fromJson(body, SubTask.class);
                                if (!body.contains("id")) {
                                    kanban.createSubTask(subTask.getName(), subTask.getDescription(),
                                            subTask.getStatus(), subTask.getEpicTaskId(), subTask.getStartTime(),
                                            subTask.getDuration());
                                } else {
                                    //endpoint: POST /tasks/subtask/ Body: {task ...} - with id: renewSubTask
                                    kanban.renewSubTask(subTask);
                                }
                                httpExchange.sendResponseHeaders(201, 0);
                                httpExchange.close();
                                break;
                            case "epic":
                                //endpoint: POST /tasks/subtask/ Body: {task ...} - without id: createEpicTask
                                EpicTask epicTask = gson.fromJson(body, EpicTask.class);
                                kanban.createEpicTask(epicTask.getName(), epicTask.getDescription(),
                                        epicTask.getStatus());
                                httpExchange.sendResponseHeaders(201, 0);
                                httpExchange.close();
                                break;
                        }
                        break;
                }
            }
        }
    }
}
