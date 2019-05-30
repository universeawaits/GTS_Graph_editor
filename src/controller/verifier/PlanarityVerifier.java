package controller.verifier;

import model.AdjacencyMatrix;
import model.Graph;
import model.Node;

import java.util.*;


public class PlanarityVerifier implements Verifier {
    private static final int COUNT_OF_NODES_K5 = 5;
    private static final int COUNT_OF_ARCS_IN_UNDIRECTED_K5 = 20;
    private static final int COUNT_OF_NODES_K33 = 6;
    private static final int COUNT_OF_ARCS_IN_UNDIRECTED_K33 = 18;

    private Graph graph;
    private AdjacencyMatrix adjacencyMatrix;


    public PlanarityVerifier(Graph graph) {
        this.graph = graph.undirectedEquivalent();
        this.adjacencyMatrix = new AdjacencyMatrix(this.graph);
    }

    public boolean verify() {
        if (graph.getNodes().size() >= COUNT_OF_NODES_K5
                && graph.getArcs().size() == graph.getNodes().size() * (graph.getNodes().size() - 1)) {
            return false;
        }

        List<Node> someKuratowskiGraph = permute();

        if (someKuratowskiGraph.size() == COUNT_OF_NODES_K5
                && graph.getArcs().size() >= COUNT_OF_ARCS_IN_UNDIRECTED_K5) {
            return false;
        } else if (someKuratowskiGraph.size() == COUNT_OF_NODES_K33
                && graph.getArcs().size() >= COUNT_OF_ARCS_IN_UNDIRECTED_K33) {
            return false;
        }

        return true;
    }

    /*
     *      Util
     */

    private List<Node> permute() {
        List<Node> permutation = new ArrayList<>();

        for (Node one : graph.getNodes()) {
            permutation.clear();
            permutation.add(one);

            for (Node two : graph.getNodes()) {
                if (permutation.contains(two)) {
                    continue;
                }
                permutation.add(two);

                for (Node three : graph.getNodes()) {
                    if (permutation.contains(three)) {
                        continue;
                    }
                    permutation.add(three);

                    for (Node four : graph.getNodes()) {
                        if (permutation.contains(four)) {
                            continue;
                        }
                        permutation.add(four);

                        for (Node five : graph.getNodes()) {
                            if (permutation.contains(five)) {
                                continue;
                            }
                            permutation.add(five);

                            for (Node six : graph.getNodes()) {
                                if (permutation.contains(six)) {
                                    continue;
                                }
                                permutation.add(six);

                                for (int i = 0; i < permutation.size(); i++) {
                                    for (int j = 0; j < permutation.size(); j++) {
                                        Collections.swap(permutation, i, j);

                                        if (isK33(permutation)) {
                                            return permutation;
                                        }
                                    }
                                }

                                permutation.remove(six);
                            }

                            if (isK5(permutation)) {
                                return permutation;
                            }

                            permutation.remove(five);
                        }
                        permutation.remove(four);
                    }
                    permutation.remove(three);
                }
                permutation.remove(two);
            }
        }

        return permutation;
    }

    private boolean isK5(List<Node> permutation) {
        boolean isK5 = true;

        for (Node begin : permutation) {
            for (Node end : permutation) {
                if (begin.equals(end)) {
                    continue;
                }

                isK5 &= isPathExist(begin, end);
            }
        }

        return isK5;
    }

    private boolean isK33(List<Node> permutation) {
        boolean isK33 = true;

        List<Node> homes = permutation.subList(0, COUNT_OF_NODES_K33 / 2);
        List<Node> wells = permutation.subList(COUNT_OF_NODES_K33 / 2, COUNT_OF_NODES_K33);

        for (Node home : homes) {
            for (Node well : wells) {
                isK33 &= isPathExist(home, well);
            }
        }

        return isK33;
    }

    private boolean isPathExist(Node source, Node destination) {
        Map<Node, Boolean> visitedNodes = new HashMap<>();

        for (Node node : graph.getNodes()) {
            visitedNodes.put(node, false);
        }

        LinkedList<Node> trackingPath = new LinkedList<>();

        visitedNodes.replace(source, true);
        trackingPath.add(source);

        while (trackingPath.size() != 0) {
            source = trackingPath.poll();

            for (Node adjacent : adjacencyMatrix.adjacentNodesOf(source)) {
                if (adjacent.equals(destination)) {
                    return true;
                }

                if (!visitedNodes.get(adjacent)) {
                    visitedNodes.replace(adjacent, true);
                    trackingPath.add(adjacent);
                }
            }
        }

        return false;
    }
}
