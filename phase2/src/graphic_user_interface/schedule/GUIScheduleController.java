package graphic_user_interface.schedule;

import event_system.EventPrompt;
import event_system.EventType;
import event_system.IEventController;
import room_system.IRoomController;
import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ComponentController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import user_system.IUserController;
import user_system.UserType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;


public class GUIScheduleController implements Initializable, ComponentController {
    private IControllerBuilder model;
    private IUserController userController;
    private IEventController eventController;
    private IRoomController roomController;
    private HashMap<String,String> roomToCap;
    private ArrayList<String> speakerIDs;
    private ArrayList<String> attendeeIDs;
    private LocalDate startDay;
    private LocalDate endDay;
    private VBox addSpeakers;
    private HBox speaker1;

    @FXML
    private ChoiceBox<String> eventTypeCB;

    @FXML
    private TextField nameField;

    @FXML
    private TextField capField;

    @FXML
    private DatePicker startDP;

    @FXML
    private ChoiceBox<String> startHourCB;

    @FXML
    private ChoiceBox<String> startMinCB;

    @FXML
    private DatePicker endDP;

    @FXML
    private ChoiceBox<String> endHourCB;

    @FXML
    private ChoiceBox<String> endMinCB;

    @FXML
    private ComboBox<String> roomCB;

    @FXML
    private VBox speakerArea;

    @FXML
    private Button addSpeakerBt;

    @FXML
    private Button newRoomBt;

    @FXML
    private HBox newRoom;

    @FXML
    private TextField newRoomN;

    @FXML
    private TextField newRoomC;

    @FXML
    private Label roomCapL;

    @FXML
    private HBox roomArea;
    @FXML
    private void clickShowRoomCreation() {
        setVisible(newRoom);
        setInvisible(roomArea);
        setInvisible(newRoomBt);
    }

    @FXML
    private void clickCancelRoomCreation() {
        setInvisible(newRoom);
        newRoomC.clear();
        newRoomN.clear();
        setVisible(roomArea);
        setVisible(newRoomBt);
    }

    @FXML
    private void clickAddSpeaker() {
        HBox speaker = userAdder(true);
        addSpeakers.getChildren().add(speaker);
    }

    @FXML
    private void clickCreateEvent() {
        try{
            String eventID = getIfNotEmpty(nameField);
            String eventType = eventTypeCB.getValue();
            EventType eventT = EventType.valueOf(eventType);
            int eventCapacity = Integer.parseInt(capField.getText());
            if (eventCapacity <=0){
                throw new NumberFormatException();
            }
            String roomID;
            if(newRoom.isVisible()){
                roomID = getIfNotEmpty(newRoomN);
                if(!addRoom(roomID,getIfNotEmpty(newRoomC))){
                    throw new Exception();
                }
            }else{
                roomID = roomCB.getValue();
            }
            LocalDateTime startTime = getTime(startDay, startHourCB, startMinCB);
            LocalDateTime endTime = getTime(endDay, endHourCB, endMinCB);
            createEvent(eventT, eventCapacity, eventID, startTime, endTime, roomID,speakerIDs,attendeeIDs);
            model.saveControllers();
            refresh();
            cleanUp();
        }catch (IOException e1){
            showAlert("Please complete the event information");
        }catch (NumberFormatException e2){
            showAlert("Invalid event capacity");
            capField.clear();
        }catch (Exception e3){
            showAlert("Fail to create room");
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cleanUp();
        setTypeBox();
        setTimeCB(startHourCB,24,1);
        setTimeCB(startMinCB,60,5);
        setTimeCB(endHourCB,24,1);
        setTimeCB(endMinCB,60,5);
        startDP.valueProperty().addListener((observableValue, localDate, t1) -> startDay = t1);
        endDP.valueProperty().addListener((observableValue, localDate, t1) -> endDay = t1);
        roomCB.valueProperty().addListener((observableValue, t, t1) -> roomCapL.setText(roomToCap.get(t1)));
        roomToCap = new HashMap<>();
        roomCapL.setText("0");
    }

    private void setTypeBox(){
        for(EventType type:EventType.values()){
            eventTypeCB.getItems().addAll(type.toString());
        }
    }
    private void setTimeCB(ChoiceBox<String> time, int limit, int step){
        for(int i= 0;i <limit; i += step){
            String t = String.valueOf(i);
            if(String.valueOf(i).length() == 1){
                t = "0" + i;
            }
            time.getItems().add(t);
        }
        time.getSelectionModel().select(0);

    }

    private HBox userAdder(boolean deletable){
        HBox h =new HBox();
        h.setAlignment(Pos.BOTTOM_LEFT);
        VBox u = new VBox();
        Label n = new Label("Speaker" + ":");
        ComboBox<String> comboBox = new ComboBox<>();
        for(String speakerID: userController.getListOfIDsByType(UserType.SPEAKER) ){
            comboBox.getItems().add(speakerID);
            }
        comboBox.setOnMouseClicked(event -> {
            comboBox.getItems().clear();
            for(String speakerID: userController.getListOfIDsByType(UserType.SPEAKER) ){
                comboBox.getItems().add(speakerID);
            }
        });
        comboBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
            speakerIDs.remove(oldValue);
            speakerIDs.add(newValue);
            }));

        u.getChildren().addAll(n,comboBox);
        h.getChildren().add(u);
        if(deletable){
            Button cancel = new Button("Delete the "+ "Speaker");
            h.getChildren().add(cancel);
            cancel.setOnAction(event1 -> addSpeakers.getChildren().remove(h));


        }
        return h;
    }


    private void setInvisible(Node node){
        node.setVisible(false);
        node.setManaged(false);
    }

    private void setVisible(Node node){
        node.setVisible(true);
        node.setManaged(true);
    }
    /**
     * Initializes the the model within this presenter
     * @param m IModel              given model with all information to the backend
     * @throws IllegalStateException    When there already exists a model within the presenter
     */
    public void initModel(IControllerBuilder m) {
        if (this.model != null) {
            throw new IllegalStateException("Model already initialized");
        }
        model = m;
        userController = model.getUserController();
        eventController = model.getEventController();
        roomController = model.getRoomController();
        speaker1 = userAdder(false);
        speakerArea.getChildren().add(speaker1);
        addSpeakers = new VBox();
        speakerArea.getChildren().add(addSpeakers);
        HBox speaker2 = userAdder(false);
        addSpeakers.getChildren().add(speaker2);
        setInvisible(addSpeakerBt);
        setInvisible(addSpeakers);
        eventTypeCB.getSelectionModel().select(1);
        eventTypeCB.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            if(t1.equals(EventType.NO_SPEAKER_EVENT.toString())){
                setInvisible(speakerArea);
                setInvisible(addSpeakerBt);
            }else if(t1.equals(EventType.MULTI_SPEAKER_EVENT.toString())){
                setVisible(addSpeakers);
                setVisible(speakerArea);
                setVisible(addSpeakerBt);
            }else {
                setVisible(speakerArea);
                setInvisible(addSpeakerBt);
                setInvisible(addSpeakers);
            }
        });
        refresh();
    }

    private void refresh(){
        roomCB.getItems().clear();
        for(List<String> id: roomController.getRoomInfo()){
            roomCB.getItems().add(id.get(0));
            roomToCap.put(id.get(0),id.get(1));
        }
        speaker1 = userAdder(false);
    }

    private void showAlert(String al){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(al);
        alert.showAndWait();
    }

    private void createEvent(EventType eventType, int eventCapacity, String eventID, LocalDateTime startTime,
                             LocalDateTime endTime, String roomID, ArrayList<String> speakerIDs,
                             ArrayList<String> attendeeIDs){
        EventPrompt e = eventController.addEvent(eventType,eventCapacity,eventID,startTime,endTime,roomID,speakerIDs,attendeeIDs);
        switch (e){
            case DOUBLE_BOOK_ROOM:
                showAlert("This room has already been booked for this time.");
                break;
            case EVENT_ALREADY_EXIST:
                showAlert("An event with this name already exists.");
                break;
            case ROOM_DNE:
                showAlert("The room does not exist");
                break;
            case ATTENDEE_OVERLOAD:
                showAlert("The number of speakers and attendees exceeds the room size");
                break;
            case INVALID_EVENT_CAPACITY:
                showAlert("Invalid event capacity");
                break;
            case INVALID_TIME_SELECTION:
                showAlert("Invalid time selection");
                break;
            case SPEAKER_DNE:
                showAlert("Speakers do not exist.");
                break;
            case DOUBLE_BOOK_SPEAKER:
                showAlert("The speaker(s) have already been booked for another event at this time");
                break;
            case ATTENDEE_DNE:
                showAlert("Attendees do not exist.");
                break;
            case EXCEEDS_ROOM_CAPACITY:
                showAlert("The number of attendees is exceeding the room capacity.");
                break;
            case NUM_SPEAKERS_MISMATCH:
                showAlert("The number of speakers does not correspond to the event type.");
                break;
            case SAME_SPEAKER_ADDED:
                showAlert("The same speaker(s) is being added more than once. Select distinct speakers.");
            case EVENT_ADDED:
                showAlert("Added event successfully!");
                break;
            default:
                showAlert("Failed to create event.");
                break;
        }
    }

    private String getIfNotEmpty(TextField textField) throws IOException {
        String text = textField.getText();
        if(text == null || text.equals("")){
            throw new IOException();
        }
        return text;
    }

    private LocalDateTime getTime(LocalDate date, ChoiceBox<String> hour, ChoiceBox<String> min){
        String hm = hour.getValue() + ":"+ min.getValue();
        LocalTime hourMin = LocalTime.parse( hm );
        return LocalDateTime.of(date,hourMin);
    }

    private boolean addRoom(String name, String cap){
        try{
            int num = Integer.parseInt(cap);
            EventPrompt eventPrompt = model.getRoomController().addRoom(name,num);
            switch (eventPrompt){
                case ROOM_ALREADY_EXISTS:
                    showAlert("Room already exists");
                    return false;
                case INVALID_ROOM_CAPACITY:
                    showAlert("Invalid room capacity");
                    return false;
                case ROOM_ADDED:
                    showAlert("Room added");
                    return true;
                default:
                    showAlert("Fail to create room");
                    return false;
            }
        }catch (NumberFormatException e){
            showAlert("Invalid room capacity");
            newRoomC.clear();
            return false;
        }
    }

    private void cleanUp(){
        attendeeIDs = new ArrayList<>();
        speakerIDs = new ArrayList<>();
        startDay = LocalDate.now();
        endDay = LocalDate.now();
        startDP.setValue(LocalDate.now());
        endDP.setValue(LocalDate.now());
        setInvisible(newRoom);
        setVisible(roomArea);
        setVisible(newRoomBt);
        startHourCB.getSelectionModel().select(0);
        startMinCB.getSelectionModel().select(0);
        endHourCB.getSelectionModel().select(0);
        endMinCB.getSelectionModel().select(0);
        roomCB.getSelectionModel().clearSelection();
        nameField.clear();
        capField.clear();
        roomCB.getSelectionModel().clearSelection();
    }

}
