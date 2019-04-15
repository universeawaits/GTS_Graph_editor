package layout.form;

import controller.GraphController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.Node;

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;


public class AppMenu {
    private GraphController graphController;

    private MenuBar menuBar;


    public AppMenu(GraphController graphController) {
        this.graphController = graphController;

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(
                createFileMenu(),
                createStatisticsMenu()
        );
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }


    /*
        Menus creating
     */

    // Creating of a file menu
    private Menu createFileMenu() {
        Menu file = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem closeFile = new MenuItem("Close");

        file.getItems().addAll(newFile, openFile, saveFile, closeFile);

        return file;
    }

    // Creating of a statistics menu
    private Menu createStatisticsMenu() {
        Menu statistics = new Menu("Statistics");
        MenuItem nodesDegrees = new MenuItem("Node degrees");
        MenuItem centers = new MenuItem("Centers");

        nodesDegrees.setOnAction(getNodeDegreeEventHandler);
        centers.setOnAction(getCentersEventHandler);

        statistics.getItems().addAll(nodesDegrees, centers);

        return statistics;
    }

    /*
        Others
     */

    private Alert createEmptyDialog(javafx.scene.Node content, String title) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);

        alert.getDialogPane().setContent(content);

        return alert;
    }

    /*
        Event handlers
     */

    // Taking all graph nodes degrees
    private EventHandler<ActionEvent> getNodeDegreeEventHandler = e -> {
           ObservableList<String> nodesDegrees = FXCollections.observableArrayList();

            for (Node node : graphController.getNodes()) {
                nodesDegrees.add("[" + node.getIdentifier() + "] " + node.getName() + ": " + graphController.degreeOf(node));
            }

            ListView<String> listView = new ListView<>();
            listView.getItems().addAll(nodesDegrees);
            listView.setPrefSize(MAIN_FORM_WIDTH / 8,MAIN_FORM_HEIGHT / 7);
            listView.setEditable(false);

            Alert nodeDegreeDialog = createEmptyDialog(listView, "Nodes' degrees");
            nodeDegreeDialog.getButtonTypes().add(ButtonType.OK);
            nodeDegreeDialog.show();
    };

    // Taking all graph nodes degrees
    private EventHandler<ActionEvent> getCentersEventHandler = e -> {
        ObservableList<String> graphCenters = FXCollections.observableArrayList();

        for (Node node : graphController.centers()) {
            graphCenters.add("[" + node.getIdentifier() + "] " + node.getName());
        }

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(graphCenters);
        listView.setPrefSize(MAIN_FORM_WIDTH / 10,MAIN_FORM_HEIGHT / 8);
        listView.setEditable(false);

        Alert centersDialog = createEmptyDialog(listView, "Centers");
        centersDialog.getButtonTypes().add(ButtonType.OK);
        centersDialog.show();
    };
}
