package layout.form;

import controller.GraphController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import model.Arc;
import model.Node;

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;


public class AppMenu {
    private GraphController graphController;
    private GraphPane graphPane;

    private MenuBar menuBar;


    public AppMenu(GraphPane graphPane) {
        this.graphPane = graphPane;
        this.graphController = graphPane.getGraphController();

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(
                createFileMenu(),
                createEditMenu(),
                createStatisticsMenu(),
                createAlgorithmMenu()
        );
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }


    /*
        Menus creating
     */

    // Creating of the file menu
    private Menu createFileMenu() {
        Menu file = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem closeFile = new MenuItem("Close");

        file.getItems().addAll(newFile, openFile, saveFile, closeFile);

        return file;
    }

    // Creating of the edit menu
    private Menu createEditMenu() {
        Menu edit = new Menu("Edit");
        MenuItem clearPane = new MenuItem("Clear pane");

        clearPane.setOnAction(graphClearingEventHandler);

        edit.getItems().addAll(clearPane);

        return edit;
    }

    // Creating of the statistics menu
    private Menu createStatisticsMenu() {
        Menu statistics = new Menu("Statistics");
        MenuItem nodesDegrees = new MenuItem("Node degrees");
        MenuItem centers = new MenuItem("Centers");
        MenuItem adjacencyMatrix = new MenuItem("Adjacency matrix");

        nodesDegrees.setOnAction(getNodeDegreeEventHandler);
        centers.setOnAction(getCentersEventHandler);
        adjacencyMatrix.setOnAction(getAdjacencyMatrixEventHandler);

        statistics.getItems().addAll(nodesDegrees, centers, adjacencyMatrix);

        return statistics;
    }

    // Creating of an algorithms menu
    private Menu createAlgorithmMenu() {
        Menu algorithm = new Menu("Algorithm");
        MenuItem hamiltonianCycles = new MenuItem("Hamiltonian cycles");

        hamiltonianCycles.setOnAction(findHamiltonianCyclesEventHandler);

        algorithm.getItems().addAll(hamiltonianCycles);

        return algorithm;
    }

    /*
        Others
     */

    private Alert createEmptyDialog(javafx.scene.Node content, String title) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);

        alert.getDialogPane().setContent(content);

        return alert;
    }

    /*
        Event handlers
     */

    // Taking all graph nodes degrees
    private EventHandler<ActionEvent> getNodeDegreeEventHandler = e -> {
           ObservableList<String> nodesDegrees = FXCollections.observableArrayList();

            for (Node node : graphController.getNodes()) {
                nodesDegrees.add(node + ": " + graphController.degreeOf(node));
            }

            ListView<String> listView = new ListView<>();
            listView.getItems().addAll(nodesDegrees);
            listView.setPrefSize(MAIN_FORM_WIDTH / 8,MAIN_FORM_HEIGHT / 7);
            listView.setEditable(false);

            Alert nodeDegreeDialog = createEmptyDialog(listView, "Nodes' degrees");
            nodeDegreeDialog.getButtonTypes().add(ButtonType.OK);
            nodeDegreeDialog.show();
    };

    // Taking all graph centers
    private EventHandler<ActionEvent> getCentersEventHandler = e -> {
        ObservableList<String> graphCenters = FXCollections.observableArrayList();

        for (Node node : graphController.centers()) {
            graphCenters.add(node.toString());
        }

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(graphCenters);
        listView.setPrefSize(MAIN_FORM_WIDTH / 10,MAIN_FORM_HEIGHT / 8);
        listView.setEditable(false);

        Alert centersDialog = createEmptyDialog(listView, "Centers");
        centersDialog.getButtonTypes().add(ButtonType.OK);
        centersDialog.show();
    };

    // Clearing the graph pane with the source graph
    private EventHandler<ActionEvent> graphClearingEventHandler = e -> {
        graphPane.getPane().getChildren().clear();
        graphPane.getDrawableArcs().clear();
        graphPane.getDrawableNodes().clear();
        graphController.getArcs().clear();
        graphController.getNodes().clear();
    };

    // Finding of hamiltonian cycles
    private EventHandler<ActionEvent> findHamiltonianCyclesEventHandler = e -> {
        ObservableList<String> cycles = FXCollections.observableArrayList();

        for (ObservableList<Arc> cycle : graphController.hamiltonianCycles()) {
            String thatCycle = "";

            for (Arc arc : cycle) {
                thatCycle = thatCycle.concat(arc.toString() + "  ");
            }

            cycles.add(thatCycle);
        }

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(cycles);
        listView.setPrefSize(MAIN_FORM_WIDTH / 5,MAIN_FORM_HEIGHT / 8);
        listView.setEditable(false);

        Alert centersDialog = createEmptyDialog(listView, "Hamiltonian cycles");
        centersDialog.getButtonTypes().add(ButtonType.OK);
        centersDialog.show();
    };

    // Taking graph's adjacency matrix
    private EventHandler<ActionEvent> getAdjacencyMatrixEventHandler = e -> {
        Label matrix = new Label(graphController.adjacencyMatrix().toString());

        if (matrix.getText().equals("")) {
            matrix.setText("The graph is empty");
        }

        Alert matrixDialog = createEmptyDialog(matrix, "Adjacency matrix");
        matrixDialog.getButtonTypes().add(ButtonType.CLOSE);
        matrixDialog.show();
    };
}
