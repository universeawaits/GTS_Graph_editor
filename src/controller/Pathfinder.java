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


    public Pathfinder(AdjacencyMatrix adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public ObservableList<Path> pathsBetween(Node begin, Node end) {
        ObservableList<Path> paths = FXCollections.observableArrayList();
        Map<Node, Boolean> visitedNodes = new HashMap<>();

        for (Node node : adjacencyMatrix.getGraph().getNodes()) {
            visitedNodes.put(node, false);
        }
        Path startPath = new Path();
        startPath.getPath().add(begin);

        dfsPathsBetweenNodes(begin, end, visitedNodes, paths, startPath);

        return paths;
    }

    private void dfsPathsBetweenNodes(Node currentNode, Node end, Map<Node, Boolean> visitedNodes, ObservableList<Path> paths, Path currentPath) {
        visitedNodes.replace(currentNode, true);

        if (currentNode.equals(end)) {
            paths.add(new Path(currentPath));
            visitedNodes.replace(currentNode, false);
            return;
        }

        for (Node adjacent : adjacencyMatrix.adjacentNodesOf(currentNode)) {
            if (!visitedNodes.get(adjacent)) {
                currentPath.getPath().add(adjacent);
                dfsPathsBetweenNodes(adjacent, end, visitedNodes, paths, currentPath);

                currentPath.getPath().remove(adjacent);
            }
        }

        visitedNodes.replace(currentNode, false);
    }
}
