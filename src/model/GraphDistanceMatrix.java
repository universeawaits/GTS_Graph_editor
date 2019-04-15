package model;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class GraphDistanceMatrix {
    public static final int INFINITY = 1000000;

    private Graph graph;
    ObservableList<ObservableList<Integer>> distancesMatrix;


    public GraphDistanceMatrix(Graph graph) {
        this.graph = graph;

        distancesMatrix = FXCollections.observableArrayList();
        configureDistancesMatrix();
    }

    public ObservableList<ObservableList<Integer>> getDistancesMatrix() {
        return distancesMatrix;
    }

    private void configureDistancesMatrix() {
        graph.getArcs().addListener((ListChangeListener) changeList -> {
            distancesMatrix.clear();
            for (Node node : graph.getNodes()) {
                distancesMatrix.add(allDistancesFrom(node));
            }
        });
    }


    public ObservableList<Integer> eccentricities() {
        ObservableList<Integer> eccentricities = FXCollections.observableArrayList();

        int eccentricity;

        for (ObservableList<Integer> distancesFromNode : distancesMatrix) {
            eccentricity = 0;

            for (Integer distance : distancesFromNode) {
                if ((distance > eccentricity) && (distance != INFINITY)) {
                    eccentricity = distance;
                }
            }

            eccentricities.add(eccentricity);
        }

        return eccentricities;
    }

    // Calcs distances between the node given and all other nodes in the graph with Bellman–Ford algorithm
    private ObservableList<Integer> allDistancesFrom(Node begin) {
        Map<Node, Integer> distanceTo = new HashMap<>();

        for (Node node : graph.getNodes()) {
            distanceTo.put(node, INFINITY);
        }
        distanceTo.replace(begin, 0);

        for (;;) {
            boolean any = false;

            for (Arc arc : graph.getArcs()) {
                if (distanceTo.get(arc.getBegin()) < INFINITY) {
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

        return FXCollections.observableArrayList(distanceTo.values());
    }
}
