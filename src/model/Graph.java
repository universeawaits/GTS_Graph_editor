package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Graph {
    private ObservableList<Node> nodes;
    private ObservableList<Arc> arcs;


    public Graph() {
        nodes = FXCollections.observableArrayList();
        arcs = FXCollections.observableArrayList();
    }

    public ObservableList<Node> getNodes() {
        return nodes;
    }

    public ObservableList<Arc> getArcs() {
        return arcs;
    }
}
