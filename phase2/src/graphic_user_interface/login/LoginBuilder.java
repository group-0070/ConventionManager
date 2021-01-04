package graphic_user_interface.login;

import graphic_user_interface.ComponentBuilder;
import graphic_user_interface.ComponentType;
import graphic_user_interface.Component;
import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.cancel_events.GUICancelEventController;
import graphic_user_interface.events.GUIEventControllerType;
import graphic_user_interface.events.GUIEventController;
import graphic_user_interface.menu.GUIMenuController;
import graphic_user_interface.message.GUIMessageController;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;

public class LoginBuilder {
    private final ComponentBuilder builder = new ComponentBuilder();
    private Component login_comp = null;
    private Stage loginWindow = null;

    /**
     * Create the login component.
     * @param controllerBuilder - Model of the application.
     */
    public void buildComponent(IControllerBuilder controllerBuilder){
        login_comp = builder.createComponent(controllerBuilder, ComponentType.LOGIN.getLoc());
    }


    /**
     * Builds the login window.
     * @param primaryStage - Stage of the application.
     */
    public void buildWindow(Stage primaryStage){
        loginWindow = new Stage();
        Scene login = new Scene(login_comp.getParent());
        loginWindow.setScene(login);
        loginWindow.initOwner(primaryStage);
        loginWindow.initModality(Modality.WINDOW_MODAL);
    }

    /**
     * Hides all menu components when the login window is set.
     * @param primaryStage - Stage of the application.
     * @param primaryStage - Stage of the application.
     * @param components - Hashmap that maps the component type to the component.
     * @param menuComponent - Menu component.
     */
    public void hideComponents(Stage primaryStage, IControllerBuilder controllerBuilder,
                               HashMap<ComponentType, Component> components, Component menuComponent){
        loginWindow.setOnHidden(event -> {
            if (controllerBuilder.getUserController().getCurrentUserID() != null) {
                ((GUIMessageController) components.get(ComponentType.MESSAGE).getPresenter()).SetupForUser();
                ((GUIMenuController)menuComponent.getPresenter()).setUp();
                ((GUICancelEventController) components.get(ComponentType.CANCEL).getPresenter()).setup();
                ((GUIEventController) components.get(ComponentType.EVENT).getPresenter()).setType(GUIEventControllerType.ALL_EVENT);
                ((GUIEventController) components.get(ComponentType.MY_EVENT).getPresenter()).setType(GUIEventControllerType.MY_EVENT);
                primaryStage.show();
            }
            else {
                primaryStage.close();
            }
        });

        primaryStage.setOnHidden(event -> controllerBuilder.saveControllers());
    }

    /**
     * Returns the login window.
     * @return loginWindow - Login Window that has been built.
     */
    public Stage getLoginWindow(){
         return loginWindow;
    }
}
