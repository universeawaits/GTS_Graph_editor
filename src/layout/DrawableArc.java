package layout;

import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.Arc;

import java.util.Objects;
import java.util.Random;


public class DrawableArc {
    private static final int SHAPE_WIDTH = 3;

    private Arc sourceArc;
    private DrawableNode begin;
    private DrawableNode end;

    private boolean isFocused;

    private Line shape;
    private Color color;


    public DrawableArc(Arc sourceArc, DrawableNode begin, DrawableNode end) {
        this.sourceArc = sourceArc;
        this.begin = begin;
        this.end = end;

        isFocused = false;

        Random randomColorComponent = new Random(System.currentTimeMillis());

        color = Color.color(
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble()
        );

        shape = new Line(
                begin.getShape().getCenterX(), begin.getShape().getCenterY(),
                end.getShape().getCenterX(), end.getShape().getCenterY()
        );
        configureShape();
    }

    public Line getShape() {
        return shape;
    }

    public Arc getSourceArc() {
        return sourceArc;
    }

    public boolean isFocused() {
        return isFocused;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrawableArc that = (DrawableArc) o;
        return Objects.equals(begin, that.begin) &&
                Objects.equals(end, that.end);
    }

    /*
        Configs
     */

    // Shape configs: events handling, coloring
    private void configureShape() {
        shape.setStrokeWidth(SHAPE_WIDTH);
        shape.setStroke(color);

        shape.startXProperty().bind(begin.getShape().centerXProperty());
        shape.startYProperty().bind(begin.getShape().centerYProperty());
        shape.endXProperty().bind(end.getShape().centerXProperty());
        shape.endYProperty().bind(end.getShape().centerYProperty());

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
