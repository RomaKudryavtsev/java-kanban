package tests;

import api.*;
import managers.HttpTaskManager;
import org.junit.jupiter.api.*;
import tasks.TaskStatus;
import tasks.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTaskServerTest {
    HttpClient client;
    KVServer repositoryServer;
    HttpTaskServer taskServer;

    HttpTaskManager httpKanban;

    //NOTE: Below tests represent the following scenario which was tested in Insomnia:
    //2 tasks were created, then 2 epics were created, finally, 2 were subtasks (one for each epic).

    //NOTE: HttpTaskServer uses HttpTaskManager.
    //To test HttpTaskManager (and its work with KVServer) please refer to HttpTaskManagerTest.

    @BeforeEach
    public void beforeEach() throws IOException {
        repositoryServer = new KVServer();
        repositoryServer.start();
        httpKanban = new HttpTaskManager("http://localhost:8078");
        taskServer = new HttpTaskServer(httpKanban);
        taskServer.start();
        client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        sendPostRequest(TaskType.TASK, false, true);
        sendPostRequest(TaskType.TASK, false, false);
        sendPostRequest(TaskType.EPIC, false, true);
        sendPostRequest(TaskType.EPIC, false, false);
        sendPostRequest(TaskType.SUBTASK, false, true);
        sendPostRequest(TaskType.SUBTASK, false, false);
        sendGetRequest(TaskType.TASK, false, true, 1, false);
        sendGetRequest(TaskType.EPIC, false, true, 3, false);
        sendGetRequest(TaskType.SUBTASK, false, true, 5, false);
    }

    @AfterEach
    public void afterEach() {
        httpKanban.clearAll();
        repositoryServer.stop();
        taskServer.stop();
    }

    //endpoint: POST /tasks/task/ Body: {task ...} - without id: createTask
    @Test
    void endpointCreateTask() {
        Assertions.assertEquals(2, httpKanban.getNumberOfTasks());
    }

    //endpoint: POST /tasks/task/ Body: {task ...} - with id: renewTask
    @Test
    void endpointRenewTask() {
        sendPostRequest(TaskType.TASK, false, true);
        HttpResponse<String> response = sendPostRequest(TaskType.TASK, true, true);
        assert response != null;
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(TaskStatus.DONE, httpKanban.getTask(1).getStatus());
    }

    //endpoint: POST /tasks/subtask/ Body: {task ...} - without id: createSubTask
    @Test
    void endpointCreateSubtask() {
        Assertions.assertEquals(2, httpKanban.getNumberOfSubtasks());
    }

    //endpoint: POST /tasks/subtask/ Body: {task ...} - with id: renewSubTask
    @Test
    void endpointRenewSubtask() {
        HttpResponse<String> response = sendPostRequest(TaskType.SUBTASK, true, true);
        assert response != null;
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(TaskStatus.DONE, httpKanban.getSubTask(5).getStatus());
    }

    //endpoint: POST /tasks/subtask/ Body: {task ...} - without id: createEpicTask
    @Test
    void endpointCreateEpicTask() {
        Assertions.assertEquals(2, httpKanban.getNumberOfEpicTasks());
    }

    //endpoint: GET /tasks
    @Test
    void endpointGetPrioritizedTasks() {
        HttpResponse<String> response = sendGetRequest(null, true, false,
                0, false);
        assert response != null;
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(4, httpKanban.getPrioritizedTasks().size());
    }

    //endpoint: GET /tasks/task/
    @Test
    void endpointGetAllTasks() {
        HttpResponse<String> response = sendGetRequest(TaskType.TASK, false, false,
                0, false);
        assert response != null;
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(2, httpKanban.getListOfTasks().size());
    }

    //endpoint: GET /tasks/task/?id=
    @Test
    void endpointGetParticularTask() {
        HttpResponse<String> response = sendGetRequest(TaskType.TASK, false, true,
                1, false);
        assert response != null;
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, httpKanban.getTask(1).getId());
    }

    //endpoint: GET /tasks/subtask/
    @Test
    void endpointGetAllSubtasks() {
        HttpResponse<String> response = sendGetRequest(TaskType.SUBTASK, false, false,
                0, false);
        assert response != null;
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(2, httpKanban.getListOfSubTasks().size());
    }

    //endpoint: GET /tasks/subtask/?id=
    @Test
    void endpointGetParticularSubtask() {
        HttpResponse<String> response = sendGetRequest(TaskType.SUBTASK, false, true,
                5, false);
        assert response != null;
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(2, httpKanban.getListOfSubTasks().size());
    }

    //endpoint: GET /tasks/subtask/epic/?id=
    @Test
    void endpointGetAllSubtasksForParticularEpic() {
        HttpResponse<String> response = sendGetRequest(TaskType.SUBTASK, false, false,
                3, true);
        assert response != null;
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, httpKanban.getListOfSubTasksForEpicTask(3).size());
    }

    //endpoint: GET /tasks/epic/
    @Test
    void endpointGetAllEpics() {
        HttpResponse<String> response = sendGetRequest(TaskType.EPIC, false, false,
                0, false);
        assert response != null;
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(2, httpKanban.getListOfEpicTasks().size());
    }

    //endpoint: GET /tasks/epic/?id=
    @Test
    void endpointGetParticularEpic() {
        HttpResponse<String> response = sendGetRequest(TaskType.EPIC, false, true,
                3, false);
        assert response != null;
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(3, httpKanban.getEpicTask(3).getId());
    }

    //endpoint: GET /tasks/history
    @Test
    void endpointGetHistory() {
        Assertions.assertEquals(3, httpKanban.getHistoryManager().getHistory().size());
    }

    //endpoint: DELETE /tasks/task/
    @Test
    void endpointClearAllTasks() {
        HttpResponse<String> response = sendDeleteRequest(TaskType.TASK, false, 0);
        assert response != null;
        Assertions.assertEquals(204, response.statusCode());
        Assertions.assertEquals(0, httpKanban.getNumberOfTasks());
    }

    //endpoint: DELETE /tasks/task/?id=
    @Test
    void endpointRemoveParticularTask() {
        HttpResponse<String> response = sendDeleteRequest(TaskType.TASK, true, 1);
        assert response != null;
        Assertions.assertEquals(204, response.statusCode());
        Assertions.assertEquals(1, httpKanban.getNumberOfTasks());
    }

    //endpoint: DELETE /tasks/subtask/
    @Test
    void endpointClearAllSubtasks() {
        HttpResponse<String> response = sendDeleteRequest(TaskType.SUBTASK, false, 0);
        assert response != null;
        Assertions.assertEquals(204, response.statusCode());
        Assertions.assertEquals(0, httpKanban.getNumberOfSubtasks());
    }

    //endpoint: DELETE /tasks/subtask/?id=
    @Test
    void endpointRemoveParticularSubtask() {
        HttpResponse<String> response = sendDeleteRequest(TaskType.SUBTASK, true, 5);
        assert response != null;
        Assertions.assertEquals(204, response.statusCode());
        Assertions.assertEquals(1, httpKanban.getNumberOfSubtasks());
    }

    //endpoint: DELETE /tasks/epic/
    @Test
    void endpointClearAllEpics() {
        HttpResponse<String> response = sendDeleteRequest(TaskType.EPIC, false, 0);
        assert response != null;
        Assertions.assertEquals(204, response.statusCode());
        Assertions.assertEquals(0, httpKanban.getNumberOfEpicTasks());
    }

    //endpoint: DELETE /tasks/epic/?id=
    @Test
    void endpointRemoveParticularEpic() {
        HttpResponse<String> response = sendDeleteRequest(TaskType.EPIC, true, 3);
        assert response != null;
        Assertions.assertEquals(204, response.statusCode());
        Assertions.assertEquals(1, httpKanban.getNumberOfEpicTasks());
        Assertions.assertEquals(1, httpKanban.getNumberOfSubtasks());
    }

    private HttpResponse<String> sendPostRequest(TaskType type, boolean toBeRenewed, boolean isFirst) {
        String typeForUrl = type.toString().toLowerCase();
        URI url = URI.create("http://localhost:8080/tasks/" + typeForUrl);
        String jsonTask = getJsonPostRequestBody(type, toBeRenewed, isFirst);
        HttpRequest request = null;
        if (jsonTask != null) {
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                    .build();
        }
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private HttpResponse<String> sendGetRequest(TaskType type, boolean toGetPrioritizedList,
                                                boolean toGetParticularTask, int id, boolean toGetSubtasksForEpic) {
        URI url;
        if (toGetPrioritizedList) {
            url = URI.create("http://localhost:8080/tasks");
        } else {
            String typeForUrl = type.toString().toLowerCase();
            if (!toGetParticularTask) {
                url = URI.create(String.format("http://localhost:8080/tasks/%s", typeForUrl));
            } else {
                url = URI.create(String.format("http://localhost:8080/tasks/%s/?id=%d", typeForUrl, id));
            }
            if (toGetSubtasksForEpic) {
                url = URI.create(String.format("http://localhost:8080/tasks/subtask/epic/?id=%d", id));
            }
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-type", "application/json")
                .GET()
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private HttpResponse<String> sendDeleteRequest(TaskType type, boolean deleteParticularTask, int id) {
        URI url;
        String typeForUrl = type.toString().toLowerCase();
        if (!deleteParticularTask) {
            url = URI.create(String.format("http://localhost:8080/tasks/%s", typeForUrl));
        } else {
            url = URI.create(String.format("http://localhost:8080/tasks/%s?id=%d", typeForUrl, id));
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-type", "application/json")
                .DELETE()
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //NOTE: Request bodies were got from Insomnia
    private String getJsonPostRequestBody(TaskType type, boolean toBeRenewed, boolean isFirst) {
        if (!toBeRenewed) {
            switch (type) {
                case TASK:
                    if (isFirst) {
                        return "{\n" +
                                "\t\"name\": \"T1\",\n" +
                                "\t\"description\": \"TT1\",\n" +
                                "\t\"status\": \"NEW\",\n" +
                                "\t\"startTime\": \"04--11--2022 18:37\",\n" +
                                "\t\"duration\": 120\n" +
                                "}";
                    } else {
                        return "{\n" +
                                "\t\"name\": \"T2\",\n" +
                                "\t\"description\": \"TT2\",\n" +
                                "\t\"status\": \"NEW\",\n" +
                                "\t\"startTime\": \"04--12--2022 18:37\",\n" +
                                "\t\"duration\": 120\n" +
                                "}";
                    }
                case SUBTASK:
                    if (isFirst) {
                        return "{\n" +
                                "\t\"name\": \"S1\",\n" +
                                "\t\"description\": \"SS1\",\n" +
                                "\t\"status\": \"NEW\",\n" +
                                "\t\"epicTaskId\": 3,\n" +
                                "\t\"startTime\": \"05--11--2022 18:37\",\n" +
                                "\t\"duration\": 120\n" +
                                "}";
                    } else {
                        return "{\n" +
                                "\t\"name\": \"S2\",\n" +
                                "\t\"description\": \"SS2\",\n" +
                                "\t\"status\": \"NEW\",\n" +
                                "\t\"epicTaskId\": 4,\n" +
                                "\t\"startTime\": \"03--12--2022 18:37\",\n" +
                                "\t\"duration\": 120\n" +
                                "}";
                    }
                case EPIC:
                    if (isFirst) {
                        return "{\n" +
                                "\t\"name\": \"E1\",\n" +
                                "\t\"description\": \"EE1\",\n" +
                                "\t\"status\": \"NEW\"\n" +
                                "}";
                    } else {
                        return "{\n" +
                                "\t\"name\": \"E2\",\n" +
                                "\t\"description\": \"EE2\",\n" +
                                "\t\"status\": \"NEW\"\n" +
                                "}";
                    }
            }
        } else {
            switch (type) {
                case TASK:
                    return "{\n" +
                            "\t\"name\": \"T1\",\n" +
                            "\t\"description\": \"TT1\",\n" +
                            "\t\"status\": \"DONE\",\n" +
                            "\t\"startTime\": \"04--11--2022 18:37\",\n" +
                            "\t\"duration\": 120,\n" +
                            "\t\"id\": 1\n" +
                            "}";
                case SUBTASK:
                    return "{\n" +
                            "\t\"name\": \"S1\",\n" +
                            "\t\"description\": \"SS1\",\n" +
                            "\t\"status\": \"DONE\",\n" +
                            "\t\"epicTaskId\": 3,\n" +
                            "\t\"startTime\": \"05--11--2022 18:37\",\n" +
                            "\t\"duration\": 120,\n" +
                            "\t\"id\": 5\n" +
                            "}";
            }
        }
        return null;
    }
}
