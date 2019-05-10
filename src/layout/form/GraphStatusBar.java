package layout.form;

import controller.GraphController;
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
    private Label nodesCount;
    private Label arcsCount;
    private Label diameter;
    private Label radius;
    private Label isComplete;


    public GraphStatusBar() {
        this.graphController = null;

        statusBar = new ToolBar();
        configureStatusBar();
    }

    public ToolBar getStatusBar() {
        return statusBar;
    }

    public void updateSource(GraphController graphController) {
        removeListeners();
        this.graphController = graphController;
        updateLabels();
        addListeners();
    }

    /*
     *      Configs
     */

    private void configureStatusBar() {
        nodesCount = new Label(NODES_COUNT + 0);
        arcsCount = new Label(ARCS_COUNT + 0);
        diameter = new Label(DIAMETER + 0);
        radius = new Label(RADIUS + 0);
        isComplete = new Label("Graph isn't complete");

        statusBar.getItems().addAll(
                nodesCount,
                new Separator(),
                arcsCount,
                new Separator(),
                diameter,
                new Separator(),
                radius,
                new Separator(),
                isComplete
        );
    }

    private void removeListeners() {
        try {
            graphController.getNodes().removeListener(nodesCountListener);
            graphController.getArcs().removeListener(arcsCountListener);
            graphController.getArcs().removeListener(diameterListener);
            graphController.getArcs().removeListener(radiusListener);
            graphController.getArcs().removeListener(isCompleteListener);
            graphController.getNodes().removeListener(isCompleteListener);
        } finally {
            return;
        }
    }

    private void addListeners() {
        graphController.getNodes().addListener(nodesCountListener);
        graphController.getArcs().addListener(arcsCountListener);
        graphController.getArcs().addListener(diameterListener);
        graphController.getArcs().addListener(radiusListener);
        graphController.getArcs().addListener(isCompleteListener);
        graphController.getNodes().addListener(isCompleteListener);
    }

    private void updateLabels() {
        nodesCount.setText(NODES_COUNT + String.valueOf(graphController.getNodes().size()));
        arcsCount.setText(ARCS_COUNT + String.valueOf(graphController.getArcs().size()));
        diameter.setText(DIAMETER + String.valueOf(graphController.diameter()));
        radius.setText(RADIUS + String.valueOf(graphController.radius()));
        isComplete.setText("Graph is" + (graphController.isComplete() ? " " : "n't ") + "complete");
    }

    /*
     *      Listeners
     */

    private ListChangeListener nodesCountListener = change -> {
        nodesCount.setText(NODES_COUNT + String.valueOf(graphController.getNodes().size()));
    };

    private ListChangeListener arcsCountListener = change -> {
        arcsCount.setText(ARCS_COUNT + String.valueOf(graphController.getArcs().size()));
    };

    private ListChangeListener diameterListener = change -> {
        diameter.setText(DIAMETER + String.valueOf(graphController.diameter()));
    };

    private ListChangeListener radiusListener = change -> {
        radius.setText(RADIUS + String.valueOf(graphController.radius()));
    };

    private ListChangeListener isCompleteListener = change -> {
        isComplete.setText("Graph is" + (graphController.isComplete() ? " " : "n't ") + "complete");
    };
}
