package controller;

import model.*;

import java.util.*;


public class PlanarityVerifier {
    private Graph graph;
    private AdjacencyMatrix adjacencyMatrix;


    public PlanarityVerifier(Graph graph) {
        this.graph = undirectedEquivalentOf(graph);
        shrinkage(this.graph);
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

            dfsKSubgraph(begin, begin, graph.getArcs(), nodeColors,
                    new Arc(new Node(), new Node()), cycle, cyclesFromThisNode);

            if (!cyclesFromThisNode.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /*
        Utility
     */

    private Graph undirectedEquivalentOf(Graph graph) {
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

    private void shrinkage(Graph graphToShrinkage) {
        List<Node> fourDegreeNodes = new ArrayList<>();
        List<Arc> arcsToRemove = new ArrayList<>();
        List<Arc> arcsToRestore = new ArrayList<>();

        while (true) {
            fourDegreeNodes.clear();
            arcsToRemove.clear();
            arcsToRestore.clear();

            for (Node node : graphToShrinkage.getNodes()) {
                int nodeDegree = 0;

                for (Arc arc : graphToShrinkage.getArcs()) {
                    if (arc.getBegin().equals(node)) {
                        nodeDegree++;
                    }

                    if (arc.getEnd().equals(node)) {
                        nodeDegree++;
                    }
                }

                if (nodeDegree == 4) {
                    fourDegreeNodes.add(node);
                }
            }

            if (!fourDegreeNodes.isEmpty()) {
                for (Node node : fourDegreeNodes) {
                    for (Arc arc : graphToShrinkage.getArcs()) {
                        if (arc.getBegin().equals(node)) {
                            arcsToRemove.add(arc);
                            Node beginForArcToRestore = null;

                            for (Arc arcToCheck : graphToShrinkage.getArcs()) {
                                if (arcToCheck.getEnd().equals(node) && !arcToCheck.getBegin().equals(arc.getEnd())) {
                                    beginForArcToRestore = arcToCheck.getBegin();
                                }
                            }

                            if (beginForArcToRestore == null) {
                                continue;
                            }

                            arcsToRestore.add(new Arc(beginForArcToRestore, arc.getEnd()));
                        }

                        if (arc.getEnd().equals(node)) {
                            arcsToRemove.add(arc);
                            Node endForArcToRestore = null;

                            for (Arc arcToCheck : graphToShrinkage.getArcs()) {
                                if (arcToCheck.getBegin().equals(node) && !arcToCheck.getEnd().equals(arc.getBegin())) {
                                    endForArcToRestore = arcToCheck.getEnd();
                                }
                            }

                            if (endForArcToRestore == null) {
                                continue;
                            }

                            arcsToRestore.add(new Arc(arc.getBegin(), endForArcToRestore));
                        }
                    }
                }

                graphToShrinkage.getArcs().removeAll(arcsToRemove);
                graphToShrinkage.getNodes().removeAll(fourDegreeNodes);
                graphToShrinkage.getArcs().addAll(arcsToRestore);
            } else {
                break;
            }
        }
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

    private boolean isK33(Path subgraph) { // ???????
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
