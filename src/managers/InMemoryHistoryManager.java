package managers;

import tasks.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int SIZE_OF_HISTORY = 10;
    final private Map<Integer, Node<Task>> tasksIdsAndNodes = new HashMap<>();
    private int sizeOfCustomLinkedList = 0;
    private Node<Task> head;
    private Node<Task> tail;

    class Node<E> {
        public E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return data.equals(node.data) && next.equals(node.next) && prev.equals(node.prev);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, next, prev);
        }
    }

    private void linkLast (Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if(oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        ++sizeOfCustomLinkedList;
        tasksIdsAndNodes.put(task.getId(), tail);
    }

    //NOTE: Three options are envisaged: removal of ordinary node, head node or tail node
    private void removeNode(Node<Task> nodeToBeRemoved) {
        if(sizeOfCustomLinkedList != 0) {
            if(nodeToBeRemoved.equals(head)) {
                final Node<Task> oldHead = head;
                if(head.equals(tail)) {
                    tail = null;
                } else {
                    head.next.prev = null;
                }
                head = oldHead.next;
                oldHead.next = null;
            } else if (nodeToBeRemoved.equals(tail)) {
                final Node<Task> oldTail = tail;
                tail = oldTail.prev;
                tail.next = null;
            } else {
                nodeToBeRemoved.prev.next = nodeToBeRemoved.next;
                nodeToBeRemoved.next.prev = nodeToBeRemoved.prev;
            }
            --sizeOfCustomLinkedList;
            tasksIdsAndNodes.remove(nodeToBeRemoved.data.getId());
        }
    }

    private List<Task> getTasks() {
        List<Task> listOfTasksFromHistory = new ArrayList<>();
        Node<Task> currentNode = this.head;
        for(int i = 0; i < sizeOfCustomLinkedList; ++i) {
            listOfTasksFromHistory.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return listOfTasksFromHistory;
    }

    @Override
    public void add(Task task) {
        if(sizeOfCustomLinkedList < SIZE_OF_HISTORY) {
            if(tasksIdsAndNodes.containsKey(task.getId())) {
                removeNode(tasksIdsAndNodes.get(task.getId()));
            }
            linkLast(task);
        } else {
            if(!tasksIdsAndNodes.containsKey(task.getId())) {
                removeNode(head);
                linkLast(task);
            } else {
                removeNode(head);
                removeNode(tasksIdsAndNodes.get(task.getId()));
                linkLast(task);
            }
        }
    }

    @Override
    public void remove(int id) {
        removeNode(tasksIdsAndNodes.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
