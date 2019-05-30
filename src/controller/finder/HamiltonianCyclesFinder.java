package controller.finder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AdjacencyMatrix;
import model.Arc;
import model.Node;
import model.Path;

import java.util.HashMap;
import java.util.Map;


public class HamiltonianCyclesFinder {
    private AdjacencyMatrix adjacencyMatrix;
    private Map<Node, Boolean> visitedNodes;
    private ObservableList<Path> hamiltonianCycles;


    public HamiltonianCyclesFinder(AdjacencyMatrix adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        visitedNodes = new HashMap<>();
        hamiltonianCycles = FXCollections.observableArrayList();
    }

    // Finding all of hamiltonian cycles in the graph
    public ObservableList<Path> find() {
        for (Node begin : adjacencyMatrix.getGraph().getNodes()) {
            ObservableList<Path> cycles = findAllHamiltonianCyclesFrom(begin);
            for (Path cycleFromThisNode : cycles) {
                if (!hamiltonianCycles.contains(cycleFromThisNode)) {
                    hamiltonianCycles.add(cycleFromThisNode);
                }
            }
        }

        return hamiltonianCycles;
    }

    /*
     *      Utility
     */

    // Finds all possible Hamiltonian cycles begins with the node given
    private ObservableList<Path> findAllHamiltonianCyclesFrom(Node begin) {
        ObservableList<Path> hamiltonianCyclesBeginsWithThisNode = FXCollections.observableArrayList();
        Path trackingCycle = new Path();

        for (Node node : adjacencyMatrix.getGraph().getNodes()) {
            visitedNodes.put(node, false);
        }

        dfsHamiltonianCycle(begin, trackingCycle, hamiltonianCyclesBeginsWithThisNode);

        return hamiltonianCyclesBeginsWithThisNode;
    }

    private void dfsHamiltonianCycle(Node begin, Path trackingCycle,
                                     ObservableList<Path> hamiltonianCyclesBeginsWithThisNode) {

        if (trackingCycle.getPath().size() == adjacencyMatrix.getGraph().getNodes().size()) {
            if (adjacencyMatrix.getGraph().getArcs().contains(
                    new Arc(trackingCycle.getPath().get(trackingCycle.getPath().size() - 1),
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

                dfsHamiltonianCycle(adjacentNode, trackingCycle, hamiltonianCyclesBeginsWithThisNode);

                visitedNodes.replace(adjacentNode, false);
                trackingCycle.getPath().remove(trackingCycle.getPath().size() - 1);
            }
        }
    }
}
