package model;

import java.util.ArrayList;
import java.util.List;


public class Faces {
    private List<List<Node>> interior;
    private List<Node> external;
    private int size;

    public Faces(List<List<Node>> interior, List<Node> external) {
        if(interior != null && external != null) {
            this.interior.addAll(interior);
            this.external.addAll(external);
            size = interior.size() + external.size();
        } else {
            size = 0;
        }
    }

    public List<List<Node>> getInterior() {
        return new ArrayList<>(interior);
    }

    public List<Node> getExternal() {
        return new ArrayList<>(external);
    }

    @Override
    public String toString() {
        String result = "Faces size = " + size + "\nExternal face:\n" + external + "\nInterior faces:\n";
        for(List<Node> f : interior) {
            result += f + "\n";
        }
        return result;
    }
}
