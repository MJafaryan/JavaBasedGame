package datastructures;

import java.io.Serializable;

public class LinkedList<T> implements Serializable {
    private class Node {
        public T data;
        public Node link;

        public Node() {
            this.data = null;
            this.link = null;
        }

        public Node(T data) {
            this.data = data;
            this.link = null;
        }
    }

    private Node headNode;
    private int length;

    public LinkedList() {
        this.headNode = new Node();
        this.length = 0;
    }

    public void addNode(T data) {
        addNode(data, length);
    }

    public void addNode(T data, int place) throws IndexOutOfBoundsException {
        if (place < 0 || place > this.length)
            throw new IndexOutOfBoundsException();

        Node perviousNode = this.headNode;
        Node currentNode = this.headNode.link;

        for (int i = 0; i < place; i++) {
            perviousNode = currentNode;
            currentNode = currentNode.link;
        }

        perviousNode.link = new Node(data);
        perviousNode.link.link = currentNode;
        this.length++;
    }

    public T getNode() {
        return this.headNode.link.data;
    }

    public T getNode(int place) throws IndexOutOfBoundsException {
        if (place < 0 || place >= this.length)
            throw new IndexOutOfBoundsException();

        Node currentNode = this.headNode.link;
        for (int i = 0; i < place; i++)
            currentNode = currentNode.link;

        return currentNode.data;
    }

    public void deleteNode() {
        Node firstDataNode = this.headNode.link;
        this.headNode.link = firstDataNode.link;
        firstDataNode.link = null;
        this.length--;
    }

    public void deleteNode(int place) throws IndexOutOfBoundsException {
        if (place < 0 || place >= this.length)
            throw new IndexOutOfBoundsException();

        Node perviousNode = this.headNode;
        Node currentNode = this.headNode.link;

        for (int i = 0; i < place; i++) {
            perviousNode = currentNode;
            currentNode = currentNode.link;
        }

        perviousNode.link = currentNode.link;
        currentNode.link = null;
        this.length--;
    }

    public void deleteNode(T data) {
        Node perviousNode = this.headNode;
        Node currentNode = this.headNode.link;

        while (currentNode != null) {
            if (currentNode.data.equals(data)) {
                perviousNode.link = currentNode.link;
                currentNode.link = null;
                this.length--;
            }

            perviousNode = currentNode;
            currentNode = currentNode.link;
        }
    }

    public void changeNode(int place, T newData) throws IndexOutOfBoundsException {
        if (place < 0 || place >= this.length)
            throw new IndexOutOfBoundsException();

        Node currentNode = this.headNode.link;
        for (int i = 0; i < place; i++) {
            currentNode = currentNode.link;
        }
        currentNode.data = newData;
    }

    public int contains(T data) {
        Node currentNode = this.headNode.link;
        int count = 0;

        while (currentNode != null) {
            if (currentNode.data.equals(data))
                return count;

            currentNode = currentNode.link;
            count++;
        }
        return -1;
    }

    public T getData(T data) { // Maybe you think it's useless but no, in this time you know this using
        Node currentNode = this.headNode.link;
        while (currentNode != null) {
            if (currentNode.data.equals(data))
                return currentNode.data;

            currentNode = currentNode.link;
        }
        return null;
    }

    public int getLength() {
        return this.length;
    }
}
