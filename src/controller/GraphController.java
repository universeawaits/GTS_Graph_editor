package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.util.*;

import static model.DistanceMatrix.INFINITY;


public class GraphController {
    private Graph graph;
    private DistanceMatrix distanceMatrix;
    private AdjacencyMatrix adjacencyMatrix;


    public GraphController(Graph graph) {
        this.graph = graph;
        distanceMatrix = new DistanceMatrix(graph);
        adjacencyMatrix = new AdjacencyMatrix(graph);
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
        return graph.getArcs().size() == graph.getNodes().size() * (graph.getNodes().size() - 1);
    }

    /*
     *      Other algorithms
     */

    // Finding all of hamiltonian cycles in the graph
    public ObservableList<Path> hamiltonianCycles() {
        ObservableList<Path> hamiltonianCycles = FXCollections.observableArrayList();

        for (Node begin : graph.getNodes()) {
            ObservableList<Path> cycles = findAllHamiltonianCyclesFrom(begin);
            for (Path cycleFromThisNode : cycles) {
                if (!hamiltonianCycles.contains(cycleFromThisNode)) {
                    hamiltonianCycles.add(cycleFromThisNode);
                }
            }
        }

        return hamiltonianCycles;
    }

    // Coloring of nodes
    public Map<Node, String> colorizeNodes() {
        return new Colorer(graph).colorizeNodes();
    }

    // Making all nodes adjacent to all nodes
    public void makeComplete() {
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
            arc.setDirected(false);
        }
    }

    /*
     *      Utility
     */

    // Finds all possible Hamiltonian cycles begins with the node given
    private ObservableList<Path> findAllHamiltonianCyclesFrom(Node begin) {
        Map<Node, Boolean> visitedNodes = new HashMap<>();
        ObservableList<Path> hamiltonianCyclesBeginsWithThisNode = FXCollections.observableArrayList();
        Path trackingCycle = new Path();

        for (Node node : graph.getNodes()) {
            visitedNodes.put(node, false);
        }

        dfsHamiltonianCycle(begin, trackingCycle, visitedNodes, hamiltonianCyclesBeginsWithThisNode);

        return hamiltonianCyclesBeginsWithThisNode;
    }

    private void dfsHamiltonianCycle(Node begin, Path trackingCycle,
                                     Map<Node, Boolean> visitedNodes,
                                     ObservableList<Path> hamiltonianCyclesBeginsWithThisNode) {

        if (trackingCycle.getPath().size() == graph.getNodes().size()) {
            if (graph.getArcs().contains(new Arc(trackingCycle.getPath().get(trackingCycle.getPath().size() - 1),
                    trackingCycle.getPath().get(0)))) {
                Path hamiltonianCycle = new Path(trackingCycle);
                hamiltonianCycle.getPath().add(trackingCycle.getPath().get(0));

                for (Path cycle : hamiltonianCyclesBeginsWithThisNode) {
                    if (hamiltonianCycle.getPath().contains(cycle.getPath())) {
                        return;
                    }
                }

                hamiltonianCyclesBeginsWithThisNode.add(hamiltonianCycle);

                return;
            }
        }

        for (Node adjacentNode : adjacencyMatrix.adjacentNodesOf(begin)) {
            if (!visitedNodes.get(adjacentNode)) {
                visitedNodes.replace(adjacentNode, true);
                trackingCycle.getPath().add(adjacentNode);

                dfsHamiltonianCycle(adjacentNode, trackingCycle, visitedNodes, hamiltonianCyclesBeginsWithThisNode);

                visitedNodes.replace(adjacentNode, false);
                trackingCycle.getPath().remove(trackingCycle.getPath().size() - 1);
            }
        }
    }
}
