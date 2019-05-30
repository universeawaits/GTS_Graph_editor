package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import model.Arc;
import model.Graph;
import model.Node;

import java.util.HashMap;
import java.util.Map;


public class GraphProducer { // holy fuck what the name
    private Graph gGraph;
    private Graph hGraph;

    ObservableList<Pair<Node, Node>> nodePairs;
    Map<Pair<Node, Node>, Node> nodePairsMatching;


    public GraphProducer(Graph gGraph, Graph hGraph) {
        this.gGraph = gGraph;
        this.hGraph = hGraph;

        nodePairs = FXCollections.observableArrayList();
        nodePairsMatching = new HashMap<>();
    }

    public Graph cartesianProduct() {
        Graph product = new Graph();
        initNodesMatching(product);

        for (Pair<Node, Node> uv : nodePairs) {
            for (Pair<Node, Node> u1v1 : nodePairs) {
                if ((uv.getKey().equals(u1v1.getKey()) && (hGraph.getArc(uv.getValue(), u1v1.getValue()) != null))
                        || (uv.getValue().equals(u1v1.getValue()) && (gGraph.getArc(uv.getKey(), u1v1.getKey()) != null))) {
                    product.getArcs().add(new Arc(nodePairsMatching.get(uv), nodePairsMatching.get(u1v1)));
                }
            }
        }

        return product;
    }

    public Graph tensorProduct() {
        Graph product = new Graph();
        initNodesMatching(product);


        for (Pair<Node, Node> uv : nodePairs) {
            for (Pair<Node, Node> u1v1 : nodePairs) {
                if ((gGraph.getArc(uv.getKey(), u1v1.getKey()) != null)
                        && (hGraph.getArc(uv.getValue(), u1v1.getValue()) != null)) {

                    if (product.getArc(nodePairsMatching.get(u1v1), nodePairsMatching.get(uv)) == null) {
                        product.getArcs().add(new Arc(nodePairsMatching.get(uv), nodePairsMatching.get(u1v1)));
                    }
                }
            }
        }

        return product;
    }

    /*
     *      Utility
     */

    private void initNodesMatching(Graph product) {
        for (Node u : gGraph.getNodes()) {
            for (Node v : hGraph.getNodes()) {
                Pair<Node, Node> uv = new Pair<>(u, v);
                Node uvMatching = new Node("<" +
                        (u.getName().equals("") ? "[" + u.getIdentifier() + "]" : u.getName())
                        + ", " +
                        (v.getName().equals("") ? "[" + v.getIdentifier() + "]" : v.getName()) + ">");
                nodePairs.add(uv);
                nodePairsMatching.put(uv, uvMatching);
                product.getNodes().add(uvMatching);
            }
        }
    }
}
