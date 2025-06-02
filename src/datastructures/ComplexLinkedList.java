package datastructures;

public class ComplexLinkedList {
    private class Node {
        public Object data;
        public Node link;

        public Node() {
            this.data = null;
            this.link = null;
        }

        public Node(Object data) {
            this.data = data;
            this.link = null;
        }
    }

    private Node headNode;
    private int length;

    public ComplexLinkedList() {
        this.headNode = new Node();
        this.length = 0;
    }

    public void addNode(Object data) {
        addNode(data, length - 1);
    }

    public void addNode(Object data, int place) throws IndexOutOfBoundsException {
        if (place >= this.length)
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

    public Object getNode() {
        return this.headNode.link.data;
    }

    public Object getNode(int place) throws IndexOutOfBoundsException {
        if (place > this.length)
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
        if (place >= this.length)
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

    public void deleteNode(Object data) {
        Node perviousNode = this.headNode;
        Node currentNode = this.headNode.link;

        while (currentNode != null) {
            if (currentNode.data == data) {
                perviousNode.link = currentNode.link;
                currentNode.link = null;
                this.length--;
            }

            perviousNode = currentNode;
            currentNode = currentNode.link;
        }
    }

    public int isInList(Object data) {
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

    public Object getData(Object data) { // Maybe you think it's useless but no, in this time you know this using
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
