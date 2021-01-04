package gui_test;

import graphic_user_interface.ControllerBuilder;
import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.schedule.GUIScheduleController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScheduleTest extends Application {
    private IControllerBuilder model;
    @Override
    public void init(){
        this.model = new ControllerBuilder();
        this.model.loadControllers();
        model.getUserController().loginUser("org1", "pass3"); //organizer
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphic_user_interface/schedule/ScheduleView.fxml"));
        Parent root = loader.load();
        GUIScheduleController GUIScheduleController = loader.getController();
        GUIScheduleController.initModel(model);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
}
