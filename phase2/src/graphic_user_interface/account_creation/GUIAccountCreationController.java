package graphic_user_interface.account_creation;

import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ComponentController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import user_system.UserType;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIAccountCreationController implements Initializable, ComponentController {

    private IControllerBuilder model;

    @FXML
    private Label warning;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private ComboBox<String> account_types;

    /**
     * Initializes the the model within this presenter
     * @param model IModel              given model with all information to the backend
     * @throws IllegalStateException    When there already exists a model within the presenter
     */
    public void initModel(IControllerBuilder model) throws IllegalStateException {
        if (this.model != null) {
            throw new IllegalStateException("Model already initialized");
        }

        this.model = model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        account_types.getItems().setAll("Attendee", "Speaker", "Admin");
        warning.setVisible(false);
    }

    /**
     * Attempts to create a user with the type selected in the ComboBox account_types, name in Text username, and
     * password in Text password
     */
    @FXML
    private void createUser() {
        if (account_types.getValue() == null) {
            showWarning("Please select a user type");
            clearFields();
            return;
        }

        boolean success = model.getUserController().addUser(username.getText(), password.getText(),
                                                            UserType.valueOf(account_types.getValue().toUpperCase()));
        if (!success) {
            showWarning("Couldn't create user");
        }
        else {
            showAlert("User created successfully!");
            warning.setVisible(false);
        }
        clearFields();
    }

    private void showWarning(String text) {
        warning.setText(text);
        warning.setVisible(true);
    }

    private void showAlert(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void clearFields() {
        username.clear();
        password.clear();
    }

}
