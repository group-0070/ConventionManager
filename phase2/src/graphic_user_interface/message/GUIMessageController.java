package graphic_user_interface.message;

import graphic_user_interface.IControllerBuilder;
import graphic_user_interface.ComponentController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import message_system.IMessageController;
import message_system.MessageIndex;
import message_system.MessagePrompt;
import message_system.MessageStatus;
import org.controlsfx.control.CheckComboBox;
import user_system.UserType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class GUIMessageController implements ComponentController {

    private IControllerBuilder model;
    private IMessageController controller;

    @FXML
    private VBox direct_message, multi_message, event_message, delete_message, view_message, archive_message;

    @FXML
    private BorderPane border;

    @FXML
    private TextField message, message_event, message_multi;

    @FXML
    private ComboBox<String> users_CB, userTypes_CB, allUsers_CB, archiveUsers_CB;

    @FXML
    private CheckComboBox<String> events_CB, deleteMessages_CB, archiveMessage_CB, deleteAllMessages_CB, markRead_CB, deleteSent_CB;

    @FXML
    private Button messageLog_btn, messageArchive_btn;

    @FXML
    private Text warning_DM, warning_event, warning_multi, warning_delete, warning_archive;

    @FXML
    private Text message_info, message_log, archive_log;

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
        this.controller = model.getMessageController();

        hideAllWarnings();

        userTypes_CB.getItems().addAll("Attendee", "Speaker");

        updateAllMessagesToDelete();

        direct_message.managedProperty().bind(direct_message.visibleProperty());
        event_message.managedProperty().bind(event_message.visibleProperty());
        multi_message.managedProperty().bind(multi_message.visibleProperty());
        view_message.managedProperty().bind(view_message.visibleProperty());
        delete_message.managedProperty().bind(delete_message.visibleProperty());
        archive_message.managedProperty().bind(archive_message.visibleProperty());

        warning_DM.setFill(Color.web("#cc0000"));
        warning_event.setFill(Color.web("#cc0000"));
        warning_multi.setFill(Color.web("#cc0000"));
        warning_delete.setFill(Color.web("#cc0000"));
        warning_archive.setFill(Color.web("#cc0000"));
    }

    /**
     *  Sets up message component depending on the current user logged in:
     *  - Hides certain aspects
     *  - Sets width to evenly format the subcomponents
     *  - Sets options in ComboBoxes
     */
    public void SetupForUser() {
        switch (model.getUserController().getCurrentUserType()) {
            case ADMIN:
                hideComponent(direct_message);
                hideComponent(event_message);
                hideComponent(multi_message);
                hideComponent(view_message);
                hideComponent(archive_message);
                border.setPrefWidth(270);
                break;
            case SPEAKER:
                hideComponent(multi_message);
                hideComponent(delete_message);
                border.setPrefWidth(930);
                break;
            case ATTENDEE:
                hideComponent(multi_message);
                hideComponent(event_message);
                hideComponent(delete_message);
                border.setPrefWidth(710);
                break;
            case ORGANIZER:
                hideComponent(event_message);
                hideComponent(delete_message);
                border.setPrefWidth(930);
                break;
        }

        users_CB.getItems().clear();
        allUsers_CB.getItems().clear();
        archiveUsers_CB.getItems().clear();
        events_CB.getItems().clear();

        users_CB.getItems().addAll(controller.getListOfUsersCanMessage());
        allUsers_CB.getItems().addAll(model.getUserController().getListOfAllIds());
        archiveUsers_CB.getItems().addAll(controller.getListOfUsersCanArchive());
        events_CB.getItems().addAll(model.getEventController().getEventsForUser());
    }

    /**
     *  Displays the messages with the user selected in users_CB
     *  Sets messages that can be marks and deleted in the CheckComboBoxes markRead_CB and deleteSend_CB
     */
    @FXML
    private void showDMMessages() {
        clearAllCheckModels();
        List<List<String>> messages = controller.showConversation(users_CB.getValue());
        StringBuilder formatted_message = new StringBuilder();

        for (String message : buildMessage(messages, true, true, false)) {
            formatted_message.append(message);
        }

        if (formatted_message.length() > 0) {
            formatted_message.deleteCharAt(formatted_message.length() - 1);
        }
        else {
            formatted_message.append("No message history with this user :(");
        }
        this.message_info.setText(formatted_message.toString());

        markRead_CB.getItems().clear();
        for (String message : buildMessage(controller.showReceivedMessage(model.getUserController().getCurrentUserID(), users_CB.getValue()),
                                false, true, true)) {
            markRead_CB.getItems().add(message);
        }

        deleteSent_CB.getItems().clear();
        for (String message : buildMessage(controller.showReceivedMessage(users_CB.getValue(), model.getUserController().getCurrentUserID()),
                                true, true, true)) {
            deleteSent_CB.getItems().add(message);
        }
    }

    /**
     * Sets messages that can be deleted from the selected user from ComboBox allUsers_CB in the
     * CheckComboBox deleteMessage_CB
     */
    @FXML
    private void showMessageDelete() {
        clearAllCheckModels();
        List<List<String>> messages = controller.showMessage(allUsers_CB.getValue());
        deleteMessages_CB.getItems().clear();

        for (String message : buildMessage(messages, true, true, true)) {
            deleteMessages_CB.getItems().add(message);
        }
    }

    /**
     * Sets messages that can be archived from the selected user from ComboBox allUsers_CB to the logged in user
     * in the CheckComboBox deleteMessage_CB
     */
    @FXML
    private void showMessageCanArchive() {
        clearAllCheckModels();
        List<List<String>> messages = controller.getArchivableMessagesByUser(archiveUsers_CB.getValue());
        archiveMessage_CB.getItems().clear();

        for (String message : buildMessage(messages, false, true, true)) {
            archiveMessage_CB.getItems().add(message);
        }
    }

    /**
     * Shows/Hides the message history for the currently logged in user
     */
    @FXML
    private void toggleMessageLog() {
        toggleLog(message_log, messageLog_btn, "DMS");
    }

    /**
     * Shows/Hides the archived messages for the currently logged in user
     */
    @FXML
    private void toggleArchiveLog() {
        toggleLog(archive_log, messageArchive_btn, "ARCHIVE");
    }

    /**
     * Attempts to send the message given in the TextField message to the user selected in the
     * ComboBox users_CB
     * Displays appropriate Text warning_DM based on output of messaging
     */
    @FXML
    private void sendDirectMessage() {
        hideAllWarnings();
        if (users_CB.getValue() == null) {
            showWarning(warning_DM, "Please select a user to message");
            message_multi.clear();
            return;
        }
        MessagePrompt output = controller.sendMessage(message.getText(), users_CB.getValue());

        showMessageWarnings(output, warning_DM);
        message.clear();
        showDMMessages();
    }

    /**
     * Attempts to send the message given in the TextField message_event to the users in the selected events in
     * CheckComboBox events_CB
     * Displays appropriate Text warning_event based on output of messaging
     */
    @FXML
    private void messageEvents() {
        hideAllWarnings();
        ObservableList<String> events = events_CB.getCheckModel().getCheckedItems();
        if (events.size() == 0) {
            showWarning(warning_event, "Please select at least one event to message");
            message_event.clear();
            return;
        }

        List<String> eventIDs = new ArrayList<>(events);

        MessagePrompt output = controller.speakerMultiMessageForEvents(message_event.getText(), eventIDs);

        showMessageWarnings(output, warning_event);
        message_event.clear();
        clearAllCheckModels();
    }

    /**
     * Attempts to send the message given in the TextField message_multi to the users of the type selected in
     * ComboBox userTypes_CB
     * Displays appropriate Text warning_multi based on output of messaging
     */
    @FXML
    private void multiMessageUsers() {
        hideAllWarnings();
        if (userTypes_CB.getValue() == null) {
            showWarning(warning_multi, "Please select a user type to multi-cast to");
            message_multi.clear();
            return;
        }

        MessagePrompt output = controller.multiMessage(message_multi.getText(),
                                                        UserType.valueOf(userTypes_CB.getValue().toUpperCase()));
        showMessageWarnings(output, warning_multi);
        message_multi.clear();
        clearAllCheckModels();
    }

    /**
     * Attempts to delete the messages selected in CheckComboBox deleteSent_CB
     * Displays appropriate Text warning_DM based on output of deleting
     */
    @FXML
    private void deleteSentMessage() {
        deleteMessages(deleteSent_CB, warning_DM);
        showDMMessages();
    }

    /**
     * Attempts to delete the messages selected in CheckComboBox deleteSent_CB
     * Displays appropriate Text warning_DM based on output of deleting
     */
    @FXML
    private void deleteUserMessage() {
        deleteMessages(deleteMessages_CB, warning_delete);
        updateAllMessagesToDelete();
        showMessageDelete();
    }

    /**
     * Attempts to delete the messages selected in CheckComboBox deleteAllMessages_CB
     * Displays appropriate Text warning_delete based on output of deleting
     */
    @FXML
    private void deleteSystemMessage() {
        deleteMessages(deleteAllMessages_CB, warning_delete);
        updateAllMessagesToDelete();
        showMessageDelete();
    }

    /**
     * Attempts to archive the messages selected in CheckComboBox archiveMessage_CB
     * Displays appropriate Text warning_archive based on output of archiving
     */
    @FXML
    private void archiveMessage() {
        hideAllWarnings();
        ObservableList<String> checked_messages = archiveMessage_CB.getCheckModel().getCheckedItems();
        if (checked_messages.size() == 0) {
            showWarning(warning_archive, "Select at least 1 message to archive");
            return;
        }

        MessagePrompt output = MessagePrompt.MARK_STATUS_ARCHIVE_SUCCESS;
        for (String message : checked_messages) {
            output = controller.changeMessageStatus((UUID.fromString(message.substring(4, 40))),
                    MessageStatus.ARCHIVE);
            if (output != MessagePrompt.MARK_STATUS_ARCHIVE_SUCCESS) {
                break;
            }
        }

        showMessageCanArchive();

        showMessageWarnings(output, warning_archive);
    }

    /**
     * Attempts to mark the messages selected in CheckComboBox markRead_CB read/unread
     * Displays appropriate Text warning_DM based on output of changing the status
     */
    @FXML
    private void markReadOrUnread() {
        hideAllWarnings();
        ObservableList<String> checked_messages = markRead_CB.getCheckModel().getCheckedItems();
        if (checked_messages.size() == 0) {
            showWarning(warning_DM, "Select at least 1 message to mark READ/UNREAD");
            return;
        }

        MessagePrompt output = MessagePrompt.CHANGE_STATUS_SUCCESS;
        for (String message : checked_messages) {
            if (message.endsWith("UNREAD\n")) {
                output = controller.changeMessageStatus((UUID.fromString(message.substring(4, 40))), MessageStatus.READ);
            }
            else {
                output = controller.changeMessageStatus((UUID.fromString(message.substring(4, 40))), MessageStatus.UNREAD);
            }
            if (output == MessagePrompt.MESSAGE_DOES_NOT_EXIST) { // the false case
                break;
            }
        }

        showMessageWarnings(output, warning_DM);
        showDMMessages();
    }

    private void showWarning(Text warning, String text) {
        warning.setText(text);
        warning.setVisible(true);
    }

    private void hideAllWarnings() {
        warning_archive.setVisible(false);
        warning_delete.setVisible(false);
        warning_DM.setVisible(false);
        warning_multi.setVisible(false);
        warning_event.setVisible(false);
    }

    private void hideComponent(VBox component) {
        component.setVisible(false);
        component.setMaxSize(0, 0);
    }

    private void updateAllMessagesToDelete() {
        clearAllCheckModels();
        deleteAllMessages_CB.getItems().clear();
        List<List<String>> messages = controller.getAllMessagesInSystem();

        for (String message : buildMessage(messages, true, true, true)) {
            deleteAllMessages_CB.getItems().add(message);
        }
    }

    private ArrayList<String> buildMessage(List<List<String>> messages, boolean includeReceiver, boolean includeStatus, boolean includeID) {
        ArrayList<String> returnList = new ArrayList<>();

        for (List<String> message : messages) {
            StringBuilder formatted_message = new StringBuilder();
            if (includeID) {
                formatted_message.append("ID: ").append(message.get(MessageIndex.MESSAGE_ID.getValue())).append(" ");
            }
            formatted_message.append(message.get(MessageIndex.TIME.getValue())).append(" ");
            formatted_message.append(message.get(MessageIndex.SENDER.getValue()));
            if (includeReceiver) {
                formatted_message.append("->").append(message.get(MessageIndex.RECEIVER.getValue()));
            }
            formatted_message.append(": ");
            formatted_message.append(message.get(MessageIndex.MESSAGE_INFO.getValue())).append(" ");

            if (includeStatus) {
                formatted_message.append(message.get(MessageIndex.STATUS.getValue()));
            }

            formatted_message.append("\n");

            returnList.add(formatted_message.toString());
        }

        return returnList;
    }

    private void showMessageWarnings(MessagePrompt output, Text warning){
        switch (output) {
            case SEND_SUCCESS:
                showWarning(warning, "Message sent successfully!");
                break;
            case MARK_STATUS_READ_SUCCESS:
                showWarning(warning, "Message(s) status changed to READ successfully!");
                break;
            case MARK_STATUS_UNREAD_SUCCESS:
                showWarning(warning, "Message(s) status changed to UNREAD successfully!");
                break;
            case MARK_STATUS_ARCHIVE_SUCCESS:
                showWarning(warning, "Message(s) archived successfully!");
                break;
            case CHANGE_STATUS_SUCCESS:
                showWarning(warning, "Message(s) status changed successfully!");
                break;
            case DELETE_SUCCESS:
                showWarning(warning, "Message(s) deleted successfully!");
                break;
            case MESSAGE_DOES_NOT_EXIST:
                showWarning(warning_DM, "Message(s) do not exist");
                break;
            case UNAUTHORIZED:
                showWarning(warning, "You aren't authorized to send this message");
                break;
            case INVALID_INPUT:
                showWarning(warning, "Invalid input in message");
                break;
            case INVALID_EVENT_INPUT:
                showWarning(warning, "Invalid input in event selection");
                break;
            case USER_NOT_FOUND:
                showWarning(warning, "User not found");
                break;
        }
    }

    private void clearAllCheckModels() {
        events_CB.getCheckModel().clearChecks();
        deleteMessages_CB.getCheckModel().clearChecks();
        archiveMessage_CB.getCheckModel().clearChecks();
        deleteAllMessages_CB.getCheckModel().clearChecks();
        markRead_CB.getCheckModel().clearChecks();
        deleteSent_CB.getCheckModel().clearChecks();
    }

    private void deleteMessages(CheckComboBox<String> cb, Text warning) {
        hideAllWarnings();
        ObservableList<String> checked_messages = cb.getCheckModel().getCheckedItems();
        if (checked_messages.size() == 0) {
            showWarning(warning, "Select at least 1 message to delete");
            return;
        }

        MessagePrompt output = MessagePrompt.DELETE_SUCCESS;
        for (String message : checked_messages) {
            output = controller.deleteMessage((UUID.fromString(message.substring(4, 40))));
            if (output != MessagePrompt.DELETE_SUCCESS) {
                break;
            }
        }

        showMessageWarnings(output, warning);
    }

    private void toggleLog(Text log, Button toggle_btn, String log_type) {
        hideAllWarnings();
        if (log.getText().length() == 0) {
            List<List<String>> messages = null;
            boolean status = false;
            StringBuilder formatted_message = new StringBuilder();

            if (log_type.equals("DMS")) {
                messages = controller.showMessage();
                status = true;
            }
            else if (log_type.equals("ARCHIVE")) {
                messages = controller.getGivenStatusMessagesByUser(MessageStatus.ARCHIVE);
                status = false;
            }

            if (messages == null) {
                throw new IllegalArgumentException("Given log type is invalid");
            }

            for (String message : buildMessage(messages, true, status, false)) {
                formatted_message.append(message);
            }

            if (formatted_message.length() > 0) {
                formatted_message.deleteCharAt(formatted_message.length() - 1);
            }
            else {
                formatted_message.append("No messages :(");
            }
            log.setText(formatted_message.toString());

            toggle_btn.setText("Hide Messages");
        }
        else {
            log.setText("");
            toggle_btn.setText("Show Messages");
        }
    }

}
