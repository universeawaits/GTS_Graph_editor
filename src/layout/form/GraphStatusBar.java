package layout.form;

import controller.GraphController;
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
    private static final int NODES_DEGREES_COMBOBOX_VISIBLE_COUNT = 5;

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

    public GraphController getGraphController() {
        return graphController;
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
        graphController.getArcs().addListener((ListChangeListener) change -> {
            arcsCount.setText(ARCS_COUNT + String.valueOf(graphController.getArcs().size()));
        });

        ComboBox<String> nodesDegrees = new ComboBox<>();
        nodesDegrees.setVisibleRowCount(NODES_DEGREES_COMBOBOX_VISIBLE_COUNT);
        nodesDegrees.setPromptText("Nodes degrees");
        graphController.getArcs().addListener((ListChangeListener) change -> {
            nodesDegrees.getItems().clear();
            for (Node node : graphController.getNodes()) {
                nodesDegrees.getItems().add(node.getName() + ": " + graphController.degreeOf(node));
            }
        });
        // TODO: when node name changes, change it also in nodes degrees' combobox
        graphController.getNodes().addListener((ListChangeListener) change -> {
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
                nodesDegrees
        );
    }
}
