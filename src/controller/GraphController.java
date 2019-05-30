package controller;

import controller.converter.TreeConverter;
import controller.finder.HamiltonianCyclesFinder;
import controller.finder.Pathfinder;
import controller.verifier.PlanarityVerifier;
import controller.verifier.TreeVerifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.util.*;

import static model.DistanceMatrix.INFINITY;


public class GraphController {
    private Graph graph;
    private DistanceMatrix distanceMatrix;
    private AdjacencyMatrix adjacencyMatrix;
    private Pathfinder pathfinder;


    public GraphController(Graph graph) {
        this.graph = graph;
        distanceMatrix = new DistanceMatrix(graph);
        adjacencyMatrix = new AdjacencyMatrix(graph);
        pathfinder = new Pathfinder(adjacencyMatrix);
    }

    public Graph getGraph() {
        return graph;
    }

    public AdjacencyMatrix adjacencyMatrix() {
        return adjacencyMatrix;
    }

    public ObservableList<Node> getNodes() { return graph.getNodes(); }

    public ObservableList<Arc> getArcs() { return graph.getArcs(); }

    public AdjacencyMatrix getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public DistanceMatrix getDistanceMatrix() {
        return distanceMatrix;
    }

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
     *      Metrics
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

        for (Node node : distanceMatrix.getDistancesMap().keySet()) {
            eccentricity = 0;

            for (Integer distance : distanceMatrix.getDistancesMap().get(node).values()) {
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

    // Check for graph planarity
    public boolean isPlanar() {
        return new PlanarityVerifier(graph).verify();
    }

    // Check for graph complete
    public boolean isComplete() {
        return !graph.containsLoop() && (graph.getArcs().size() == graph.getNodes().size() * (graph.getNodes().size() - 1));
    }

    // Check is graph a tree
    public boolean isTree() {
        return !graph.containsLoop() && new TreeVerifier(graph).verify();
    }

    /*
     *      Other algorithms
     */

    // Finding all of hamiltonian cycles in the graph
    public ObservableList<Path> hamiltonianCycles() {
        return new HamiltonianCyclesFinder(adjacencyMatrix).find();
    }

    // Finding all of paths between two specified nodes
    public ObservableList<Path> pathsBetweenNodes(Node begin, Node end) {
        return pathfinder.pathsBetween(begin, end);
    }

    // Taking all of the shortest paths between two nodes
    public ObservableList<Path> shortestPaths() {
        return pathfinder.shortestPaths();
    }

    // Coloring of nodes
    public Map<Node, String> colorizeNodes() {
        return new Colorer(graph).colorizeNodes();
    }

    // Making all nodes adjacent to all nodes
    public void makeComplete() {
        List<Arc> arcsToDelete = new ArrayList<>();

        for (Node begin : graph.getNodes()) {
            for (Node end : graph.getNodes()) {
                if (begin.equals(end)) {
                    continue;
                }

                if (!graph.getArcs().contains(new Arc(begin, end))) {
                    graph.getArcs().add(new Arc(begin, end));
                }
            }
        }

        for (Arc arc : graph.getArcs()) {
            if (arc.getBegin().equals(arc.getEnd())) {
                arcsToDelete.add(arc);
                continue;
            }

            arc.setDirected(false);
        }

        graph.getArcs().removeAll(arcsToDelete);
    }

    // Making graph tree-like
    public void makeTree() {
        new TreeConverter(graph).convert();
    }
}
