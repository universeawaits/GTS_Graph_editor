package sample;

import model.Arc;
import model.Graph;
import model.Node;

public class GraphController {
    private Graph graph;


    public GraphController(Graph graph) {
        this.graph = graph;
    }


    public void addNode(Node node) {
        graph.getNodes().add(node);
    }

    public void addArc(Arc arc) {
        graph.getArcs().add(arc);
    }
}
