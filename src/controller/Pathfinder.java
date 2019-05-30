package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AdjacencyMatrix;
import model.Node;
import model.Path;

import java.util.HashMap;
import java.util.Map;


public class Pathfinder {
    private AdjacencyMatrix adjacencyMatrix;
    private Map<Node, Boolean> visitedNodes;
    private ObservableList<Path> paths;


    public Pathfinder(AdjacencyMatrix adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        visitedNodes = new HashMap<>();
        paths = FXCollections.observableArrayList();
    }

    public ObservableList<Path> pathsBetween(Node begin, Node end) {
        paths.clear();
        visitedNodes.clear();
        for (Node node : adjacencyMatrix.getGraph().getNodes()) {
            visitedNodes.put(node, false);
        }
        Path startPath = new Path();
        startPath.getPath().add(begin);

        dfsPathsBetweenNodes(begin, end, startPath);

        return paths;
    }

    private void dfsPathsBetweenNodes(Node currentNode, Node end, Path currentPath) {
        visitedNodes.replace(currentNode, true);

        if (currentNode.equals(end)) {
            paths.add(new Path(currentPath));
            visitedNodes.replace(currentNode, false);
            return;
        }

        for (Node adjacent : adjacencyMatrix.adjacentNodesOf(currentNode)) {
            if (!visitedNodes.get(adjacent)) {
                currentPath.getPath().add(adjacent);
                dfsPathsBetweenNodes(adjacent, end, currentPath);

                currentPath.getPath().remove(adjacent);
            }
        }

        visitedNodes.replace(currentNode, false);
    }
}
