package layout.form;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Border;


public class GraphToolBar {
    private GraphPane graphPane;

    private Button pointer;
    private Button addArc;
    private Label actionTypeText;

    private ToolBar toolBar;


    public GraphToolBar() {
        this.graphPane = null;

        pointer = new Button("Pointer");
        addArc = new Button("Add arc");
        actionTypeText = new Label(pointer.getText() + " mode");

        toolBar = new ToolBar();
        configureToolBar();
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    /*
     *      Configs
     */

    private void configureToolBar() {
        toolBar.getItems().addAll(pointer, addArc, new Separator(Orientation.VERTICAL), actionTypeText);
    }


    public void updateSource(GraphPane graphPane) {
        graphPane.getPane().removeEventHandler(Event.ANY, anyEventToListenPaneActionTypePerforming);
        this.graphPane = graphPane;

        updateEventHandlers();
    }

    private void updateEventHandlers() {
        graphPane.getPane().addEventHandler(Event.ANY, anyEventToListenPaneActionTypePerforming);

        pointer.setOnAction(e -> {
            actionTypeText.setText(pointer.getText() + " mode");
            graphPane.setActionType(GraphPane.ActionType.POINTER);
            graphPane.getPane().requestFocus();
        });

        addArc.setOnAction(e -> {
            actionTypeText.setText(addArc.getText() + " mode");
            graphPane.setActionType(GraphPane.ActionType.ADD_ARC);
            graphPane.getPane().requestFocus();
        });
    }

    private EventHandler<Event> anyEventToListenPaneActionTypePerforming = e -> {
        switch (graphPane.getActionType()) {
            case POINTER: {
                actionTypeText.setText(pointer.getText() + " mode");
                break;
            }
            case ADD_ARC: {
                actionTypeText.setText(addArc.getText() + " mode");
                break;
            }
        }
    };
}
