package gui_test;

import graphic_user_interface.ComponentType;
import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ControllerBuilder;
import graphic_user_interface.account_creation.GUIAccountCreationController;
import graphic_user_interface.cancel_events.GUICancelEventController;
import graphic_user_interface.events.GUIEventControllerType;
import graphic_user_interface.events.GUIEventController;
import graphic_user_interface.menu.GUIMenuController;
import graphic_user_interface.schedule.GUIScheduleController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class MenuTest extends Application {
    private IControllerBuilder model;
    @Override
    public void init(){
        this.model = new ControllerBuilder();
        this.model.loadControllers();
//        model.getUserController().loginUser("dbTestUser2", "12345test"); //admin
        model.getUserController().loginUser("att1", "pass1"); //attendee
//        model.getUserController().loginUser("org1", "pass3"); //organizer
//        model.getUserController().loginUser("spk1", "pass4"); //speaker

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loaderS = new FXMLLoader(getClass().getResource("/graphic_user_interface/schedule/ScheduleView.fxml"));
        GUIScheduleController GUIScheduleController = (GUIScheduleController) loaderS.getController();
//        schedulePresenter.initModel(model);
        Node s = loaderS.load();

        FXMLLoader loaderMYEvents = new FXMLLoader(getClass().getResource("/graphic_user_interface/events/EventsView.fxml"));
        Node nodeAddMYEvents = loaderMYEvents.load();
        GUIEventController eventsMYPresenter = loaderMYEvents.getController();
        eventsMYPresenter.initModel(model);
        eventsMYPresenter.setType(GUIEventControllerType.MY_EVENT);

        FXMLLoader loaderEvents = new FXMLLoader(getClass().getResource("/graphic_user_interface/events/EventsView.fxml"));
        Node nodeAddEvents = loaderEvents.load();
        GUIEventController eventsPresenter = loaderEvents.getController();
        eventsPresenter.initModel(model);
        eventsPresenter.setType(GUIEventControllerType.ALL_EVENT);

        FXMLLoader loaderC = new FXMLLoader(getClass().getResource("/graphic_user_interface/cancel_events/CancelEventsView.fxml"));
        Node nodeCancel = loaderC.load();
        GUICancelEventController cancelEventsPresenter = (GUICancelEventController) loaderC.getController();
        cancelEventsPresenter.initModel(model);
        cancelEventsPresenter.setup();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphic_user_interface/menu/MenuView.fxml"));
        SplitPane root = loader.load();
        GUIMenuController menuPresenter = loader.getController();
        menuPresenter.initModel(model);

        FXMLLoader loaderU = new FXMLLoader(getClass().getResource("/graphic_user_interface/account_creation/AccountCreationView.fxml"));
        GUIAccountCreationController accountCreationPresenter = (GUIAccountCreationController) loaderU.getController();
        //accountCreationPresenter.initModel(model);
        Node u = loaderU.load();

        FXMLLoader loaderM = new FXMLLoader(getClass().getResource("/graphic_user_interface/message/MessageView.fxml"));
        //MessagePresenter presenter = loader.getController();
        //presenter.initModel(model);
        //presenter.SetupForUser();
        Node m = loaderM.load();
        menuPresenter.addPane(nodeAddEvents, ComponentType.EVENT);
        menuPresenter.addPane(nodeAddMYEvents, ComponentType.MY_EVENT);
        menuPresenter.addPane(s, ComponentType.SCHEDULE);
        menuPresenter.addPane(m, ComponentType.MESSAGE);
        menuPresenter.addPane(nodeCancel, ComponentType.CANCEL);
        menuPresenter.addPane(u, ComponentType.CREATION);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
