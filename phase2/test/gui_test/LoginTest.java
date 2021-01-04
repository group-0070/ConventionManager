package gui_test;

import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ControllerBuilder;
import graphic_user_interface.login.GUILoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        IControllerBuilder m = new ControllerBuilder();
//        m.loadControllers();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphic_user_interface/login/LoginView.fxml"));
        Parent root = loader.load();
        GUILoginController presenter = loader.getController();
        presenter.initModel(m);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
