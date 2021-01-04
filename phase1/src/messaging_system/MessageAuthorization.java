package messaging_system;

/**
 * Use case (Authorizer) interface with canMessage and canMulticast functions.
 */
public interface MessageAuthorization {
    /**
     * Check if the type of user can message the other type of user
     * @param receiverId the id for the message receiver
     * @return true if the sending behavior is legal
     */
    boolean canMessage(String receiverId);

    /**
     * check if the type of user can multimessage
     * Note: only Speaker, Organizer can do this
     * @return true if the multicasting is legal.
     */
    boolean canMulticast();
}
