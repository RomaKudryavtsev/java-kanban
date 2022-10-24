package managers;

import java.util.Objects;

public class Node<E> {
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
