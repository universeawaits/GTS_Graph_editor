package model;

public class Node {
    private static long nodeCounter;
    private final long IDENTIFIER = nodeCounter++;

    private String name;


    public Node() {
        name = "";
    }

    public Node(String name) {
        this.name = name;
    }

    public long getIdentifier() {
        return IDENTIFIER;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name.equals("") ? ("[" + IDENTIFIER + "]") : name;
    }
}
