package graphic_user_interface.events;

import event_system.EventIndex;
import event_system.EventPrompt;
import event_system.IEventController;
import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ComponentController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import user_system.UserType;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GUIEventController implements Initializable, ComponentController {
    private IControllerBuilder model;
    private GUIEventControllerType type;
    private IEventController eventController;

    @FXML
    private ScrollPane scPane;

    @FXML
    private VBox eventArea;

    @FXML
    private Label typeL;

    @FXML
    private Label numL;

    @FXML
    private Label timeL;

    @FXML
    private Label locationL;

    @FXML
    private Label speakerL;

    @FXML
    private Label remainingL;

    @FXML
    private void updateClick() {
        refresh();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        numL.setWrapText(true);
        remainingL.setWrapText(true);
    }

    private Label setUpLabel(int width, String text){
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setWrapText(true);
        label.setFont(Font.font(10));
        label.setAlignment(Pos.TOP_CENTER);
        return label;
    }

    private String listToString(List<String> target){
        StringBuilder res = new StringBuilder();
        for (String item: target){
            res.append(item).append(", ");
        }
        if (res.length() > 2){
            res.deleteCharAt(res.length() - 1);
            res.deleteCharAt(res.length() - 1);
            return res.toString();
        } else {
            return "N/A";
        }
    }

    private void addEvent(List<List<String>> eventInfo){
        Label eventType = setUpLabel(100, eventInfo.get(EventIndex.EVENT_TYPE.getValue()).get(0));
        Label eventCap = setUpLabel(60,eventInfo.get(EventIndex.EVENT_CAPACITY.getValue()).get(0));
        Label eventName = setUpLabel(100, eventInfo.get(EventIndex.EVENT_ID.getValue()).get(0));
        Label eventTime = setUpLabel(120, eventInfo.get(EventIndex.START_TIME.getValue()).get(0) +" - "+
                                        eventInfo.get(EventIndex.END_TIME.getValue()).get(0));
        Label eventLocation = setUpLabel(100, eventInfo.get(EventIndex.ROOM_ID.getValue()).get(0));
        Label eventSpeaker = setUpLabel(100, listToString(eventInfo.get(EventIndex.SPEAKER_IDS.getValue())));

        int remaining = Integer.parseInt(eventInfo.get(EventIndex.EVENT_CAPACITY.getValue()).get(0)) -
                eventInfo.get(EventIndex.SPEAKER_IDS.getValue()).size() - eventInfo.get(EventIndex.ATTENDEE_IDS.getValue()).size();
        if(eventInfo.get(EventIndex.SPEAKER_IDS.getValue()).contains("")){
            remaining += 1;
        }
        if(eventInfo.get(EventIndex.ATTENDEE_IDS.getValue()).contains("")){
            remaining += 1;
        }
        Label spotsRemaining = setUpLabel(100,Integer.toString(remaining));
        HBox newEvent = new HBox();
        newEvent.setPrefHeight(60);
        newEvent.setMinHeight(60);
        newEvent.setSpacing(5);
        newEvent.setAlignment(Pos.CENTER_LEFT);
        newEvent.getChildren().addAll(eventType,eventCap, eventName,eventTime,eventLocation,eventSpeaker,spotsRemaining);
        eventArea.getChildren().add(newEvent);
        if(model.getUserController().getCurrentUserType().equals(UserType.ATTENDEE)){
            Button button = new Button();
            button.setFont(Font.font(10));
            button.setMinWidth(55);
            newEvent.getChildren().add(button);
                button.setText("Sign up");
                button.setOnAction(event -> {
                    signUp(eventInfo.get(2).get(0));
                    model.saveControllers();
                    refresh();
            });
        }
    }

    private void addMyEvent(String name){
        Label eventName = setUpLabel(80, name);
        HBox newEvent = new HBox();
        newEvent.setPrefHeight(50);
        newEvent.setSpacing(5);
        newEvent.setAlignment(Pos.CENTER_LEFT);
        newEvent.getChildren().add(eventName);
        eventArea.getChildren().add(newEvent);
        if (model.getUserController().getCurrentUserType().toString().equals("ATTENDEE")){
            Button button = new Button("Cancel");
            button.setOnAction(event -> {
                cancelSignUp(model.getUserController().getCurrentUserID(), name);
                eventArea.getChildren().remove(newEvent);
                model.saveControllers();
                refresh(); });
            newEvent.getChildren().add(button);
        }
    }

    /**
     * Set the type of event page want to show
     * @param type the name of the GUIEventControllerType
     */
    public void setType(GUIEventControllerType type){
        this.type = type;
        switch (type){
            case MY_EVENT:
                setInvisible(typeL);
                setInvisible(timeL);
                setInvisible(locationL);
                setInvisible(numL);
                setInvisible(speakerL);
                setInvisible(remainingL);
                for(String eventID :eventController.getEventsForUser()){
                    addMyEvent(eventID);
                }
            break;
            case ALL_EVENT:
                for(List<List<String>> x :eventController.getListEvents()){
                    addEvent(x);
                }
                break;
        }
    }

    private void refresh(){
        eventArea.getChildren().clear();
        switch (type){
            case MY_EVENT:
                for(String eventID :eventController.getEventsForUser()){
                    addMyEvent(eventID);
                }
                break;
            case ALL_EVENT:
                for(List<List<String>> x : eventController.getListEvents()){
                   addEvent(x);
                }
                break;
        }
    }

    /**
     * Initializes the the model within this presenter
     * @param model IModel              given model with all information to the backend
     * @throws IllegalStateException    When there already exists a model within the presenter
     */
    public void initModel(IControllerBuilder model) {
        this.model = model;
        this.eventController = model.getEventController();

    }

    private void cancelSignUp(String user, String event){
        EventPrompt e = eventController.cancelSignUp(user,event);
        switch (e) {
            case EVENT_DNE:
                showAlert("Event does not exist");
                break;
            case ATTENDEE_DNE:
                showAlert("Attendee does not exist");
                break;
            case ATTENDEE_NOT_IN_EVENT:
                showAlert("Not in the event");
                break;
            case CANCEL_SUCCESS:
                showAlert("Cancel successfully");
                break;
            default:
                showAlert("Failed to cancel");
        }
    }

    private void showAlert(String al){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(al);
        alert.showAndWait();
    }

    private void setInvisible(Node node){
        node.setVisible(false);
        node.setManaged(false);
    }

    private void signUp(String eventID){
        EventPrompt e = model.getEventController().signUp(eventID);
        switch (e) {
            case USER_DOUBLE_SIGNUP:
                showAlert("Already in the event.");
                break;
            case EVENT_FULL:
                showAlert("The event is full.");
                break;
            case EVENT_DNE:
                showAlert("Event does not exist");
                break;
            case ATTENDEE_DNE:
                showAlert("Attendee does not exist");
                break;
            case SIGNUP_SUCCESS:
                showAlert("Signup successfully");
                break;
            default:
                showAlert("Failed to signup");
        }
    }
}
