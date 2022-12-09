package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String urlToKVServer;
    private String apiToken;

    public KVTaskClient(String urlToKVServer) {
        this.urlToKVServer = urlToKVServer;
        URI urlToRegister = URI.create(urlToKVServer + "/register");
        HttpRequest requestToRegister = HttpRequest.newBuilder()
                .uri(urlToRegister)
                .GET()
                .build();
        try {
            final HttpResponse<String> registerResponse =
                    client.send(requestToRegister, HttpResponse.BodyHandlers.ofString());
            if (registerResponse.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(registerResponse.body());
                apiToken = jsonElement.getAsString();
                System.out.println(String.format("Регистрация по API_TOKEN %s прошла успешно", apiToken));
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " +
                        registerResponse.statusCode());
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void put(String key, String json) {
        URI urlToSave = URI.create(urlToKVServer + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest requestToSave = HttpRequest.newBuilder()
                .uri(urlToSave)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            final HttpResponse<String> saveResponse = client.send(requestToSave, HttpResponse.BodyHandlers.ofString());
            if (saveResponse.statusCode() == 200) {
                System.out.println("Значение сохранено.");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + saveResponse.statusCode());
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        URI urlToSave = URI.create(urlToKVServer + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest requestToLoad = HttpRequest.newBuilder()
                .uri(urlToSave)
                .GET()
                .build();
        try {
            final HttpResponse<String> loadResponse = client.send(requestToLoad, HttpResponse.BodyHandlers.ofString());
            if(loadResponse.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(loadResponse.body());
                System.out.println(String.format("Загружено значение для ключа %s", key));
                return jsonElement.getAsString();
            } else if (loadResponse.statusCode() == 404) {
                return "";
            } else {
                return "Что-то пошло не так. Сервер вернул код состояния: " + loadResponse.statusCode();
            }
        } catch (InterruptedException | IOException e) {
            return "Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.";
        }
    }

    public void clear() {
        URI urlToClear = URI.create(urlToKVServer + "/clear" + "?API_TOKEN=" + apiToken);
        HttpRequest requestToClear = HttpRequest.newBuilder()
                .uri(urlToClear)
                .DELETE()
                .build();
        try {
            final HttpResponse<String> clearResponse = client.send(requestToClear,
                    HttpResponse.BodyHandlers.ofString());
            if (clearResponse.statusCode() == 200) {
                System.out.println("Сервер очищен.");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + clearResponse.statusCode());
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}
