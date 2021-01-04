package gui_test;

import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ControllerBuilder;
import graphic_user_interface.message.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MessageTest extends Application {

    private IControllerBuilder model;


    @Override
    public void init() throws Exception {
        this.model = new ControllerBuilder();
        this.model.loadControllers();
//        model.getUserController().loginUser("dbTestUser2", "12345test"); //admin
//        model.getUserController().loginUser("att1", "pass1"); //attendee
//        model.getUserController().loginUser("org1", "pass3"); //organizer
//        model.getUserController().loginUser("spk1", "pass4"); //speaker
        model.getUserController().loginUser("sMinfan", "pass8"); //speaker
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphic_user_interface/message/MessageView.fxml"));
        root.setCenter(loader.load());

        GUIMessageController presenter = loader.getController();

        presenter.initModel(model);
        presenter.SetupForUser();

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
