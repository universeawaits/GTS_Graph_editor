package model;


import java.util.Objects;

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

    public Node getBegin() {
        return begin;
    }

    public Node getEnd() {
        return end;
    }

    public boolean isDirected() {
        return isDirected;
    }

    @Override
    public String toString() {
        return isDirected ?
                begin.getName() + " -> " + end.getName() :
                begin.getName() + " - " + end.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arc arc = (Arc) o;
        return  Objects.equals(begin, arc.begin) &&
                Objects.equals(end, arc.end);
    }
}
