package model;

import javafx.collections.ListChangeListener;

import java.util.*;

public class AdjacencyMatrix {
    private Graph graph;
    private Map<Node, Map<Node, Boolean>> adjacencyMatrix;


    public AdjacencyMatrix() {
        graph = new Graph();

        adjacencyMatrix = new HashMap<>();
        configureAdjacencyMatrix();
    }

    public AdjacencyMatrix(Graph graph) {
        this.graph = graph;

        adjacencyMatrix = new HashMap<>();
        configureAdjacencyMatrix();
    }

    public String matrixToString() {
        String toString = "";

        for (Node node : adjacencyMatrix.keySet()) {
            for (Boolean isAdjacent : adjacencyMatrix.get(node).values()) {
                toString = toString.concat(isAdjacent ? "1 " : "0 ");
            }
            toString = toString.concat("\n");
        }

        return toString;
    }

    public Graph getGraph() {
        return graph;
    }

    /*
        Configs
     */

    private void configureAdjacencyMatrix() {
        for (Node node : graph.getNodes()) {
            adjacencyMatrix.put(node, setAdjacentNodesFor(node));
        }

        graph.getArcs().addListener((ListChangeListener) changeList -> {
            adjacencyMatrix.clear();
            for (Node node : graph.getNodes()) {
                adjacencyMatrix.put(node, setAdjacentNodesFor(node));
            }
        });

        graph.getNodes().addListener((ListChangeListener) changeList -> {
            adjacencyMatrix.clear();
            for (Node node : graph.getNodes()) {
                adjacencyMatrix.put(node, setAdjacentNodesFor(node));
            }
        });
    }

    /*
        Utility
     */

    private Map<Node, Boolean> setAdjacentNodesFor(Node node) {
        Map<Node, Boolean> adjacentNodes = new HashMap<>();

        for (Node graphNode : graph.getNodes()) {
            adjacentNodes.put(graphNode, false);
        }

        for (Node anotherGraphNode : graph.getNodes()) {
            adjacentNodes.replace(anotherGraphNode, graph.getArcs().contains(new Arc(node, anotherGraphNode)));
        }

        return adjacentNodes;
    }

    /*
        Others
     */

    public List<Node> adjacentNodesOf(Node node) {
        List<Node> adjacents = new ArrayList<>();

        Map<Node, Boolean> nodesMap = adjacencyMatrix.get(node);
        Set<Node> nodes = nodesMap.keySet();


        for (Node foundNode : nodes) {
            if (adjacencyMatrix.get(node).get(foundNode)) {
                adjacents.add(foundNode);
            }
        }

        return adjacents;
    }
}
