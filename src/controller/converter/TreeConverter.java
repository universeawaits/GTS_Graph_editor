package controller.converter;

import model.AdjacencyMatrix;
import model.Graph;
import model.Node;

import java.util.List;
import java.util.Map;


public class TreeConverter implements Converter {
    private Node selectedNode;
    private Node cycleBegin;
    private Node cycleEnd;

    private Map<Node, Boolean> visitedNodes;
    private Map<Node, Node> ancestors;
    private List<Node> cycle;

    private Graph graph;
    private AdjacencyMatrix adjacencyMatrix;


    public TreeConverter(Graph graph) {
        this.graph = graph.undirectedEquivalent();
        adjacencyMatrix = new AdjacencyMatrix(this.graph);
    }

    public void convert() {

    }
}
