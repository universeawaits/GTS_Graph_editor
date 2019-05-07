package layout.form;

import controller.GraphController;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import model.Graph;
import java.util.HashMap;
import java.util.Map;

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;


public class GraphTabPane {
    private static final int TAB_TITLE_WIDTH = 100;

    private TabPane tabPane;
    private Map<Tab, GraphPane> managingGraphs;

    private GraphStatusBar graphStatusBar;
    private GraphToolBar graphToolBar;


    public GraphTabPane(GraphToolBar graphToolBar, GraphStatusBar graphStatusBar) {
        tabPane = new TabPane();
        managingGraphs = new HashMap<>();

        this.graphToolBar = graphToolBar;
        this.graphStatusBar = graphStatusBar;

        configureTabPane();
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public Map<Tab, GraphPane> getManagingGraphs() {
        return managingGraphs;
    }

    /*
     *      Configs
     */

    private void configureTabPane() {
        tabPane.setPrefSize(MAIN_FORM_WIDTH, 4 * MAIN_FORM_HEIGHT / 5);
        tabPane.setTabMaxWidth(TAB_TITLE_WIDTH);
        tabPane.setTabMinWidth(TAB_TITLE_WIDTH);

        tabPane.getSelectionModel().selectedItemProperty().addListener(e -> {
            try {
                graphToolBar.updateSource(currentGraphPane());
                graphStatusBar.updateSource(currentGraphPane().getGraphController());
            } finally {
                return;
            }
        });

        tabPane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            try {
                currentGraphPane().performKeyAction(e);
            } finally {
                return;
            }
        });
    }


    public void newTab(Graph newGraph) {
        Tab tab = new Tab(newGraph.getName());

        GraphController graphController = new GraphController(newGraph);
        GraphPane graphPane = new GraphPane(graphController);

        tab.setContent(graphPane.getPane());
        managingGraphs.put(tab, graphPane);

        tab.setOnClosed(e -> {
            managingGraphs.remove(tab);
        });

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void newTab(GraphPane graphPane) {
        Tab tab = new Tab(graphPane.getGraphController().getGraph().getName());

        tab.setContent(graphPane.getPane());
        managingGraphs.put(tab, graphPane);

        tab.setOnClosed(e -> {
            managingGraphs.remove(tab);
        });

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public GraphPane currentGraphPane() {
        return managingGraphs.get(tabPane.getSelectionModel().getSelectedItem());
    }

    public GraphPane getGraphPaneAtTab(String name) {
        for (Tab tab : managingGraphs.keySet()) {
            if (tab.getText().equals(name)) {
                return managingGraphs.get(tab);
            }
        }

        return null;
    }
}
