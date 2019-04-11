package layout.form;

import javafx.scene.layout.VBox;
import controller.GraphController;

public class AppForm {
    private GraphPane graphPane;
    private AppMenu appMenu;
    private GraphStatusBar graphStatusBar;

    private VBox vBox;


    public AppForm(GraphController graphController) {
        graphPane = new GraphPane(graphController);
        appMenu = new AppMenu(graphPane);
        graphStatusBar = new GraphStatusBar(graphController);

        vBox = new VBox();
        configureVBox();
    }

    public VBox getVBox() {
        return vBox;
    }

    // Configs
    private void configureVBox() {
        vBox.getChildren().addAll(appMenu.getMenuBar(), graphPane.getPane(), graphStatusBar.getStatusBar());
    }
}
