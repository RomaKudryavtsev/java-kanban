package managers;

import java.io.File;

public class Managers {
    public static TaskManager getDefault(String uri) {
        return new HttpTaskManager(uri);
    }

    public static TaskManager getFileBacked(File file) {
        return new FileBackedTasksManager(file);
    }

    public static TaskManager getInMemory() {return new InMemoryTaskManager();}

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
