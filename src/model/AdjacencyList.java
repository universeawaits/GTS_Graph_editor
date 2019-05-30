package model;

import javafx.collections.ListChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdjacencyList {
    private Graph graph;

    private Map<Node, List<Node>> adjacencyLists;


    public AdjacencyList(Graph graph) {
        this.graph = graph;

        adjacencyLists = new HashMap<>();
        configureAdjacencyList();
    }

    public List<Node> setAdjacentNodesFor(Node node) {
        List<Node> adjacents = new ArrayList<>();

        for (Arc arc : graph.getArcs()) {
            if (arc.getBegin().equals(node)) {
                adjacents.add(arc.getEnd());
            }
        }

        return adjacents;
    }

    /*
     *      Configs
     */

    private void configureAdjacencyList() {
        for (Node node : graph.getNodes()) {
            adjacencyLists.put(node, setAdjacentNodesFor(node));
        }

        graph.getArcs().addListener((ListChangeListener) changeList -> {
            adjacencyLists.clear();
            for (Node node : graph.getNodes()) {
                adjacencyLists.put(node, setAdjacentNodesFor(node));
            }
        });

        graph.getNodes().addListener((ListChangeListener) changeList -> {
            adjacencyLists.clear();
            for (Node node : graph.getNodes()) {
                adjacencyLists.put(node, setAdjacentNodesFor(node));
            }
        });
    }


    public Map<Node, List<Node>> getAdjacencyLists() {
        return adjacencyLists;
    }
}
