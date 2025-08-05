package datastructures;

import java.io.Serializable;

public class HashMap<T> implements Serializable{
    private class Node {
        public String key;
        public T value;

        public Node(String key) {
            this.key = key;
        }

        public Node(String key, T value) {
            this(key);
            this.value = value;
        }

        // We just need check keys, because we want to use it in put and change method
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof HashMap.Node))
                return false;
            Node other = (Node) obj;
            return this.key.equals(other.key);
        }
    }

    private LinkedList<Node> nodesList;

    public HashMap() {
        nodesList = new LinkedList<Node>();
    }

    public void put(String key, T value) {
        Node newNode = new Node(key, value);
        int nodeStatus = nodesList.contains(newNode);
        if (nodeStatus == -1) {
            nodesList.addNode(newNode);
        } else {
            // If the key already exists, update the value
            nodesList.changeNode(nodeStatus, newNode);
        }
    }

    public T get(String key) {
        Node searchNode = new Node(key);
        int nodeStatus = nodesList.contains(searchNode);
        if (nodeStatus != -1) {
            return nodesList.getNode(nodeStatus).value;
        }
        return null;
    }

    public void delete(String key) {
        Node deletingNode = new Node(key);
        nodesList.deleteNode(deletingNode);
    }
}
