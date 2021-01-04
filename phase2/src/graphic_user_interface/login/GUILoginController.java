package graphic_user_interface.login;

import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ComponentController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import user_system.IUserController;

import java.net.URL;
import java.util.ResourceBundle;

public class GUILoginController implements Initializable, ComponentController {
    private IUserController controller;
    private IControllerBuilder model;
    private String pw;
    private String name;

    @FXML
    private TextField nameTextField;

    @FXML
    private PasswordField pwField;

    @FXML
    private Label warning;

    @FXML
    private Button loginBt;


    @FXML
    private void clickExitButton() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void clickLoginButton()  {
        login();
    }

    @FXML
    private void loginEnter(KeyEvent event)  {
        if (event.getCode().equals(KeyCode.ENTER)) {
            login();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        warning.setVisible(false);
        warning.setManaged(false);
        name = "";
        pw = "";
        pwField.textProperty().addListener((observable, oldValue, newValue) -> pw = newValue);
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> name = newValue);
    }

    private void clearField(){
        nameTextField.clear();
        pwField.clear();
    }

    private void login() {
        if(controller.loginUser(name,pw)){
            warning.setVisible(false);
            warning.setManaged(false);
            showAlert("Login successfully!");
            Stage primaryStage=(Stage)loginBt.getScene().getWindow();
            primaryStage.close(); // close the window
        }else {
            warning.setVisible(true);
            warning.setManaged(true);
            warning.setWrapText(true);
            warning.setText("Incorrect username/ password");
            warning.setTextFill(Color.web("#cc0000"));
            warning.setFont(Font.font("Cambria", 14));
            showAlert("Incorrect username/ password. Please try again!");
            clearField();
        }
    }

    private void showAlert(String al){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(al);
        alert.showAndWait();
    }

    /**
     * Initializes the the model within this presenter
     * @param m IControllerBuilder      given model with all information to the backend
     * @throws IllegalStateException    When there already exists a model within the presenter
     */
    public void initModel(IControllerBuilder m) {
        if (this.model != null) {
            throw new IllegalStateException("Model already initialized");
        }
        model = m;
        controller = model.getUserController();
    }


}
