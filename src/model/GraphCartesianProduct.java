package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;


public class GraphCartesianProduct {
    public static Graph cartesianProduct(Graph g, Graph h) {
        ObservableList<Pair<Node, Node>> nodePairs = FXCollections.observableArrayList();
        Map<Pair<Node, Node>, Node> nodePairsMatching = new HashMap<>();

        ObservableList<Node> gNodes = g.getNodes();
        ObservableList<Node> hNodes = h.getNodes();

        Graph product = new Graph();

        for (Node u : gNodes) {
            for (Node v : hNodes) {
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
}
