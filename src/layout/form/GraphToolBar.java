package layout.form;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

public class GraphToolBar {
    private GraphPane graphPane;

    private Button pointer;
    private Button addArc;

    private ToolBar toolBar;


    public GraphToolBar(GraphPane graphPane) {
        this.graphPane = graphPane;

        pointer = new Button("Pointer");
        addArc = new Button("Add arc");
        configureButtons();

        toolBar = new ToolBar();
        configureToolBar();
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    /*
        Configs
     */

    private void configureButtons() {
        pointer.setOnAction(e -> {
            graphPane.setActionType(GraphPane.ActionType.POINTER);
            graphPane.getPane().requestFocus();
        });

        addArc.setOnAction(e -> {
            graphPane.setActionType(GraphPane.ActionType.ADD_ARC);
            graphPane.getPane().requestFocus();
        });
    }

    private void configureToolBar() {
        toolBar.getItems().addAll(pointer, addArc);
    }
}
