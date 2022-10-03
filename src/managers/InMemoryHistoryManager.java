package managers;

import tasks.*;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int SIZE_OF_HISTORY = 10;
    private ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if(history.size() < SIZE_OF_HISTORY) {
            history.add(task);
        } else {
            history.remove(0);
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
