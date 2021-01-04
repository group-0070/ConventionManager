import graphic_user_interface.*;
import graphic_user_interface.login.LoginBuilder;
import graphic_user_interface.menu.MenuBuilder;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.HashMap;

// GUI design based on: https://github.com/james-d/SimpleMVP

public class Main extends Application {

    private IControllerBuilder controllerBuilder;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Convention Manager");

        MenuBuilder menuBuilder = new MenuBuilder();
        LoginBuilder loginBuilder = new LoginBuilder();

        loginBuilder.buildComponent(controllerBuilder);
        loginBuilder.buildWindow(primaryStage);
        Stage loginWindow = loginBuilder.getLoginWindow();

        menuBuilder.buildComponents(controllerBuilder);
        menuBuilder.buildPanes();
        menuBuilder.buildScene();

        primaryStage.setScene(menuBuilder.getScene());
        loginWindow.show();
        HashMap<ComponentType, Component> components = menuBuilder.getComponentsMap();
        Component menu_comp = menuBuilder.getMenuComponent();
        loginBuilder.hideComponents(primaryStage, controllerBuilder, components, menu_comp);
    }

    @Override
    public void init() throws Exception {
        controllerBuilder = new ControllerBuilder();
        try {
            controllerBuilder.loadControllers();
        } catch (Exception e) {
            System.out.print("Failed to load controllers");
            throw new Exception(e);
        }
    }

    public static void main(String[] args) {
        Main.launch();
    }
}
