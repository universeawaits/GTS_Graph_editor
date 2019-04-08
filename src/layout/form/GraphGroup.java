package layout.form;

import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import layout.DrawableNode;
import model.Graph;
import model.Node;

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;

public class GraphGroup {
    private static final int DOUBLE_MOUSE_CLICK_COUNT = 2;

    private Graph sourceGraph;

    private Group group;


    public GraphGroup(Graph sourceGraph) {
        this.sourceGraph = sourceGraph;

        group = new Group();
        configureGroup();
    }

    public Group getGroup() {
        return group;
    }

    public Graph getSourceGraph() {
        return sourceGraph;
    }

    // Configs
    private void configureGroup() {
        Rectangle rectangle = new Rectangle(MAIN_FORM_WIDTH, MAIN_FORM_HEIGHT, Color.WHITE);
        group.getChildren().add(rectangle);

        // Adding a new node to group & graph with double mouse click
        group.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == DOUBLE_MOUSE_CLICK_COUNT) {
                Node newSourceNode = new Node();
                sourceGraph.getNodes().add(newSourceNode);

                DrawableNode newNodeShape = new DrawableNode(newSourceNode);
                newNodeShape.getShape().setTranslateX(e.getSceneX() - DrawableNode.SHAPE_SIZE / 2);
                newNodeShape.getShape().setTranslateY(e.getSceneY() - DrawableNode.SHAPE_SIZE / 2);
                group.getChildren().add(newNodeShape.getShape());
            }
        });
    }
}
