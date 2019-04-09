package layout;

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
    private Rectangle shape;
    private Color color;


    public DrawableNode(Color color) {
        this.color = color;
        shape = new Rectangle(SHAPE_SIZE, SHAPE_SIZE, color);

        configureShape();
    }

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

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        shape.setFill(color);
    }


    // Configs
    private void configureShape() {
        shape.setFill(color);

        // Node moving
        shape.setOnMouseDragged(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                shape.setX(e.getX());
                shape.setY(e.getY());
            }
        });

        // Node lightning when mouse entered
        shape.setOnMouseEntered(e -> {
            shape.setEffect(new Bloom());
        });

        // Remove lightning when mouse exited
        shape.setOnMouseExited(e -> {
            shape.setEffect(null);
        });
    }
}
