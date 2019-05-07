package model;

import javafx.collections.ListChangeListener;

import java.util.HashMap;
import java.util.Map;


public class DistanceMatrix {
    public static final int INFINITY = 1000000;

    private Graph graph;
    private Map<Node, Map<Node, Integer>> distancesMap;


    public DistanceMatrix(Graph graph) {
        this.graph = graph;

        distancesMap = new HashMap<>();
        configureDistancesMatrix();
    }

    public Map<Node, Map<Node, Integer>> getDistancesMap() {
        return distancesMap;
    }

    @Override
    public String toString() {
        String toString = "";

        for (Node node : distancesMap.keySet()) {
            for (Integer distance : distancesMap.get(node).values()) {
                toString = toString.concat(String.valueOf(distance) + ' ');
            }
            toString = toString.concat("\n");
        }

        return toString;
    }

    /*
     *      Configs
     */

    private void configureDistancesMatrix() {
        for (Node node : graph.getNodes()) {
            distancesMap.put(node, allDistancesFrom(node));
        }

        graph.getArcs().addListener((ListChangeListener) changeList -> {
            distancesMap.clear();
            for (Node node : graph.getNodes()) {
                distancesMap.put(node, allDistancesFrom(node));
            }
        });
    }

    /*
     *      Calculations
     */

    // Calculation of distances between the node given and all other nodes in the graph
    // with modified Bellman–Ford algorithm
    private Map<Node, Integer> allDistancesFrom(Node begin) {
        Map<Node, Integer> distanceTo = new HashMap<>();

        for (Node node : graph.getNodes()) {
            distanceTo.put(node, INFINITY);
        }
        distanceTo.replace(begin, 0);

        while (true) {
            boolean any = false;

            for (Arc arc : graph.getArcs()) {
                if ((distanceTo.get(arc.getBegin()) < INFINITY)) {
                    if ((distanceTo.get(arc.getEnd()) > distanceTo.get(arc.getBegin()) + Arc.WEIGHT)) {
                        distanceTo.replace(arc.getEnd(), distanceTo.get(arc.getBegin()) + Arc.WEIGHT);
                        any = true;
                    }
                }
            }

            if (!any)  {
                break;
            }
        }

        return distanceTo;
    }
}
