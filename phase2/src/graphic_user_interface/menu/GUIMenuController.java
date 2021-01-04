package graphic_user_interface.menu;

import event_system.EventIndex;
import graphic_user_interface.ComponentType;
import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ComponentController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GUIMenuController implements Initializable, ComponentController {

    @FXML
    private Label nameL;

    @FXML
    private Button eventBt;

    @FXML
    private Button myBt;

    @FXML
    private Button createEventBt;

    @FXML
    private Button cancelEventBt;

    @FXML
    private Button createUserBt;

    @FXML
    private AnchorPane homePane;

    @FXML
    private ScrollPane eventPane;

    @FXML
    private ScrollPane myPane;

    @FXML
    private ScrollPane createEventPane;

    @FXML
    private ScrollPane cancelEventPane;

    @FXML
    private ScrollPane createUserPane;

    @FXML
    private ScrollPane messagePane;

    @FXML
    private VBox mEventCapPane;

    @FXML
    private Button mEveCapBt;

    @FXML
    private ComboBox<String> mEventIdF;

    @FXML
    private Label mRoomIdF;

    @FXML
    private TextField mCapF;

    @FXML
    private Label roomCapL;

    @FXML
    private void clickToModify(){
        try {
            String eventId = mEventIdF.getValue();
            String roomId = mRoomIdF.getText();
            int cap = Integer.parseInt(mCapF.getText());
            if(cap<=0){
                throw new NumberFormatException();
            }
            if(model.getEventController().modifyEventCapacity(eventId,roomId,cap)){
                showAlert("Modified successfully!");
            }else {
                showAlert("Failed to modify.");
            }
            model.saveControllers();
        }catch (NumberFormatException e){
            showAlert("Invalid event capacity");
        }catch (NullPointerException e){
            showAlert("Please complete the event information.");
        }
    }

    @FXML
    private void clickM(){
        mEventCapPane.toFront();
        mEventIdF.getItems().clear();
        for(List<List<String>> events: model.getEventController().getListEvents()){
            mEventIdF.getItems().add(events.get(EventIndex.EVENT_ID.getValue()).get(0));
        }

    }

    @FXML
    private void cancelEventClick() {
        cancelEventPane.toFront();
    }

    @FXML
    private void clickEvent() {
        eventPane.toFront();
    }

    @FXML
    private void clickHome() {
        homePane.toFront();
    }

    @FXML
    private void createUserClick() {
        createUserPane.toFront();
    }

    @FXML
    private void createEventClick() {
        createEventPane.toFront();
    }

    @FXML
    private void myClick() {
        myPane.toFront();
    }

    @FXML
    private void messageClick() {
        messagePane.toFront();
    }
    @FXML
    private void outClick() {
        model.saveControllers();
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homePane.toFront();
    }

    IControllerBuilder model;

    /**
     * Initializes the the model within this presenter
     * @param model IModel              given model with all information to the backend
     * @throws IllegalStateException    When there already exists a model within the presenter
     */
    public void initModel(IControllerBuilder model) {
        if (this.model != null) {
            throw new IllegalStateException("Model already initialized");
        }
        this.model = model;
        mEventIdF.valueProperty().addListener(((observable, oldValue, newValue) -> {
            for(List<List<String>> events: model.getEventController().getListEvents()){
                if(events.get(EventIndex.EVENT_ID.getValue()).get(0).equals(newValue)){
                    mRoomIdF.setText(events.get(EventIndex.ROOM_ID.getValue()).get(0));
                }
            }
            for(List<String> room: model.getRoomController().getRoomInfo()){
                if(mRoomIdF.getText().equals(room.get(0))){
                    roomCapL.setText(room.get(1));
                }
            }
        }));
    }

    /**
     * Set up the view to show according to user type
     */
    public void setUp(){
        switch (model.getUserController().getCurrentUserType()){
            case ORGANIZER:
                setInvisible(myBt);
                break;
            case SPEAKER:
                setInvisible(cancelEventBt);
                setInvisible(createEventBt);
                setInvisible(createUserBt);
                setInvisible(eventBt);
                setInvisible(mEveCapBt);
                break;
            case ATTENDEE:
                setInvisible(cancelEventBt);
                setInvisible(createEventBt);
                setInvisible(createUserBt);
                setInvisible(mEveCapBt);
                break;
            case ADMIN:
                setInvisible(createEventBt);
                setInvisible(createUserBt);
                setInvisible(myBt);
                setInvisible(mEveCapBt);
        }
        nameL.setText(model.getUserController().getCurrentUserID());
    }

    /**
     * Add the pane to menu
     * @param node the node to add to pane
     * @param cType the type of node
     */
    public void addPane(Node node, ComponentType cType){
        switch (cType) {
            case EVENT:
                eventPane.setContent(node);
            case MY_EVENT:
                myPane.setContent(node);
            case SCHEDULE:
                createEventPane.setContent(node);
            case MESSAGE:
                messagePane.setContent(node);
            case CANCEL:
                cancelEventPane.setContent(node);
            case CREATION:
                createUserPane.setContent(node);
        }
    }

    private void setInvisible(Node node){
        node.setVisible(false);
        node.setManaged(false);
    }

    private void showAlert(String al){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(al);
        alert.showAndWait();
    }
}
