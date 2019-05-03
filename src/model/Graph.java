package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Graph {
    private String name;

    private ObservableList<Node> nodes;
    private ObservableList<Arc> arcs;


    public Graph(String name) {
        this.name = name;

        nodes = FXCollections.observableArrayList();
        arcs = FXCollections.observableArrayList();
    }

    public Graph() {
        this("");
    }

    public ObservableList<Node> getNodes() {
        return nodes;
    }

    public ObservableList<Arc> getArcs() {
        return arcs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Arc getArc(Node begin, Node end) {
        for (Arc arc : arcs) {
            if (arc.getBegin().equals(begin) && arc.getEnd().equals(end)) {
                return arc;
            }
        }

        return null;
    }

    public Node getNode(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }

        return null;
    }
}
