package layout.form;

import controller.GraphController;

import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;


public class AppMenu {
    private GraphController graphController;

    private MenuBar menuBar;


    public AppMenu(GraphController graphController) {
        this.graphController = graphController;

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(
                createFileMenu()
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

    /*
        Event handlers
     */
}
