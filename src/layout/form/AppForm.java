package layout.form;

import javafx.scene.layout.VBox;
import sample.GraphController;

public class AppForm {
    private AppMenu appMenu;
    private GraphGroup graphGroup;
    private GraphToolBar graphToolBar;

    private VBox vBox;


    public AppForm(GraphController graphController) {
        appMenu = new AppMenu();
        graphGroup = new GraphGroup(graphController);
        graphToolBar = new GraphToolBar();

        vBox = new VBox();
        configureVBox();
    }

    public VBox getVBox() {
        return vBox;
    }

    // Configs
    private void configureVBox() {
        vBox.getChildren().addAll(appMenu.getMenuBar(), graphGroup.getGroup(), graphToolBar.getToolBar());
    }
}
