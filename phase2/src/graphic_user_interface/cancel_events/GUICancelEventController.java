package graphic_user_interface.cancel_events;

import event_system.EventIndex;
import event_system.EventType;
import event_system.IEventController;
import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ComponentController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GUICancelEventController implements Initializable, ComponentController {
    IControllerBuilder model;
    IEventController eventController;

    @FXML
    private Button deleteAllBt;

    @FXML
    private ComboBox<String> eventNameF;

    @FXML
    private ChoiceBox<String> typeCB;

    @FXML
    private TextField capF;

    @FXML
    private void clickCap() {
        try {int cap = Integer.parseInt(capF.getText());
            if(eventController.cancelEventsBySize(cap, true)){
                capF.clear();
                model.saveControllers();
                showAlert("Cancel successfully");
            }else {
                showAlert("Fail to cancel");
            }
        }catch (NumberFormatException e){
            showAlert("Invalid event capacity");
            capF.clear();
        }
    }

    @FXML
    void clickName() {
        if(eventController.cancelEventByID(eventNameF.getValue())){
            model.saveControllers();
            showAlert("Cancel successfully");
        }else {
            showAlert("Failed to cancel event. Please enter an existing event name");
        }
    }

    @FXML
    private void clickType() {
        try{
        EventType type = EventType.valueOf(typeCB.getValue());
        if(eventController.cancelEventsByType(type)){
            model.saveControllers();
            typeCB.getSelectionModel().clearSelection();
            showAlert("Cancel successfully");
        }else {
            showAlert("Fail to cancel");
        }}catch (NullPointerException e){
            showAlert("Please select a type");
        }
    }

    @FXML
    private void clickEmpty() {
        if(eventController.cancelEventsBySize(0, false)){
            showAlert("Cancel successfully");
            model.saveControllers();
        }else {
            showAlert("Fail to cancel");}
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(EventType type:EventType.values()){
            typeCB.getItems().add(type.toString());
        }
    }

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
        this.eventController = model.getEventController();
        eventNameF.setOnMouseClicked(event -> {
            eventNameF.getItems().clear();
            for(List<List<String>> events: model.getEventController().getListEvents()){
                eventNameF.getItems().add(events.get(EventIndex.EVENT_ID.getValue()).get(0));
            }
        });
    }

    /**
     * Set up the view to show according to user type
     */
    public void setup(){
        if(model.getUserController().getCurrentUserType().toString().equals("ORGANIZER")){
            deleteAllBt.setVisible(false);
            deleteAllBt.setManaged(false);
        }
    }

    private void showAlert(String al){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(al);
        alert.showAndWait();
    }
}
