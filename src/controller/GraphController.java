package controller;

import javafx.collections.ObservableList;
import model.Arc;
import model.Graph;
import model.Node;

public class GraphController {
    private Graph graph;


    public GraphController(Graph graph) {
        this.graph = graph;
    }

    public ObservableList<Node> getNodes() { return graph.getNodes(); }

    public ObservableList<Arc> getArcs() { return graph.getArcs(); }

    public void addNode(Node node) {
        graph.getNodes().add(node);
    }

    public void removeNode(Node node) {
        graph.getNodes().remove(node);
    }

    public void addArc(Arc arc) {
        graph.getArcs().add(arc);
    }
}
