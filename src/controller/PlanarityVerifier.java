package controller;

import model.*;

import java.util.*;


public class PlanarityVerifier {
    private Graph graph;
    private AdjacencyMatrix adjacencyMatrix;


    public PlanarityVerifier(Graph graph) {
        this.graph = makeGraphUndirected(graph);
        this.adjacencyMatrix = new AdjacencyMatrix(this.graph);
    }


    public boolean isPlanar() {
        Map<Node, Integer> nodeColors = new HashMap<>();

        for (Node begin : graph.getNodes()) {
            nodeColors.clear();

            for (Node node : graph.getNodes()) {
                nodeColors.put(node, 1);
            }

            Path cycle = new Path();
            List<Path> cyclesFromThisNode = new ArrayList<>();

            dfsKSubgraph(begin, begin, graph.getArcs(), nodeColors, new Arc(new Node(), new Node()), cycle, cyclesFromThisNode);

            if (!cyclesFromThisNode.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /*
        Utility
     */

    private Graph makeGraphUndirected(Graph graph) {
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

    private boolean isK5(Path subgraph) {
        boolean isK5 = true;

        for (int i = 0; i < subgraph.getPath().size() - 1; i++) {
            for (int j = i + 1; j < subgraph.getPath().size(); j++) {
                isK5 &= adjacencyMatrix.adjacentNodesOf(
                        subgraph.getPath().get(i)).contains(subgraph.getPath().get(j));
            }
        }

        return isK5;
    }

    private boolean isK33(Path subgraph) {
        boolean isK33 = true;

        for (int i = 0; i < subgraph.getPath().size() - 1; i++) {
            for (int j = i + 1; j < subgraph.getPath().size(); j += 2) {
                isK33 &= adjacencyMatrix.adjacentNodesOf(
                        subgraph.getPath().get(i)).contains(subgraph.getPath().get(j));
            }
        }

        return isK33;
    }

    private void dfsKSubgraph(Node currentNode, Node cycleEndNode, List<Arc> arcs,
                              Map<Node, Integer> nodeColors, Arc unavailableArc, Path trackingCycle, List<Path> cycles) {

        if (!currentNode.equals(cycleEndNode)) {
            nodeColors.replace(currentNode, 2);
        }

        if (trackingCycle.getPath().size() == 5) {
            if (isK5(trackingCycle)) {
                cycles.add(trackingCycle);
            }

            return;
        }

        if (trackingCycle.getPath().size() == 6) {
            if (isK33(trackingCycle)) {
                cycles.add(trackingCycle);
            }

            return;
        }

        for (Arc arc : arcs)
        {
            if (arc.equals(unavailableArc)) {
                continue;
            }

            if ((nodeColors.get(arc.getEnd()) == 1) && arc.getBegin().equals(currentNode)) {
                List<Node> trackingCycleCopy = new ArrayList<>(trackingCycle.getPath());
                trackingCycleCopy.add(arc.getEnd()); // +1 ???
                dfsKSubgraph(arc.getEnd(), cycleEndNode, arcs, nodeColors, arc, new Path(trackingCycleCopy), cycles);
                nodeColors.replace(arc.getEnd(), 1);
            }

            else if (nodeColors.get(arc.getBegin()) == 1 && arc.getEnd().equals(currentNode)) {
                List<Node> trackingCycleCopy = new ArrayList<>(trackingCycle.getPath());
                trackingCycleCopy.add(arc.getBegin()); // +1 ???
                dfsKSubgraph(arc.getBegin(), cycleEndNode, arcs, nodeColors, arc, new Path(trackingCycleCopy), cycles);
                nodeColors.replace(arc.getBegin(), 1);
            }
        }
    }
}
