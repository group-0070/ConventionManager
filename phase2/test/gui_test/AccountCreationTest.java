package gui_test;

import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ControllerBuilder;
import graphic_user_interface.account_creation.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AccountCreationTest extends Application {

    private IControllerBuilder model;


    @Override
    public void init() throws Exception {
        this.model = new ControllerBuilder();
//        this.model.loadControllers();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphic_user_interface/account_creation/AccountCreationView.fxml"));
        root.setCenter(loader.load());

        GUIAccountCreationController presenter = loader.getController();

        presenter.initModel(model);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
