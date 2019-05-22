package controller;

import model.*;

import java.util.*;


public class Colorer {
    private static final int NO_COLOR = -1;

    private Graph graph;
    private AdjacencyList adjacencyList;

    private Map<Node, String> nodesColors;


    public Colorer(Graph graph) {
        this.graph = graph.undirectedEquivalent();
        this.adjacencyList = new AdjacencyList(this.graph);

        nodesColors = new HashMap<>();
    }

    public Map<Node, String> colorizeNodes() {
        boolean isFirstNodeColored = false;
        int color = 0;

        for (Node node : adjacencyList.getAdjacencyLists().keySet()) {
            if (!isFirstNodeColored) {
                nodesColors.put(node, String.valueOf(color));
                isFirstNodeColored = true;
                color++;

                continue;
            }

            nodesColors.put(node, String.valueOf(color));
            color++;
        }

        Map<String, Boolean> availableColors = new HashMap<>();

        for (String currentColor : nodesColors.values()) {
            availableColors.put(currentColor, true);
        }

        for (Node node : adjacencyList.getAdjacencyLists().keySet()) {
            if (!nodesColors.get(node).equals(String.valueOf(NO_COLOR))) {
                for (Node someAdjacentNode : adjacencyList.getAdjacencyLists().get(node)) {
                    if (!nodesColors.get(someAdjacentNode).equals(String.valueOf(NO_COLOR)))
                        availableColors.replace(nodesColors.get(someAdjacentNode), false);
                }

                String currentColor = String.valueOf(0);
                for (String someColor : availableColors.keySet()){
                    if (availableColors.get(someColor)) {
                        currentColor = someColor;
                        break;
                    }
                }

                nodesColors.replace(node, currentColor);

                availableColors.clear();

                for (String someColor : nodesColors.values()) {
                    availableColors.put(someColor, true);
                }
            }
        }

        return nodesColors;
    }
}
