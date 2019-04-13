package layout;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import model.Arc;

import java.util.Objects;
import java.util.Random;

import static layout.DrawableNode.CIRCLE_RADIUS;


public class DrawableArc {
    private static final Bloom bloom = new Bloom();
    private static final int LINE_WIDTH = 3;
    private static final int ARROW_SIDE = 2 * CIRCLE_RADIUS;

    private Arc sourceArc;
    private DrawableNode begin;
    private DrawableNode end;

    private boolean isFocused;

    private Polygon arrow;
    private Line line;
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

        line = new Line(
                begin.getShape().getCenterX(), begin.getShape().getCenterY(),
                end.getShape().getCenterX(), end.getShape().getCenterY()
        );
        configureLine();

        if (sourceArc.isDirected()) {
            arrow = new Polygon();
            configureArrow();
        } else {
            arrow = null;
        }
    }

    public Polygon getArrow() {
        return arrow;
    }

    public Line getLine() {
        return line;
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

    // Line configs: events handling, coloring
    private void configureLine() {
        line.setStrokeWidth(LINE_WIDTH);
        line.setStroke(color);

        line.startXProperty().bind(begin.getShape().centerXProperty());
        line.startYProperty().bind(begin.getShape().centerYProperty());
        line.endXProperty().bind(end.getShape().centerXProperty());
        line.endYProperty().bind(end.getShape().centerYProperty());

        // Line lightning when mouse entered
        line.setOnMouseEntered(e -> {
            line.setEffect(bloom);
            arrow.setEffect(bloom);
            isFocused = true;
        });

        // Remove lightning when mouse exited
        line.setOnMouseExited(e -> {
            line.setEffect(null);
            arrow.setEffect(null);
            isFocused = false;
        });
    }

    // Arrow configs: events handling, coloring
    private void configureArrow() {
        arrow.setStrokeWidth(LINE_WIDTH);
        arrow.setFill(color);
        arrow.setStroke(color);

        final DoubleProperty headXProperty = new SimpleDoubleProperty(0);
        DoubleProperty headYProperty = new SimpleDoubleProperty(0);
        DoubleProperty leftXProperty = new SimpleDoubleProperty(0);
        DoubleProperty leftYProperty = new SimpleDoubleProperty(0);
        DoubleProperty rightXProperty = new SimpleDoubleProperty(0);
        DoubleProperty rightYProperty = new SimpleDoubleProperty(0);

        DoubleProperty headXMod = new SimpleDoubleProperty(0);
        DoubleProperty headYMod = new SimpleDoubleProperty(0);
        DoubleProperty leftXMod = new SimpleDoubleProperty(0);
        DoubleProperty leftYMod = new SimpleDoubleProperty(0);
        DoubleProperty rightXMod = new SimpleDoubleProperty(0);
        DoubleProperty rightYMod = new SimpleDoubleProperty(0);

        DoubleProperty cos = new SimpleDoubleProperty(0);
        DoubleProperty sin = new SimpleDoubleProperty(0);

        DoubleProperty one = new SimpleDoubleProperty(1);

        // cos = |endX - startX| / sqrt((endX - startX)^2 + (endY - startY)^2)
        cos.bind(one.multiply(Math.abs(end.getShape()
                .centerXProperty().add(begin.getShape().centerXProperty().multiply(-1)).doubleValue()))
                .divide(Math.sqrt(
                        Math.pow(end.getShape()
                                .centerXProperty().add(begin.getShape()
                                .centerXProperty().multiply(-1)).doubleValue(), 2)
                        + Math.pow(end.getShape()
                                .centerYProperty().add(begin.getShape()
                                .centerYProperty().multiply(-1)).doubleValue(), 2)
                ))
        );
        // sin = sqrt(1 - cos^2)
        sin.bind(one.multiply(
                Math.sqrt(cos.multiply(cos).multiply(-1).add(1).doubleValue()))
        );

        headXMod.bind(cos.multiply(CIRCLE_RADIUS));
        headYMod.bind(sin.multiply(CIRCLE_RADIUS));

        headXProperty.bind(end.getShape().centerXProperty().add(headXMod.multiply(-1)));
        headYProperty.bind(end.getShape().centerYProperty().add(headYMod.multiply(-1)));

        // sin (90 - a - y) = cos a / 2 - sin a / 2 &&& cos (90 - a - y) = sin a / 2 + cos a / 2 =>>>
        rightXMod.bind(cos.divide(2).subtract(sin.divide(2)).multiply(ARROW_SIDE));
        rightYMod.bind(cos.divide(2).add(sin.divide(2)).multiply(ARROW_SIDE));

        rightXProperty.bind(headXProperty.subtract(rightXMod));
        rightYProperty.bind(headYProperty.subtract(rightYMod));

        //  cos (a - y) = cos a / 2 + sin a / 2 &&& sin (a - y) = sin a / 2 - cos a / 2
        leftXMod.bind(cos.divide(2).add(sin.divide(2)).multiply(ARROW_SIDE));
        leftYMod.bind(sin.divide(2).subtract(cos.divide(2)).multiply(ARROW_SIDE));

        leftXProperty.bind(headXProperty.subtract(leftXMod));
        leftYProperty.bind(headYProperty.subtract(leftYMod));



        begin.getShape().centerXProperty().addListener(change -> {
            arrow.getPoints().clear();
            arrow.getPoints().addAll(
                    headXProperty.get(), headYProperty.get(), // head
                    leftXProperty.get(), leftYProperty.get(), // left
                    rightXProperty.get(), rightYProperty.get(), // right
                    headXProperty.get(), headYProperty.get() // head
            );
        });
        begin.getShape().centerYProperty().addListener(change -> {
            arrow.getPoints().clear();
            arrow.getPoints().addAll(
                    headXProperty.get(), headYProperty.get(), // head
                    leftXProperty.get(), leftYProperty.get(), // left
                    rightXProperty.get(), rightYProperty.get(), // right
                    headXProperty.get(), headYProperty.get() // head
            );
        });
        end.getShape().centerXProperty().addListener(change -> {
            arrow.getPoints().clear();
            arrow.getPoints().addAll(
                    headXProperty.get(), headYProperty.get(), // head
                    leftXProperty.get(), leftYProperty.get(), // left
                    rightXProperty.get(), rightYProperty.get(), // right
                    headXProperty.get(), headYProperty.get() // head
            );
        });
        end.getShape().centerYProperty().addListener(change -> {
            arrow.getPoints().clear();
            arrow.getPoints().addAll(
                    headXProperty.get(), headYProperty.get(), // head
                    leftXProperty.get(), leftYProperty.get(), // left
                    rightXProperty.get(), rightYProperty.get(), // right
                    headXProperty.get(), headYProperty.get() // head
            );
        });

        arrow.getPoints().addAll(
                headXProperty.get(), headYProperty.get(), // head
                leftXProperty.get(), leftYProperty.get(), // left
                rightXProperty.get(), rightYProperty.get(), // right
                headXProperty.get(), headYProperty.get() // head
        );

        // Arrow lightning when mouse entered
        arrow.setOnMouseEntered(e -> {
            arrow.setEffect(bloom);
            line.setEffect(bloom);
            isFocused = true;
        });

        // Remove lightning when mouse exited
        arrow.setOnMouseExited(e -> {
            arrow.setEffect(null);
            line.setEffect(null);
            isFocused = false;
        });
    }
}
