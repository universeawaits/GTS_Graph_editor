package model;

import java.util.Objects;

public class Node {
    private String name;


    public Node() {
        name = "";
    }

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Node " + name;
    }
}
