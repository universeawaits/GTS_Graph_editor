package layout;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Node;

import java.util.Random;


public class DrawableNode {
    public static final int SHAPE_SIZE = 11;

    private Node sourceNode;

    private boolean isFocused;

    private Circle shape;
    private Color color;


    public DrawableNode(Node sourceNode) {
        this.sourceNode = sourceNode;

        Random randomColorComponent = new Random(System.currentTimeMillis());

        color = Color.color(
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble()
        );

        shape = new Circle(SHAPE_SIZE, color);

        configureShape();
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        shape.setFill(color);
    }

    /*
        Configs
     */

    // Shape configs: events handling, coloring
    private void configureShape() {
        shape.setFill(color);
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
}
