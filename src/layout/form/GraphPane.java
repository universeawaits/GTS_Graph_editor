package layout.form;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import layout.DrawableArc;
import layout.DrawableNode;
import model.Arc;
import model.Node;
import controller.GraphController;

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;


public class GraphPane {
    public enum ActionType { POINTER, ADD_ARC }
    private static final int DOUBLE_MOUSE_CLICK_COUNT = 2;

    private GraphController graphController;
    private ActionType actionType;

    private boolean isNodesForArcSelected;
    private DrawableNode beginForArc;
    private DrawableNode endForArc;

    private ObservableList<DrawableNode> drawableNodes;
    private ObservableList<DrawableArc> drawableArcs;

    private Pane pane;


    public GraphPane(GraphController graphController) {
        this.graphController = graphController;
        actionType = ActionType.POINTER;

        isNodesForArcSelected = false;
        beginForArc = null;
        endForArc = null;

        drawableNodes = FXCollections.observableArrayList();
        drawableArcs = FXCollections.observableArrayList();

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

        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, nodeAddingEventHandler);
        pane.addEventHandler(KeyEvent.KEY_PRESSED, nodeOrArcRemovingEventHandler);
        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, arcAddingEventHandler);
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
            nodeShape.getShape().setCenterX(e.getSceneX());
            nodeShape.getShape().setCenterY(e.getSceneY() - 2 * DrawableNode.SHAPE_SIZE);

            drawableNodes.add(nodeShape);
            pane.getChildren().add(nodeShape.getShape());
        }
    };

    // Removing the selected node with incident arcs from pane & graph with R key pressed and node hover
    private EventHandler<KeyEvent> nodeOrArcRemovingEventHandler = e -> {
        if (e.getCode().equals(KeyCode.R) && (actionType == ActionType.POINTER)) {
            for (DrawableNode drawableNode : drawableNodes) {
                if (drawableNode.isFocused()) {
                    graphController.removeNode(drawableNode.getSourceNode());
                    drawableNodes.remove(drawableNode);
                    pane.getChildren().remove(drawableNode.getShape());


                    ObservableList<DrawableArc> arcsToRemove = FXCollections.observableArrayList();

                    for (DrawableArc drawableArc : drawableArcs) {
                        if (drawableArc.getSourceArc().getBegin().equals(drawableNode.getSourceNode())
                                || drawableArc.getSourceArc().getEnd().equals(drawableNode.getSourceNode())) {

                            arcsToRemove.add(drawableArc);
                        }
                    }

                    drawableArcs.removeAll(arcsToRemove);

                    for (DrawableArc drawableArc : arcsToRemove) {
                        pane.getChildren().remove(drawableArc.getShape());
                    }
                    break;
                }
            }

            for (DrawableArc drawableArc : drawableArcs) {
                if (drawableArc.isFocused()) {
                    graphController.removeArc(drawableArc.getSourceArc());
                    drawableNodes.remove(drawableArc);
                    pane.getChildren().remove(drawableArc.getShape());
                    break;
                }
            }
        }
    };

    // Adding arc between selected nodes to graph & pane
    private EventHandler<MouseEvent> arcAddingEventHandler = e -> {
        if (e.getButton().equals(MouseButton.PRIMARY)
                && (actionType == ActionType.ADD_ARC)) {

            for (DrawableNode drawableNode : drawableNodes) {
                if (drawableNode.isFocused()) {
                    if (!isNodesForArcSelected) {
                        beginForArc = drawableNode;
                        isNodesForArcSelected = true;

                        return;
                    } else {
                        endForArc = drawableNode;
                        isNodesForArcSelected = false;

                        break;
                    }
                }
            }

            if ((beginForArc == null)
                    || (endForArc == null)) {

                isNodesForArcSelected = false;
                actionType = ActionType.POINTER;
            }

            if ((beginForArc != null)
                    && (endForArc != null)) {

                Arc arc = new Arc(beginForArc.getSourceNode(), endForArc.getSourceNode());

                if (graphController.getArcs().contains(arc)) {
                    return;
                }

                graphController.addArc(arc);

                DrawableArc arcShape = new DrawableArc(arc, beginForArc, endForArc);

                drawableArcs.add(arcShape);
                pane.getChildren().add(arcShape.getShape());

                beginForArc = null;
                endForArc = null;
            }
        }
    };
}
