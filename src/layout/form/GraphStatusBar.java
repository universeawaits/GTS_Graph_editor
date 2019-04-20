package layout.form;

import controller.GraphController;
import controller.PlanarityVerifier;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;


public class GraphStatusBar {
    private static final String NODES_COUNT = "Nodes count: ";
    private static final String ARCS_COUNT = "Arcs count: ";
    private static final String DIAMETER = "Diameter: ";
    private static final String RADIUS = "Radius: ";

    private GraphController graphController;

    private ToolBar statusBar;


    public GraphStatusBar(GraphController graphController) {
        this.graphController = graphController;

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

        Label diameter = new Label(DIAMETER + String.valueOf(graphController.diameter()));
        graphController.getArcs().addListener((ListChangeListener) change -> {
            diameter.setText(DIAMETER + String.valueOf(graphController.diameter()));
        });

        Label radius = new Label(RADIUS + 0);
        graphController.getArcs().addListener((ListChangeListener) change -> {
            radius.setText(RADIUS + String.valueOf(graphController.radius()));
        });

        Label isPlanar = new Label("Graph is planar");
        graphController.getArcs().addListener((ListChangeListener) changeList -> {
            isPlanar.setText("Graph is" + (graphController.isPlanar() ? "" : "n't") + " planar");
        });

        statusBar.getItems().addAll(
                nodesCount,
                new Separator(),
                arcsCount,
                new Separator(),
                diameter,
                new Separator(),
                radius,
                new Separator(),
                isPlanar
        );
    }
}
