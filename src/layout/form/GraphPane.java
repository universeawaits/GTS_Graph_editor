package layout.form;

import javafx.collections.FXCollections;
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

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;


public class GraphPane {
    public enum ActionType { POINTER, ADD_ARC }
    private static final int DOUBLE_MOUSE_CLICK_COUNT = 2;

    private GraphController graphController;
    private ActionType actionType;
    private int countOfSelectedNodes;

    private ObservableList<DrawableNode> drawableNodes;

    private Pane pane;


    public GraphPane(GraphController graphController) {
        this.graphController = graphController;
        actionType = ActionType.POINTER;
        countOfSelectedNodes = 0;

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

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    /*
        Configs
     */

    // Group configs: event handling, background fill
    private void configurePane() {
        pane.setPrefSize(MAIN_FORM_WIDTH, 4 * MAIN_FORM_HEIGHT / 5);
        pane.setFocusTraversable(true);

        pane.setOnMouseClicked(nodeAddingEventHandler);
        pane.setOnKeyPressed(nodeRemovingEventHandler);
    }

    /*
        Event handlers
     */

    // Adding a new node to pane & graph with double mouse click
    private EventHandler<MouseEvent> nodeAddingEventHandler = e -> {
        if (e.getButton().equals(MouseButton.PRIMARY)
                && (e.getClickCount() == DOUBLE_MOUSE_CLICK_COUNT)
                && (actionType == ActionType.POINTER)) {
            Node node = new Node();
            graphController.addNode(node);

            DrawableNode nodeShape = new DrawableNode(node);
            nodeShape.getShape().setTranslateX(e.getSceneX() - DrawableNode.SHAPE_SIZE / 2);
            nodeShape.getShape().setTranslateY(e.getSceneY() - 2 * DrawableNode.SHAPE_SIZE);

            drawableNodes.add(nodeShape);
            pane.getChildren().add(nodeShape.getShape());
        }
    };

    // Removing the selected node from pane & graph with R key pressed and node hover
    private EventHandler<KeyEvent> nodeRemovingEventHandler = e -> {
        if (e.getCode().equals(KeyCode.R) && (actionType == ActionType.POINTER)) {
            for (DrawableNode drawableNode : drawableNodes) {
                if (drawableNode.isFocused()) {
                    graphController.removeNode(drawableNode.getSourceNode());
                    drawableNodes.remove(drawableNode);
                    pane.getChildren().remove(drawableNode.getShape());
                    break;
                }
            }
        }
    };

    // Adding arc between selected nodes to graph & pane
    private EventHandler<MouseEvent> arcAddingEventHandler = e -> {

    };
}
