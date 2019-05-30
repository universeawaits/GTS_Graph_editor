package layout.form;

import controller.FileProcessor;
import controller.GraphController;
import controller.GraphProducer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import layout.DrawableArc;
import layout.DrawableNode;
import model.*;

import java.io.File;
import java.util.*;

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
                createMetricsMenu(),
                createOperationMenu(),
                createAlgorithmMenu(),
                createModificationMenu()
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

    // Creating of metrics menu
    private Menu createMetricsMenu() {
        Menu metrics = new Menu("Metrics");
        MenuItem nodesDegrees = new MenuItem("Node degrees");
        MenuItem centers = new MenuItem("Centers");
        MenuItem adjacencyMatrix = new MenuItem("Adjacency matrix");

        nodesDegrees.setOnAction(getNodeDegreeEventHandler);
        centers.setOnAction(getCentersEventHandler);
        adjacencyMatrix.setOnAction(getAdjacencyMatrixEventHandler);

        metrics.getItems().addAll(nodesDegrees, centers, adjacencyMatrix);

        return metrics;
    }

    // Creating of algorithms menu
    private Menu createAlgorithmMenu() {
        Menu algorithm = new Menu("Algorithms");
        MenuItem hamiltonianCycles = new MenuItem("Hamiltonian cycles");
        MenuItem distanceBetweenNodes = new MenuItem("Distance between nodes");
        MenuItem pathsBetweenNodes = new MenuItem("Paths between nodes");
        Menu coloring = new Menu("Coloring");
        MenuItem coloringNodes = new MenuItem("Coloring of nodes");

        hamiltonianCycles.setOnAction(findHamiltonianCyclesEventHandler);
        coloringNodes.setOnAction(coloringNodesEventHandler);
        distanceBetweenNodes.setOnAction(distanceBetweenNodesEventHandler);
        pathsBetweenNodes.setOnAction(findAllPathsBetweenTwoNodes);

        coloring.getItems().add(coloringNodes);
        algorithm.getItems().addAll(hamiltonianCycles, distanceBetweenNodes, pathsBetweenNodes, coloring);

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

    // Creating modification menu
    private Menu createModificationMenu() {
        Menu modification = new Menu("Modification");
        MenuItem makeComplete = new MenuItem("Make complete");

        makeComplete.setOnAction(makeCompleteEventHandler);

        modification.getItems().addAll(makeComplete);

        return modification;
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

    private GraphPane createGraphPaneFromSource(GraphController graphController) {
        GraphPane graphPane = new GraphPane(graphController);

        Random nodePositionRandom = new Random(System.currentTimeMillis());

        for (Node node : graphController.getNodes()) {
            DrawableNode drawableNode = new DrawableNode(node);
            drawableNode.getShape().setCenterX(
                    nodePositionRandom.nextInt((int) MAIN_FORM_WIDTH - 100) + 50
            );
            drawableNode.getShape().setCenterY(
                    nodePositionRandom.nextInt((int) MAIN_FORM_HEIGHT - 300) + 50
            );

            graphPane.getPane().getChildren().addAll(
                    drawableNode.getShape(), drawableNode.getName(), drawableNode.getIdentifier()
            );
            graphPane.getDrawableNodes().add(drawableNode);
            drawableNode.getShape().toFront();
        }

        for (DrawableNode begin : graphPane.getDrawableNodes()) {
            for (DrawableNode end : graphPane.getDrawableNodes()) {
                Arc arc = graphController.getGraph().getArc(begin.getSourceNode(), end.getSourceNode());

                if (arc != null) {
                    DrawableArc newInverse = new DrawableArc(
                            new Arc(arc.getEnd(), arc.getBegin(),
                                    false),
                            new DrawableNode(arc.getEnd()),
                            new DrawableNode(arc.getBegin())
                    );

                    if (graphPane.getDrawableArcs().indexOf(newInverse) != -1) {
                        DrawableArc inverseFound = graphPane.getDrawableArcs()
                                .get(graphPane.getDrawableArcs().indexOf(newInverse));

                        graphPane.getPane().getChildren()
                                .remove(inverseFound.getArrow()); // kaef

                        continue;
                    }

                    DrawableArc drawableArc = new DrawableArc(arc, begin, end);
                    graphPane.getPane().getChildren().addAll(drawableArc.getLine(), drawableArc.getArrow());
                    graphPane.getDrawableArcs().add(drawableArc);
                }
            }
        }

        for (DrawableNode drawableNode : graphPane.getDrawableNodes()) {
            drawableNode.getShape().toFront();
        }

        return graphPane;
    }

    private boolean isGraphAlreadyExist(String name) {
        for (Tab tab : graphTabPane.getManagingGraphs().keySet()) {
            if (tab.getText().equals(name)) {
                Alert error = createEmptyDialog(new Label("Such graph is already exists"), "Error");

                ButtonType OK = new ButtonType("OK");
                error.getButtonTypes().add(OK);

                error.showAndWait();

                return true;
            }
        }

        return false;
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
            Graph newGraph = new Graph(name.getText());

            if (!isGraphAlreadyExist(newGraph.getName())) {
                graphTabPane.newTab(newGraph);
            } else {
                newGraphDialog.show();
            }
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
            GraphPane namedGraphPane = new FileProcessor(selectedFile.getAbsolutePath()).read();

            if (!isGraphAlreadyExist(namedGraphPane.getGraphController().getGraph().getName())) {
                graphTabPane.newTab(namedGraphPane);
            } else {
                createOpenFileDialog();
            }
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

        for (Tab tab : graphTabPane.getManagingGraphs().keySet()) {
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

            if (!isGraphAlreadyExist(graphName)) {
                Graph product = new GraphProducer(
                        gGraphPane.getGraphController().getGraph(),
                        hGraphPane.getGraphController().getGraph())
                        .cartesianProduct();
                product.setName(graphName);

                graphTabPane.newTab(createGraphPaneFromSource(new GraphController(product)));
            } else {
                cartesianProductDialog.show();
            }
        });

        cartesianProductDialog.show();
    };

    // Tensor product of two specified graphs
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

        Alert tensorProductDialog = createEmptyDialog(gridPane, "Tensor product");

        ButtonType CREATE = new ButtonType("Create");
        tensorProductDialog.getButtonTypes().add(CREATE);

        ((Button) tensorProductDialog.getDialogPane().lookupButton(CREATE)).setOnAction(actionEvent -> {
            GraphPane gGraphPane = graphTabPane.getGraphPaneAtTab(gGraphName.getSelectionModel().getSelectedItem());
            GraphPane hGraphPane = graphTabPane.getGraphPaneAtTab(hGraphName.getSelectionModel().getSelectedItem());

            String graphName = gGraphName.getSelectionModel().getSelectedItem()
                    + " × " + hGraphName.getSelectionModel().getSelectedItem();

            if (!isGraphAlreadyExist(graphName)) {
                Graph product = new GraphProducer(
                        gGraphPane.getGraphController().getGraph(),
                        hGraphPane.getGraphController().getGraph())
                        .tensorProduct();
                product.setName(graphName);

                graphTabPane.newTab(createGraphPaneFromSource(new GraphController(product)));
            } else {
                tensorProductDialog.show();
            }
        });

        tensorProductDialog.show();
    };

    // Making graph complete
    private EventHandler<ActionEvent> makeCompleteEventHandler = e -> {
        GraphPane currentGraphPane = graphTabPane.currentGraphPane();
        currentGraphPane.removeLoops();
        currentGraphPane.getGraphController().makeComplete();

        for (Arc arc : currentGraphPane.getGraphController().getArcs()) {
            DrawableArc newInverse = new DrawableArc(
                    new Arc(arc.getEnd(), arc.getBegin(),
                            false),
                    new DrawableNode(arc.getEnd()),
                    new DrawableNode(arc.getBegin())
            );

            if (currentGraphPane.getDrawableArcs().indexOf(newInverse) != -1) {
                DrawableArc inverseFound = currentGraphPane.getDrawableArcs()
                        .get(currentGraphPane.getDrawableArcs().indexOf(newInverse));

                currentGraphPane.getPane().getChildren()
                        .remove(inverseFound.getArrow()); // kaef

                continue;
            }

            DrawableNode newBegin = currentGraphPane.getDrawableNodes()
                    .get(currentGraphPane.getDrawableNodes().indexOf(new DrawableNode(arc.getBegin())));
            DrawableNode newEnd = currentGraphPane.getDrawableNodes()
                    .get(currentGraphPane.getDrawableNodes().indexOf(new DrawableNode(arc.getEnd())));

            DrawableArc newPrime = new DrawableArc(
                    arc,
                    newBegin,
                    newEnd
            );

            if (currentGraphPane.getDrawableArcs().indexOf(newPrime) != -1) {
                continue;
            }

            currentGraphPane.getPane().getChildren().add(newPrime.getLine());
            currentGraphPane.getDrawableArcs().add(newPrime);
        }

        for (DrawableNode drawableNode : currentGraphPane.getDrawableNodes()) {
            drawableNode.getShape().toFront();
        }
    };

    // TODO: Conversion to a tree
    private EventHandler<ActionEvent> treeConversionEventHandler = e -> {
        GraphPane currentGraphPane = graphTabPane.currentGraphPane();
        currentGraphPane.removeLoops();

        if (!currentGraphPane.getGraphController().isTree()) {
            currentGraphPane.getGraphController().makeTree();
        }
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

    // Coloring the graph nodes
    private EventHandler<ActionEvent> coloringNodesEventHandler = e -> {
        Map<Node, String> stringColors = graphTabPane.currentGraphPane().getGraphController().colorizeNodes();
        Map<String, Color> colors = new HashMap<>();

        Random random = new Random(System.currentTimeMillis());

        for (String stringColor : stringColors.values()) {
            colors.put(
                    stringColor,
                    Color.color(random.nextDouble(),
                            random.nextDouble(),
                            random.nextDouble())
            );
        }

        List<DrawableNode> drawableNodes = graphTabPane.currentGraphPane().getDrawableNodes();

        for (DrawableNode drawableNode : drawableNodes) {
            drawableNode.getShape().setFill(colors.get(stringColors.get(drawableNode.getSourceNode())));
        }
    };

    // Distance between two specified nodes
    private EventHandler<ActionEvent> distanceBetweenNodesEventHandler = e -> {
        ComboBox<String> firstNodeName = new ComboBox<>();
        ComboBox<String> secondNodeName = new ComboBox<>();

        for (DrawableNode drawableNode : graphTabPane.currentGraphPane().getDrawableNodes()) {
            firstNodeName.getItems().add(drawableNode.getSourceNode().toString());
            secondNodeName.getItems().add(drawableNode.getSourceNode().toString());
        }

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("Source node:"), 0, 0);
        gridPane.add(new Label("Destination node:"), 1, 0);
        gridPane.add(firstNodeName, 0, 1);
        gridPane.add(secondNodeName, 1, 1);
        GridPane.setMargin(firstNodeName, new Insets(CIRCLE_RADIUS));
        GridPane.setMargin(secondNodeName, new Insets(CIRCLE_RADIUS));

        Alert distanceDialog = createEmptyDialog(gridPane, "Distance between two nodes");

        ButtonType GET = new ButtonType("Get");
        distanceDialog.getButtonTypes().add(GET);

        ((Button) distanceDialog.getDialogPane().lookupButton(GET)).setOnAction(actionEvent -> {
            Node begin = new Node();
            Node end = new Node();

            for (DrawableNode drawableNode : graphTabPane.currentGraphPane().getDrawableNodes()) {
                if (drawableNode.getSourceNode().toString().equals(
                        firstNodeName.getSelectionModel().getSelectedItem())) {
                    begin = drawableNode.getSourceNode();
                }

                if (drawableNode.getSourceNode().toString().equals(
                        secondNodeName.getSelectionModel().getSelectedItem())) {
                    end = drawableNode.getSourceNode();
                }
            }

            Integer distance = graphTabPane.currentGraphPane().getGraphController()
                    .getDistanceMatrix().getDistancesMap()
                    .get(begin).get(end);

            Label distanceText = new Label();
            Alert distanceAsItIs = createEmptyDialog(distanceText, "Distance");
            distanceAsItIs.getButtonTypes().add(ButtonType.OK);

            if (distance == DistanceMatrix.INFINITY) {
                distanceText.setText("The way from " + begin + " to " + end + " wasn't found");
            } else {
                distanceText.setText("Distance between " + begin + " and " + end + " is " +
                                graphTabPane.currentGraphPane().getGraphController()
                                        .getDistanceMatrix().getDistancesMap()
                                        .get(begin).get(end));
            }

            distanceAsItIs.show();
        });

        distanceDialog.show();
    };

    // All possible ways from one node to another
    private EventHandler<ActionEvent> findAllPathsBetweenTwoNodes = e -> {
        ComboBox<String> firstNodeName = new ComboBox<>();
        ComboBox<String> secondNodeName = new ComboBox<>();

        for (DrawableNode drawableNode : graphTabPane.currentGraphPane().getDrawableNodes()) {
            firstNodeName.getItems().add(drawableNode.getSourceNode().toString());
            secondNodeName.getItems().add(drawableNode.getSourceNode().toString());
        }

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("Source node:"), 0, 0);
        gridPane.add(new Label("Destination node:"), 1, 0);
        gridPane.add(firstNodeName, 0, 1);
        gridPane.add(secondNodeName, 1, 1);
        GridPane.setMargin(firstNodeName, new Insets(CIRCLE_RADIUS));
        GridPane.setMargin(secondNodeName, new Insets(CIRCLE_RADIUS));

        Alert distanceDialog = createEmptyDialog(gridPane, "Find all paths between two nodes");

        ButtonType GET = new ButtonType("Get");
        distanceDialog.getButtonTypes().add(GET);

        ((Button) distanceDialog.getDialogPane().lookupButton(GET)).setOnAction(actionEvent -> {
            Node begin = new Node();
            Node end = new Node();

            for (DrawableNode drawableNode : graphTabPane.currentGraphPane().getDrawableNodes()) {
                if (drawableNode.getSourceNode().toString().equals(
                        firstNodeName.getSelectionModel().getSelectedItem())) {
                    begin = drawableNode.getSourceNode();
                }

                if (drawableNode.getSourceNode().toString().equals(
                        secondNodeName.getSelectionModel().getSelectedItem())) {
                    end = drawableNode.getSourceNode();
                }
            }

            ObservableList<Path> paths = graphTabPane.currentGraphPane().getGraphController().pathsBetweenNodes(begin, end);
            ObservableList<String> pathsString = FXCollections.observableArrayList();
            ObservableList<String> shortestPathsString = FXCollections.observableArrayList();

            try {
                for (Path path : paths) {
                    pathsString.add(path.toString());
                }

                for (Path shortestPath : graphTabPane.currentGraphPane().getGraphController().shortestPaths()) {
                    shortestPathsString.add(shortestPath.toString());
                }
            } catch (NullPointerException ex) {
                return;
            }

            ListView<String> allPathsListView = new ListView<>();
            allPathsListView.getItems().addAll(pathsString);
            allPathsListView.setPrefSize(MAIN_FORM_WIDTH / 6, MAIN_FORM_HEIGHT / 8);
            allPathsListView.setEditable(false);

            ListView<String> shortestPathsListView = new ListView<>();
            shortestPathsListView.getItems().addAll(shortestPathsString);
            shortestPathsListView.setPrefSize(MAIN_FORM_WIDTH / 6, MAIN_FORM_HEIGHT / 8);
            shortestPathsListView.setEditable(false);

            GridPane gridPane1 = new GridPane();
            gridPane1.add(new Label("All paths"), 0, 0);
            gridPane1.add(new Label("Shortest paths"), 1, 0);
            gridPane1.add(allPathsListView, 0, 1);
            gridPane1.add(shortestPathsListView, 1, 1);
            gridPane1.setAlignment(Pos.CENTER);

            Alert pathsDialog = createEmptyDialog(gridPane1, "Paths");
            pathsDialog.getButtonTypes().add(ButtonType.OK);
            pathsDialog.show();
        });

        distanceDialog.show();
    };

    // Taking graph's adjacency matrix
    private EventHandler<ActionEvent> getAdjacencyMatrixEventHandler = e -> {
        Label matrix;

        try {
            matrix = new Label(graphTabPane.currentGraphPane().getGraphController().adjacencyMatrix().matrixToString());
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
