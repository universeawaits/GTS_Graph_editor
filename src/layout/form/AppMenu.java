package layout.form;

import controller.FileProcessor;
import controller.GraphController;
import controller.GraphProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import layout.DrawableArc;
import layout.DrawableNode;
import model.*;

import java.io.File;
import java.util.Random;

import static layout.DrawableNode.CIRCLE_RADIUS;
import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;


public class AppMenu {
    private static final String FILE_FORMAT = "*.graph";

    private GraphTabPane graphTabPane;

    private MenuBar menuBar;
    private Stage ownerStage;


    public AppMenu(GraphTabPane graphTabPane, Stage stage) {
        this.graphTabPane = graphTabPane;

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(
                createFileMenu(),
                createEditMenu(),
                createStatisticsMenu(),
                createOperationMenu(),
                createAlgorithmMenu()
        );

        ownerStage = stage;
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

        newFile.setOnAction(newGraphEventHandler);
        openFile.setOnAction(openGraphEventHandler);
        saveFile.setOnAction(saveGraphEventHandler);

        file.getItems().addAll(newFile, openFile, saveFile, closeFile);

        return file;
    }

    // Creating of edit menu
    private Menu createEditMenu() {
        Menu edit = new Menu("Edit");
        MenuItem clearPane = new MenuItem("Clear pane");

        clearPane.setOnAction(graphClearingEventHandler);

        edit.getItems().addAll(clearPane);

        return edit;
    }

    // Creating of statistics menu
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

    // Creating of algorithms menu
    private Menu createAlgorithmMenu() {
        Menu algorithm = new Menu("Algorithms");
        MenuItem hamiltonianCycles = new MenuItem("Hamiltonian cycles");

        hamiltonianCycles.setOnAction(findHamiltonianCyclesEventHandler);

        algorithm.getItems().addAll(hamiltonianCycles);

        return algorithm;
    }

    // Creating of operations menu
    private Menu createOperationMenu() {
        Menu operation = new Menu("Operations");
        MenuItem cartesianProduct = new MenuItem("Cartesian product");
        MenuItem tensorProduct = new MenuItem("Tensor product");

        cartesianProduct.setOnAction(cartesianProductEventHandler);
        tensorProduct.setOnAction(tensorProductEventHandler);

        operation.getItems().addAll(cartesianProduct, tensorProduct);

        return operation;
    }

    /*
     *      Others
     */

    private Alert createEmptyDialog(javafx.scene.Node content, String title) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);

        alert.getDialogPane().setContent(content);

        return alert;
    }

    private File createSaveFileDialog() {
        FileChooser saveFileChooser = new FileChooser();
        saveFileChooser.setTitle("Save graph");
        saveFileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Graph file", FILE_FORMAT)
        );

        try {
            saveFileChooser.setInitialFileName(
                    graphTabPane.getTabPane().getSelectionModel().getSelectedItem().getText()
            );
        } catch (NullPointerException ex){
            return null;
        }

        return saveFileChooser.showSaveDialog(ownerStage);
    }

    private File createOpenFileDialog() {
        FileChooser openFileChooser = new FileChooser();
        openFileChooser.setTitle("Open graph");
        openFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Graph file", FILE_FORMAT)
        );

        return openFileChooser.showOpenDialog(ownerStage);
    }

    /*
        Event handlers
     */

    // Creating of a new graph
    private EventHandler<ActionEvent> newGraphEventHandler = e -> {
        TextField name = new TextField();

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("Graph name"), 0, 0);
        gridPane.add(name, 1, 0);
        GridPane.setMargin(name, new Insets(CIRCLE_RADIUS));

        Alert newGraphDialog = createEmptyDialog(gridPane, "New graph");

        ButtonType CREATE = new ButtonType("Create");
        newGraphDialog.getButtonTypes().add(CREATE);

        ((Button) newGraphDialog.getDialogPane().lookupButton(CREATE)).setOnAction(actionEvent -> {
            String graphName = name.getText();

            for (Tab tab : graphTabPane.getManagingGraphs().keySet()) {
                if (tab.getText().equals(graphName)) {
                    Alert error = createEmptyDialog(new Label("Such graph is already exists"), "Error");

                    ButtonType OK = new ButtonType("OK");
                    error.getButtonTypes().add(OK);

                    ((Button) error.getDialogPane().lookupButton(OK)).setOnAction(aaa -> {
                        newGraphDialog.show();
                    });

                    error.show();
                    return;
                }
            }

            graphTabPane.newTab(name.getText());
        });

        newGraphDialog.show();
    };

    // Saving of a graph
    private EventHandler<ActionEvent> saveGraphEventHandler = e -> {
        File selectedFile = createSaveFileDialog();

        if (selectedFile != null) {
            new FileProcessor(selectedFile.getAbsolutePath()).write(
                    graphTabPane.currentGraphPane(),
                    graphTabPane.getTabPane().getSelectionModel().getSelectedItem().getText()
            );
        }
    };

    // Opening of a graph
    private EventHandler<ActionEvent> openGraphEventHandler = e -> {
        File selectedFile = createOpenFileDialog();

        if (selectedFile != null) {
            Pair<String, GraphPane> namedGraphPane = new FileProcessor(selectedFile.getAbsolutePath()).read();
            graphTabPane.newTab(namedGraphPane.getKey(), namedGraphPane.getValue());
        }
    };

    // Taking all graph nodes degrees
    private EventHandler<ActionEvent> getNodeDegreeEventHandler = e -> {
        ObservableList<String> nodesDegrees = FXCollections.observableArrayList();

        try {
            for (Node node : graphTabPane.currentGraphPane().getGraphController().getNodes()) {
               nodesDegrees.add(node + ": " + graphTabPane.currentGraphPane().getGraphController().degreeOf(node));
            }
        } catch (NullPointerException ex) {
            return;
        }

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(nodesDegrees);
        listView.setPrefSize(MAIN_FORM_WIDTH / 8,MAIN_FORM_HEIGHT / 7);
        listView.setEditable(false);

        Alert nodeDegreeDialog = createEmptyDialog(listView, "Nodes' degrees");
        nodeDegreeDialog.getButtonTypes().add(ButtonType.OK);
        nodeDegreeDialog.show();
    };

    // Taking all graph's centers
    private EventHandler<ActionEvent> getCentersEventHandler = e -> {
        ObservableList<String> graphCenters = FXCollections.observableArrayList();

        try {
            for (Node node : graphTabPane.currentGraphPane().getGraphController().centers()) {
                graphCenters.add(node.toString());
            }
        } catch (NullPointerException ex) {
            return;
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
        try {
            graphTabPane.currentGraphPane().getPane().getChildren().clear();
            graphTabPane.currentGraphPane().getDrawableArcs().clear();
            graphTabPane.currentGraphPane().getDrawableNodes().clear();
            graphTabPane.currentGraphPane().getGraphController().getArcs().clear();
            graphTabPane.currentGraphPane().getGraphController().getNodes().clear();
        } finally {
            return;
        }
    };

    // Cartesian product of two specified graphs
    private EventHandler<ActionEvent> cartesianProductEventHandler = e -> {
        ComboBox<String> gGraphName = new ComboBox<>();
        ComboBox<String> hGraphName = new ComboBox<>();

        for (Tab tab : graphTabPane.getTabPane().getTabs()) {
            gGraphName.getItems().add(tab.getText());
            hGraphName.getItems().add(tab.getText());
        }

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("First graph:"), 0, 0);
        gridPane.add(new Label("Second graph:"), 1, 0);
        gridPane.add(gGraphName, 0, 1);
        gridPane.add(hGraphName, 1, 1);
        GridPane.setMargin(gGraphName, new Insets(CIRCLE_RADIUS));
        GridPane.setMargin(hGraphName, new Insets(CIRCLE_RADIUS));

        Alert cartesianProductDialog = createEmptyDialog(gridPane, "Cartesian product");

        ButtonType CREATE = new ButtonType("Create");
        cartesianProductDialog.getButtonTypes().add(CREATE);

        ((Button) cartesianProductDialog.getDialogPane().lookupButton(CREATE)).setOnAction(actionEvent -> {
            GraphPane gGraphPane = graphTabPane.getGraphPaneAtTab(gGraphName.getSelectionModel().getSelectedItem());
            GraphPane hGraphPane = graphTabPane.getGraphPaneAtTab(hGraphName.getSelectionModel().getSelectedItem());

            String graphName = gGraphName.getSelectionModel().getSelectedItem()
                    + " □ " + hGraphName.getSelectionModel().getSelectedItem();

            Graph product = GraphProduct.cartesianProduct(
                    gGraphPane.getGraphController().getGraph(),
                    hGraphPane.getGraphController().getGraph()
            );

            GraphController productController = new GraphController(product);
            GraphPane productGraphPane = new GraphPane(productController);

            Random nodePositionRandom = new Random(System.currentTimeMillis());

            for (Node node : product.getNodes()) {
                DrawableNode drawableNode = new DrawableNode(node);
                drawableNode.getShape().setCenterX(
                        nodePositionRandom.nextInt((int) MAIN_FORM_WIDTH - 100) + 50
                );
                drawableNode.getShape().setCenterY(
                        nodePositionRandom.nextInt((int) MAIN_FORM_HEIGHT - 300) + 50
                );

                productGraphPane.getPane().getChildren().add(drawableNode.getShape());
                productGraphPane.getDrawableNodes().add(drawableNode);
                drawableNode.getShape().toFront();
            }

            for (DrawableNode begin : productGraphPane.getDrawableNodes()) {
                for (DrawableNode end : productGraphPane.getDrawableNodes()) {
                    Arc arc = product.getArc(begin.getSourceNode(), end.getSourceNode());
                    if (arc != null) {
                        DrawableArc drawableArc = new DrawableArc(arc, begin, end);
                        productGraphPane.getPane().getChildren().addAll(drawableArc.getLine(), drawableArc.getArrow());
                        productGraphPane.getDrawableArcs().add(drawableArc);
                    }
                }
            }

            for (DrawableNode drawableNode : productGraphPane.getDrawableNodes()) {
                drawableNode.getShape().toFront();
            }

            graphTabPane.newTab(graphName, productGraphPane);
        });

        cartesianProductDialog.show();
    };

    // Vector product of two specified graphs
    private EventHandler<ActionEvent> tensorProductEventHandler = e -> {
        ComboBox<String> gGraphName = new ComboBox<>();
        ComboBox<String> hGraphName = new ComboBox<>();

        for (Tab tab : graphTabPane.getTabPane().getTabs()) {
            gGraphName.getItems().add(tab.getText());
            hGraphName.getItems().add(tab.getText());
        }

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("First graph:"), 0, 0);
        gridPane.add(new Label("Second graph:"), 1, 0);
        gridPane.add(gGraphName, 0, 1);
        gridPane.add(hGraphName, 1, 1);
        GridPane.setMargin(gGraphName, new Insets(CIRCLE_RADIUS));
        GridPane.setMargin(hGraphName, new Insets(CIRCLE_RADIUS));

        Alert tensorProductDialog = createEmptyDialog(gridPane, "Cartesian product");

        ButtonType CREATE = new ButtonType("Create");
        tensorProductDialog.getButtonTypes().add(CREATE);

        ((Button) tensorProductDialog.getDialogPane().lookupButton(CREATE)).setOnAction(actionEvent -> {
            GraphPane gGraphPane = graphTabPane.getGraphPaneAtTab(gGraphName.getSelectionModel().getSelectedItem());
            GraphPane hGraphPane = graphTabPane.getGraphPaneAtTab(hGraphName.getSelectionModel().getSelectedItem());

            String graphName = gGraphName.getSelectionModel().getSelectedItem()
                    + " × " + hGraphName.getSelectionModel().getSelectedItem();

            Graph product = GraphProduct.tensorProduct(
                    gGraphPane.getGraphController().getGraph(),
                    hGraphPane.getGraphController().getGraph()
            );

            GraphController productController = new GraphController(product);
            GraphPane productGraphPane = new GraphPane(productController);

            Random nodePositionRandom = new Random(System.currentTimeMillis());

            for (Node node : product.getNodes()) {
                DrawableNode drawableNode = new DrawableNode(node);
                drawableNode.getShape().setCenterX(
                        nodePositionRandom.nextInt((int) MAIN_FORM_WIDTH - 100) + 50
                );
                drawableNode.getShape().setCenterY(
                        nodePositionRandom.nextInt((int) MAIN_FORM_HEIGHT - 300) + 50
                );

                productGraphPane.getPane().getChildren().add(drawableNode.getShape());
                productGraphPane.getDrawableNodes().add(drawableNode);
                drawableNode.getShape().toFront();
            }

            for (DrawableNode begin : productGraphPane.getDrawableNodes()) {
                for (DrawableNode end : productGraphPane.getDrawableNodes()) {
                    Arc arc = product.getArc(begin.getSourceNode(), end.getSourceNode());
                    if (arc != null) {
                        DrawableArc drawableArc = new DrawableArc(arc, begin, end);
                        productGraphPane.getPane().getChildren().addAll(drawableArc.getLine());
                        productGraphPane.getDrawableArcs().add(drawableArc);
                    }
                }
            }

            for (DrawableNode drawableNode : productGraphPane.getDrawableNodes()) {
                drawableNode.getShape().toFront();
            }

            graphTabPane.newTab(graphName, productGraphPane);
        });

        tensorProductDialog.show();
    };

    // Finding of hamiltonian cycles
    private EventHandler<ActionEvent> findHamiltonianCyclesEventHandler = e -> {
        ObservableList<String> cycles = FXCollections.observableArrayList();

        try {
            for (Path cycle : graphTabPane.currentGraphPane().getGraphController().hamiltonianCycles()) {
                cycles.add(cycle.toString());
            }
        } catch (NullPointerException ex){
            return;
        }

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(cycles);
        listView.setPrefSize(MAIN_FORM_WIDTH / 3,MAIN_FORM_HEIGHT / 5);
        listView.setEditable(false);

        Alert hamiltonianCyclesDialog = createEmptyDialog(listView, "Hamiltonian cycles");
        hamiltonianCyclesDialog.getButtonTypes().add(ButtonType.OK);
        hamiltonianCyclesDialog.show();
    };

    // Taking graph's adjacency matrix
    private EventHandler<ActionEvent> getAdjacencyMatrixEventHandler = e -> {
        Label matrix;

        try {
            matrix = new Label(graphTabPane.currentGraphPane().getGraphController().adjacencyMatrix().toString());
        } catch (NullPointerException ex) {
            return;
        }

        if (matrix.getText().equals("")) {
            matrix.setText("The graph is empty");
        }

        Alert matrixDialog = createEmptyDialog(matrix, "Adjacency matrix");
        matrixDialog.getButtonTypes().add(ButtonType.CLOSE);
        matrixDialog.show();
    };
}
