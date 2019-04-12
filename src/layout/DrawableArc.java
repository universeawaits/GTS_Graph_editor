package layout;

import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Objects;
import java.util.Random;


public class DrawableArc {
    private static final int SHAPE_WIDTH = 2;

    private DrawableNode begin;
    private DrawableNode end;

    private Line shape;
    private Color color;


    public DrawableArc(DrawableNode begin, DrawableNode end) {
        this.begin = begin;
        this.end = end;

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
        shape.setFill(color);
        shape.setStrokeWidth(SHAPE_WIDTH);

        shape.startXProperty().bind(begin.getShape().centerXProperty());
        shape.startYProperty().bind(begin.getShape().centerYProperty());
        shape.endXProperty().bind(end.getShape().centerXProperty());
        shape.endYProperty().bind(end.getShape().centerYProperty());

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
