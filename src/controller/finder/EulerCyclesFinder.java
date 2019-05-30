package controller.finder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AdjacencyMatrix;
import model.Arc;
import model.Node;
import model.Path;

import java.util.HashMap;
import java.util.Map;


public class EulerCyclesFinder {
    private AdjacencyMatrix adjacencyMatrix;

    private Map<Arc, Boolean> visitedArcs;
    private ObservableList<Path> eulerCycles;
    private boolean isCycleFound = false;


    public EulerCyclesFinder(AdjacencyMatrix adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        visitedArcs = new HashMap<>();
        eulerCycles = FXCollections.observableArrayList();
    }

    // Finding all of euler cycles in the graph
    public ObservableList<Path> find() {
        for (Arc begin : adjacencyMatrix.getGraph().getArcs()) {
            ObservableList<Path> cycles = eulerCyclesFrom(begin);
            for (Path cycleFromThisArc : cycles) {
                if (!eulerCycles.contains(cycleFromThisArc)) {
                    eulerCycles.add(cycleFromThisArc);
                }
            }
        }

        return eulerCycles;
    }

    /*
     *      Utility
     */

    // Finds all possible Euler cycles begins with the arc given
    private ObservableList<Path> eulerCyclesFrom(Arc begin) {
        ObservableList<Path> hamiltonianCyclesBeginsWithThisNode = FXCollections.observableArrayList();
        Path trackingCycle = new Path();

        for (Arc arc : adjacencyMatrix.getGraph().getArcs()) {
            visitedArcs.put(arc, false);
        }

        trackingCycle.getPath().add(begin.getBegin());
        trackingCycle.getPath().add(begin.getEnd());
        visitedArcs.replace(begin, true);
        dfsEulerCycle(begin, trackingCycle, hamiltonianCyclesBeginsWithThisNode);

        return hamiltonianCyclesBeginsWithThisNode;
    }

    private void dfsEulerCycle(Arc currentArc, Path trackingCycle,
                               ObservableList<Path> eulerCyclesBeginsWithThisNode) {

        isCycleFound = true;

        for (Arc arc : visitedArcs.keySet()) {
            if (!visitedArcs.get(arc)) {
                isCycleFound = false;
                break;
            }
        }

        if (isCycleFound) {
            if (trackingCycle.getPath().get(trackingCycle.getPath().size() - 1)
                    .equals(trackingCycle.getPath().get(0))) {

                Path eulerCycle = new Path(trackingCycle);
                eulerCyclesBeginsWithThisNode.add(eulerCycle);

                return;
            }
        }

        for (Node adjacent : adjacencyMatrix.adjacentNodesOf(currentArc.getEnd())) {
            Arc adjacentArc = adjacencyMatrix.getGraph().getArc(currentArc.getEnd(), adjacent);

            if (!visitedArcs.get(adjacentArc)) {
                visitedArcs.replace(adjacentArc, true);
                trackingCycle.getPath().add(adjacent);

                dfsEulerCycle(adjacentArc, trackingCycle, eulerCyclesBeginsWithThisNode);

                visitedArcs.replace(adjacentArc, false);
                trackingCycle.getPath().remove(trackingCycle.getPath().size() - 1);
            }
        }
    }
}
