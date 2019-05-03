package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import model.Arc;
import model.Graph;
import model.Node;

import java.util.HashMap;
import java.util.Map;


public class GraphProduct {
    public static Graph cartesianProduct(Graph g, Graph h) {
        ObservableList<Pair<Node, Node>> nodePairs = FXCollections.observableArrayList();
        Map<Pair<Node, Node>, Node> nodePairsMatching = new HashMap<>();

        ObservableList<Node> gNodes = g.getNodes();
        ObservableList<Node> hNodes = h.getNodes();

        Graph product = new Graph();

        for (Node u : gNodes) {
            for (Node v : hNodes) {
                if (u.getName().equals(v.getName())) {
                    continue;
                }

                Pair<Node, Node> uv = new Pair<>(u, v);
                Node uvMatching = new Node('<' + u.getName() + ", " + v.getName() + '>');
                nodePairs.add(uv);
                nodePairsMatching.put(uv, uvMatching);
                product.getNodes().add(uvMatching);
            }
        }

        for (Pair<Node, Node> uv : nodePairs) {
            for (Pair<Node, Node> u1v1 : nodePairs) {
                if ((uv.getKey().equals(u1v1.getKey()) && (h.getArc(uv.getValue(), u1v1.getValue()) != null))
                        || (uv.getValue().equals(u1v1.getValue()) && (g.getArc(uv.getKey(), u1v1.getKey()) != null))) {
                    product.getArcs().add(new Arc(nodePairsMatching.get(uv), nodePairsMatching.get(u1v1)));
                }
            }
        }

        return product;
    }

    public static Graph tensorProduct(Graph g, Graph h) {
        ObservableList<Pair<Node, Node>> nodePairs = FXCollections.observableArrayList();
        Map<Pair<Node, Node>, Node> nodePairsMatching = new HashMap<>();

        ObservableList<Node> gNodes = g.getNodes();
        ObservableList<Node> hNodes = h.getNodes();

        Graph product = new Graph();

        for (Node u : gNodes) {
            for (Node v : hNodes) {
                if (u.getName().equals(v.getName())) {
                    continue;
                }

                Pair<Node, Node> uv = new Pair<>(u, v);
                Node uvMatching = new Node('<' + u.getName() + ", " + v.getName() + '>');
                nodePairs.add(uv);
                nodePairsMatching.put(uv, uvMatching);
                product.getNodes().add(uvMatching);
            }
        }

        for (Pair<Node, Node> uv : nodePairs) {
            for (Pair<Node, Node> u1v1 : nodePairs) {
                if ((h.getArc(uv.getValue(), u1v1.getValue()) != null || h.getArc(u1v1.getValue(), uv.getValue()) != null)
                        && (g.getArc(uv.getKey(), u1v1.getKey()) != null || g.getArc(u1v1.getKey(), uv.getKey()) != null)) {

                    if (product.getArc(nodePairsMatching.get(u1v1), nodePairsMatching.get(uv)) == null) {
                        product.getArcs().add(new Arc(nodePairsMatching.get(uv), nodePairsMatching.get(u1v1), false));
                    }
                }
            }
        }

        return product;
    }
}
