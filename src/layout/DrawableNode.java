package layout;

import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.Node;

import java.util.Random;


public class DrawableNode {
    public static final int SHAPE_SIZE = 10;

    private Node sourceNode;

    private boolean isFocused;

    private Circle shape;
    private Text name;


    public DrawableNode(Node sourceNode) {
        this.sourceNode = sourceNode;

        Random randomColorComponent = new Random(System.currentTimeMillis());

        shape = new Circle(
                SHAPE_SIZE, Color.color(
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble()
        ));
        configureShape();

        name = new Text();
        configureName();
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public Circle getShape() {
        return shape;
    }

    public Text getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    /*
        Configs
     */

    // Shape configs: events handling, coloring
    private void configureShape() {
        shape.setFocusTraversable(true);

        // Node moving
        shape.setOnMouseDragged(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                shape.setCenterX(e.getX() - SHAPE_SIZE / 2);
                shape.setCenterY(e.getY() - SHAPE_SIZE / 2);
            }
        });

        // Node inner dark lightning when mouse entered
        shape.setOnMouseEntered(e -> {
            shape.setEffect(new Bloom());
            shape.toFront();
            isFocused = true;
        });

        // Remove lightning when mouse exited
        shape.setOnMouseExited(e -> {
            shape.setEffect(null);
            shape.toFront();
            isFocused = false;
        });
    }

    private void configureName() {
        name.setFont(Font.font(3 * SHAPE_SIZE / 2));
        name.setText(sourceNode.getName());

        name.setX(shape.getCenterX() + 2 * SHAPE_SIZE);
        name.setY(shape.getCenterY() - 2 * SHAPE_SIZE);

        name.xProperty().bind(shape.centerXProperty()); // set to right upper........how???
        name.yProperty().bind(shape.centerYProperty());
    }
}
