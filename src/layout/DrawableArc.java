package layout;

import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.Arc;

import java.util.Random;


public class DrawableArc {
    private Arc sourceArc;

    private DrawableNode begin;
    private DrawableNode end;

    private Line shape;
    private Color color;


    public DrawableArc(Arc sourceArc, DrawableNode begin, DrawableNode end) {
        this.sourceArc = sourceArc;
        this.begin = begin;
        this.end = end;

        Random randomColorComponent = new Random(System.currentTimeMillis());

        color = Color.color(
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble(),
                randomColorComponent.nextDouble()
        );

        shape = new Line();
    }

    /*
        Configs
     */

    // Shape configs: events handling, coloring
    private void configureShape() {
        shape.setFill(color);

        shape.setOnMouseEntered(e -> {
            // Node lightning when mouse entered
            shape.setEffect(new Bloom());
        });

        // Remove lightning when mouse exited
        shape.setOnMouseExited(e -> {
            shape.setEffect(null);
        });


    }
}
