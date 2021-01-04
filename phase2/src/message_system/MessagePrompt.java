package message_system;

/**
 * Enum Class for returning informative error/success messages for the Controller class of Message
 */
public enum MessagePrompt {
    SEND_SUCCESS,
    CHANGE_STATUS_SUCCESS,
    MARK_STATUS_READ_SUCCESS,
    MARK_STATUS_UNREAD_SUCCESS,
    MARK_STATUS_ARCHIVE_SUCCESS,
    DELETE_SUCCESS,
    UNAUTHORIZED,
    USER_NOT_FOUND,
    INVALID_INPUT,
    INVALID_EVENT_INPUT,
    MESSAGE_DOES_NOT_EXIST
}
