package layout.form;

import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

public class GraphToolBar {
    private static final String PRESSED_BUTTON_STYLE = "-fx-border-color: linear-gradient(#e29f9f 0%, #d98585 49%, #c86367 50%, #c84951 100%); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5, 5, 5, 5; " +
            "-fx-background-insets: 1, 1, 1, 1; ";
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
        pointer.setStyle(PRESSED_BUTTON_STYLE);

        graphPane.getPane().addEventHandler(Event.ANY, e -> {
            switch (graphPane.getActionType()) {
                case POINTER: {
                    pointer.setStyle(PRESSED_BUTTON_STYLE);
                    addArc.setStyle(null);
                    break;
                }
                case ADD_ARC: {
                    addArc.setStyle(PRESSED_BUTTON_STYLE);
                    pointer.setStyle(null);
                    break;
                }
            }
        });

        pointer.setOnAction(e -> {
            pointer.setStyle(PRESSED_BUTTON_STYLE);
            addArc.setStyle(null);
            graphPane.setActionType(GraphPane.ActionType.POINTER);
            graphPane.getPane().requestFocus();
        });

        addArc.setOnAction(e -> {
            addArc.setStyle(PRESSED_BUTTON_STYLE);
            pointer.setStyle(null);
            graphPane.setActionType(GraphPane.ActionType.ADD_ARC);
            graphPane.getPane().requestFocus();
        });
    }

    private void configureToolBar() {
        toolBar.getItems().addAll(pointer, addArc);

        graphPane.getActionType();
    }
}
