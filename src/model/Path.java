package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;


public class Path {
    private ObservableList<Node> path;


    public Path() {
        path = FXCollections.observableArrayList();
    }

    public Path(Path path) {
        this();
        this.path.addAll(path.path);
    }

    public ObservableList<Node> getPath() {
        return path;
    }

    public void setPath(ObservableList<Node> path) {
        this.path = path;
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

        for (int nodeIter = 0; nodeIter < this.path.size(); nodeIter++) {
            if (!pathToCheck.getPath().get(nodeIter).equals(this.path.get(nodeIter))) {
                return false;
            }
        }

        return path.size() == pathToCheck.getPath().size();
    }
}
