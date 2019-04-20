package model;


import java.util.ArrayList;
import java.util.List;


public class Path {
    private List<Node> path;


    public Path() {
        path = new ArrayList<>();
    }

    public Path(List<Node> path) {
        this();
        this.path.addAll(path);
    }

    public Path(Path path) {
        this();
        this.path.addAll(path.path);
    }

    public List<Node> getPath() {
        return path;
    }

    @Override
    public String toString() {
        String pathToString = "";

        for (int nodeIter = 0; nodeIter < path.size() - 1; nodeIter++) {
            pathToString = pathToString.concat(path.get(nodeIter) + " -> ");
        }
        pathToString = pathToString.concat(path.get(path.size() - 1).toString());

        return pathToString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path pathToCheck = (Path) o;

        for (int nodeIter = 0; nodeIter < this.path.size() && nodeIter < pathToCheck.getPath().size(); nodeIter++) {
            if (!pathToCheck.getPath().get(nodeIter).equals(this.path.get(nodeIter))) {
                return false;
            }
        }

        return path.size() == pathToCheck.getPath().size();
    }
}
