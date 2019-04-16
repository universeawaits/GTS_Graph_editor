package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Arc;
import model.Graph;
import model.GraphDistanceMatrix;
import model.Node;

import java.util.HashMap;
import java.util.Map;

import static model.GraphDistanceMatrix.INFINITY;


public class GraphController {
    private Graph graph;
    private GraphDistanceMatrix graphDistanceMatrix;


    public GraphController(Graph graph) {
        this.graph = graph;
        graphDistanceMatrix = new GraphDistanceMatrix(graph);
    }

    public Graph getGraph() {
        return graph;
    }

    public ObservableList<Node> getNodes() { return graph.getNodes(); }

    public ObservableList<Arc> getArcs() { return graph.getArcs(); }

    public void addNode(Node node) {
        graph.getNodes().add(node);
    }

    public void removeNode(Node node) {
        graph.getNodes().remove(node);

        ObservableList<Arc> arcsToRemove = FXCollections.observableArrayList();

        for (Arc arc : graph.getArcs()) {
            if (arc.getBegin().equals(node) || arc.getEnd().equals(node)) {
                arcsToRemove.add(arc);
            }
        }

        graph.getArcs().removeAll(arcsToRemove);
    }

    public void addArc(Arc arc) {
        graph.getArcs().add(arc);
    }

    public void removeArc(Arc arc) {
        graph.getArcs().remove(arc);
    }

    /*
        Metrics
     */

    // Calculation of a node degree
    public int degreeOf(Node node) {
        int degree = 0;

        for (Arc arc : graph.getArcs()) {
            if (arc.getBegin().equals(node) || arc.getEnd().equals(node)) {
                degree++;
            }
        }

        return degree;
    }

    // Calculation of the nodes' eccentricities
    private Map<Node, Integer> eccentricities() {
        Map<Node, Integer> eccentricities = new HashMap<>();

        int eccentricity;

        for (Node node : graphDistanceMatrix.getDistancesMap().keySet()) {
            eccentricity = 0;

            for (Integer distance : graphDistanceMatrix.getDistancesMap().get(node).values()) {
                if ((distance > eccentricity) && (distance != INFINITY)) {
                    eccentricity = distance;
                }
            }

            eccentricities.put(node, eccentricity);
        }

        return eccentricities;
    }

    // Calculation of a graph diameter
    public int diameter() {
        int diameter = 0;

        for (Integer eccentricity : eccentricities().values()) {
            if ((eccentricity > diameter) && (eccentricity != INFINITY)) {
                diameter = eccentricity;
            }
        }

        return diameter;
    }

    // Calculation of a graph radius
    public int radius() {
        int radius = INFINITY;

        for (Integer eccentricity : eccentricities().values()) {
            if ((eccentricity < radius) && (eccentricity != 0)) {
                radius = eccentricity;
            }
        }

        return radius == INFINITY ? 0 : radius;
    }

    // Taking of graph centers
    public ObservableList<Node> centers() {
        ObservableList<Node> centres = FXCollections.observableArrayList();
        int radius = radius();

        for (Node node : eccentricities().keySet()) {
            if (eccentricities().get(node) == radius) {
                centres.add(node);
            }
        }

        return centres;
    }

    // Finding all of hamiltonian cycles in the graph
    public ObservableList<ObservableList<Arc>> hamiltonianCycles() {
        ObservableList<ObservableList<Arc>> hamiltonianCycles = FXCollections.observableArrayList();

        for (Node begin : graph.getNodes()) {
            ObservableList<Arc> cycle = findHamiltonianCycleFrom(begin);
            if (!cycle.isEmpty()) {
                hamiltonianCycles.add(cycle);
            }
        }

        return hamiltonianCycles;
    }

    // Finds one of all possible cycles begins with the node given
    private ObservableList<Arc> findHamiltonianCycleFrom(Node begin) {
        Map<Node, Boolean> nodeVisited = new HashMap<>();
        Map<Arc, Boolean> arcVisited = new HashMap<>();
        ObservableList<Arc> cycle = FXCollections.observableArrayList();

        for (Node node : graph.getNodes()) {
            nodeVisited.put(node, false);
        }
        for (Arc arc : graph.getArcs()) {
            arcVisited.put(arc, false);
        }

        deepFirstSearch(begin, nodeVisited, arcVisited, cycle);

        System.out.println(" ");

        //System.out.println(cycle);

        if (cycle.isEmpty()) {
            return cycle;
        }

        if (getIncidentArc(cycle.get(cycle.size() - 1).getEnd(), begin) == null) {
            cycle.clear();
            return cycle;
        } else {
            for (Node node : nodeVisited.keySet()) {
                if (!nodeVisited.get(node)) {
                    cycle.clear();
                    return cycle;
                }
            }
        }

        cycle.add(getIncidentArc(cycle.get(cycle.size() - 1).getEnd(), begin));

        return cycle;
    }

    private void deepFirstSearch(Node begin, Map<Node, Boolean> nodeVisited, Map<Arc, Boolean> arcVisited, ObservableList<Arc> cycle) {
        nodeVisited.replace(begin, true);
        Arc incidentArc = null;

        for (Node node : graph.getNodes()) {
            incidentArc = getIncidentArc(begin, node);

            if ((incidentArc != null) && arcVisited.get(incidentArc)) {
                incidentArc = null;
                continue;
            }

            if ((incidentArc != null) && nodeVisited.get(incidentArc.getEnd()) && !arcVisited.get(incidentArc)){
                incidentArc = null;
                continue;
            }

            if ((incidentArc != null) && !nodeVisited.get(incidentArc.getEnd())) {
                cycle.add(incidentArc);
                arcVisited.replace(incidentArc, true);
                deepFirstSearch(incidentArc.getEnd(), nodeVisited, arcVisited, cycle);
            }
        }

        if ((incidentArc == null)) {
            for (Node node : nodeVisited.keySet()) {
                if (!nodeVisited.get(node)){
                    nodeVisited.replace(begin, false);
                    return;
                }
            }
        }
    }

    private Arc getIncidentArc(Node begin, Node end) {
        for (Arc arc : graph.getArcs()) {
            if (arc.getBegin().equals(begin) && arc.getEnd().equals(end)) {
                return arc;
            }
        }

        return null;
    }
}
