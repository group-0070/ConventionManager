package graphic_user_interface;

import javafx.fxml.FXMLLoader;

public class ComponentBuilder {

    /**
     * Creates and returns the GUIComponent with the model and corresponding to the given location
     * @param model     IControllerBuilder  model that connects the GUI to the backend
     * @param location  String              file location of the FXML for the component
     * @return          GUIComponent        the GUIComponent corresponding to the given location
     */
    public Component createComponent(IControllerBuilder model, String location) {
        Component comp = new Component();
        comp.load(new FXMLLoader(getClass().getResource(location)));
        comp.getPresenter().initModel(model);

        return comp;
    }
}
