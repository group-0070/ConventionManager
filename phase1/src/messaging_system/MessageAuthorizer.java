package messaging_system;

import login_system.ILoginController;

/**
 * Use case (Authorizer) class that defines the messaging rules by checking who can send/multicast
 * messages.
 */
public class MessageAuthorizer implements MessageAuthorization {

    private final String userType;

    /**
     * Constructor for MessageAuthorizer
     * @param loginController the LoginController from the login_system
     */
    public MessageAuthorizer(ILoginController loginController){
        super();
        userType = loginController.getCurrentUserType();
    }

    /**
     * Check whether the user is allowed to send message
     * @param receiverId    the id for the message receiver
     * @return              true if the user can send the message
     */
    public boolean canMessage(String receiverId) {
        if (userType.equals("Attendee")) {
            return !(receiverId.charAt(0)=='o');
        } else {
            return true;
        }
    }

    /**
     * check if the type of user can multimessage
     * Note: only Speaker, Organizer can do this
     * @return true if the multicasting is legal.
     */
    public boolean canMulticast () {
        return (userType.equals("Speaker") || userType.equals("Organizer"));
    }
}