package layout.form;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import layout.DrawableNode;
import model.Node;
import controller.GraphController;


import java.awt.*;

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;


public class GraphPane {
    private static final int DOUBLE_MOUSE_CLICK_COUNT = 2;

    private GraphController graphController;

    private ObservableList<DrawableNode> drawableNodes;

    private Pane pane;


    public GraphPane(GraphController graphController) {
        this.graphController = graphController;

        drawableNodes = FXCollections.observableArrayList();

        pane = new Pane();
        configurePane();
    }

    public Pane getPane() {
        return pane;
    }

    public GraphController getGraphController() {
        return graphController;
    }

    /*
        Configs
     */

    // Group configs: event handling, background fill
    private void configurePane() {
        pane.setPrefSize(MAIN_FORM_WIDTH, 4 * MAIN_FORM_HEIGHT / 5);
        pane.setFocusTraversable(true);

        // Binding source graph changes with view panels
        graphController.getNodes().addListener((ListChangeListener) changedListItem -> {
            while (changedListItem.next()) {
                if (changedListItem.wasAdded()) {
                    DrawableNode nodeShape = new DrawableNode(graphController.getNodes().get(changedListItem.getFrom()));
                    nodeShape.getShape().setTranslateX(
                            MouseInfo.getPointerInfo().getLocation().getX() // how to get centered??????
                    );
                    nodeShape.getShape().setTranslateY(
                            MouseInfo.getPointerInfo().getLocation().getY()
                    );

                    drawableNodes.add(nodeShape);
                    pane.getChildren().add(nodeShape.getShape());
                }

                if (changedListItem.wasRemoved()) {
                    drawableNodes.remove(changedListItem.getFrom());
                    pane.getChildren().remove(changedListItem.getFrom());
                }
            }
        });

        pane.setOnMouseClicked(nodeAddingEventHandler);
        pane.setOnKeyPressed(nodeRemovingEventHandler);
    }

    /*
        Event handlers
     */

    // Adding a new node to pane & graph with double mouse click
    private EventHandler<MouseEvent> nodeAddingEventHandler = e -> {
        if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == DOUBLE_MOUSE_CLICK_COUNT) {
            Node node = new Node();
            graphController.addNode(node);
        }
    };

    // Removing the selected node from pane & graph with R key pressed and node hover
    private EventHandler<KeyEvent> nodeRemovingEventHandler = e -> {
        if (e.getCode().equals(KeyCode.R)) {
            for (DrawableNode drawableNode : drawableNodes) {
                if (drawableNode.isFocused()) {
                    graphController.removeNode(drawableNode.getSourceNode());
                    break;
                }
            }
        }
    };
}
