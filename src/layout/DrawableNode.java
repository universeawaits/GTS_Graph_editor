package layout;

import javafx.geometry.Point2D;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import model.Node;

import java.util.Random;


public class DrawableNode {
    public static final int SHAPE_SIZE = 20;


    private Node sourceNode;

    private boolean isFocused;

    private Rectangle shape;
    private Color color;


    public DrawableNode(Node sourceNode) {
        this.sourceNode = sourceNode;

        Random randomColorComponent = new Random(System.currentTimeMillis());

        color = Color.color(
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble()
        );

        shape = new Rectangle(SHAPE_SIZE, SHAPE_SIZE, color);

        configureShape();
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        shape.setFill(color);
    }

    public Point2D getCenter() {
        return new Point2D(
                shape.getX() + SHAPE_SIZE / 2,
                shape.getY() + SHAPE_SIZE / 2
        );
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
                shape.setX(e.getX() - SHAPE_SIZE / 2);
                shape.setY(e.getY() - SHAPE_SIZE / 2);
            }
        });

        // Node lightning when mouse entered
        shape.setOnMouseEntered(e -> {
            shape.setEffect(new Bloom());
            isFocused = true;
        });

        // Remove lightning when mouse exited
        shape.setOnMouseExited(e -> {
            shape.setEffect(null);
            isFocused = false;
        });
    }
}
