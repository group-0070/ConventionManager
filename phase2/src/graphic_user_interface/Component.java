package graphic_user_interface;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class Component {
    private FXMLLoader loader;
    private Parent parent;

    /**
     * Loads the FXML for the GUIComponent
     * @param loader    FXMLLoader  loader with the file location information
     */
    public void load(FXMLLoader loader) {
        this.loader = loader;
        try {
            this.parent = loader.load();
        } catch (IOException e) {
            System.out.println("Couldn't load fxml");
        }
    }

    /**
     * Getter for the presenter of a GUIComponent
     * @return  Presenter   The presenter of the component upcasted to the general Presenter interface
     */
    public ComponentController getPresenter() {
        return this.loader.getController();
    }

    /**
     * Getter for the parent node for this GUIComponent
     * @return  Parent  Base node for this GUIComponent upcasted to Parent
     */
    public Parent getParent() {
        return this.parent;
    }
}
