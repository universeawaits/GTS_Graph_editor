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



        statusBar.getItems().addAll(
                nodesCount,
                new Separator(),
                arcsCount
        );
    }
}
