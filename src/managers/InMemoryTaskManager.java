package managers;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> mapOfTasks = new HashMap<>();
    protected HashMap<Integer, SubTask> mapOfSubTasks = new HashMap<>();
    protected HashMap<Integer, EpicTask> mapOfEpicTasks = new HashMap<>();
    Comparator<Task> priorityComparator = (task1, task2) -> {
        if(!task1.getStartTime().equals(task2.getStartTime())) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else {
            return task1.getId() - task2.getId();
        }
    };
    protected TreeSet<Task> setOfPrioritizedTasksAndSubtasks = new TreeSet<>(priorityComparator);
    // NOTE: In this version the single id is used for all types of tasks
    protected Integer taskId = 0;
    HistoryManager defaultHistory = Managers.getDefaultHistory();
    protected final static int SLOT_SIZE_IN_MIN = 15;
    //NOTE: If slot is empty the value is false
    protected Map<Long, Boolean> slotsValidationMap = new LinkedHashMap<>();

    //NOTE: In calendar year - 525600 minutes or 35040 15-minutes-slots
    private long defineDurationInMinutesFromYearStartToStartTime(LocalDateTime startTime) {
        LocalDateTime yearStart = LocalDateTime.of(startTime.getYear(), 1, 1, 0, 0);
        return Duration.between(yearStart, startTime).toMinutes();
    }

    private long defineFirstSlotOfTask(LocalDateTime startTime) {
        long durationFromYearStart = defineDurationInMinutesFromYearStartToStartTime(startTime);
        long firstSlot = 1;
        for(int i = 1; i <= durationFromYearStart; ++i) {
            if(i % SLOT_SIZE_IN_MIN == 0) {
                ++firstSlot;
            }
        }
        return firstSlot;
    }

    private long defineNumberOfSlotsNeededForTask(Duration duration, LocalDateTime startTime) {
        long durationFromYearStart = defineDurationInMinutesFromYearStartToStartTime(startTime);
        int numberOfSlots = 1;
        for(long l = durationFromYearStart + 1; l < durationFromYearStart + duration.toMinutes(); ++l) {
            if(l % SLOT_SIZE_IN_MIN == 0) {
                ++numberOfSlots;
            }
        }
        return numberOfSlots;
    }

    private void reserveSlotsForTask(Task task) {
        long firstSlotForTask = defineFirstSlotOfTask(task.getStartTime());
        long slotsNeededForTask = defineNumberOfSlotsNeededForTask(task.getDuration(), task.getStartTime());
        for(long l = firstSlotForTask; l < firstSlotForTask + slotsNeededForTask; ++l) {
            slotsValidationMap.put(l, true);
        }
    }

    //NOTE: Validation is made for O(1) - according to additional task
    private boolean validateIfSlotsForTaskAreEmpty(Duration duration, LocalDateTime startTime) {
        long firstSlotForTaskUnderValidation = defineFirstSlotOfTask(startTime);
        long slotsNeededForTaskUnderValidation = defineNumberOfSlotsNeededForTask(duration, startTime);
        for(long l = firstSlotForTaskUnderValidation;
            l < firstSlotForTaskUnderValidation + slotsNeededForTaskUnderValidation; ++l) {
            if(slotsValidationMap.containsKey(l)) {
                if (slotsValidationMap.get(l)) {
                    return false;
                }
            } else {
                continue;
            }
        }
        return true;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(setOfPrioritizedTasksAndSubtasks);
    }

    @Override
    public void createTask(String name, String description, TaskStatus status, LocalDateTime startTime,
                           Duration duration) throws NonemptySlotsException {
        if(validateIfSlotsForTaskAreEmpty(duration, startTime)) {
            taskId = taskId + 1;
            Task task = new Task(name, description, status, TaskType.TASK, startTime, duration);
            task.setId(taskId);
            mapOfTasks.put(task.getId(), task);
            setOfPrioritizedTasksAndSubtasks.add(task);
            reserveSlotsForTask(task);
        } else {
            throw new NonemptySlotsException("Slots are not empty");
        }
    }

    @Override
    public void createEpicTask(String name, String description, TaskStatus status) {
        taskId = taskId + 1;
        EpicTask epicTask = new EpicTask(name, description, status, TaskType.EPIC);
        epicTask.setId(taskId);
        mapOfEpicTasks.put(epicTask.getId(), epicTask);
    }

    @Override
    public void createSubTask(String name, String description, TaskStatus status, int epicTaskId,
                              LocalDateTime startTime, Duration duration) throws NonemptySlotsException {
        if(validateIfSlotsForTaskAreEmpty(duration, startTime)) {
            taskId = taskId + 1;
            SubTask subTask = new SubTask(name, description, status, epicTaskId, TaskType.SUBTASK, startTime, duration);
            subTask.setId(taskId);
            mapOfSubTasks.put(subTask.getId(), subTask);
            mapOfEpicTasks.get(epicTaskId).addSubTask(subTask);
            setOfPrioritizedTasksAndSubtasks.add(subTask);
            reserveSlotsForTask(subTask);
        } else {
            throw new NonemptySlotsException("Slots are not empty");
        }
    }

    @Override
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(mapOfTasks.values());
    }

    @Override
    public ArrayList<SubTask> getListOfSubTasks() {
        return new ArrayList<>(mapOfSubTasks.values());
    }

    @Override
    public ArrayList<EpicTask> getListOfEpicTasks() {
        return new ArrayList<>(mapOfEpicTasks.values());
    }

    @Override
    public Task getTask(Integer id) throws NoTasksCreatedException, NonexistentIdException {
        if(!mapOfTasks.isEmpty()) {
            if (mapOfTasks.containsKey(id)) {
                defaultHistory.add(mapOfTasks.get(id));
                return mapOfTasks.get(id);
            } else {
                throw new NonexistentIdException("No task with such id was created");
            }
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public SubTask getSubTask(Integer id) throws NoTasksCreatedException, NonexistentIdException {
        if(!mapOfSubTasks.isEmpty()) {
            if (mapOfSubTasks.containsKey(id)) {
                defaultHistory.add(mapOfSubTasks.get(id));
                return mapOfSubTasks.get(id);
            } else {
                throw new NonexistentIdException("No task with such id was created");
            }
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public EpicTask getEpicTask(Integer id) throws NoTasksCreatedException, NonexistentIdException {
        if(!mapOfEpicTasks.isEmpty()) {
            if (mapOfEpicTasks.containsKey(id)) {
                defaultHistory.add(mapOfEpicTasks.get(id));
                return mapOfEpicTasks.get(id);
            } else {
                throw new NonexistentIdException("No task with such id was created");
            }
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public void renewTask(Task task) {
        if(!mapOfTasks.isEmpty()) {
            if (mapOfTasks.containsKey(task.getId())) {
                Task renewedTask = mapOfTasks.get(task.getId());
                mapOfTasks.put(task.getId(), task);
                setOfPrioritizedTasksAndSubtasks.add(task);
                setOfPrioritizedTasksAndSubtasks.remove(renewedTask);
            } else {
                throw new NonexistentIdException("No task with such id was created");
            }
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public void renewSubTask(SubTask subTask) {
        if(!mapOfSubTasks.isEmpty()) {
            if (mapOfSubTasks.containsKey(subTask.getId())) {
                Task renewedSubtask = mapOfSubTasks.get(subTask.getId());
                mapOfSubTasks.put(subTask.getId(), subTask);
                setOfPrioritizedTasksAndSubtasks.add(subTask);
                setOfPrioritizedTasksAndSubtasks.remove(renewedSubtask);
                mapOfEpicTasks.get(subTask.getEpicTaskId()).renewSubTaskInEpicTask(subTask);
            } else {
                throw new NonexistentIdException("No task with such id was created");
            }
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public void clearListOfTasks() {
        if (!mapOfTasks.isEmpty()) {
            Iterator<Task> iterator = setOfPrioritizedTasksAndSubtasks.iterator();
            while(iterator.hasNext()){
                if(iterator.next().getType() == TaskType.TASK) {
                    iterator.remove();
                }
            }
            mapOfTasks.clear();
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public void clearListOfSubtasks() {
        //NOTE: If all subtasks are cleared, epic tasks have to be updated as well
        if (!mapOfSubTasks.isEmpty()) {
            for (SubTask subTaskToBeCleared : mapOfSubTasks.values()) {
                int idOfEpicTask = subTaskToBeCleared.getEpicTaskId();
                mapOfEpicTasks.get(idOfEpicTask).removeSubTaskInEpicTask(subTaskToBeCleared);
            }
            Iterator<Task> iterator = setOfPrioritizedTasksAndSubtasks.iterator();
            while(iterator.hasNext()){
                if(iterator.next().getType() == TaskType.SUBTASK) {
                    iterator.remove();
                }
            }
            mapOfSubTasks.clear();
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public void clearListOfEpicTasks() {
        //NOTE: If all epic tasks are cleared, subtasks have to be cleared as well
        if (!mapOfEpicTasks.isEmpty()) {
            mapOfEpicTasks.clear();
            mapOfSubTasks.clear();
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public void removeTask(Integer id) {
        if(!mapOfTasks.isEmpty()) {
            if (mapOfTasks.containsKey(id)) {
                setOfPrioritizedTasksAndSubtasks.remove(mapOfTasks.get(id));
                mapOfTasks.remove(id);
                defaultHistory.remove(id);
            } else {
                throw new NonexistentIdException("No task with such id was created");
            }
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public void removeSubTask(Integer id) {
        //NOTE: Subtask is also removed from the epic task
        if(!mapOfSubTasks.isEmpty()) {
            if (mapOfSubTasks.containsKey(id)) {
                SubTask subTaskToBeRemoved = mapOfSubTasks.get(id);
                setOfPrioritizedTasksAndSubtasks.remove(mapOfSubTasks.get(id));
                mapOfEpicTasks.get(subTaskToBeRemoved.getEpicTaskId()).removeSubTaskInEpicTask(subTaskToBeRemoved);
                mapOfSubTasks.remove(id);
                defaultHistory.remove(id);
            } else {
                throw new NonexistentIdException("No task with such id was created");
            }
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public void removeEpicTask (Integer id) {
        //NOTE: All subtasks of the epic task are removed
        if(!mapOfEpicTasks.isEmpty()) {
            if (mapOfEpicTasks.containsKey(id)) {
                for (SubTask subtaskToBeRemoved : mapOfEpicTasks.get(id).getListOfSubTasks()) {
                    setOfPrioritizedTasksAndSubtasks.remove(subtaskToBeRemoved);
                    mapOfSubTasks.remove(subtaskToBeRemoved.getId());
                    //NOTE: Removal of epic task from history invokes removal of all subtasks of this epic from history
                    defaultHistory.remove(subtaskToBeRemoved.getId());
                }
                mapOfEpicTasks.get(id).clearAllSubTasks();
                mapOfEpicTasks.remove(id);
                defaultHistory.remove(id);
            } else {
                throw new NonexistentIdException("No task with such id was created");
            }
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public ArrayList<SubTask> getListOfSubTasksForEpicTask(Integer id) {
        if(!mapOfEpicTasks.isEmpty()) {
            ArrayList<SubTask> result;
            if (mapOfEpicTasks.containsKey(id)) {
                result = mapOfEpicTasks.get(id).getListOfSubTasks();
            } else {
                throw new NonexistentIdException("No task with such id was created");
            }
            return result;
        } else {
            throw new NoTasksCreatedException("No tasks were created or list of tasks is empty");
        }
    }

    @Override
    public HistoryManager getHistoryManager() {
        return defaultHistory;
    }

    @Override
    public void clearAll() {
        if(!mapOfTasks.isEmpty()) {
            mapOfTasks.clear();
        }
        if(!mapOfSubTasks.isEmpty()) {
            mapOfSubTasks.clear();
        }
        if(!mapOfEpicTasks.isEmpty()) {
            mapOfEpicTasks.clear();
        }
        taskId = 0;
        if(!this.getHistoryManager().getHistory().isEmpty()) {
            this.getHistoryManager().removeAll();
        }
        if(!setOfPrioritizedTasksAndSubtasks.isEmpty()) {
            setOfPrioritizedTasksAndSubtasks.clear();
        }
        if(!slotsValidationMap.isEmpty()) {
            slotsValidationMap.clear();
        }
    }

    @Override
    public int getNumberOfTasks() {
        return mapOfTasks.size();
    }

    @Override
    public int getNumberOfSubtasks() {
        return mapOfSubTasks.size();
    }

    @Override
    public int getNumberOfEpicTasks() {
        return mapOfEpicTasks.size();
    }
}
