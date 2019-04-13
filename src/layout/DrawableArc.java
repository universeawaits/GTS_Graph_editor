package layout;

import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import model.Arc;

import java.util.Objects;
import java.util.Random;

import static layout.DrawableNode.CIRCLE_RADIUS;


public class DrawableArc {
    private static final Bloom BLOOM = new Bloom();
    private static final int LINE_WIDTH = 3;
    private static final int ARROW_SIDE = 2 * CIRCLE_RADIUS;
    private static final double SIN_Y = 0.3;

    private Arc sourceArc;
    private DrawableNode begin;
    private DrawableNode end;

    private boolean isFocused;

    private Polygon arrow;
    private Line line;
    private Color color;

    // Properties for arrow correct rotating and locating
    double headX;
    double headY;
    double leftX;
    double leftY;
    double rightX;
    double rightY;

    double headXMod;
    double headYMod;
    double leftXMod;
    double leftYMod;
    double rightXMod;
    double rightYMod;

    double cos;
    double sin;


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
            line.setEffect(BLOOM);
            arrow.setEffect(BLOOM);
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

        updateArrowShape();

        begin.getShape().centerXProperty().addListener(change -> {
            updateArrowShape();
        });
        begin.getShape().centerYProperty().addListener(change -> {
            updateArrowShape();
        });
        end.getShape().centerXProperty().addListener(change -> {
            updateArrowShape();
        });
        end.getShape().centerYProperty().addListener(change -> {
            updateArrowShape();
        });

        // Arrow lightning when mouse entered
        arrow.setOnMouseEntered(e -> {
            arrow.setEffect(BLOOM);
            line.setEffect(BLOOM);
            isFocused = true;
        });

        // Remove lightning when mouse exited
        arrow.setOnMouseExited(e -> {
            arrow.setEffect(null);
            line.setEffect(null);
            isFocused = false;
        });
    }

    // Updates arrow coordinates if incident node was moved
    private void updateArrowTransform() {
        // cos = |endX - startX| / sqrt((endX - startX)^2 + (endY - startY)^2)
        cos = Math.abs(end.getShape().getCenterX() - begin.getShape().getCenterX())
                / Math.sqrt(Math.pow(end.getShape().getCenterX() - begin.getShape().getCenterX(), 2)
                    + Math.pow(end.getShape().getCenterY() - begin.getShape().getCenterY(), 2));

        // sin = sqrt(1 - cos^2)
        sin = Math.sqrt(1 - Math.pow(cos, 2));


        headXMod = CIRCLE_RADIUS * cos;
        headYMod = CIRCLE_RADIUS * sin;

        headX = end.getShape().getCenterX() > begin.getShape().getCenterX() ?
                end.getShape().getCenterX() - headXMod
                : end.getShape().getCenterX() + headXMod;

        headY = end.getShape().getCenterY() > begin.getShape().getCenterY() ?
                end.getShape().getCenterY() - headYMod
                : end.getShape().getCenterY() + headYMod;

        // sin (90 - a - y) = cos a / 2 - sin a / 2 &&& cos (90 - a - y) = sin a / 2 + cos a / 2 =>>>
        rightXMod = ARROW_SIDE * (cos * SIN_Y - sin * SIN_Y);
        rightYMod = ARROW_SIDE * (cos * SIN_Y + sin * SIN_Y);

        rightX = end.getShape().getCenterX() > begin.getShape().getCenterX() ?
                headX - rightXMod
                : headX + rightXMod;
        rightY = end.getShape().getCenterY() > begin.getShape().getCenterY() ?
                headY - rightYMod
                : headY + rightYMod;

        //  cos (a - y) = cos a / 2 + sin a / 2 &&& sin (a - y) = sin a / 2 - cos a / 2
        leftXMod = ARROW_SIDE * (cos * SIN_Y + sin * SIN_Y);
        leftYMod = ARROW_SIDE * (sin * SIN_Y - cos * SIN_Y);

        leftX = end.getShape().getCenterX() > begin.getShape().getCenterX() ?
                headX - leftXMod
                : headX + leftXMod;
        leftY = end.getShape().getCenterY() > begin.getShape().getCenterY() ?
                headY - leftYMod
                : headY + leftYMod;
    }

    // Redraws arrow polygon
    private void updateArrowShape() {
        updateArrowTransform();

        arrow.getPoints().clear();
        arrow.getPoints().addAll(
                headX, headY,
                leftX, leftY,
                rightX, rightY,
                headX, headY
        );
    }
}
