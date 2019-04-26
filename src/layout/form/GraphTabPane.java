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
                currentGraphPane().perforrmKeyAction(e);
            } finally {
                return;
            }
        });
    }


    public void newTab(String name) {
        Tab tab = new Tab(name);

        Graph graph = new Graph();
        GraphController graphController = new GraphController(graph);

        GraphPane graphPane = new GraphPane(graphController);

        tab.setContent(graphPane.getPane());
        managingGraphs.put(tab, graphPane);

        tab.setOnClosed(e -> {
            managingGraphs.remove(tab);
        });

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void newTab(String name, GraphPane graphPane) {
        Tab tab = new Tab(name);

        tab.setContent(graphPane.getPane());
        managingGraphs.put(tab, graphPane);

        tab.setOnClosed(e -> {
            managingGraphs.remove(tab);
        });

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);

        graphToolBar.updateSource(graphPane);
        graphStatusBar.updateSource(graphPane.getGraphController());
    }

    public GraphPane currentGraphPane() {
        return managingGraphs.get(tabPane.getSelectionModel().getSelectedItem());
    }
}