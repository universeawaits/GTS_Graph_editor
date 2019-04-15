package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Arc;
import model.Graph;
import model.GraphDistanceMatrix;
import model.Node;


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

    // Calcs degree of a node
    public int degreeOf(Node node) {
        int degree = 0;

        for (Arc arc : graph.getArcs()) {
            if (arc.getBegin().equals(node) || arc.getEnd().equals(node)) {
                degree++;
            }
        }

        return degree;
    }

    // Calcs graph diameter
    public int diameter() {
        int diameter = 0;

        for (Integer eccentricity : graphDistanceMatrix.eccentricities()) {
            if ((eccentricity > diameter) && (eccentricity != INFINITY)) {
                diameter = eccentricity;
            }
        }

        return diameter;
    }

    // Calcs graph radius
    public int radius() {
        int radius = INFINITY;

        for (Integer eccentricity : graphDistanceMatrix.eccentricities()) {
            if ((eccentricity < radius) && (eccentricity != 0)) {
                radius = eccentricity;
            }
        }

        return radius == INFINITY ? 0 : radius;
    }
}
