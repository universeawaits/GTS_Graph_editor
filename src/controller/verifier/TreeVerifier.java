package controller.verifier;

import model.AdjacencyMatrix;
import model.Graph;
import model.Node;

import java.util.HashMap;
import java.util.Map;


public class TreeVerifier {
    private Graph graph;
    private AdjacencyMatrix adjacencyMatrix;


    public TreeVerifier(Graph graph) {
        this.graph = graph.undirectedEquivalent();
        adjacencyMatrix = new AdjacencyMatrix(this.graph);
    }

    public boolean verify() {
        Map<Node, Boolean> visitedNodes = new HashMap<>();

        for (Node node : graph.getNodes()) {
            visitedNodes.put(node, false);
        }

        if (dfsIsCyclic(graph.getNodes().get(0), new Node(), visitedNodes)) {
            return false;
        }

        for (Node node : graph.getNodes()) {
            if (!visitedNodes.get(node)) {
                return false;
            }
        }

        return true;
    }

    /*
     *      Utility
     */

    private Boolean dfsIsCyclic(Node currentNode, Node parent, Map<Node, Boolean> visitedNodes) {
        visitedNodes.replace(currentNode, true);

        for (Node adjacent : adjacencyMatrix.adjacentNodesOf(currentNode)) {
            if (!visitedNodes.get(adjacent)) {
                if (dfsIsCyclic(adjacent, currentNode, visitedNodes)) {
                    return true;
                }
            }

            else if (!adjacent.equals(parent)) {
                return true;
            }
        }

        return false;
    }
}
