package gui_test;

import graphic_user_interface.ControllerBuilder;
import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.events.GUIEventControllerType;
import graphic_user_interface.events.GUIEventController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EventsTest extends Application {
    private IControllerBuilder model;
    @Override
    public void init(){
        this.model = new ControllerBuilder();
        this.model.loadControllers();
//        model.getUserController().loginUser("dbTestUser2", "12345test"); //admin
        model.getUserController().loginUser("att1", "pass1"); //attendee
//        model.getUserController().loginUser("org1", "pass3"); //organizer
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphic_user_interface/events/EventsView.fxml"));
        Parent root = loader.load();
        GUIEventController p = loader.getController();
        p.initModel(model);
        p.setType(GUIEventControllerType.ALL_EVENT);//Event page
//        p.setType(GUIEventControllerType.MY_EVENT); //My event page
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
