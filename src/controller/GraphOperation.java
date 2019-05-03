package controller;

import model.Arc;
import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.List;


public class GraphOperation {
    public static Graph union(Graph g, Graph h) {
        Graph union = new Graph();

        // fix to unduplicate!!!

        union.getNodes().addAll(g.getNodes());
        union.getNodes().addAll(h.getNodes());

        union.getArcs().addAll(g.getArcs());
        union.getArcs().addAll(h.getArcs());

        return union;
    }

    public static Graph intersection(Graph g, Graph h) {
        Graph intersection = new Graph();

        intersection.getArcs().addAll(g.getArcs());
        intersection.getArcs().addAll(h.getArcs());

        for (Arc arc : g.getArcs()) {
            Node begin;
            Node end;

            if (intersection.getNode(arc.getBegin().getName()) == null) {
                begin = new Node(arc.getBegin().getName());
            } else {
                begin = intersection.getNode(arc.getBegin().getName());
            }

            if (intersection.getNode(arc.getEnd().getName()) == null) {
                end = new Node(arc.getEnd().getName());
            } else {
                end = intersection.getNode(arc.getEnd().getName());
            }

            intersection.getNodes().add(begin);
            intersection.getNodes().add(end);

            Arc newArc = new Arc(begin, end);

            intersection.getArcs().add(newArc);
        }

        for (Arc firstForCheck : g.getArcs()) {
            for (Arc secondForCheck : h.getArcs()) {
                if (firstForCheck.getBegin().getName().equals(secondForCheck.getBegin().getName())
                        && firstForCheck.getEnd().getName().equals(secondForCheck.getEnd().getName())) {

                    Node begin = new Node(secondForCheck.getBegin().getName());
                    Node end = new Node(secondForCheck.getEnd().getName());

                    intersection.getNodes().add(begin);
                    intersection.getNodes().add(end);
                    intersection.getArcs().add(new Arc(begin, end));
                }
            }
        }

        return intersection;
    }
}
