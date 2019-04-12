package layout.form;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import layout.DrawableArc;
import layout.DrawableNode;
import model.Arc;
import model.Node;
import controller.GraphController;

import static layout.DrawableNode.SHAPE_SIZE;
import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;


public class GraphPane {
    public enum ActionType { POINTER, ADD_ARC }

    private static final ColorPicker colorPicker = new ColorPicker();
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

    public ObservableList<DrawableNode> getDrawableNodes() {
        return drawableNodes;
    }

    public ObservableList<DrawableArc> getDrawableArcs() {
        return drawableArcs;
    }

    /*
        Configs
     */

    // Group configs: event handling, background fill
    private void configurePane() {
        pane.setPrefSize(MAIN_FORM_WIDTH, 4 * MAIN_FORM_HEIGHT / 5);
        pane.setFocusTraversable(true);

        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, nodeAddingEventHandler);
        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, arcAddingEventHandler);
        pane.addEventHandler(KeyEvent.KEY_PRESSED, nodeOrArcRemovingEventHandler);
        pane.addEventHandler(KeyEvent.KEY_PRESSED, nodeOrArcColoringEventHandler);
        pane.addEventHandler(KeyEvent.KEY_PRESSED, nodeRenamingEventHandler);
    }

    /*
        Others
     */

    private Alert createEmptyDialog(javafx.scene.Node content, String title) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);

        alert.getDialogPane().setContent(content);

        return alert;
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
            nodeShape.getShape().setCenterY(e.getSceneY() - 2 * SHAPE_SIZE);

            drawableNodes.add(nodeShape);
            pane.getChildren().addAll(nodeShape.getShape(), nodeShape.getName());
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
                Arc inverseArc = new Arc(endForArc.getSourceNode(), beginForArc.getSourceNode());

                if ((graphController.getArcs().contains(arc))
                        || (graphController.getArcs().contains(inverseArc))) {
                    isNodesForArcSelected = false;
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

    // Removing the selected node with/or incident arcs from pane & graph with DELETE key pressed and node hover
    private EventHandler<KeyEvent> nodeOrArcRemovingEventHandler = e -> {
        if (e.getCode().equals(KeyCode.DELETE) && (actionType == ActionType.POINTER)) {
            for (DrawableNode drawableNode : drawableNodes) {
                if (drawableNode.isFocused()) {
                    graphController.removeNode(drawableNode.getSourceNode());
                    drawableNodes.remove(drawableNode);
                    pane.getChildren().removeAll(drawableNode.getShape(), drawableNode.getName());


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

    // Coloring of a node or an arc in focus with C key pressed
    private EventHandler<KeyEvent> nodeOrArcColoringEventHandler = e -> {
        if (e.getCode().equals(KeyCode.C) && (actionType == ActionType.POINTER)) {
            for (DrawableNode drawableNode : drawableNodes) {
                if (drawableNode.isFocused()) {
                    colorPicker.setOnAction(actionEvent -> {
                        drawableNode.getShape().setFill(colorPicker.getValue());
                    });

                    Alert colorChooseDialog = createEmptyDialog(colorPicker, "Color choosing");
                    colorChooseDialog.getButtonTypes().add(ButtonType.APPLY);
                    colorChooseDialog.show();
                    break;
                }
            }

            for (DrawableArc drawableArc : drawableArcs) {
                if (drawableArc.isFocused()) {
                    colorPicker.setOnAction(actionEvent -> {
                        drawableArc.getShape().setStroke(colorPicker.getValue());
                    });

                    Alert colorChoose = createEmptyDialog(colorPicker, "Color choosing");
                    colorChoose.getButtonTypes().add(ButtonType.APPLY);
                    colorChoose.show();
                    break;
                }
            }
        }
    };

    // Renaming of a node in focus with R key pressed
    private EventHandler<KeyEvent> nodeRenamingEventHandler = e -> {
        if (e.getCode().equals(KeyCode.R) && (actionType == ActionType.POINTER)) {
            for (DrawableNode drawableNode : drawableNodes) {
                if (drawableNode.isFocused()) {
                    TextField newName = new TextField();

                    GridPane gridPane = new GridPane();
                    gridPane.add(new Label("New name"), 0, 0);
                    gridPane.add(newName, 1, 0);
                    GridPane.setMargin(newName, new Insets(SHAPE_SIZE));

                    Alert renameDialog = createEmptyDialog(gridPane, "Node renaming");

                    ButtonType RENAME = new ButtonType("Rename");
                    renameDialog.getButtonTypes().add(RENAME);

                    ((Button) renameDialog.getDialogPane().lookupButton(RENAME)).setOnAction(actionEvent -> {
                        drawableNode.getSourceNode().setName(newName.getText());
                        drawableNode.setName(newName.getText());
                    });

                    renameDialog.show();
                    break;
                }
            }
        }
    };
}
