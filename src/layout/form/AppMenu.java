package layout.form;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class AppMenu {
    private MenuBar menuBar;


    public AppMenu() {
        menuBar = new MenuBar();
        menuBar.getMenus().add(createFileMenu());
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }


    // Inits and configs
    private Menu createFileMenu() {
        Menu file = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem closeFile = new MenuItem("Close");

        file.getItems().addAll(newFile, openFile, saveFile, closeFile);

        return file;
    }
}
