package layout.form;

import controller.GraphController;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import model.Node;


public class GraphStatusBar {
    private static final String NODES_COUNT = "Nodes count: ";
    private static final String ARCS_COUNT = "Arcs count: ";
    private static final String NODE_DEGREE = "Node degree: ";

    private GraphController graphController;
    private GraphPane graphPane;

    private ToolBar statusBar;


    public GraphStatusBar(GraphPane graphPane) {
        this.graphPane = graphPane;
        graphController = graphPane.getGraphController();

        statusBar = new ToolBar();
        configureStatusBar();
    }

    public ToolBar getStatusBar() {
        return statusBar;
    }

    /*
        Configs
     */

    // Configure status bar: binding source graph changes with view panels
    private void configureStatusBar() {
        Label nodesCount = new Label(NODES_COUNT + String.valueOf(graphController.getNodes().size()));
        graphController.getNodes().addListener((ListChangeListener) e -> {
            nodesCount.setText(NODES_COUNT + String.valueOf(graphController.getNodes().size()));
        });

        Label arcsCount = new Label(ARCS_COUNT + String.valueOf(graphController.getArcs().size()));
        graphController.getArcs().addListener((ListChangeListener) e -> {
            arcsCount.setText(ARCS_COUNT + String.valueOf(graphController.getArcs().size()));
        });

        // TODO: how to bind this with focus event?
        Label focusedNodeDegree = new Label(NODE_DEGREE + '-');

        ComboBox<String> nodesDegrees = new ComboBox<>();
        graphController.getNodes().addListener((ListChangeListener) e -> {
            nodesDegrees.getItems().clear();
            for (Node node : graphController.getNodes()) {
                nodesDegrees.getItems().add(node.getName() + ": " + graphController.degreeOf(node));
            }
        });



        statusBar.getItems().addAll(
                nodesCount,
                new Separator(),
                arcsCount,
                new Separator(),
                focusedNodeDegree,
                new Separator(),
                nodesDegrees
        );
    }
}
