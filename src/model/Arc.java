package model;


public class Arc {
    private Node begin;
    private Node end;
    private boolean isDirected;


    public Arc(Node begin, Node end) {
        this.begin = begin;
        this.end = end;
        isDirected = true;
    }

    public Arc(Node begin, Node end, boolean isDirected) {
        this.begin = begin;
        this.end = end;
        this.isDirected = isDirected;
    }
}
