package controller;

import model.*;

import java.util.*;

public class Colorer {
    private static final int NO_COLOR = -1;

    private Graph graph;
    private AdjacencyList adjacencyList;
    private IncidenceList incidenceList;

    private Map<Node, String> nodesColors;
    private Map<Arc, String> arcsColors;

    public Colorer(Graph graph) {
        this.graph = undirectedEquivalentOf(graph);
        this.adjacencyList = new AdjacencyList(this.graph);
        this.incidenceList = new IncidenceList(this.graph);

        nodesColors = new HashMap<>();
        arcsColors = new HashMap<>();
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

    /*
     *      Utility
     */

    private Graph undirectedEquivalentOf(Graph graph) {
        Graph undirectedGraph = new Graph();

        undirectedGraph.getNodes().addAll(graph.getNodes());

        for (Arc arc : graph.getArcs()) {
            undirectedGraph.getArcs().add(arc);

            if (arc.isDirected()) {
                undirectedGraph.getArcs().add(new Arc(arc.getEnd(), arc.getBegin()));
            }
        }

        return undirectedGraph;
    }
}
