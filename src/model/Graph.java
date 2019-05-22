package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
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

    /*
     *      Searchers
     */

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

    public List<Arc> loops() {
        List<Arc> loops = new ArrayList<>();

        for (Arc arc : arcs) {
            if (arc.getBegin().equals(arc.getEnd())) {
                loops.add(arc);
            }
        }

        return loops;
    }

    /*
     *      'Contains' methods
     */

    public boolean containsLoop() {
        for (Arc arc : arcs) {
            if (arc.getBegin().equals(arc.getEnd())) {
                return true;
            }
        }

        return false;
    }

    /*
     *      Mods??
     */

    public Graph undirectedEquivalent() {
        Graph undirectedGraph = new Graph();

        undirectedGraph.getNodes().addAll(nodes);

        for (Arc arc : arcs) {
            undirectedGraph.getArcs().add(arc);

            if (arc.isDirected()) {
                undirectedGraph.getArcs().add(new Arc(arc.getEnd(), arc.getBegin()));
            }
        }

        return undirectedGraph;
    }
}
