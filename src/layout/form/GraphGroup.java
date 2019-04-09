package layout.form;

import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import layout.DrawableNode;
import model.Graph;
import model.Node;
import sample.GraphController;

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;

public class GraphGroup {
    private static final int DOUBLE_MOUSE_CLICK_COUNT = 2;

    private GraphController graphController;

    private Group group;


    public GraphGroup(GraphController graphController) {
        this.graphController = graphController;

        group = new Group();
        configureGroup();
    }

    public Group getGroup() {
        return group;
    }

    public GraphController getGraphController() {
        return graphController;
    }


    // Configs
    private void configureGroup() {
        Rectangle rectangle = new Rectangle(MAIN_FORM_WIDTH, 4 * MAIN_FORM_HEIGHT / 5, Color.WHITE);
        group.getChildren().add(rectangle);

        // Adding a new node to group & graph with double mouse click
        group.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == DOUBLE_MOUSE_CLICK_COUNT) {
                Node newSourceNode = new Node();
                graphController.addNode(newSourceNode);

                DrawableNode newNodeShape = new DrawableNode(newSourceNode);
                newNodeShape.getShape().setTranslateX(e.getX() - DrawableNode.SHAPE_SIZE / 2);
                newNodeShape.getShape().setTranslateY(e.getY() - DrawableNode.SHAPE_SIZE / 2);
                group.getChildren().add(newNodeShape.getShape());
            }
        });
    }
}
