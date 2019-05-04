package model;

import javafx.collections.ListChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IncidenceList {
    private Graph graph;

    private Map<Node, List<Arc>> incidenceLists;


    public IncidenceList(Graph graph) {
        this.graph = graph;

        incidenceLists = new HashMap<>();
        configureIncidenceList();
    }

    public List<Arc> setIncidentArcsFor(Node node) {
        List<Arc> incidents = new ArrayList<>();

        for (Arc arc : graph.getArcs()) {
            if (arc.getBegin().equals(node)) {
                incidents.add(arc);
            }
        }

        //incidenceLists.put(node, incidents);

        return incidents;
    }

    /*
     *      Configs
     */

    private void configureIncidenceList() {
        for (Node node : graph.getNodes()) {
            incidenceLists.put(node, setIncidentArcsFor(node));
        }

        graph.getArcs().addListener((ListChangeListener) changeList -> {
            incidenceLists.clear();
            for (Node node : graph.getNodes()) {
                incidenceLists.put(node, setIncidentArcsFor(node));
            }
        });

        graph.getNodes().addListener((ListChangeListener) changeList -> {
            incidenceLists.clear();
            for (Node node : graph.getNodes()) {
                incidenceLists.put(node, setIncidentArcsFor(node));
            }
        });
    }


    public Map<Node, List<Arc>> getIncidenceLists() {
        return incidenceLists;
    }

    public String listsToString() {
        String toString = "";

        for (Node node : incidenceLists.keySet()) {
            toString = toString.concat(node + " " + incidenceLists.get(node).toString() + "\n");
        }

        return toString;
    }
}
