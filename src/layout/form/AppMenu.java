package layout.form;

import controller.GraphController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Node;


public class AppMenu {
    private static final int GRIDPANE_INSETS = 10;

    private GraphPane graphPane;
    private GraphController graphController;

    private MenuBar menuBar;


    public AppMenu(GraphPane graphPane) {
        this.graphPane = graphPane;
        graphController = graphPane.getGraphController();

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(
                createFileMenu(),
                createGraphMenu()
        );
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }


    /*
        Menus creating
     */

    // Creating of a file menu
    private Menu createFileMenu() {
        Menu file = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem closeFile = new MenuItem("Close");

        file.getItems().addAll(newFile, openFile, saveFile, closeFile);

        return file;
    }

    // Creating of a graph menu
    private Menu createGraphMenu() {
        Menu graph = new Menu("Graph");
        Menu node = new Menu("Node");
        Menu arc = new Menu("Arc");

        MenuItem addNode = new MenuItem("Add");
        MenuItem removeNode = new MenuItem("Remove");
        MenuItem renameNode = new MenuItem("Rename");
        MenuItem colorizeNode = new MenuItem("Colorize");

        Menu addArc = new Menu("Add");
        MenuItem removeArc = new MenuItem("Remove");
        MenuItem colorizeArc = new MenuItem("Colorize");

        MenuItem directedArc = new MenuItem("Directed");
        MenuItem undirectedArc = new MenuItem("Undirected");


        addNode.setOnAction(nodeAddingEventHandler);


        node.getItems().addAll(addNode, removeNode, renameNode, colorizeNode);
        arc.getItems().addAll(addArc, removeArc, colorizeArc);
        addArc.getItems().addAll(directedArc, undirectedArc);
        graph.getItems().addAll(node, arc);

        return graph;
    }

    /*
        Event handlers
     */

    // Adding a new node to pane & graph
    private EventHandler<ActionEvent> nodeAddingEventHandler = e -> {
        Alert nodeAddingAlert = new Alert(Alert.AlertType.NONE);
        nodeAddingAlert.setTitle("Node adding");

        ButtonType ADD = new ButtonType("Add");
        nodeAddingAlert.getButtonTypes().add(ADD);

        TextField nodeNameTextField = new TextField();
        Button addNode = (Button) nodeAddingAlert.getDialogPane().lookupButton(ADD);

        addNode.setOnAction(actionEvent -> {
            String nodeName = nodeNameTextField.getText();
            if (nodeName == null) {
                nodeName = "";
            }

            graphController.addNode(new Node(nodeName));
        });

        GridPane content = new GridPane();
        content.add(new Label("Name"), 0, 0);
        content.add(nodeNameTextField, 1, 0);
        GridPane.setMargin(nodeNameTextField, new Insets(GRIDPANE_INSETS));

        nodeAddingAlert.getDialogPane().setContent(content);

        nodeAddingAlert.show();
    };

    // Adding a new arc between selected nodes to pane & graph with R key pressed and node hover
    private EventHandler<ActionEvent> nodeRemovingEventHandler = e -> {

    };
}
