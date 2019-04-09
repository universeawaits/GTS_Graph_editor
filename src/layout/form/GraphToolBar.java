package layout.form;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

public class GraphToolBar {
    private ToolBar toolBar;


    public GraphToolBar() {
        toolBar = new ToolBar();
        configureToolBar();
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    // Inits and configs
    private void configureToolBar() {
        Button addArc = new Button("Add arc");

        toolBar.getItems().addAll(addArc);
    }
}
